package com.lgu.ccss.api.cust.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.map.LinkedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.lgu.ccss.api.cust.model.RequestCustJSON;
import com.lgu.ccss.api.cust.util.CheckAccessUtil;
import com.lgu.ccss.api.cust.util.ResConvertUtil;
import com.lgu.ccss.api.cust.util.RestClientUtil;
import com.lgu.ccss.common.constant.CCSSConst;
import com.lgu.ccss.common.model.RequestJSON;
import com.lgu.ccss.common.model.ResponseJSON;
import com.lgu.ccss.common.util.ResultCodeUtil;
import com.lgu.ccss.common.validation.CheckResultData;
import com.lgu.ccss.common.validation.ValidationChecker;

@Service
public class CustListServiceImpl implements CustListService 
{
	
	private static final Logger logger = LoggerFactory.getLogger(CustListServiceImpl.class);
	
	private static List<String> mandatoryList;
	
	@Value("#{config['custClient.dateBlockYN']}")
	private String dateBlockYN;
	
	@Autowired
	private RestClientUtil client;
	
	@Autowired
	private CheckAccessUtil access;
	
	@Autowired
	private ResConvertUtil resUtil;
		
	@Override
	public ResponseJSON doService(HttpHeaders headers, RequestCustJSON reqJson) {
		ResponseJSON response = null;
		CheckResultData accessResult = access.doCheck(headers, reqJson);
		
		if(accessResult.isStatus() == false){
			response = ResultCodeUtil.createResultMsg(ResultCodeUtil.RC_3C003010, reqJson.getCommon().getApiId(), accessResult.getReason());			
			return response;
		}
		
		CheckResultData result = CheckValidation(headers, reqJson);		
		LinkedMap data = new LinkedMap();
		if(result.isStatus() == false){
			response = ResultCodeUtil.createResultMsg(ResultCodeUtil.RC_3C004000, reqJson.getCommon().getApiId(), result.getReason(), data);
			//logger.debug(result.toString());
			return response;
		}		
		
		try {				
			return listService(checkNumValue(reqJson));
		} catch (Exception e) {
			return ResultCodeUtil.createResultMsg(ResultCodeUtil.RC_4C005000, reqJson.getCommon().getApiId(), "service error");
		}
	}
	
	public ResponseJSON listService(RequestCustJSON reqJson) throws Exception {
		ResponseJSON response = new ResponseJSON();
		response = client.doClient(reqJson);		
		return response;
	}
		
	public CheckResultData CheckValidation(HttpHeaders headers, RequestCustJSON req) {		
		CheckResultData checkResult = null;
		String apiId = req.getCommon().getApiId();		
		checkResult = ValidationChecker.checkValue(headers.getFirst("transactionId"), "header.transactionId");
		if(checkResult.isStatus() == false) {
			return checkResult;
		}
		checkResult = ValidationChecker.checkValue(headers.getFirst("User-Agent"), "header.User-Agent");
		if(checkResult.isStatus() == false) {
			return checkResult;
		}
		if(!CCSSConst.API_ID_CUST_LOG_SERVICE_LIST.equals(apiId)) {
			checkResult = new CheckResultData();
			checkResult.setStatus(false);
			checkResult.setReason("API_ID unmatched");
			return checkResult;
		}
		checkResult = ValidationChecker.CheckCustCommon(req.getCommon());
		if(checkResult.isStatus() == false) {
			return checkResult;
		}		
		String appType = resUtil.convertSvcTypeToNameForEnglish((String)req.getParam().get(RequestJSON.PARAM_SVC_TYPE));
		checkResult = ValidationChecker.checkValue((String)req.getParam().get(RequestJSON.PARAM_SVC_TYPE), "param.appType");
		
		if(checkResult.isStatus() == false) {
			appType = "ALL";
		}
		mandatoryList = new ArrayList<String>(Arrays.asList(
				RequestJSON.PARAM_DEVICE_TYPE, RequestJSON.PARAM_CAR_CTN, 
				RequestJSON.PARAM_DATE_FROM, RequestJSON.PARAM_DATE_TO
				));
		if(appType.equals(CCSSConst.SVC_TYPE_011)) {	
			mandatoryList.add(RequestJSON.PARAM_CUS_CTN);			
		}		
		for(String key : mandatoryList) {			
			checkResult = ValidationChecker.falsification((String)req.getParam().get(key), "param."+key);
			if(checkResult.isStatus() == false) {
				return checkResult;
			}			
		}
		String dateFrom =  (String) req.getParam().get(RequestJSON.PARAM_DATE_FROM);
		String dateTo =  (String) req.getParam().get(RequestJSON.PARAM_DATE_TO);
		
		if(dateBlockYN.equals("Y")) {
			boolean result = checkDateRange(dateFrom, dateTo);			
			if(result ==false) {
				checkResult.setStatus(result);
				checkResult.setReason("param."+RequestJSON.PARAM_DATE_FROM +" | param."+ RequestJSON.PARAM_DATE_TO);
				return checkResult;
			}
		}
		if(dateFrom.length() > 14 || dateTo.length() > 14) {
			checkResult.setStatus(false);
			return checkResult;
		}
		//logger.debug("serviceImpl Validation check OK");
		checkResult.setStatus(true);
		return checkResult;		
	}
	/* 
	 *	pageNo와 pageCnt의 값이 null일경우 default 값을 적용하는 method
	 * 
	 */	
	public RequestCustJSON checkNumValue(RequestCustJSON req) 
	{
		CheckResultData checkResult = new CheckResultData();
		Object pageNo = req.getParam().get(RequestCustJSON.PARAM_PAGE_NO);
		Object pageCnt = req.getParam().get(RequestCustJSON.PARAM_PAGE_CNT);
		if(pageNo instanceof Integer) {
			req.getParam().put(RequestCustJSON.PARAM_PAGE_NO, Integer.toString((int)req.getParam().get(RequestCustJSON.PARAM_PAGE_NO)));
		}
		if(pageCnt instanceof Integer) {
			req.getParam().put(RequestCustJSON.PARAM_PAGE_CNT, Integer.toString((int)req.getParam().get(RequestCustJSON.PARAM_PAGE_CNT)));
		}
		
		checkResult = ValidationChecker.checkInt((String)req.getParam().get(RequestCustJSON.PARAM_PAGE_NO), "param."+RequestCustJSON.PARAM_PAGE_NO);
		if(checkResult.isStatus() == false) 
		{
			req.getParam().put(RequestCustJSON.PARAM_PAGE_NO, "1"); 
		}
		
		checkResult = ValidationChecker.checkInt((String)req.getParam().get(RequestCustJSON.PARAM_PAGE_CNT), "param."+RequestCustJSON.PARAM_PAGE_CNT);
		if(checkResult.isStatus() == false) 
		{
			req.getParam().put(RequestCustJSON.PARAM_PAGE_CNT, "10");
		}
		
		return req;
	}
	/*
	 *	param.dateFrom 과 param.dateTo의 기간 차이 를 검사하는 method  
	 *  dateTo가 dateFrom보다 앞설수 없고, 둘의 기간 사이는 30일을 넘어서는 안되며, 둘의 년도수가 달라서도 안된다.
	 * 
	 */
	public boolean checkDateRange(String fromValue, String toValue) 
	{
		boolean result = true;
		int fromYear = Integer.parseInt(fromValue.substring(0, 4));
		int toYear = Integer.parseInt(toValue.substring(0, 4));
		int fromMonth = Integer.parseInt(fromValue.substring(4, 6));
		int toMonth = Integer.parseInt(toValue.substring(4, 6));
		int fromDay = Integer.parseInt(fromValue.substring(6, 8));
		int toDay = Integer.parseInt(toValue.substring(6, 8));
		
		logger.debug("fromYear : ({})  toYear : ({}) fromMonth: ({}) toMonth: ({}) fromDay: ({}) toDay: ({})", fromYear, toYear, fromMonth, toMonth, fromDay, toDay);
		
		if(toYear < fromYear || toYear-fromYear != 0) 
		{
			result = false;
		}
		if(toMonth < fromMonth || toMonth - fromMonth > 1 || toMonth > 12 || fromMonth > 12)
		{
			result = false;
		}
		if(toDay < fromDay || toDay > 31 || fromDay > 31)
		{
			result = false;
		}
		if(toMonth - fromMonth == 1 && (31 - fromDay)+(31-toDay) > 31)
		{
			result = false;
		}
		
		return result;
	}
}
