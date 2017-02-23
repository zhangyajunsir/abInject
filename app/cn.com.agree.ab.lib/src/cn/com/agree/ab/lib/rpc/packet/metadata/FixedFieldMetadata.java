package cn.com.agree.ab.lib.rpc.packet.metadata;

public interface FixedFieldMetadata extends VariableFieldMetadata {
	
	public int getLength();
	
	public boolean isRightFill();
	
	public void setLength(int length);
	
	public void setRightFill(boolean rightFill);
	
	public String getFillChar(); 

	public void setFillChar(String fillChar);
}
