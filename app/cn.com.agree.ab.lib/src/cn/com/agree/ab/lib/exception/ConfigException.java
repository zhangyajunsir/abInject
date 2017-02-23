package cn.com.agree.ab.lib.exception;

public class ConfigException  extends BasicRuntimeException {

	private static final long serialVersionUID = -81807750034716084L;

	public ConfigException() {
	}

	public ConfigException(ExceptionLevel level, String message, Throwable cause) {
		super(level, message, cause);
	}

	public ConfigException(ExceptionLevel level, String message) {
		super(level, message);
	}

	public ConfigException(ExceptionLevel level, Throwable cause) {
		super(level, cause);
	}

	public ConfigException(ExceptionLevel level) {
		super(level);
	}

	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(Throwable cause) {
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
