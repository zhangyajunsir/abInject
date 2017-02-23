package jsrccb.common.exception;

import cn.com.agree.ab.lib.exception.BasicRuntimeException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;



/**
 * 密码非检查异常类
 *
 */
public class PwdException extends BasicRuntimeException {
	/** 序列化版本标识 */
	private static final long serialVersionUID = 8236804044254264101L;

	public PwdException() {
	}

	public PwdException(ExceptionLevel level, String message, Throwable cause) {
		super(level, message, cause);
	}

	public PwdException(ExceptionLevel level, String message) {
		super(level, message);
	}

	public PwdException(ExceptionLevel level, Throwable cause) {
		super(level, cause);
	}

	public PwdException(ExceptionLevel level) {
		super(level);
	}

	public PwdException(String message, Throwable cause) {
		super(message, cause);
	}

	public PwdException(String message) {
		super(message);
	}

	public PwdException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 不填充线程异常栈信息，以提高性能(注意不会记录该异常抛出位置)
	 */
	public Throwable fillInStackTrace() {    
        return this;    
    }  
}
