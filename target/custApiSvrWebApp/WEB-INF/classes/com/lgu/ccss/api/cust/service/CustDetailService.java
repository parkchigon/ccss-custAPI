package com.lgu.ccss.api.cust.service;

import org.springframework.http.HttpHeaders;

import com.lgu.ccss.api.cust.model.RequestCustJSON;
import com.lgu.ccss.common.model.ResponseJSON;

public interface CustDetailService 
{
	public ResponseJSON doService(HttpHeaders headers, RequestCustJSON reqJson);
}
