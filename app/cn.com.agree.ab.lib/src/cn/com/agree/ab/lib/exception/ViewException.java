package cn.com.agree.ab.lib.exception;



/**
 * View视图非检查异常类，系统View层异常超类
 *
 */
public class ViewException extends BasicRuntimeException {
	/** 序列化版本标识 */
	private static final long serialVersionUID = 8236804044254264101L;

	public ViewException() {
	}

	public ViewException(ExceptionLevel level, String message, Throwable cause) {
		super(level, message, cause);
	}

	public ViewException(ExceptionLevel level, String message) {
		super(level, message);
	}

	public ViewException(ExceptionLevel level, Throwable cause) {
		super(level, cause);
	}

	public ViewException(ExceptionLevel level) {
		super(level);
	}

	public ViewException(String message, Throwable cause) {
		super(message, cause);
	}

	public ViewException(String message) {
		super(message);
	}

	public ViewException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 不填充线程异常栈信息，以提高性能(注意不会记录该异常抛出位置)
	 */
	@Override
	public Throwable fillInStackTrace() {    
        return this;    
    }  
}
