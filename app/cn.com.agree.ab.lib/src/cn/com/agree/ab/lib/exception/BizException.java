package cn.com.agree.ab.lib.exception;



/**
 * Biz业务非检查异常类，系统Service层异常超类
 *
 * 创建日期：2012-12-18
 * @author wangk
 */
public class BizException extends BasicRuntimeException {
	/** 序列化版本标识 */
	private static final long serialVersionUID = 8236804044254264101L;

	/**
	 * 构造方法
	 * 创建日期：2012-12-18
	 * 修改说明：
	 * @author wangk
	 */
	public BizException() {
	}

	public BizException(ExceptionLevel level, String message, Throwable cause) {
		super(level, message, cause);
	}

	public BizException(ExceptionLevel level, String message) {
		super(level, message);
	}

	public BizException(ExceptionLevel level, Throwable cause) {
		super(level, cause);
	}

	public BizException(ExceptionLevel level) {
		super(level);
	}

	public BizException(String message, Throwable cause) {
		super(message, cause);
	}

	public BizException(String message) {
		super(message);
	}

	public BizException(Throwable cause) {
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
