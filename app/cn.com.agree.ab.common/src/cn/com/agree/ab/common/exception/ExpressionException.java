package cn.com.agree.ab.common.exception;

import cn.com.agree.ab.lib.exception.BasicException;
import cn.com.agree.ab.lib.exception.ExceptionLevel;



/**
 * 表达式业务检查异常类
 *
 */
public class ExpressionException extends BasicException {
	/** 序列化版本标识 */
	private static final long serialVersionUID = 8236804044254264101L;

	public ExpressionException() {
	}

	public ExpressionException(ExceptionLevel level, String message, Throwable cause) {
		super(level, message, cause);
	}

	public ExpressionException(ExceptionLevel level, String message) {
		super(level, message);
	}

	public ExpressionException(ExceptionLevel level, Throwable cause) {
		super(level, cause);
	}

	public ExpressionException(ExceptionLevel level) {
		super(level);
	}

	public ExpressionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpressionException(String message) {
		super(message);
	}

	public ExpressionException(Throwable cause) {
		super(cause);
	}
	
}
