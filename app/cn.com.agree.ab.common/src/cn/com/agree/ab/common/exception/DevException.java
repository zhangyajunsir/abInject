package cn.com.agree.ab.common.exception;

import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;



/**
 * 外设操作业务非检查异常类
 *
 */
public class DevException extends BasicRuntimeException {
	/** 序列化版本标识 */
	private static final long serialVersionUID = 8236804044254264101L;

	public DevException() {
	}

	public DevException(ExceptionLevel level, String message, Throwable cause) {
		super(level, message, cause);
	}

	public DevException(ExceptionLevel level, String message) {
		super(level, message);
	}

	public DevException(ExceptionLevel level, Throwable cause) {
		super(level, cause);
	}

	public DevException(ExceptionLevel level) {
		super(level);
	}

	public DevException(String message, Throwable cause) {
		super(message, cause);
	}

	public DevException(String message) {
		super(message);
	}

	public DevException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 不填充线程异常栈信息，以提高性能(注意不会记录该异常抛出位置)
	 */
	public Throwable fillInStackTrace() {    
        return this;    
    }  
}
