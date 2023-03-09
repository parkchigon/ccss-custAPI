package com.lgu.ccss.common.util;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.lgu.ccss.api.cust.model.RequestCustJSON;
import com.lgu.ccss.common.constant.HeaderConst;
import com.lgu.ccss.common.constant.MDCConst;
import com.lgu.ccss.common.model.DeviceSessVO;
import com.lgu.ccss.common.model.RequestJSON;
import com.lgu.ccss.common.tlo.TloData;

import devonframe.dataaccess.CommonDao;

@Component
public class CCSSUtil {
	private static Logger logger = LoggerFactory.getLogger(CCSSUtil.class);

	@Autowired
	private CommonDao commonDao_altibase;
	private static CommonDao _altibaseDao;

	@Autowired
	private CommonDao commonDao_oracle;
	private static CommonDao _oracleDao;

	@PostConstruct
	public void init() {
		CCSSUtil._altibaseDao = commonDao_altibase;
		CCSSUtil._oracleDao = commonDao_oracle;
	}
	
	public static void setCommonDataCustAPI(HttpServletRequest request, String requestUrl, RequestCustJSON resCusJSON) 
	{
		if(resCusJSON == null) 
		{				
			return;
		} 
		else
		{
			String appType = (String)resCusJSON.getParam().get("svcType");			
			String ctn = (String) resCusJSON.getParam().get(RequestJSON.PARAM_CAR_CTN);							
			String loginId = (String) resCusJSON.getCommon().getLoginId();
			String osInfo = (String) resCusJSON.getCommon().getLogData().getOsInfo();
			String devInfo = (String) resCusJSON.getCommon().getLogData().getDevInfo();
			String svcName = (String) resCusJSON.getCommon().getLogData().getSvcName();
			String carOem = (String) resCusJSON.getCommon().getCarOem();				
			String transactionId = request.getHeader(HeaderConst.HD_NAME_TRANSACTION_ID);
			String clientIp = (String) resCusJSON.getCommon().getLogData().getClientIp();
			logger.debug("clientIp : {}", clientIp);
			DeviceSessVO deviceSess = new DeviceSessVO();
			deviceSess.setDeviceCtn(ctn);		
			deviceSess = _altibaseDao.select("DeviceSession.selectDeviceSessionForCtn", deviceSess);			
			
			MDC.put(MDCConst.COMMON_CTN, ctn);
			MDC.put(MDCConst.COMMON_LOGIN_ID, loginId);
			MDC.put(MDCConst.LOG_DATA_OS_INFO, osInfo);
			MDC.put(MDCConst.LOG_DATA_DEV_INFO, devInfo);
			MDC.put(MDCConst.LOG_DATA_SVC_NAME, svcName);
			MDC.put(MDCConst.COMMON_CAR_OEM, carOem);
			MDC.put(MDCConst.COMMON_MGR_SESSION_ID, transactionId);
			MDC.put(MDCConst.LOG_DATA_CLIENT_IP, clientIp);
			MDC.put(MDCConst.CUST_REQ_TIME, TloData.getNowDate());			
			if (deviceSess != null) 
			{	
				logger.debug("deviceSess : {}", deviceSess);
				MDC.put(MDCConst.COMMON_MEMB_ID, deviceSess.getMembId());
				MDC.put(MDCConst.COMMON_MEMB_NO, deviceSess.getMembNo());
				MDC.put(MDCConst.COMMON_SESS_EXP_DT, deviceSess.getDeviceSessExpDt());
				MDC.put(MDCConst.COMMON_SERIAL, deviceSess.getDeviceSerial());			
				
			} 			
			else
			{
				logger.debug("memberData is null");
			}
			logger.debug(
					"ctn({}) loginId({}) osInfo({}) devInfo({}) svcName({}) carOem({}) transactionId({}) clientIp({}) membId({}) membNo({}) deviceSessExt({}) deviceSerial({})",
					MDC.get(MDCConst.COMMON_CTN), MDC.get(MDCConst.COMMON_LOGIN_ID),
					MDC.get(MDCConst.LOG_DATA_OS_INFO), MDC.get(MDCConst.LOG_DATA_DEV_INFO),
					MDC.get(MDCConst.LOG_DATA_SVC_NAME), MDC.get(MDCConst.COMMON_CAR_OEM),	
					MDC.get(MDCConst.COMMON_MGR_SESSION_ID), MDC.get(MDCConst.LOG_DATA_CLIENT_IP),
					MDC.get(MDCConst.COMMON_MEMB_ID), MDC.get(MDCConst.COMMON_MEMB_NO),
					MDC.get(MDCConst.COMMON_SESS_EXP_DT), MDC.get(MDCConst.COMMON_SERIAL)
					);
			
			return;		
		}
	}
	
	public static void setMembInfo(String ctn)
	{
		logger.debug("ctn : {}", ctn);
		DeviceSessVO deviceSess = new DeviceSessVO();
		deviceSess.setDeviceCtn(ctn);
		
		deviceSess = _altibaseDao.select("DeviceSession.selectDeviceSessionForCtn", deviceSess);
		logger.debug("deviceSess : {}", deviceSess);
		if (deviceSess != null) 
		{					
			MDC.put(MDCConst.COMMON_MEMB_ID, deviceSess.getMembId());
			MDC.put(MDCConst.COMMON_MEMB_NO, deviceSess.getMembNo());
			MDC.put(MDCConst.COMMON_SESS_EXP_DT, deviceSess.getDeviceSessExpDt());
			MDC.put(MDCConst.COMMON_SERIAL, deviceSess.getDeviceSerial());				
		}		
		else 
		{
			logger.debug("memberData is null");			
		}
		logger.debug(
				"membId({}) membNo({}) deviceSessExt({}) deviceSerial({})",				
				MDC.get(MDCConst.COMMON_MEMB_ID), MDC.get(MDCConst.COMMON_MEMB_NO),
				MDC.get(MDCConst.COMMON_SESS_EXP_DT), MDC.get(MDCConst.COMMON_SERIAL)
				);
		return;
	}
	
	public static String getCtn() {
		return MDC.get(MDCConst.COMMON_CTN);
	}

	public static String getSerial() {
		return MDC.get(MDCConst.COMMON_SERIAL);
	}

	public static String getCcssToken() {
		return MDC.get(MDCConst.COMMON_CCSS_TOKEN);
	}

	public static String getMembId() {
		return MDC.get(MDCConst.COMMON_MEMB_ID);
	}
	
	public static String getLoginId() {
		return MDC.get(MDCConst.COMMON_LOGIN_ID);
	}

	public static String getMembNo() {
		return MDC.get(MDCConst.COMMON_MEMB_NO);
	}

	public static String getSessExpDt() {
		return MDC.get(MDCConst.COMMON_SESS_EXP_DT);
	}

	public static String getMgrSessionId() {
		return MDC.get(MDCConst.COMMON_MGR_SESSION_ID);
	}

	/*public static String getMgrUserCtn() {
		return MDC.get(MDCConst.COMMON_MGR_USER_ID);
	}*/
	
	public static String getMgrUserLoginId() {
		return MDC.get(MDCConst.COMMON_MGR_USER_ID);
	}

	public static String getMgrSessExpDt() {
		return MDC.get(MDCConst.COMMON_MGR_SESS_EXP_DT);
	}

	public static String getMgrRandomKey() {
		return MDC.get(MDCConst.COMMON_MGR_SESS_RANDOM_KEY);
	}
	public static String getUuid() {
		return MDC.get(MDCConst.COMMON_UUID);
	}

	public static String getMgrappId() {
		return MDC.get(MDCConst.COMMON_MGRAPP_ID);
	}
	
	public static String getOsType() {
		return MDC.get(MDCConst.COMMON_OS_TYPE);
	}
	
	public static String getDevInfo() {
		return MDC.get(MDCConst.LOG_DATA_DEV_INFO);
	}
	
	public static String getOsInfo() {
		return MDC.get(MDCConst.LOG_DATA_OS_INFO);
	}
	
	public static String getSvcName() {
		return MDC.get(MDCConst.LOG_DATA_SVC_NAME);
	}
	public static String getCarOem() {
		return MDC.get(MDCConst.COMMON_CAR_OEM);
	}
	public static String getDeviceType() {
		return MDC.get(MDCConst.LOG_DATA_DEVICE_TYPE);
	}
	public static String getDeviceModel() {
		return MDC.get(MDCConst.LOG_DATA_DEVICE_MODEL);
	}
	public static String getCustSvcClass() {
		return MDC.get(MDCConst.CUST_SVC_CLASS);
	}
	public static String getCustResultCode() {
		return MDC.get(MDCConst.CUST_RESULT_CODE);
	}
	public static String getCustReqTime() {
		return MDC.get(MDCConst.CUST_REQ_TIME);
	}
	public static String getCustResTime() {
		return MDC.get(MDCConst.CUST_RES_TIME);
	}
	
	public static Object getBean(String beanName)
	{
		// 현재 요청중인 thread local의 HttpServletRequest 객체 가져오기
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

		// HttpSession 객체 가져오기
		HttpSession session = request.getSession();

		// ServletContext 객체 가져오기
		ServletContext conext = session.getServletContext();

		// Spring Context 가져오기
		WebApplicationContext wContext = WebApplicationContextUtils.getWebApplicationContext(conext);

		// 스프링 빈 가져오기 & casting
		return wContext.getBean(beanName);
	}
}
