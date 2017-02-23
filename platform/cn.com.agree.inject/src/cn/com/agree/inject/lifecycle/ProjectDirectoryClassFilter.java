package cn.com.agree.inject.lifecycle;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import cn.com.agree.ab.resource.ProjectClassLoader;

import com.netflix.governator.lifecycle.DirectoryClassFilter;

public class ProjectDirectoryClassFilter extends DirectoryClassFilter {

	public ProjectDirectoryClassFilter(ProjectClassLoader loader) {
		super(loader);
	}
	
	public InputStream bytecodeOf(String className) throws IOException {
        int pos = className.indexOf("<");
        if (pos > -1) {
            className = className.substring(0, pos);
        }
        pos = className.indexOf(">");
        if (pos > -1) {
            className = className.substring(0, pos);
        }
        if (!className.endsWith(".class")) {
            className = className.replace('.', '/') + ".class";
        }

        URL resource = ((ProjectClassLoader)loader).getOutputClassLoader().getResource(className);
        if ( resource != null )
        {
            return new BufferedInputStream(resource.openStream());
        }
        throw new IOException("Unable to open class with name " + className + " because the class loader was unable to locate it");
    }

}
