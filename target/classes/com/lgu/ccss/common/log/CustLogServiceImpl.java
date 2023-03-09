package com.lgu.ccss.common.log;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.lgu.ccss.api.cust.model.RequestCustJSON;
import com.lgu.ccss.api.cust.util.ResConvertUtil;
import com.lgu.ccss.common.constant.HeaderConst;
import com.lgu.ccss.common.constant.MDCConst;
import com.lgu.ccss.common.model.RequestCommonJSON;
import com.lgu.ccss.common.model.ResponseJSON;
import com.lgu.ccss.common.tlo.TloData;
import com.lgu.ccss.common.tlo.TloUtil;
import com.lgu.ccss.common.util.CCSSUtil;
import com.lgu.common.http.HttpRequestWrapper;
import com.lgu.common.http.HttpResponseWrapper;
import com.lgu.common.tlo.TloWriter;
import com.lgu.common.util.StringUtils;

@Component
public class CustLogServiceImpl implements LogService{
	
	private static Logger logger = LoggerFactory.getLogger(CustLogServiceImpl.class);
	
	@Value("#{systemProperties.SERVER_ID}")
	private String serverId;
	
	@Autowired
	private TloWriter tloWriter;
	
	@Autowired
	private ResConvertUtil resUtil;
	
	@Override
	public void setRequestLog(HttpServletRequest request) {
		HttpRequestWrapper reqWrapper = (HttpRequestWrapper) request;
		byte[] body = reqWrapper.getBodyData();
		String content = new String(body, 0, body.length);

		Gson gson = new Gson();
		RequestCustJSON apiReq = gson.fromJson(content, RequestCustJSON.class);

		MDC.put(MDCConst.API_ID, apiReq.getCommon().getApiId());
		//MDC.put(MDCConst.REMOTE_IP, reqWrapper.getRemoteAddr());

		String requestUrl = StringUtils.nvl(request.getRequestURI());
		CCSSUtil.setCommonDataCustAPI(request, requestUrl, apiReq);
		setRequestTloData(reqWrapper, apiReq);
	}
	
	@Override
	public void setResponseLog(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader(HeaderConst.HD_NAME_TRANSACTION_ID, request.getHeader(HeaderConst.HD_NAME_TRANSACTION_ID));
		HttpRequestWrapper reqWrapper = (HttpRequestWrapper) request;
		HttpResponseWrapper resWrapper = (HttpResponseWrapper) response;

		String content = resWrapper.getResponseText();

		Gson gson = new Gson();
		ResponseJSON apiRes = gson.fromJson(content, ResponseJSON.class);

		setResponseTloData(apiRes);
		logger.debug("tloMapData : {}", TloUtil.getTloData());
		tloWriter.write(TloUtil.getTloData());
	}
	
	private void setRequestTloData(HttpRequestWrapper reqWrapper, RequestCustJSON reqCusJSON) {
		RequestCommonJSON common = reqCusJSON.getCommon();
		Map<String, Object> cusParamMap =  reqCusJSON.getParam();
		Map<String, String> tloMap = new HashMap<String, String>();
		String deviceType = (String) cusParamMap.get("deviceType");
		tloMap.put(TloData.LOG_TYPE, "SVC"); //????
		
		//tlo.put(TloData.SID, );		
		tloMap.put(TloData.REQ_TIME, TloData.getNowDate());
		//tlo.put(TloData.CLIENT_IP, reqWrapper.getRemoteAddr());
		tloMap.put(TloData.CLIENT_IP, common.getLogData().getClientIp());
		tloMap.put(TloData.DEV_INFO, common.getLogData().getDevInfo());
		tloMap.put(TloData.OS_INFO, common.getLogData().getOsInfo());
		tloMap.put(TloData.SVC_NAME, common.getLogData().getSvcName());
		tloMap.put(TloData.SERVER_ID, serverId);
		tloMap.put(TloData.MEMB_ID, CCSSUtil.getMembId());
		tloMap.put(TloData.MEMB_NO, CCSSUtil.getMembNo());		
		tloMap.put(TloData.DEVICE_TYPE, (String) cusParamMap.get("deviceType"));
		tloMap.put(TloData.APP_TYPE, resUtil.convertSvcTypeToNameForEnglish((String)cusParamMap.get("svcType")));
		tloMap.put(TloData.CAR_OEM, common.getCarOem());
		tloMap.put(TloData.CLIENT_ID, CCSSUtil.getSerial());
		tloMap.put(TloData.SESSION_ID, common.getLoginId()+"-"+CCSSUtil.getMgrSessionId());
		tloMap.put(TloData.SVC_CLASS, TloUtil.getSvcClass(common.getApiId()));	
		logger.debug("tloMap : {}", tloMap);
		TloUtil.setTloData(tloMap);
	}
	
	private void setResponseTloData(ResponseJSON apiRes) 
	{
		Map<String, String> tloMap = new HashMap<String, String>();		
		tloMap.put(TloData.RSP_TIME, TloData.getNowDate());
		tloMap.put(TloData.RESULT_CODE, apiRes.getResultCode());		
		String apiId = apiRes.getCommon().getApiId();
		
		/*if(apiId.equals(CCSSConst.API_ID_CUST_LOG_SERVICE_DETAIL)) 
		{*/
			String ctn = CCSSUtil.getCtn();
			logger.debug("ctn : ({})", ctn);
			tloMap.put(TloData.MEMB_ID, CCSSUtil.getMembId());
			tloMap.put(TloData.MEMB_NO, CCSSUtil.getMembNo());
			
		/*}*/
		logger.debug("tloMap : {}", tloMap);
		TloUtil.setTloData(tloMap);
	}
	
}
