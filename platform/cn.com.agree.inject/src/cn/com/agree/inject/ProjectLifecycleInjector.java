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

// ����LifecycleInjector
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
		Preconditions.checkState(injector != null, "����Injectorʧ��");
		injectorRef.set(injector);
		boolean isDebug = Platform.getPreferencesService().getBoolean("Platform", "debugMode", false, null);
		if (isDebug) {
			timer = new Timer("RestartInjectorTimer");
			timer.schedule(restartInjector(), restartInjectorCheckSpan, restartInjectorCheckSpan);	// �ӳ�1����ÿ��1���Ӽ��һ��
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
						projectLifecycleInjectorBuilder.getModuleListBuilder().clearResolvedModule();	// ����֮ǰ������Ӧ�ù������Module
						for (String project : changeProject) {
							projectLifecycleInjectorBuilder.getClasspathScanner(ResourcePlugin.getDefault().getProjectClassLoader(project)).refresh();
						}
						Injector injector = createInjector();
						Preconditions.checkState(injector != null, "����Injectorʧ��");
						injectorRef.set(injector);
						changeProject.clear();
						for (InjectChangeListener listener : injectChangeListeners) {
							listener.changeAfter();
						}
					} catch (Exception e) {
						log.error("����Injectorʧ��", e);
					}
				}
			}
		};
	}
	
	public static ProjectLifecycleInjector bootstrap(final Module externalBindings, final BootstrapModule... externalBootstrapModules) {
		ProjectLifecycleInjectorBuilder builder = new ProjectLifecycleInjectorBuilder();
		// ͨ���ⲿ��Binding�����������ⲿ��BootstrapModuleע��������������ʵ��
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
		// ��ȡBootstrapModuleʵ�����ϣ��ü����Ѿ�������������ʵ��ע���ȥ��
		builder.withAdditionalBootstrapModules(injector.getInstance(Key.get(new TypeLiteral<Set<BootstrapModule>>() {})));
		// ����ɨ���·�������ŷָ�
		String basePackages = Platform.getPreferencesService().getString("cn.com.agree.inject", "basePackages", ".", null);
		builder.usingBasePackages(Sets.newHashSet(Splitter.on(',').trimResults().omitEmptyStrings().split(basePackages)));
		// ʹ�ÿ���ģʽ����֤ProjectOutputClassLoader�仯ʱ���ܹ�����Ĳ����µ�injector
		builder.inStage(Stage.DEVELOPMENT);	// We want fast startup times at the expense of runtime performance and some up front error checking.
		// ʹ�ø��Ƶķ�ʽ������INJECTORS
		builder.withMode(LifecycleInjectorMode.SIMULATED_CHILD_INJECTORS);
		
		// ModuleԤ����
		builder.withModuleTransformer();
		// Injector��������
		builder.withPostInjectorActions(
				new BindingReport(),				// ��־�����������
				new CreateAllBoundSingletons(),		// ��������ѭ������ʵ��
				new LifecycleManagerStarter()		// ����LifecycleManager
				);
		
		return new ProjectLifecycleInjector(builder);
	}
	

	public Injector createInjector() {
		log.info("����Injector��ʼ...");
		long begin = System.currentTimeMillis();
		List<BootstrapModule> bootstrapModules = Lists.newArrayList();
		bootstrapModules.addAll(projectLifecycleInjectorBuilder.getBootstrapModules());
		for (ProjectClassLoader projectClassLoader : ResourcePlugin.getDefault().getDefaultProjectClassLoaders()) {
			ClasspathScanner classpathScanner = projectLifecycleInjectorBuilder.getClasspathScanner(projectClassLoader);
			// ֧��Ӧ�ù����ﱻ�����AutoBindSingletonע�⣬��ʵ����ConfigurationProvider�ӿڵ���
			bootstrapModules.add(new LoadersBootstrapModule(classpathScanner));	
			// ֧��Ӧ�ù����ﱻ�����AutoBindSingletonע�⣬��ʵ����Module�ӿ�
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
		
		// ���ص�actions��transformersʵ�����ϣ��Ѿ�������������ʵ��ע���ȥ��
		Set<PostInjectorAction> actions     = injector.getInstance(Key.get(new TypeLiteral<Set<PostInjectorAction>>(){}));
		Set<ModuleTransformer> transformers = injector.getInstance(Key.get(new TypeLiteral<Set<ModuleTransformer>> (){}));
        // ��ȡӦ�ù�����Module��ʵ��
		List<Module> modules = Lists.newArrayList();
        try {
        	modules.addAll(internalBootstrapModule.getModuleListBuilder().build(injector));
        } catch (Exception e) {
            throw new ProvisionException("Unable to resolve list of modules", e);
        }
        // ɨ��Ӧ�ù������Զ���
        if ( !projectLifecycleInjectorBuilder.isDisableAutoBinding() ) {
            Collection<Class<?>>    localIgnoreClasses = Sets.newHashSet(projectLifecycleInjectorBuilder.getIgnoreClasses());
            for (ProjectClassLoader projectClassLoader : ResourcePlugin.getDefault().getDefaultProjectClassLoaders()) {
    			ClasspathScanner classpathScanner = projectLifecycleInjectorBuilder.getClasspathScanner(projectClassLoader);
    			//ɨ��Ӧ�ù����ﱻ��AutoBindMapper��AutoBindMappersע�����
    			modules.add(new ProjectAutoBindModule(injector, classpathScanner, localIgnoreClasses));
            }
        }
        // ���ص�lifecycleManagerʵ�����Ѿ�������������ʵ��ע���ȥ��
        lifecycleManager = injector.getInstance(LifecycleManager.class);
        lifecycleManagerRef.set(lifecycleManager);
        Injector childInjector = createChildInjector(injector, modules, actions, transformers);
        long end = System.currentTimeMillis();
        log.info("����Injector����...��ʱ" + (end-begin) + "����.");
		return childInjector;
	}

	@Override
	public void projectClassLoaderChanged(String project) {
		// ĳ���̷����ȼ��أ�����������˹��̵��������̶��ᷢ���ȼ��أ��ʲ����첽��ʱ��ⷽʽ
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
			throw new InjectNotReadyException("����ע����δ���������Ե�");
		if (injectorRef.get() == null)
			throw new RuntimeException("û�п��õ�����ע����");
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
