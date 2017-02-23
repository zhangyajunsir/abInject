package cn.com.agree.inject;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.annotation.Resources;

import org.eclipse.core.resources.IProject;

import cn.com.agree.ab.resource.ProjectClassLoader;
import cn.com.agree.inject.annotations.AutoBindMapper;
import cn.com.agree.inject.annotations.AutoBindMappers;
import cn.com.agree.inject.lifecycle.ProjectClasspathScanner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.governator.guice.LifecycleInjectorBuilder;
import com.netflix.governator.guice.LifecycleInjectorBuilderImpl;
import com.netflix.governator.lifecycle.ClasspathScanner;

// ≤Œ’’LifecycleInjectorBuilderImpl
class ProjectLifecycleInjectorBuilder extends LifecycleInjectorBuilderImpl {
	
	private Map<IProject,ProjectClasspathScanner> projectClasspathScanners = Maps.newConcurrentMap();
	
	public ProjectLifecycleInjectorBuilder() {
		super();
	}
	
	@Override
	@Deprecated
    public LifecycleInjectorBuilder usingClasspathScanner(ClasspathScanner scanner) {
		throw new UnsupportedOperationException("ProjectLifecycleInjectorBuilder not support this method");
    }

	@Override
	@Deprecated
    public LifecycleInjector build() {
		throw new UnsupportedOperationException("ProjectLifecycleInjectorBuilder not support this method");
    }
	
	
	public ProjectClasspathScanner getClasspathScanner(ProjectClassLoader projectClassLoader) {
		IProject project = projectClassLoader.getProject();
		ProjectClasspathScanner projectClasspathScanner = projectClasspathScanners.get(project);
		if (projectClasspathScanner == null) {
			synchronized (projectClasspathScanners) {
				projectClasspathScanner = projectClasspathScanners.get(project);
				if (projectClasspathScanner == null) {
					projectClasspathScanner = createStandardClasspathScanner(
							getBasePackages(), 
							ImmutableList.<Class<? extends Annotation>>builder().add(AutoBindMapper.class).add(AutoBindMappers.class).build(), 
							projectClassLoader);
					projectClasspathScanners.put(project, projectClasspathScanner);
				}
			}
		}
		return projectClasspathScanner;
	}
	
	protected ProjectClasspathScanner createStandardClasspathScanner(Collection<String> basePackages, List<Class<? extends Annotation>> additionalAnnotations,  ProjectClassLoader projectClassLoader) {
        List<Class<? extends Annotation>> annotations = Lists.newArrayList();
        annotations.add(Inject.class);
        annotations.add(javax.inject.Inject.class);
        annotations.add(Resource.class);
        annotations.add(Resources.class);
        if ( additionalAnnotations != null ) {
            annotations.addAll(additionalAnnotations);
        }
        return new ProjectClasspathScanner(basePackages, annotations, projectClassLoader);
    }
	
}
