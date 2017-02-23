package cn.com.agree.inject;


public class InjectNotReadyException extends RuntimeException {

	private static final long serialVersionUID = 5365382729782567557L;


	public InjectNotReadyException() {
	}


	public InjectNotReadyException(String message, Throwable cause) {
		super(message, cause);
	}

	public InjectNotReadyException(String message) {
		super(message);
	}

	public InjectNotReadyException(Throwable cause) {
		super(cause);
	}
	
	
	@Override
	public Throwable fillInStackTrace() {    
        return this;    
    }  
}
