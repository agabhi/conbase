package com.derive.conbase.exception;

public class MessageFaultException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	//this will be the key defined in the resource bundles
	private String faultCode;
	
	private String faultDescription;

	private Throwable faultDetail;

	public MessageFaultException(String faultCode) {
		this.faultCode = faultCode;
	}
	
	public String getFaultCode() {
		return faultCode;
	}

	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}

	public String getFaultDescription() {
		return faultDescription;
	}

	public void setFaultDescription(String faultDescription) {
		this.faultDescription = faultDescription;
	}

	public Throwable getFaultDetail() {
		return faultDetail;
	}

	public void setFaultDetail(Throwable faultDetail) {
		this.faultDetail = faultDetail;
	}
	
}
