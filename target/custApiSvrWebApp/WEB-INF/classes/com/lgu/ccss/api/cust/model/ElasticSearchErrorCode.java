package com.lgu.ccss.api.cust.model;

import com.lgu.common.model.ResultCode;

public class ElasticSearchErrorCode {
	
	public final static ResultCode RC_20000000 = new ResultCode("ES20000000","성공");
	public final static ResultCode RC_60000000 = new ResultCode("ES60000000","ELASTIC_SEARCH 연동 오류");

}
