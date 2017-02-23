package cn.com.agree.ab.lib.exception;



/**
 * Dao业务非检查异常类，系统DAO层异常超类
 *
 * 创建日期：2012-12-18
 * @author wangk
 */
public class DaoException extends BasicRuntimeException {
	/** 序列化版本标识 */
	private static final long serialVersionUID = 8236804044254264101L;

	/**
	 * 构造方法
	 * 创建日期：2012-12-18
	 * 修改说明：
	 * @author wangk
	 */
	public DaoException() {
	}

	public DaoException(ExceptionLevel level, String message, Throwable cause) {
		super(level, message, cause);
	}

	public DaoException(ExceptionLevel level, String message) {
		super(level, message);
	}

	public DaoException(ExceptionLevel level, Throwable cause) {
		super(level, cause);
	}

	public DaoException(ExceptionLevel level) {
		super(level);
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException(Throwable cause) {
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
