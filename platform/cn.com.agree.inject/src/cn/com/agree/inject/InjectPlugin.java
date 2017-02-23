package cn.com.agree.inject;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.resource.ResourcePlugin;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class InjectPlugin extends Plugin {

	private static final Logger logger = LoggerFactory.getLogger(InjectPlugin.class);
	
    private static InjectPlugin plugin;
    
    private ProjectLifecycleInjector projectLifecycleInjector;
    
    public  static InjectPlugin getDefault() {
        return plugin;
    }
    
    public InjectPlugin() {
    	plugin = this;
    }
    
    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }
    
    /**
     * 用于预启动，而非在插件第一次使用时才启动
     */
    public void startInject () {
    	projectLifecycleInjector = ProjectLifecycleInjector.bootstrap(new AbstractModule() {
			@Override
			protected void configure() {
			}
		});
        ResourcePlugin.getDefault().addProjectClassLoaderListener(projectLifecycleInjector);
    }
    
    public void injectMembers(Object o) {
    	projectLifecycleInjector.getInjector().injectMembers(o);
    }
    
    public <T> T getInstance(Class<T> clazz) {
    	return projectLifecycleInjector.getInjector().getInstance(clazz);
    }
    
    public <T> T getInstance(Class<T> clazz, String name) {
    	// 参照com.netflix.governator.guice.InternalAutoBindModule.bindAutoBindSingleton方法中绑定时采用的Named注解
    	return projectLifecycleInjector.getInjector().getInstance(Key.get(clazz, Names.named(name)));
    }
    
    public Injector getInjector() {
    	return projectLifecycleInjector.getInjector();
    }
    
    public void addChangeListener(InjectChangeListener listener) {
    	projectLifecycleInjector.injectChangeListeners.add(listener);
    }
    
    public void removeChangeListener(InjectChangeListener listener) {
    	projectLifecycleInjector.injectChangeListeners.remove(listener);
    }
}
