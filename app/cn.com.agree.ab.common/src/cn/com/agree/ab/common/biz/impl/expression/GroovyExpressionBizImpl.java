package cn.com.agree.ab.common.biz.impl.expression;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Singleton;

import org.codehaus.groovy.GroovyException;
import org.codehaus.groovy.runtime.InvokerHelper;

import cn.com.agree.ab.common.biz.ExpressionBiz;
import cn.com.agree.ab.common.dm.ExpressionDM;
import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.exception.ExpressionException;
import cn.com.agree.ab.lib.annotation.Biz;
import cn.com.agree.inject.annotations.AutoBindMapper;

@AutoBindMapper(baseClass = ExpressionBiz.class, multiple = true)
@Singleton
@Biz("groovyExpressionBiz")
public class GroovyExpressionBizImpl implements ExpressionBiz {
	
	private GroovyClassLoader     gcl  = new GroovyClassLoader();
	// 对相同脚本只需要生成一回GroovyClass，针对脚本修改后，老的GroovyClass是不会释放的
	private Map<String, Class<?>> pool = new HashMap<String, Class<?>>();
	
	@Override
	public String type() {
		return "groovy";
	}
	
	@Override
	public Object execute(ExpressionDM expressionDM, TradeDataDM context) throws ExpressionException {
		if (expressionDM.getExpression() == null || expressionDM.getExpression().equals("")) {
			return context;
		}
		try {
			Script script = getScriptByString(expressionDM.getExpression(), context.toFieldMapping());
			return script.run();
		} catch (GroovyException e) {
			throw new ExpressionException(e.getMessage(), e);
		}
	}

	/**
	 * @param  scriptString
	 * @param  context
	 * @return Groovy脚本对象
	 * @throws GroovyException
	 */
	public Script getScriptByString(String scriptString, Map<String, Object> context) throws GroovyException {
		Binding binding = new Binding();
		binding.setVariable("context", context);
		binding.setVariable("ctx",     context);
		for (Iterator<String> iter = context.keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			binding.setVariable((String) key, context.get(key));
		}
		Class<?> groovyClazz = getClassByScriptString(scriptString);
		return InvokerHelper.createScript(groovyClazz, binding);
	}

	/**
	 * 根据Groovy脚本字符串获取产生脚本的class
	 * @param  scriptString Groovy脚本字符串
	 * @return 产生脚本的class
	 * @throws GroovyException
	 */
	public Class<?> getClassByScriptString(String scriptString) throws GroovyException {
		if (scriptString == null)
			throw new GroovyException("脚本内容为空");
		
		String hcStr = generateKey(scriptString);
		Class<?> clazz = (Class<?>) pool.get(hcStr);
		if (clazz != null)
			return clazz;
		synchronized (pool) {
			clazz = (Class<?>) pool.get(hcStr);
			if (clazz != null)
				return clazz;
			clazz = gcl.parseClass(scriptString, "class" + hcStr);
			if (clazz != null)
				pool.put(hcStr, clazz);
		}
		
		if (clazz == null)
			throw new GroovyException("产生脚本定义为空");
		return clazz;
	}

	/**
	 * 根据脚本生成脚本对应的class名称，主要利用了脚本字符串的hashcode
	 * 
	 * @param string 要生成class名称的脚本字符串
	 * @return 生成的class名称
	 */
	private static String generateKey(String string) {
		if (string == null)
			return null;
		int hashCode = string.hashCode();
		String hcStr = null;
		if (hashCode < 0) {
			hashCode = -hashCode;
			hcStr = "classN" + hashCode;
		} else {
			hcStr = "classP" + hashCode;
		}
		return hcStr;
	}

}
