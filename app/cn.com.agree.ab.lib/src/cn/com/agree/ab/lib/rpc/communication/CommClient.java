package cn.com.agree.ab.lib.rpc.communication;

import java.util.Map;

import cn.com.agree.ab.lib.rpc.TransformException;

public interface CommClient {
	
	public void initialize(Map<String, String> configMap);

	public byte[] transform(byte[] input)  throws TransformException;
	
	public void destroy();
	
}
