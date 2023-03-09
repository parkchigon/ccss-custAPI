package com.lgu.ccss.common.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.NotEmpty;

public class RequestCommonJSON{
	public static final String COMMON_LOGIN_ID = "loginId";
	
	@NotEmpty
	private String apiId;
	private String ccssToken;
	private String sessionId;
	private String carOem;
	private String loginId;
	private RequestCommonDeviceJSON device;
	private RequestCommonLogDataJSON logData;
	
	public RequestCommonJSON() {
		this.logData = new RequestCommonLogDataJSON();
		this.device = new RequestCommonDeviceJSON();
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public String getCcssToken() {
		return ccssToken;
	}

	public void setCcssToken(String ccssToken) {
		this.ccssToken = ccssToken;
	}
	

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public RequestCommonDeviceJSON getDevice() {
		return device;
	}

	public void setDevice(RequestCommonDeviceJSON device) {
		this.device = device;
	}

	public RequestCommonLogDataJSON getLogData() {
		return logData;
	}

	public void setLogData(RequestCommonLogDataJSON logData) {
		this.logData = logData;
	}
	
	public String getCarOem() {
		return carOem;
	}

	public void setCarOem(String carOem) {
		this.carOem = carOem;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	@Override
	public String toString() {
		return "RequestCommonJSON [apiId=" + apiId + ", ccssToken=" + ccssToken + ", sessionId=" + sessionId
				+ ", carOem=" + carOem + ", loginId=" + loginId + ", device=" + device + ", logData=" + logData + "]";
	}

	
}