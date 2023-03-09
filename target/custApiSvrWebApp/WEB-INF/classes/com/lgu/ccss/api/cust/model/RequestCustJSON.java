package com.lgu.ccss.api.cust.model;

import java.util.Map;

import com.lgu.ccss.common.model.RequestCommonJSON;

public class RequestCustJSON {
	// 0109 고객센터 추가
	public static final String PARAM_DEVICE_TYPE = "deviceType";
	public static final String PARAM_CAR_CTN = "carCtn";
	public static final String PARAM_DATE_FROM = "dateFrom";
	public static final String PARAM_DATE_TO = "dateTo";
	public static final String PARAM_CUS_CTN = "cusCtn";
	public static final String PARAM_SVC_TYPE = "svcType";
	public static final String PARAM_LOG_SEQ = "seq";
	public static final String PARAM_PAGE_NO = "pageNo";
	public static final String PARAM_PAGE_CNT = "pageCnt";
	public static final String PARAM_SORT = "sort";
	
	private RequestCommonJSON common;
	private Map<String, Object> param;
	
	public RequestCommonJSON getCommon() {
		return common;
	}
	public void setCommon(RequestCommonJSON common) {
		this.common = common;
	}
	public Map<String, Object> getParam() {
		return param;
	}
	public void setParam(Map<String, Object> param) {
		this.param = param;
	}
	@Override
	public String toString() {
		return "RequestCustSJON [common=" + common + ", param=" + param + "]";
	}
}
