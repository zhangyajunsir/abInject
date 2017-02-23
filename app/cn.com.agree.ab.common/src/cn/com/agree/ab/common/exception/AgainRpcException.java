package cn.com.agree.ab.common.exception;

import cn.com.agree.ab.lib.exception.ExceptionLevel;
import cn.com.agree.ab.lib.exception.RpcException;



/**
 * 再次操作业务非检查异常类
 *
 */
public class AgainRpcException extends RpcException {
	/** 序列化版本标识 */
	private static final long serialVersionUID = 8236804044254264101L;

	public AgainRpcException() {
	}

	public AgainRpcException(ExceptionLevel level, String message, Throwable cause) {
		super(level, message, cause);
	}

	public AgainRpcException(ExceptionLevel level, String message) {
		super(level, message);
	}

	public AgainRpcException(ExceptionLevel level, Throwable cause) {
		super(level, cause);
	}

	public AgainRpcException(ExceptionLevel level) {
		super(level);
	}

	public AgainRpcException(String message, Throwable cause) {
		super(message, cause);
	}

	public AgainRpcException(String message) {
		super(message);
	}

	public AgainRpcException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 不填充线程异常栈信息，以提高性能(注意不会记录该异常抛出位置)
	 */
	public Throwable fillInStackTrace() {    
        return this;    
    }  
}
