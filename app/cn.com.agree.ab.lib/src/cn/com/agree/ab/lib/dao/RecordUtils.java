package cn.com.agree.ab.lib.dao;

import java.lang.reflect.Field;

import cn.com.agree.ab.lib.dm.EntityDM;
import cn.com.agree.ab.lib.utils.ObjectUtil;
import cn.com.agree.ab.trade.ext.persistence.Reference;

public class RecordUtils {

	private RecordUtils() {
	}
	
	public static Field getReferenceField(Class<? extends EntityDM> sourceClass,
			Class<? extends EntityDM> referencedClass) {
		for (Field field  : ObjectUtil.getFieldsByAnnotation(sourceClass, Reference.class)) {
			Reference reference = field.getAnnotation(Reference.class);
			Class<?> referenceValue = reference.targetClass();
			if(referenceValue == referencedClass) {
				return field;
			}
		}
		return null;
	}
	
	
}
