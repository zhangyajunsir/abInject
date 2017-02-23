package cn.com.agree.ab.lib.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

@Singleton
public class StringFreemarker {
	private Configuration        cfg          = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
	private StringTemplateLoader stringLoader = new StringTemplateLoader();
	
	/** 调用构造器且已完成依赖注入后 */
	@PostConstruct
	public void init() {
		cfg.setTemplateLoader(stringLoader);
	}
	/**
	 * 当GUICE框架发生卸载时，会调用加此注解的方法
	 * 具体详见ProjectLifecycleInjector类里的lifecycleManager.close()
	 * */
	@PreDestroy 
	public void destroy(){
		// 清空freemarker内部的模板对象的缓存
		cfg.clearTemplateCache();
		// 清空模板字符串
		stringLoader.templates.clear();
	}
	
	public String process(String name, String templateSource, Map<String, Object> data) {
		Preconditions.checkState(name != null, "模板名称不可为空");
		Preconditions.checkState(templateSource != null, "模板内容不可为空");
		Preconditions.checkState(data != null, "模板数据不可为空");
		StringTemplateLoader.StringTemplateSource _templateSource_ = stringLoader.templates.get(name);
		if (_templateSource_ == null || !_templateSource_.source.equals(templateSource)) {
			synchronized (stringLoader.templates) {
				_templateSource_ = stringLoader.templates.get(name);
				if (_templateSource_ == null || !_templateSource_.source.equals(templateSource)) {
					stringLoader.putTemplate(name, templateSource);
				}
			}
		}
		
		try {
			Template template = cfg.getTemplate(name, "utf-8");
			StringWriter writer = new StringWriter(); 
			template.process(data, writer);
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
    class StringTemplateLoader implements TemplateLoader {
    	private final Map<String, StringTemplateSource> templates = new HashMap<String, StringTemplateSource>();
    	
		public void putTemplate(String name, String templateSource) {
    		templates.put(name, new StringTemplateSource(name, templateSource, System.currentTimeMillis()));
        }
    	
		@Override
		public void closeTemplateSource(Object name) throws IOException {
		}

		@Override
		public Object findTemplateSource(String name) throws IOException {
			return templates.get(name);
		}

		@Override
		public long getLastModified(Object templateSource) {
			return ((StringTemplateSource)templateSource).lastModified;
		}

		@Override
		public Reader getReader(Object templateSource, String encoding) throws IOException {
			return new StringReader(((StringTemplateSource)templateSource).source);
		}
    	
		class StringTemplateSource {
	        private final String name;
	        private final String source;
	        private final long lastModified;
	        
	        StringTemplateSource(String name, String source, long lastModified) {
	            if(name == null) {
	                throw new IllegalArgumentException("name == null");
	            }
	            if(source == null) {
	                throw new IllegalArgumentException("source == null");
	            }
	            if(lastModified < -1L) {
	                throw new IllegalArgumentException("lastModified < -1L");
	            }
	            this.name = name;
	            this.source = source;
	            this.lastModified = lastModified;
	        }
	        
	        public boolean equals(Object obj) {
	            if(obj instanceof StringTemplateSource) {
	                return name.equals(((StringTemplateSource)obj).name);
	            }
	            return false;
	        }
	        
	        public int hashCode() {
	            return name.hashCode();
	        }
	    }

		public String toString() {
	        return templates.toString();
	    }
    }
}
