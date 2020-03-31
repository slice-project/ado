package org.etri.ado;

public class ADOException extends Exception {

	private static final long serialVersionUID = -6389894644393357996L;

	private String m_details;
	
	public ADOException() {
		m_details = null;
	}
	
	
	public ADOException(String details) {
		super(details);
		
		m_details = details;
	}
	
	public ADOException(String details, Throwable cause) {
		super(details, cause);
		
		m_details = details + ", cause=" + cause;
	}
	
	public ADOException(Throwable cause) {
		super(cause);
		
		m_details = "cause=" + cause;
	}
	
	public String getDetails() {
		return m_details;
	}
}
