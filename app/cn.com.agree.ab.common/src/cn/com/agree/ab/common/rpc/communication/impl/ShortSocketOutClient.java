package cn.com.agree.ab.common.rpc.communication.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.agree.ab.lib.rpc.TransformException;
import cn.com.agree.ab.lib.rpc.communication.CommClient;
import cn.com.agree.ab.lib.utils.Hex;

/**
 * @author zhangyajun
 */
public class ShortSocketOutClient implements CommClient {
	private static final Logger	logger	= LoggerFactory.getLogger(ShortSocketOutClient.class);
	private String  ip;
	private Integer port;
	private Integer timeout;
	
	@Override
	public void initialize(Map<String, String> configMap) {
		ip      = configMap.get("IP");
		port    = Integer.valueOf(configMap.get("PORT"));
		timeout = Integer.valueOf(configMap.get("TIMEOUT"))*1000;
	}

	@Override
	public byte[] transform(byte[] input) throws TransformException {
		Socket socket   = null;
		InputStream  is = null;
		OutputStream os = null;
		long startTime  = System.currentTimeMillis();
		try {
			logger.debug("Socket链接:IP:{} PORT:{} TIMEOUT:{}", ip, port, timeout);
			socket = new Socket();
			socket.setSoTimeout(timeout);
			SocketAddress sockAddr = new InetSocketAddress(InetAddress.getByName(ip), port);
			socket.connect(sockAddr, 10000);
			logger.debug("Socket链接已经建立");
			os = socket.getOutputStream();
			is = socket.getInputStream();
			logger.info("向服务器发送数据\n{}", Hex.toDisplayString(input));
			os.write(input);
			os.flush();
			socket.shutdownOutput();
			ByteArrayOutputStream resultBuf = new ByteArrayOutputStream();
			byte[] buff = new byte[8192];
			int length = 0;
			while ((length = is.read(buff)) != -1) {
				resultBuf.write(buff, 0, length);
			}
			byte[] responseBytes = resultBuf.toByteArray();
			logger.info("从服务器收到数据大小为[{}],耗时{}ms\n{}", responseBytes.length, System.currentTimeMillis()-startTime, Hex.toDisplayString(responseBytes));
			return responseBytes;
		} catch (Exception e) {
			throw new TransformException("IP地址:" + ip + ", 端口:" + port, e);
		} finally {
			try {
				if (socket != null) socket.close();
				if (is != null) is.close(); 
				if (os != null) os.close();
			} catch (Exception e) {
				throw new TransformException(e);
			}
		}
	}

	@Override
	public void destroy() {
	}

}
