package com.lgu.ccss.common.validation;


import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lgu.ccss.api.cust.model.RequestCustJSON;
import com.lgu.ccss.common.model.RequestCommonDeviceJSON;
import com.lgu.ccss.common.model.RequestCommonJSON;
import com.lgu.ccss.common.model.RequestCommonLogDataJSON;

/**
 * @author admin
 * @Date   2019. 1. 28.
 */
public class ValidationChecker {
	
	private static final Logger logger = LoggerFactory.getLogger(ValidationChecker.class);
	
	static Pattern falsification = Pattern.compile("[!@#$%^&*]");
	static Pattern WhiteSpace = Pattern.compile("[\\s]");
	static Pattern Number = Pattern.compile("[\\D]");
	
	public static CheckResultData checkCommon(RequestCommonJSON common) {
		CheckResultData result = null;
		
		RequestCommonDeviceJSON device = common.getDevice();
		result = checkValue(device.getDeviceType(), "common.device.deviceType");
		if (result.isStatus() == false) {
			return result;
		}
		
		result = checkValue(device.getAppType(), "common.device.appType");
		if (result.isStatus() == false) {
			return result;
		}	
		
		//device.getCarOem();		// optional
		
		RequestCommonLogDataJSON logData = common.getLogData();
		result = checkValue(logData.getClientIp(), "common.logData.clientIp");
		if (result.isStatus() == false) {
			return result;
		}
		
		result = checkValue(logData.getDevInfo(), "common.logData.devInfo");
		if (result.isStatus() == false) {
			return result;
		}
		
		result = checkValue(logData.getOsInfo(), "common.logData.osInfo");
		if (result.isStatus() == false) {
			return result;
		}
		
		result = checkValue(logData.getNwInfo(), "common.logData.nwInfo");
		if (result.isStatus() == false) {
			return result;
		}
		
		result = checkValue(logData.getSvcName(), "common.logData.svcName");
		if (result.isStatus() == false) {
			return result;
		}
		
		result = checkValue(logData.getDevModel(), "common.logData.devModel");
		if (result.isStatus() == false) {
			return result;
		}
		
		result = checkValue(logData.getCarrierType(), "common.logData.carrierType");
		if (result.isStatus() == false) {
			return result;
		}
		
		//logData.getSvcType();	// optional
		//logData.getDevType();	// optional
		
		return result;
	}
	
	public static CheckResultData checkValue(String value, String reason) {		
		CheckResultData result = new CheckResultData();
		if (value == null || value.length() == 0) {			
			result.setStatus(false);
			result.setReason(reason);
			return result;
		}
		result.setStatus(true);		
		return result;
	}
	
	public static CheckResultData CheckCustCommon(RequestCommonJSON common) 
	{
		CheckResultData result = new CheckResultData();
		
		result = falsification(common.getCarOem(), "common.carOem");		
		if(result.isStatus() == false) 
		{
			return result;
		}
		
		result = falsification(common.getLoginId(), "common.loginId");		
		if(result.isStatus() == false) 
		{
			return result;
		}
		
		RequestCommonLogDataJSON logData = common.getLogData();
		
		result = falsification(logData.getClientIp(), "common.logData.clientIp");
		if (result.isStatus() == false) 
		{
			return result;
		}	
		
		result = falsification(logData.getDevInfo(), "common.logData.devInfo");
		if (result.isStatus() == false) 
		{
			return result;
		}
		
		result = falsification(logData.getOsInfo(), "common.logData.osInfo");
		if (result.isStatus() == false) 
		{
			return result;
		}
		
		result = falsification(logData.getSvcName(), "common.logData.svcName");
		if (result.isStatus() == false) 
		{
			return result;
		}
		
		return result;
	}
	
	public static CheckResultData checkCust(String value, String reason) 
	{
		CheckResultData result = new CheckResultData();
		
		//null check
		if (StringUtils.isBlank(value) == true) 
		{			
			result.setStatus(false);
			result.setReason(reason);
			return result;
		}
		
		//blank check
		if(WhiteSpace.matcher(value).find() == true) 
		{				
			result.setStatus(false);
			return result;
		}
		result.setStatus(true);			
		return result;		
	}
	public static CheckResultData CheckCustParam(RequestCustJSON reqJson) 
	{
		CheckResultData result = new CheckResultData();
		Map<String, Object> map = reqJson.getParam();
		for(String key : map.keySet()) 
		{
			//logger.debug("key : ({})", key);
			//logger.debug("value : ({})", map.get(key));				
			result = checkFalsificationValue((String)map.get(key), "param."+key);
			if(result.isStatus() == false) 
			{
				return result;
			}
		}
		result.setStatus(true);
		return result;
	}
	public static CheckResultData checkFalsificationValue(String value, String reason) 
	{
		CheckResultData result = new CheckResultData();
		
		if(falsification.matcher(value).find() == true) 
		{
			result.setStatus(false);
			result.setReason(reason);
			return result;
		}
		result.setStatus(true);
		return result;
	}
	public static CheckResultData falsification(String value, String reason) 
	{
		CheckResultData result = new CheckResultData();
		
		if (StringUtils.isBlank(value)) 
		{			
			result.setStatus(false);
			result.setReason(reason);
			return result;
		}
		
		//blank check
		if(WhiteSpace.matcher(value).find() == true) 
		{				
			result.setStatus(false);
			result.setReason(reason);
			return result;
		}
		
		if(falsification.matcher(value).find() == true) 
		{
			result.setStatus(false);
			result.setReason(reason);
			return result;
		}
		
		result.setStatus(true);
		return result;
	}
	
	public static CheckResultData checkInt(String value, String reason) 
	{
		CheckResultData result = new CheckResultData();
		if (StringUtils.isBlank(value)) 
		{			
			result.setStatus(false);
			result.setReason(reason);
			return result;
		}
		
		if(Number.matcher(value).find() == true) 
		{
			//logger.debug("Number");
			result.setStatus(false);
			result.setReason(reason);
			return result;
		}
		result.setStatus(true);
		return result;
	}
}
