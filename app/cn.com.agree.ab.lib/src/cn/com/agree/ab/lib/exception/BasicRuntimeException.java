package cn.com.agree.ab.lib.exception;

/**
 * 运行时异常基类
 *
 * 创建日期：2012-12-18
 * @author wangk
 */
public class BasicRuntimeException extends RuntimeException implements IExceptionLevel {
	/** 序列化版本标识 */
	private static final long serialVersionUID = -4195975136323130509L;
	
	/** 异常日志级别，默认为error级别 */
	private ExceptionLevel level = ExceptionLevel.ERROR;

	/**
	 * 无参构造方法
	 * 创建日期：2012-12-18
	 * 修改说明：
	 * @author wangk
	 */
	public BasicRuntimeException() {
	}

	/**
	 * 构造方法，初始化level
	 * @param level  异常日志级别
	 * 创建日期：2012-12-18
	 * 修改说明：
	 * @author wangk
	 */
	public BasicRuntimeException(ExceptionLevel level) {
		this.level = level;
	}
	
	/**
	 * 构造方法
	 * @param message   异常信息
	 * 创建日期：2012-12-18
	 * 修改说明：
	 * @author wangk
	 */
	public BasicRuntimeException(String message) {
		super(message);
	}

	/**
	 * 构造方法
	 * @param cause 上层异常对象
	 * 创建日期：2012-12-18
	 * 修改说明：
	 * @author wangk
	 */
	public BasicRuntimeException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 构造方法
	 * @param level
	 * @param message
	 * 创建日期：2012-12-18
	 * 修改说明：
	 * @author wangk
	 */
	public BasicRuntimeException(ExceptionLevel level, String message) {
		super(message);
		this.level = level;
	}

	/**
	 * 构造方法
	 * @param level
	 * @param cause
	 * 创建日期：2012-12-18
	 * 修改说明：
	 * @author wangk
	 */
	public BasicRuntimeException(ExceptionLevel level, Throwable cause) {
		super(cause);
		this.level = level;
	}
	
	/**
	 * 构造方法
	 * @param message
	 * @param cause
	 * 创建日期：2012-12-18
	 * 修改说明：
	 * @author wangk
	 */
	public BasicRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 构造方法
	 * @param level
	 * @param message
	 * @param cause
	 * 创建日期：2012-12-18
	 * 修改说明：
	 * @author wangk
	 */
	public BasicRuntimeException(ExceptionLevel level, String message, Throwable cause) {
		super(message, cause);
		this.level = level;
	}
	
	@Override
	public ExceptionLevel getLevel() {
		return level;
	}

	public void setLevel(ExceptionLevel level) {
		this.level = level;
	}

}
