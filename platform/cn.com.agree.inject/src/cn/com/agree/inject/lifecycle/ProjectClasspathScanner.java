package cn.com.agree.inject.lifecycle;

import static org.objectweb.asm.ClassReader.SKIP_CODE;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.resource.ProjectClassLoader;
import cn.com.agree.ab.resource.ProjectOutputClassLoader;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.netflix.governator.lifecycle.AnnotationFinder;
import com.netflix.governator.lifecycle.ClasspathScanner;
import com.netflix.governator.lifecycle.DirectoryClassFilter;

public class ProjectClasspathScanner extends ClasspathScanner {
	
	private static final Logger log = LoggerFactory.getLogger(ProjectClasspathScanner.class);
	
	private Collection<String> basePackages;
	
	private Collection<Class<? extends Annotation>> annotations;
	

	public ProjectClasspathScanner(Collection<String> basePackages, Collection<Class<? extends Annotation>> annotations, ProjectClassLoader projectClassLoader) {
		super(basePackages, annotations, projectClassLoader);
		this.basePackages = basePackages;
		this.annotations  = annotations;
	}

	protected void doScanning(Collection<String> basePackages, Collection<Class<? extends Annotation>> annotations, Set<Class<?>> localClasses, Set<Constructor> localConstructors, Set<Method> localMethods, Set<Field> localFields) {
		if ( basePackages.isEmpty() ) {
            log.warn("No base packages specified - no classpath scanning will be done");
            return;
        }
        log.info("Scanning packages : " + basePackages + " for annotations " + annotations);
        
        ProjectOutputClassLoader projectOutputClassLoader = ((ProjectClassLoader)classLoader).getOutputClassLoader();
        URL[] projectClassRoots = ((URLClassLoader)projectOutputClassLoader).getURLs();
        for (URL projectClassRoot : projectClassRoots) {
        	for ( String basePackage : basePackages )  {
        		String basePackageWithSlashes;
        		if (".".equals(basePackage))
        			basePackageWithSlashes = basePackage = "";
        		else
        			basePackageWithSlashes = basePackage.replace(".", "/");
        		try {
        			URL url = new URL(projectClassRoot.getProtocol(), projectClassRoot.getHost(), projectClassRoot.getFile()+basePackageWithSlashes);
	        		DirectoryClassFilter filter = new ProjectDirectoryClassFilter((ProjectClassLoader)classLoader);
	                for ( String className : filter.filesInPackage(url, basePackage) ) {
	                    AnnotationFinder finder = new AnnotationFinder(classLoader, annotations);
	                    InputStream is = null;
	                    try {
		                    is = filter.bytecodeOf(className);
		                    new ClassReader(is).accept(finder, SKIP_CODE);
	                    } finally {
	                    	is.close();
	                    }
	                    applyFinderResults(localClasses, localConstructors, localMethods, localFields, finder);
	                }
        		} catch (Exception e) {
                    log.error("Unable to scan path '{}{}'. {} ", new Object[]{projectClassRoot.getFile(), basePackageWithSlashes, e.getMessage()});
                }
        	}
        }
        
	}
	
	public synchronized void refresh() {
		log.info("Restart Classpath scanning...");

        Set<Class<?>>       localClasses = Sets.newHashSet();
        Set<Constructor>    localConstructors = Sets.newHashSet();
        Set<Method>         localMethods = Sets.newHashSet();
        Set<Field>          localFields = Sets.newHashSet();

        doScanning(this.basePackages, this.annotations, localClasses, localConstructors, localMethods, localFields);

        classes = ImmutableSet.copyOf(localClasses);
        constructors = ImmutableSet.copyOf(localConstructors);
        methods = ImmutableSet.copyOf(localMethods);
        fields = ImmutableSet.copyOf(localFields);

        log.info("Restart Classpath scanning done");
	}
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}

}
