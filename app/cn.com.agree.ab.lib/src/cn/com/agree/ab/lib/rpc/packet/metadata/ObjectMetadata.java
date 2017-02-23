package cn.com.agree.ab.lib.rpc.packet.metadata;

import java.util.List;

public interface ObjectMetadata<K> extends UnitMetadata<Object, K> {
	
	public String getWrapClazzName();
	
	public void setWrapClazzName(String clazzName);
	
	public Class<?> getWrapClazz();

	public void setWrapClazz(Class<?> clazz);

	public void addUnitMetadata(UnitMetadata<Object, Object> unitMetadata);

	public UnitMetadata<Object, Object> getUnitMetadata(String name);

	public List<UnitMetadata<Object, Object>> getUnitMetadatas();

	public int getMaxLength();

	public int getMinLength();
	
	public void setValue(Object value);
	
	public List<Mapping> getMappings();
	
	public void addMapping(Mapping mapping);
	
	public static class Mapping {
		private String expID;
		private String tradeCode;
		private String value;
		private int[]  itemBit;
		
		public Mapping(String expID, String tradeCode, String value, String itemBit) {
			this.expID     = expID;
			this.tradeCode = tradeCode;
			this.value     = value;
			if (itemBit != null && itemBit.length() > 0) {
				String[]  sArray = itemBit.split(",");
				int[]     iArray = new int[sArray.length];
				for (int i=0; i<sArray.length; i++) {
					iArray[i] = Integer.valueOf(sArray[i]);
				}
				this.itemBit = iArray;
			}
		}

		public String getExpID() {
			return expID;
		}

		public String getTradeCode() {
			return tradeCode;
		}

		public String getValue() {
			return value;
		}

		public int[] getItemBit() {
			return itemBit;
		}
		
	}
}
