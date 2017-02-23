package cn.com.agree.ab.lib.exception;



/**
 * 通讯业务非检查异常类，系统通讯层异常超类
 *
 * 创建日期：2012-12-18
 * @author wangk
 */
public class RpcException extends BasicRuntimeException {
	/** 序列化版本标识 */
	private static final long serialVersionUID = 8236804044254264101L;
	
	private Object msg;

	public RpcException() {
	}

	public RpcException(ExceptionLevel level, String message, Throwable cause) {
		super(level, message, cause);
	}

	public RpcException(ExceptionLevel level, String message) {
		super(level, message);
	}

	public RpcException(ExceptionLevel level, Throwable cause) {
		super(level, cause);
	}

	public RpcException(ExceptionLevel level) {
		super(level);
	}

	public RpcException(String message, Throwable cause) {
		super(message, cause);
	}

	public RpcException(String message) {
		super(message);
	}

	public RpcException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * 不填充线程异常栈信息，以提高性能(注意不会记录该异常抛出位置)
	 */
	@Override
	public Throwable fillInStackTrace() {    
        return this;    
    }

	public Object getMsg() {
		return msg;
	}

	public void setMsg(Object msg) {
		this.msg = msg;
	}  
	
	
}
