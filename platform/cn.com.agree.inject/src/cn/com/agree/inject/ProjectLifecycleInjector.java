package cn.com.agree.inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.resource.IProjectClassLoaderListener;
import cn.com.agree.ab.resource.ProjectClassLoader;
import cn.com.agree.ab.resource.ResourcePlugin;
import cn.com.agree.inject.annotations.FineGrainedLazySingleton;
import cn.com.agree.inject.annotations.LazySingleton;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.netflix.governator.guice.BootstrapModule;
import com.netflix.governator.guice.InternalAutoBindModuleBootstrapModule;
import com.netflix.governator.guice.InternalBootstrapModule;
import com.netflix.governator.guice.InternalLifecycleModule;
import com.netflix.governator.guice.LifecycleInjectorMode;
import com.netflix.governator.guice.LoadersBootstrapModule;
import com.netflix.governator.guice.MemberInjectingInstanceProvider;
import com.netflix.governator.guice.ModuleTransformer;
import com.netflix.governator.guice.PostInjectorAction;
import com.netflix.governator.guice.actions.BindingReport;
import com.netflix.governator.guice.actions.CreateAllBoundSingletons;
import com.netflix.governator.guice.actions.LifecycleManagerStarter;
import com.netflix.governator.guice.lazy.FineGrainedLazySingletonScope;
import com.netflix.governator.guice.lazy.LazySingletonScope;
import com.netflix.governator.lifecycle.ClasspathScanner;
import com.netflix.governator.lifecycle.LifecycleManager;

// 参照LifecycleInjector
class ProjectLifecycleInjector implements IProjectClassLoaderListener {

	private static final Logger log = LoggerFactory.getLogger(ProjectLifecycleInjector.class);
	
	private ProjectLifecycleInjectorBuilder projectLifecycleInjectorBuilder;
	
	private LifecycleManager lifecycleManager;
	
	private AtomicReference<Injector> injectorRef = new AtomicReference<Injector>(null);
	
	private Set<String> changeProject = Sets.newHashSet();
	
	private Timer timer;
	
	private int restartInjectorCheckSpan = Platform.getPreferencesService().getInt("cn.com.agree.inject", "RestartInjectorCheckSpan", 60*1000, null);
	
	List<InjectChangeListener> injectChangeListeners = new ArrayList<InjectChangeListener>();
	
	private ProjectLifecycleInjector(ProjectLifecycleInjectorBuilder projectLifecycleInjectorBuilder) {
		this.projectLifecycleInjectorBuilder = projectLifecycleInjectorBuilder;
		Injector injector = createInjector();
		Preconditions.checkState(injector != null, "创建Injector失败");
		injectorRef.set(injector);
		boolean isDebug = Platform.getPreferencesService().getBoolean("Platform", "debugMode", false, null);
		if (isDebug) {
			timer = new Timer("RestartInjectorTimer");
			timer.schedule(restartInjector(), restartInjectorCheckSpan, restartInjectorCheckSpan);	// 延迟1分钟每隔1分钟检查一次
		}
	}
	
	private TimerTask restartInjector() {
		return new TimerTask() {
			@Override
			public void run() {
				if (changeProject.isEmpty())
					return;
				synchronized (changeProject) {
					try {
						for (InjectChangeListener listener : injectChangeListeners) {
							listener.changeBefor();
						}
						injectorRef.set(null);
						lifecycleManager.close();
						projectLifecycleInjectorBuilder.getModuleListBuilder().clearResolvedModule();	// 清理之前解析的应用工程里的Module
						for (String project : changeProject) {
							projectLifecycleInjectorBuilder.getClasspathScanner(ResourcePlugin.getDefault().getProjectClassLoader(project)).refresh();
						}
						Injector injector = createInjector();
						Preconditions.checkState(injector != null, "更新Injector失败");
						injectorRef.set(injector);
						changeProject.clear();
						for (InjectChangeListener listener : injectChangeListeners) {
							listener.changeAfter();
						}
					} catch (Exception e) {
						log.error("重启Injector失败", e);
					}
				}
			}
		};
	}
	
	public static ProjectLifecycleInjector bootstrap(final Module externalBindings, final BootstrapModule... externalBootstrapModules) {
		ProjectLifecycleInjectorBuilder builder = new ProjectLifecycleInjectorBuilder();
		// 通过外部的Binding配置来帮助外部的BootstrapModule注入所依赖的其它实现
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				if (externalBindings != null)
                    install(externalBindings);
				Multibinder<BootstrapModule> bootstrapModules = Multibinder.newSetBinder(binder(), BootstrapModule.class);
				if (externalBootstrapModules != null) {
                    for (final BootstrapModule bootstrapModule : externalBootstrapModules) {
                        bootstrapModules.addBinding().toProvider(new MemberInjectingInstanceProvider<BootstrapModule>(bootstrapModule));
                    }
                }
			}
		});
		// 获取BootstrapModule实例集合，该集合已经将其依赖其它实现注入进去了
		builder.withAdditionalBootstrapModules(injector.getInstance(Key.get(new TypeLiteral<Set<BootstrapModule>>() {})));
		// 设置扫描包路径，逗号分隔
		String basePackages = Platform.getPreferencesService().getString("cn.com.agree.inject", "basePackages", ".", null);
		builder.usingBasePackages(Sets.newHashSet(Splitter.on(',').trimResults().omitEmptyStrings().split(basePackages)));
		// 使用开发模式，保证ProjectOutputClassLoader变化时，能够更快的产生新的injector
		builder.inStage(Stage.DEVELOPMENT);	// We want fast startup times at the expense of runtime performance and some up front error checking.
		// 使用复制的方式创建子INJECTORS
		builder.withMode(LifecycleInjectorMode.SIMULATED_CHILD_INJECTORS);
		
		// Module预处理
		builder.withModuleTransformer();
		// Injector创建后处理
		builder.withPostInjectorActions(
				new BindingReport(),				// 日志输出依赖报告
				new CreateAllBoundSingletons(),		// 立即创建循环依赖实例
				new LifecycleManagerStarter()		// 启动LifecycleManager
				);
		
		return new ProjectLifecycleInjector(builder);
	}
	

	public Injector createInjector() {
		log.info("创建Injector开始...");
		long begin = System.currentTimeMillis();
		List<BootstrapModule> bootstrapModules = Lists.newArrayList();
		bootstrapModules.addAll(projectLifecycleInjectorBuilder.getBootstrapModules());
		for (ProjectClassLoader projectClassLoader : ResourcePlugin.getDefault().getDefaultProjectClassLoaders()) {
			ClasspathScanner classpathScanner = projectLifecycleInjectorBuilder.getClasspathScanner(projectClassLoader);
			// 支持应用工程里被添加了AutoBindSingleton注解，且实现了ConfigurationProvider接口的类
			bootstrapModules.add(new LoadersBootstrapModule(classpathScanner));	
			// 支持应用工程里被添加了AutoBindSingleton注解，且实现了Module接口
            bootstrapModules.add(new InternalAutoBindModuleBootstrapModule(classpathScanner, projectLifecycleInjectorBuilder.getIgnoreClasses()));
		}
		InternalBootstrapModule internalBootstrapModule = new InternalBootstrapModule(
				bootstrapModules,
                null, 
                projectLifecycleInjectorBuilder.getStage(),
                projectLifecycleInjectorBuilder.getLifecycleInjectorMode(),
                projectLifecycleInjectorBuilder.getModuleListBuilder(),
                projectLifecycleInjectorBuilder.getPostInjectorActions(),
                projectLifecycleInjectorBuilder.getModuleTransformers(),
                projectLifecycleInjectorBuilder.isDisableAutoBinding());
		
		AtomicReference<LifecycleManager> lifecycleManagerRef = new AtomicReference<LifecycleManager>();
		
		Injector injector = Guice.createInjector(
					projectLifecycleInjectorBuilder.getStage(),
		            internalBootstrapModule,
		            new InternalLifecycleModule(lifecycleManagerRef)
		        );
		
		// 返回的actions或transformers实例集合，已经将其依赖其它实现注入进去了
		Set<PostInjectorAction> actions     = injector.getInstance(Key.get(new TypeLiteral<Set<PostInjectorAction>>(){}));
		Set<ModuleTransformer> transformers = injector.getInstance(Key.get(new TypeLiteral<Set<ModuleTransformer>> (){}));
        // 获取应用工程里Module的实现
		List<Module> modules = Lists.newArrayList();
        try {
        	modules.addAll(internalBootstrapModule.getModuleListBuilder().build(injector));
        } catch (Exception e) {
            throw new ProvisionException("Unable to resolve list of modules", e);
        }
        // 扫描应用工程里自动绑定
        if ( !projectLifecycleInjectorBuilder.isDisableAutoBinding() ) {
            Collection<Class<?>>    localIgnoreClasses = Sets.newHashSet(projectLifecycleInjectorBuilder.getIgnoreClasses());
            for (ProjectClassLoader projectClassLoader : ResourcePlugin.getDefault().getDefaultProjectClassLoaders()) {
    			ClasspathScanner classpathScanner = projectLifecycleInjectorBuilder.getClasspathScanner(projectClassLoader);
    			//扫描应用工程里被加AutoBindMapper或AutoBindMappers注解的类
    			modules.add(new ProjectAutoBindModule(injector, classpathScanner, localIgnoreClasses));
            }
        }
        // 返回的lifecycleManager实例，已经将其依赖其它实现注入进去了
        lifecycleManager = injector.getInstance(LifecycleManager.class);
        lifecycleManagerRef.set(lifecycleManager);
        Injector childInjector = createChildInjector(injector, modules, actions, transformers);
        long end = System.currentTimeMillis();
        log.info("创建Injector结束...用时" + (end-begin) + "毫秒.");
		return childInjector;
	}

	@Override
	public void projectClassLoaderChanged(String project) {
		// 某工程发生热加载，会造成依赖此工程的其它工程都会发生热加载，故采用异步定时监测方式
		boolean isDebug = Platform.getPreferencesService().getBoolean("Platform", "debugMode", false, null);
		if (isDebug) {
			synchronized (changeProject) {
				changeProject.add(project);
			}
		}
	}
	
	public LifecycleManager getLifecycleManager() {
        return lifecycleManager;
    }
	
	public Injector getInjector() {
		if (!changeProject.isEmpty())
			throw new InjectNotReadyException("依赖注入框架未就绪，请稍等");
		if (injectorRef.get() == null)
			throw new RuntimeException("没有可用的依赖注入框架");
		return injectorRef.get();
	}
	
    private Injector createChildInjector(Injector parentInjector, Collection<Module> modules, Set<PostInjectorAction> actions , Set<ModuleTransformer> transformers) {
        Injector childInjector;
        
        Collection<Module> localModules = modules;
        for (ModuleTransformer transformer  : transformers) {
            localModules = transformer.call(localModules);
        }
        //noinspection deprecation
        if ( projectLifecycleInjectorBuilder.getLifecycleInjectorMode() == LifecycleInjectorMode.REAL_CHILD_INJECTORS ) {
            childInjector = parentInjector.createChildInjector(localModules);
        } else {
            childInjector = createSimulatedInjector(parentInjector, localModules);
        }
        for (PostInjectorAction action : actions) {
            action.call(childInjector);
        }
        return childInjector;
    }
    
    private Injector createSimulatedInjector(final Injector parentInjector, Collection<Module> modules) {
        AbstractModule parentObjects = new AbstractModule() {
            @Override
            protected void configure() {
                bindScope(LazySingleton.class, LazySingletonScope.get());
                bindScope(FineGrainedLazySingleton.class, FineGrainedLazySingletonScope.get());
                
                // Manually copy bindings from the bootstrap injector to the simulated child injector.
                Map<Key<?>, Binding<?>> bindings = parentInjector.getAllBindings();
                for (Entry<Key<?>, Binding<?>> binding : bindings.entrySet()) {
                    Class<?> cls = binding.getKey().getTypeLiteral().getRawType();
                    if (   Module.class.isAssignableFrom(cls)
                        || Injector.class.isAssignableFrom(cls)
                        || Stage.class.isAssignableFrom(cls)
                        || Logger.class.isAssignableFrom(cls)
                        || java.util.logging.Logger.class.isAssignableFrom(cls)
                        ) {
                        continue;
                    }
                    Provider provider = binding.getValue().getProvider();
                    bind(binding.getKey()).toProvider(provider);
                }
            }
        };

        AtomicReference<LifecycleManager> lifecycleManagerAtomicReference = new AtomicReference<LifecycleManager>(lifecycleManager);
        InternalLifecycleModule internalLifecycleModule = new InternalLifecycleModule(lifecycleManagerAtomicReference);

        List<Module> localModules = Lists.newArrayList(modules);
        localModules.add(parentObjects);
        localModules.add(internalLifecycleModule);
        return Guice.createInjector(projectLifecycleInjectorBuilder.getStage(), localModules);
    }
}
