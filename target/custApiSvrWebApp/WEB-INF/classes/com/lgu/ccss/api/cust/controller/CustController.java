package com.lgu.ccss.api.cust.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lgu.ccss.api.cust.model.RequestCustJSON;
import com.lgu.ccss.api.cust.service.CustDetailService;
import com.lgu.ccss.api.cust.service.CustListService;
import com.lgu.ccss.common.model.ResponseJSON;

@Controller
@RequestMapping(value = "/cust/log/service")
public class CustController {
	
	//private static final Logger logger = LoggerFactory.getLogger(CustController.class);
	
	@Autowired
	private CustListService ListService;
	
	@Autowired
	private CustDetailService DetailService;
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJSON serviceHistoryList(@RequestHeader HttpHeaders headers, @RequestBody RequestCustJSON reqJson) throws Exception 
	{		
		return ListService.doService(headers, reqJson);
	}
	
	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	@ResponseBody
	public ResponseJSON serviceHistoryDetail(@RequestHeader HttpHeaders headers, @RequestBody RequestCustJSON reqJson) throws Exception 
	{
		return DetailService.doService(headers, reqJson);	
	}
	
}
