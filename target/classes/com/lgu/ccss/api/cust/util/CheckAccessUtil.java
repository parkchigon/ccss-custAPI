package com.lgu.ccss.api.cust.util;

import java.io.IOException;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.lgu.ccss.api.cust.model.RequestCustJSON;
import com.lgu.ccss.common.constant.HeaderConst;
import com.lgu.ccss.common.constant.MDCConst;
import com.lgu.ccss.common.model.RequestCommonJSON;
import com.lgu.ccss.common.model.RequestCommonLogDataJSON;
import com.lgu.ccss.common.validation.CheckResultData;
import com.lgu.ccss.common.validation.ValidationChecker;

@Component
public class CheckAccessUtil {

	private static final Logger logger = LoggerFactory.getLogger(CheckAccessUtil.class);

	// config value
	@Value("${spring.profiles.active}")
	private String profiles;

	public CheckResultData doCheck(HttpHeaders headers, RequestCustJSON reqCusJSON, HttpServletRequest request) {

		List<String> IPList = null;
		List<String> IDList = null;
		String ipBlockYN = "";
		String idBlockYN = "";
		
		String path = "config/" + profiles + "/config.properties";
		Properties properties = new Properties();
		

		try {
			 Reader reader = Resources.getResourceAsReader(path);
			 properties.load(reader);
			 
			IPList = Arrays.asList(properties.getProperty("custClient.access_ip_list").split(","));
			IDList = Arrays.asList(properties.getProperty("custClient.access_id_list").split(","));
			
			ipBlockYN = properties.getProperty("custClient.ipBlockYN");
			idBlockYN = properties.getProperty("custClient.idBlockYN");
					
				
		} catch (IOException e) {
			e.printStackTrace();
		}

		CheckResultData resultData = new CheckResultData();
		
		//String accessIp = MDC.get(MDCConst.REMOTE_IP);
		//String accessIP = MDC.get(MDCConst.LOG_DATA_CLIENT_IP);	
				
		String accessIP = request.getHeader("X-Forwarded-For");
		if (accessIP == null || accessIP.length() == 0 || "unknown".equalsIgnoreCase(accessIP)) { 
		    accessIP = request.getHeader("Proxy-Client-accessIP"); 
		} 
		if (accessIP == null || accessIP.length() == 0 || "unknown".equalsIgnoreCase(accessIP)) { 
		    accessIP = request.getHeader("WL-Proxy-Client-accessIP"); 
		} 
		if (accessIP == null || accessIP.length() == 0 || "unknown".equalsIgnoreCase(accessIP)) { 
		    accessIP = request.getHeader("HTTP_CLIENT_accessIP"); 
		} 
		if (accessIP == null || accessIP.length() == 0 || "unknown".equalsIgnoreCase(accessIP)) { 
		    accessIP = request.getHeader("HTTP_X_FORWARDED_FOR"); 
		} 
		if (accessIP == null || accessIP.length() == 0 || "unknown".equalsIgnoreCase(accessIP)) { 
		    accessIP = request.getHeader("X-Real-accessIP"); 
		} 
		if (accessIP == null || accessIP.length() == 0 || "unknown".equalsIgnoreCase(accessIP)) { 
		    accessIP = request.getHeader("X-RealaccessIP"); 
		} 
		if (accessIP == null || accessIP.length() == 0 || "unknown".equalsIgnoreCase(accessIP)) { 
		    accessIP = request.getRemoteAddr(); 
		}

		String accessID = reqCusJSON.getCommon().getLoginId();
		logger.debug("accessIp({}) accessId({}) IP check YN({}) ID check YN({})", accessIP, accessID, ipBlockYN,
				idBlockYN);
		try {
			if (ipBlockYN.equals("N")) {
				logger.debug("IP NOT CHECKED");
			} else {
				for (Object key : IPList) {
					if (StringUtils.isBlank((String) key)) {
						resultData.setStatus(false);
						resultData.setReason("common.logData." + RequestCommonLogDataJSON.LOGDATA_CLIENT_IP);
						logger.debug("ACCESS ERROR : config.properties's IP check List not found");
						return resultData;
					}
				}

				resultData = ValidationChecker.checkCust(accessIP, "remote.ip");
				if (resultData.isStatus() == false) {
					return resultData;
				}

				boolean resultIP = checkAccess(IPList, accessIP);
				if (resultIP == false) {
					resultData.setStatus(false);
					resultData.setReason("common.logData." + RequestCommonLogDataJSON.LOGDATA_CLIENT_IP);
					logger.debug("ACCESS ERROR : ({})", HeaderConst.HD_NAME_UNAUTHORIZATION_IP);
					return resultData;
				}
				// logger.debug("IP CHECK OK | AccessIP : {} PermitIP : {}",accessIP, IPList);
			}

			if (idBlockYN.equals("N")) {
				logger.debug("ID NOT CHECKED");
			} else {
				for (Object key : IDList) {
					if (StringUtils.isBlank((String) key)) {
						resultData.setStatus(false);
						resultData.setReason("common." + RequestCommonJSON.COMMON_LOGIN_ID);
						logger.debug("ACCESS ERROR : Config.properties's ID check List not found");
						return resultData;
					}
				}

				boolean resultID = checkAccess(IDList, accessID);
				if (resultID == false) {
					resultData.setStatus(false);
					resultData.setReason("common." + RequestCommonJSON.COMMON_LOGIN_ID);
					logger.debug("ACCESS ERROR : ({})", HeaderConst.HD_NAME_UNAUTHORIZATION_ID);
					return resultData;
				}
				// logger.debug("ID CHECK OK | AccessID : {} PermitID : {}",accessID, IDList);
			}
			resultData.setStatus(true);
			return resultData;

		} catch (Exception e) {
			resultData.setStatus(false);
			e.printStackTrace();
		}
		resultData.setStatus(true);
		return resultData;

	}

	public boolean checkAccess(List permitList, String value) {
		boolean resultData = true;
		String resultValue = null;
		try {
			for (Object key : permitList) {
				String KEY = key.toString().trim();
				if (KEY.equals(value)) {
					resultValue = value;
				}
			}

			if (!StringUtils.isNotBlank(resultValue)) {
				resultData = false;
				return resultData;
			}
			return resultData;
		} catch (Exception e) {
			e.printStackTrace();
			return resultData = false;
		}
	}
}
