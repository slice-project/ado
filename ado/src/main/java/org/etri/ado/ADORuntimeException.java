package org.etri.ado;

public class ADORuntimeException extends RuntimeException {
	
	private static final long serialVersionUID = 1640347564670874910L;

	private String m_details;
	
	public ADORuntimeException() {
		m_details = null;
	}
	
	
	public ADORuntimeException(String details) {
		super(details);
		
		m_details = details;
	}
	
	public ADORuntimeException(String details, Throwable cause) {
		super(details, cause);
		
		m_details = details + ", cause=" + cause;
	}
	
	public ADORuntimeException(Throwable cause) {
		super(cause);
		
		m_details = "cause=" + cause;
	}
	
	public String getDetails() {
		return m_details;
	}
		
}
