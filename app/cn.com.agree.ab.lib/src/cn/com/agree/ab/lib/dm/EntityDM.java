package cn.com.agree.ab.lib.dm;

import java.math.BigDecimal;

import cn.com.agree.ab.trade.ext.persistence.MappingAccessor;
import cn.com.agree.ab.trade.ext.persistence.Record;

public class EntityDM extends BasicDM implements Comparable<EntityDM>{

	private static final long serialVersionUID = 6887675670871346139L;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		String idstr = identityString();
		Long id = idstr == null ? 0 : Long.valueOf(idstr);
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		EntityDM other = (EntityDM) obj;
		String idstr = identityString();
		String oidstr = other.identityString();
		if(idstr == oidstr) {
			return true;
		}
		if(idstr == null) {
			return false;
		}
		return idstr.equals(oidstr);
	}

	@Override
	public int compareTo(EntityDM other) {
		String idstr = identityString();
		String oidstr = other.identityString();
		if(idstr == null && oidstr == null) {
			return 0;
		}
		if(idstr == null) {
			return 1;
		}
		if(oidstr == null) {
			return -1;
		}
		return new BigDecimal(idstr).compareTo(new BigDecimal(oidstr));
	}
	
	/**
	 * 获得实体对象的ID值的字符串形式
	 * @return
	 */
	public String identityString() {
		String identityString = null;
		Record record = MappingAccessor.getInstance().parseRecord(this, null, true, false);
		if (!record.getKeys().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Object id : record.getKeys()) {
				sb.append(id).append('|');
			}
			identityString = sb.toString();
		}
		return identityString;
	}
	
	/**
	 * 判断实体对象的状态是否为瞬时状态(ID值为空)
	 * @return
	 */
	public boolean isTransient() {
		return identityString() == null;
	}
	
	/**
	 * 获得当前对象和other对象属性值不同的列集合，当other的类型和当前对象所属类型不同时将抛出RuntimeException
	 * @param other         比较的对象
	 * @return List<String> 属性值不同的列集合      
	 */
/*	public List<String> getDifferentColumns(EntityBean other) {
		List<String> differentColumns = new ArrayList<String>();
		Class<? extends EntityBean> clazz = getClass();
		for (Field field  : ObjectUtil.getAllField(clazz)) {
			Column column = field.getAnnotation(Column.class);
			if(column == null) {
				continue;
			}
			String name = column.name();
			if(name.equals(COLUMN_NAME_DEFAULT)) {
				name = field.getName().toUpperCase();
			}
			Object value = null;
			Object otherValue = null;
			try {
				Method method = new PropertyDescriptor(field.getName(), clazz).getReadMethod();
				value = method.invoke(this);
				otherValue = method.invoke(other);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if(value==null && otherValue!=null || value!=null && !value.equals(otherValue)) {
				differentColumns.add(name);
			}
		}
		return differentColumns;
	}*/

	/**
	 * 获得指定列名的属性值
	 * @param <T>         属性类型参数，必须用属性的实际类型及其父类型接收返回值，否则将抛出类型转换异常， 指定的列为空或不存在时将抛出RuntimeException
	 * @param columnName  列名
	 * @return T          属性值
	 */
/*	public <T> T getFieldValue(String columnName) {
		Class<? extends EntityBean> clazz = getClass();
		for (Field field  : ObjectUtil.getAllField(clazz)) {
			Column column = field.getAnnotation(Column.class);
			if(column == null) {
				continue;
			}
			String name = column.name();
			if(name.equals(COLUMN_NAME_DEFAULT)) {
				name = field.getName().toUpperCase();
			}
			if(name.equalsIgnoreCase(columnName)) {
				try {
					@SuppressWarnings("unchecked")
					T value = (T)new PropertyDescriptor(field.getName(), clazz).getReadMethod().invoke(this);
					return value;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		throw new FieldColumnMappingException(ErrorCode.FIELD_COLUMN_MAPPING_AS_NOT_EXIST_COLUMN, 
				"Column name " + columnName + " not exist for " + clazz.getName());
	}*/
}
