package com.lgu.ccss.api.cust.model;

public class RequestCommonJSON {
	private String apiId;
	private String carOem;
	private String loginId;
	private RequestCustLogDataJSON logData;
	
	public String getApiId() {
		return apiId;
	}
	public void setApiId(String apiId) {
		this.apiId = apiId;
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
	public RequestCustLogDataJSON getLogData() {
		return logData;
	}
	public void setLogData(RequestCustLogDataJSON logData) {
		this.logData = logData;
	}
	@Override
	public String toString() {
		return "RequestCommonJSON [apiId=" + apiId + ", carOem=" + carOem + ", loginId=" + loginId + ", logData="
				+ logData + "]";
	}
}
