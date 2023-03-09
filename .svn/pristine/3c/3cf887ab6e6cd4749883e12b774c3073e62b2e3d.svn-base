package com.lgu.ccss.api.cust.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.lgu.ccss.api.cust.model.RequestCustJSON;
import com.lgu.ccss.api.cust.util.CheckAccessUtil;
import com.lgu.ccss.api.cust.util.RestClientUtil;
import com.lgu.ccss.common.constant.CCSSConst;
import com.lgu.ccss.common.model.RequestJSON;
import com.lgu.ccss.common.model.ResponseJSON;
import com.lgu.ccss.common.util.ResultCodeUtil;
import com.lgu.ccss.common.validation.CheckResultData;
import com.lgu.ccss.common.validation.ValidationChecker;

@Service
public class CustDetailServiceImpl implements CustDetailService 
{
	
	private static final Logger logger = LoggerFactory.getLogger(CustDetailServiceImpl.class);
	
	private static final List<String> mandatoryList = new ArrayList<String>(Arrays.asList(
			RequestJSON.PARAM_LOG_SEQ
			));
	
	@Autowired
	private RestClientUtil clientUtil;
	
	@Autowired
	private CheckAccessUtil access;
	
	@Override
	public ResponseJSON doService(HttpHeaders headers, RequestCustJSON req) 
	{
		ResponseJSON response = null;
		
		/*
		 * header와 request 데이터의 유효성 검사
		 * false시 파라미터 오류로 표기한다. 
		 */
		CheckResultData resultData = checkValidation(headers, req);
		
		if(resultData.isStatus() == false)
		{
			return ResultCodeUtil.createResultMsg(ResultCodeUtil.RC_3C004000, req.getCommon().getApiId(), resultData.getReason());			
		}
		
		/*
		 * ip 및 loginId가 허가 여부 검사
		 * false 시 접근 권한이 없는 IP 또는 ID로 표기한다.
		 */
		CheckResultData accessResult = access.doCheck(headers, req);
		
		if(accessResult.isStatus() == false)
		{
			response = ResultCodeUtil.createResultMsg(ResultCodeUtil.RC_3C003010, req.getCommon().getApiId(), accessResult.getReason());			
			return response;
		}
		
		/*
		 * request data를 이용하여, elastic search에서 정보를 가져온다.
		 * try이 실패시 서버내부오류로 표기한다. 이후 설정이 변경된다면 네트워크 오류 등으로 표기할수 있다.
		 */
		try 
		{
			return DetailService(req);			
		} 
		
		catch (Exception e) 
		{
			return ResultCodeUtil.createResultMsg(ResultCodeUtil.RC_4C005000, req.getCommon().getApiId(), null);			
		}
	}
	
	public ResponseJSON DetailService(RequestCustJSON req) throws Exception 
	{
		ResponseJSON response = null;
		response = clientUtil.doClient(req);	
		return response;
	}
	
	/*
	 * 유효성 검사
	 * API_ID 유효성 체크(null, matched, 공백)
	 * common data 유효성 체크
	 * mandatory value 체크
	 */
	public CheckResultData checkValidation(HttpHeaders headers, RequestCustJSON req) 
	{
		CheckResultData checkResult = null;	
		
		checkResult = ValidationChecker.checkValue(headers.getFirst("transactionId"), "header.transactionId");
		if(checkResult.isStatus() == false) 
		{
			return checkResult;
		}
		
		String apiId = req.getCommon().getApiId();	
		
		//checkCust에 null/""/" "이 전부 포함되어 있으므로 주석처리
		/*checkResult = ValidationChecker.checkValue(apiId, "common.apiId");
		
		if(checkResult.isStatus() == false) 
		{
			return checkResult;
		}*/
		
		checkResult = ValidationChecker.checkCust(apiId, "common.apiId");
		
		if(!CCSSConst.API_ID_CUST_LOG_SERVICE_DETAIL.equals(apiId)) 
		{
			checkResult = new CheckResultData();
			checkResult.setStatus(false);
			checkResult.setReason("API_ID unmatched");
			return checkResult;
		}
		
		if(checkResult.isStatus() == false) 
		{
			checkResult = new CheckResultData();
			checkResult.setStatus(false);
			checkResult.setReason("API_ID unmatched");
			return checkResult;
		}
		//COMMON CHECK
		checkResult = ValidationChecker.CheckCustCommon(req.getCommon());
		
		if(checkResult.isStatus() == false) 
		{
			return checkResult;
		}	
		
		//MANDATORY 값 CHECK		
		for(String key : mandatoryList) 
		{
			checkResult = ValidationChecker.falsification((String)req.getParam().get(key), "param."+key);
			if(checkResult.isStatus() == false) 
			{
				return checkResult;
			}
		}
		checkResult.setStatus(true);
		return checkResult;		
	}
}
