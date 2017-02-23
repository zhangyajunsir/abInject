package cn.com.agree.ab.common.utils;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import cn.com.agree.ab.common.dm.TradeDataDM;
import cn.com.agree.ab.common.view.AbstractCommonTrade;
import cn.com.agree.ab.delta.BooleanDelta;
import cn.com.agree.ab.delta.IntegerArrayDelta;
import cn.com.agree.ab.delta.IntegerDelta;
import cn.com.agree.ab.delta.MapDelta;
import cn.com.agree.ab.delta.StringArrayDelta;
import cn.com.agree.ab.delta.StringDelta;
import cn.com.agree.ab.key.IComponentKeys;
import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.resource.ResourcePlugin;
import cn.com.agree.ab.trade.TradePlugin;
import cn.com.agree.ab.trade.core.Trade;
import cn.com.agree.ab.trade.core.TradeRequisiteException;
import cn.com.agree.ab.trade.core.component.Button;
import cn.com.agree.ab.trade.core.component.CalendarCombo;
import cn.com.agree.ab.trade.core.component.CheckBox;
import cn.com.agree.ab.trade.core.component.ComboBox;
import cn.com.agree.ab.trade.core.component.DateText;
import cn.com.agree.ab.trade.core.component.DoubleInputText;
import cn.com.agree.ab.trade.core.component.MultiLineText;
import cn.com.agree.ab.trade.core.component.RadioButton;
import cn.com.agree.ab.trade.core.component.Table;
import cn.com.agree.ab.trade.core.component.TextField;
import cn.com.agree.ab.trade.core.component.common.Component;
import cn.com.agree.ab.trade.local.TradeData;
import cn.com.agree.ab.trade.local.TradeManager;
import cn.com.agree.ab.trade.local.TradeServiceProvider;

import com.google.common.collect.Maps;

public class TradeHelper {
    private static String FLAG  = "90^$AN!CXF";
    private static String FLAG2 = "90^$AN!CXF.";
	
	private TradeHelper() {}
	
	
	public static Component getComponent(Trade trade ,String name) throws Exception {
		Field fields[] = trade.getClass().getFields();
		for (int i = 0; i < fields.length; i++) {
			Object obj = fields[i].get(trade);
			if (obj instanceof Component && ((Component)obj).getName().equals(name)) {
				return (Component)obj;
			}
		}
		return null;
		
		
	}
	/**
	 * 设置交易整体画面是否可用
	 * @param trade
	 * @param enable
	 * @throws Exception
	 */
	public static void setComponentEnable(Trade trade, boolean enable) throws Exception {
		Field fields[] = trade.getClass().getFields();
		for (int i = 0; i < fields.length; i++) {
			Object obj = fields[i].get(trade);
			if (obj instanceof TextField) {
				TextField textfield = (TextField) obj;
				textfield.setEnabled(enable);
			}
			if (obj instanceof ComboBox) {
				ComboBox comboBox = (ComboBox) obj;
				comboBox.setEnabled(enable);
			}
			if (obj instanceof DateText) {
				DateText dateText = (DateText) obj;
				dateText.setEnabled(enable);
			}
			if (obj instanceof Button) {
				Button but = (Button) obj;
				but.setEnabled(enable);
			}
			if (obj instanceof Table) {
				Table table = (Table) obj;
				table.setEnabled(enable);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static TradeDataDM getTradeData(Trade trade) {
		TradeDataDM tradeDataDM = new TradeDataDM();
		try {
			tradeDataDM.setUiData(getComponentData(trade));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		tradeDataDM.setStoreData   ((Map<String, Object>)trade.getTradeComponentData().get(IComponentKeys.STOREDATAID));
		tradeDataDM.setTellerInfo  (trade.getTellerInfo());
		if (trade instanceof AbstractCommonTrade) {
			tradeDataDM.setTempArea(((AbstractCommonTrade)trade).getTempArea());
			tradeDataDM.setContext (((AbstractCommonTrade)trade).getContext());
		}
		return tradeDataDM;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setTradeData(Trade trade, TradeDataDM tradeData) {
		Map map = (Map<String, Object>)trade.getTradeComponentData().get(IComponentKeys.STOREDATAID);
		if (map != tradeData.getStoreData())
			ObjectMergeUtil.merge(map, tradeData.getStoreData(), true, true);
		// 每次获取TellerInfo仅仅是copy
		map = trade.getTellerInfo();
		if (map != null && !map.isEmpty()) {	// 降低updateTellerInfo频率
			ObjectMergeUtil.merge(map, tradeData.getTellerInfo(), true, true);
			try {
				trade.updateTellerInfo(map);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		//
		if (trade instanceof AbstractCommonTrade) {
			map = ((AbstractCommonTrade)trade).getTempArea();
			if (map != tradeData.getTempArea())
				ObjectMergeUtil.merge(map, tradeData.getTempArea(), true, true);
			map = ((AbstractCommonTrade)trade).getContext();
			if (map != tradeData.getContext())
				ObjectMergeUtil.merge(map, tradeData.getContext(), true, true);
		}
		try {
			setComponentData(trade, tradeData.getUiData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String[] getComponentId(String tradeClazzPath) {
		Class<? extends Trade> tradeClazz = null;
		try {
			//通过类路径加载对应的class,需要对类路径tradeClazzPath进行去空的处理
			tradeClazz = (Class<? extends Trade>)ResourcePlugin.getDefault().loadClass(tradeClazzPath.trim());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		List<String> componentIds = new ArrayList<String>();
		Field[] fields = tradeClazz.getFields(); //B交易页面的public的属性数组
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			Class<?> fieldClass  = f.getType();
			if (fieldClass == TextField.class ) {
//				Preconditions.checkState(f.getName().startsWith("text"),            "属性【"+f.getName()+"】非【text】开头");
				componentIds.add(f.getName());
			}
			if (fieldClass == ComboBox.class ) {
//				Preconditions.checkState(f.getName().startsWith("combo"),           "属性【"+f.getName()+"】非【combo】开头");
				componentIds.add(f.getName());
			}
			if (fieldClass == MultiLineText.class ) {
//				Preconditions.checkState(f.getName().startsWith("multilinetext"),   "属性【"+f.getName()+"】非【multilinetext】开头");
				componentIds.add(f.getName());
			}
			if (fieldClass == DoubleInputText.class ) {
//				Preconditions.checkState(f.getName().startsWith("doubleinputtext"), "属性【"+f.getName()+"】非【doubleinputtext】开头");
				componentIds.add(f.getName());
			}
			if (fieldClass == CheckBox.class ) {
//				Preconditions.checkState(f.getName().startsWith("checkbox"),        "属性【"+f.getName()+"】非【checkbox】开头");
				componentIds.add(f.getName());
			}
			if (fieldClass == RadioButton.class ) {
//				Preconditions.checkState(f.getName().startsWith("radiobutton"),     "属性【"+f.getName()+"】非【radiobutton】开头");
				componentIds.add(f.getName());
			}
			if (fieldClass == CalendarCombo.class ) {
//				Preconditions.checkState(f.getName().startsWith("calendarcombo"),   "属性【"+f.getName()+"】非【calendarcombo】开头");
				componentIds.add(f.getName());
			}
			if (fieldClass == DateText.class ) {
//				Preconditions.checkState(f.getName().startsWith("datetext"),        "属性【"+f.getName()+"】非【datetext】开头");
				componentIds.add(f.getName());
			}
			if (fieldClass == Table.class ) {
//				Preconditions.checkState(f.getName().startsWith("table"),           "属性【"+f.getName()+"】非【table】开头");
				componentIds.add(f.getName());
			}
		}
		return componentIds.toArray(new String[componentIds.size()]);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> getComponentData(Trade trade) throws Exception {
		/**
		 * 解决同一事件过程中多次调用，起缓存作用
		 * 无效：因为交易事件中对组件进行赋值，无法更新uiData。
		Map<String, Object> uiData = (Map<String, Object>)ContextHelper.getContext("uiData");
		if (uiData != null)
			return uiData;
		 */
		
		/* TradeComponentData即TradeData里保存的是变化的数据，是不完整的，不采用这种方案
		Map allComponentData = trade.getTradeComponentData();
		*/
		Map<String, Object> componentData = Maps.newHashMap();
		Field[] fields = trade.getClass().getFields(); //A交易页面的public的属性数组
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			Class<?> fieldClass  = f.getType();
			Object   fieldObject = f.get(trade);
			try{
				if (fieldClass == TextField.class ) {
//					Preconditions.checkState(f.getName().startsWith("text"),            "属性【"+f.getName()+"】非【text】开头");
					componentData.put(f.getName()+"|value",                              ((TextField)fieldObject).getValue());
					componentData.put(f.getName()+"|"+IComponentKeys.TEXT,               ((TextField)fieldObject).getText());
					componentData.put(f.getName()+"|digits",                             ((TextField)fieldObject).getDigits());
				}
				if (fieldClass == ComboBox.class ) {
//					Preconditions.checkState(f.getName().startsWith("combo"),           "属性【"+f.getName()+"】非【combo】开头");
					componentData.put(f.getName()+"|prefix",                             ((ComboBox)fieldObject).getPrefix());
					componentData.put(f.getName()+"|"+IComponentKeys.TEXT,               ((ComboBox)fieldObject).getText());
				}
				if (fieldClass == MultiLineText.class ) {
//					Preconditions.checkState(f.getName().startsWith("multilinetext"),   "属性【"+f.getName()+"】非【multilinetext】开头");
					componentData.put(f.getName()+"|"+IComponentKeys.TEXT,               ((MultiLineText)fieldObject).getText());
				}
				if (fieldClass == DoubleInputText.class ) {
//					Preconditions.checkState(f.getName().startsWith("doubleinputtext"), "属性【"+f.getName()+"】非【doubleinputtext】开头");
					componentData.put(f.getName()+"|"+IComponentKeys.TEXT,               ((DoubleInputText)fieldObject).getText());
					componentData.put(f.getName()+"|value",                              getValue (trade, f.getName()));
					componentData.put(f.getName()+"|digits",                             getDigits(trade, f.getName()));
				}
				if (fieldClass == CheckBox.class ) {
//					Preconditions.checkState(f.getName().startsWith("checkbox"),        "属性【"+f.getName()+"】非【checkbox】开头");
					componentData.put(f.getName()+"|"+IComponentKeys.CHECKED,            ((CheckBox)fieldObject).isChecked());
				}
				if (fieldClass == RadioButton.class ) {
//					Preconditions.checkState(f.getName().startsWith("radiobutton"),     "属性【"+f.getName()+"】非【radiobutton】开头");
					componentData.put(f.getName()+"|"+IComponentKeys.CHECKED,            ((RadioButton)fieldObject).isChecked());
				}
				if (fieldClass == CalendarCombo.class ) {
//					Preconditions.checkState(f.getName().startsWith("calendarcombo"),   "属性【"+f.getName()+"】非【calendarcombo】开头");
					componentData.put(f.getName()+"|"+IComponentKeys.FORMAT,             ((CalendarCombo)fieldObject).getFormat());
					componentData.put(f.getName()+"|"+IComponentKeys.TEXT,               ((CalendarCombo)fieldObject).getText());
				}
				if (fieldClass == DateText.class ) {
//					Preconditions.checkState(f.getName().startsWith("datetext"),        "属性【"+f.getName()+"】非【datetext】开头");
					componentData.put(f.getName()+"|"+IComponentKeys.FORMAT,             ((DateText)fieldObject).getFormat());
					componentData.put(f.getName()+"|"+IComponentKeys.TEXT,               ((DateText)fieldObject).getDateString());
					componentData.put(f.getName()+"|date",                               ((DateText)fieldObject).getDate());
				}
				if (fieldClass == Table.class ) {
//					Preconditions.checkState(f.getName().startsWith("table"),           "属性【"+f.getName()+"】非【table】开头");
//					componentData.put(f.getName()+"|rows",                               trade.getProperty(f.getName(), "table_data"));//此方法效率虽高，但使用的是取值方式是引用传递，不是值传递，值会跟着变。
					 List list = new ArrayList();
					 List tableData = (List) trade.getProperty(f.getName(), "table_data");
					 if (null != tableData) {
						 for (int j = 0; j < tableData.size();j++)
							 list.add((String[]) tableData.get(j));
					}
					componentData.put(f.getName()+"|rows",                               list);
				}
			} catch(TradeRequisiteException  e){
			} catch(IllegalArgumentException e){
			}
		}
		/**
		 * 无效：因为交易事件中对组件进行赋值，无法更新uiData。
		ContextHelper.setContext("uiData", componentData);
		 */
		return componentData;
	}
	
	public static Map<String, Component> getComponent(Trade trade) throws Exception {
		Map<String, Component> components = Maps.newHashMap();
		Field[] fields = trade.getClass().getFields(); //A交易页面的public的属性数组
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			Class<?> fieldClass  = f.getType();
			if (Component.class.isAssignableFrom(fieldClass)) {
				Component component = (Component)f.get(trade);
				components.put(f.getName(), component);
			}
		}
		return components;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static void setComponentData(Trade trade, Map<String, Object> uiData) throws Exception {
		if(uiData.size() == 0 || uiData == null)
			return;
		/**
		 * 更新事件缓存中的uiData
		 * 无效：因为交易事件中对组件进行赋值，无法更新uiData。
		Map<String, Object> _uiData_ = (Map<String, Object>)ContextHelper.getContext("uiData");
		if (_uiData_ != null && _uiData_ != uiData) {
			ObjectMergeUtil.merge(_uiData_, uiData, true, true);
		}
		 */
		
		Map<String, Component> components = getComponent(trade);
		Iterator<Entry<String, Object>> iterator = uiData.entrySet().iterator(); 
    	while(iterator.hasNext()) { 
    		Entry<String, Object> entry =iterator.next(); 
    		if (entry.getKey().indexOf('|') < 0)
    			continue;
    		String name  = entry.getKey().substring(0, entry.getKey().indexOf('|'));
			String prop  = entry.getKey().substring(entry.getKey().indexOf('|')+1);
			if("".equals(name) || "".equals(prop))
				continue;
			Object value = entry.getValue();
			if(value instanceof Integer){
				trade.delta(new IntegerDelta(name, prop, ((Integer) value).intValue()));
			} else if(value instanceof Boolean){
				trade.delta(new BooleanDelta(name, prop, (Boolean)value));
			} else if(value instanceof Map){
				trade.delta(new MapDelta(name, prop, (Map)value));
			} else if(value instanceof String[]){
				trade.delta(new StringArrayDelta(name, prop, (String[])value));
			} else if(value instanceof Integer[]){
				Integer[] ia = (Integer[])value;
				int[] ib = new int[ia.length];
                for (int i = 0; i < ia.length; i++) {
                	ib[i] = ia[i];
                }
				trade.delta(new IntegerArrayDelta(name, prop, ib));
			} else if(name.startsWith("table") && value instanceof List){
				Table table = (Table) components.get(name);
				if(table != null && table instanceof Table){
					table.clear();
					List rows = (List)value;
					for (int i = 0; i < rows.size(); i++) {
						if (rows.get(i) instanceof String[]) {
							table.addRow(-1, (String[])rows.get(i));
//							trade.delta(new  TableDataAddDelta(name, IComponentKeys.TABLE_DATA, -1, (String[])rows.get(i)));
		        		}
		        	}
				}
			} else if(name.startsWith("combo") && prop.equals("prefix")){
				Component combo = components.get(name);
				if (combo != null && combo instanceof ComboBox) {
					((ComboBox)combo).setPrefix(value.toString());
				}
			} else if(name.startsWith("combo") && prop.equals("items") && value instanceof List){
				Component combo = components.get(name);
				if (combo != null && combo instanceof ComboBox) {
					List     rows  = (List)value;
					String[] items = new String[rows.size()];
					for (int i = 0; i < rows.size(); i++) {
						if (rows.get(i) instanceof String[]) {
							String[] row = (String[])rows.get(i);
							items[i] = row[0].trim() + " " + row[1].trim();
						}
					}
					((ComboBox)combo).setItems(items);
				}
			}
			else {
            	trade.delta(new StringDelta(name, prop,  value.toString()));
            }
    	}

	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getTradeContext(String tradeId, String key) {
		TradeServiceProvider tradeServiceProvider = ((TradeServiceProvider)TradePlugin.getDefault().getTradeService(TradePlugin.TRADESERVICE_LOCAL));
		if (tradeServiceProvider == null)
			return null;
		TradeManager tradeManager = tradeServiceProvider.getTradeManager();
		if (tradeManager == null)
			return null;
		TradeData tradeData = tradeManager.getTradeData(tradeId);
		if (tradeData == null)
			return null;
		Map<String, Object> context = (Map<String, Object>)tradeData.getFieldsData().get("context");
		if (context == null)
			return null;
		return (T) context.get(key);
	}
	
	public static void callTradeEventMethod(Trade trade, String eventName) {
		if (!Pattern.matches(".*_On.*", eventName)) 
			throw new BasicRuntimeException(eventName+"非交易自定义事件方法");
		// 
		Class<?> tradeClass  = null;
		Method   tradeMethod = null;
		for (Class<?> _class_  = trade.getClass(); _class_ != Trade.class; _class_ = _class_.getSuperclass()) {
			if (_class_.getName().indexOf("$$") > 0)	// cgLib动态生成的子类
				continue;
			try {
				tradeMethod = _class_.getDeclaredMethod(eventName);
				tradeMethod.setAccessible(true);
			} catch (NoSuchMethodException e) {
				// Method不在当前类定义，向上转型
				continue;
			} catch (SecurityException e) {
				// Method不在当前类定义，向上转型
				continue;
			}
			tradeClass = _class_;
			break;
		}
		if (tradeClass == null || tradeMethod == null)
			throw new BasicRuntimeException("未找到交易自定义事件方法"+eventName);
		
		// MethodHandles.Lookup为1.7版本提供
		try {
			Constructor<MethodHandles.Lookup> lookupConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
			if (!lookupConstructor.isAccessible()) 
				lookupConstructor.setAccessible(true);
			MethodHandles.Lookup lookUp = lookupConstructor.newInstance(tradeClass, MethodHandles.Lookup.PRIVATE);
			lookUp.unreflectSpecial(tradeMethod, tradeClass).bindTo(trade).invokeWithArguments();
		} catch (Throwable e) {
			throw new BasicRuntimeException(e.getMessage(), e);
		}
	}
	
	private static String getValue(Trade trade, String name) {
        String text = (String) trade.getProperty(name, IComponentKeys.TEXT);
        if (text == null||text.length() == 0)
            return "";
        String picture = (String) trade.getProperty(name,IComponentKeys.PICTURE);
        if (picture == null || picture.length() == 0 || picture.indexOf('\"') == -1)
            return text;
        picture = picture.substring(picture.indexOf("\"") + 1, picture.lastIndexOf("\""));
        if (picture.charAt(0) == '+' || picture.charAt(0) == '-')
            picture = picture.substring(1);
        if (picture.charAt(0) == '$') {
            while (text.startsWith("$")) {
                text = text.substring(1);
            }
        }
        if (picture.length() <= 1)
            return text;
        char[] textArray = text.toCharArray();
        text = "";
        for (int i = 0; i < textArray.length && i < picture.length(); i++) {
            if (FLAG2.indexOf(picture.charAt(picture.length() - 1 - i)) != -1) {
                text = textArray[textArray.length - 1 - i] + text;
            } else {
                if (textArray[textArray.length - 1 - i] != picture.charAt(picture.length() - 1 - i))
                    text = textArray[textArray.length - 1 - i] + text;
            }
        }
        while(text.startsWith("0") && !text.startsWith("0.")&& !"0".equals(text)) {
            text = text.substring(1);
        }
        return text;
    }
	
	private static String getDigits(Trade trade, String name) {
        String text = (String) trade.getProperty(name, IComponentKeys.TEXT);
        if (text == null)
            text = "";
        String picture = (String) trade.getProperty(name, IComponentKeys.PICTURE);
        if (picture == null || picture.length() == 0 || picture.indexOf('\"') == -1)
            return text;
        picture = picture.substring(picture.indexOf("\"") + 1, picture.lastIndexOf("\""));
        if (picture.charAt(0) == '+' || picture.charAt(0) == '-')
            picture = picture.substring(1);
        if (picture.charAt(0) == '$') {
            while (text.startsWith("$")) {
                text = text.substring(1);
            }
        }
        if (picture.length() <= 1)
            return text;
        char[] textArray = text.toCharArray();
        text = "";
        for (int i = 0; i < textArray.length; i++) {
            if (FLAG.indexOf(picture.charAt(picture.length() - 1 - i)) != -1) {
                text = textArray[textArray.length - 1 - i] + text;
            } else {
                if (textArray[textArray.length - 1 - i] != picture.charAt(picture.length() - 1 - i))
                    text = textArray[textArray.length - 1 - i] + text;
            }
        }
        while (text.length() > 1 && text.charAt(0) == '0') {
            text = text.substring(1);
        }
        return text;
    }
}
