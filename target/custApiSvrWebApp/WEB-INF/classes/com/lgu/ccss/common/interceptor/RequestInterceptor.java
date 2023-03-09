package com.lgu.ccss.common.interceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.lgu.ccss.common.constant.CCSSConst;
import com.lgu.ccss.common.constant.HeaderConst;
import com.lgu.ccss.common.constant.MDCConst;
import com.lgu.ccss.common.log.LogManager;
import com.lgu.ccss.common.log.LogService;
import com.lgu.ccss.common.tlo.TloData;
import com.lgu.ccss.common.tlo.TloUtil;
import com.lgu.ccss.common.util.ResultCodeUtil;
import com.lgu.common.model.ResultCode;
import com.lgu.common.tlo.TloWriter;
import com.lgu.common.util.DateUtils;
import com.lgu.common.util.StringUtils;

import devonframe.util.RandomUtil;

/**
 * 컨트롤러의 전반적인 전후 처리를 위한 인터셉터 클래스. <br/>
 * 시스템 점검, 요청 정보 등을 다룬다.
 * 
 */
public class RequestInterceptor extends HandlerInterceptorAdapter implements MessageSourceAware {

	private static final Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);

	@Value("#{systemProperties.SERVER_ID}")
	private String serverId;

	@Value("#{config['session.timeoutMin']}")
	private String sessionTimeoutMin;

	@Value("#{config['mgrapp.session.timeoutMin']}")
	private String mgrappSessionTimeoutMin;

	@Autowired
	private TloWriter tloWriter;
	
	@Autowired
	private LogManager logManager;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.debug("requestURL:" + request.getRequestURI());		
		
		try {
			String requestUrl = StringUtils.nvl(request.getRequestURI());
			
			if (requestUrl.startsWith("/api/internal")) {
				return true;
			}			
			
			String prefix = urlToPrefix(requestUrl);
			
			LogService logService = (LogService) logManager.getLogService(prefix);
			if (logService == null) {
				logger.error("logService is null. prefix({}) url({})", prefix, requestUrl);
			} else {
				logService.setRequestLog(request);
			}
	
			if(prefix.equals(CCSSConst.PREFIX_CUST)) {				
				if(request.getMethod().equals("POST")) {								
					boolean status = checkCustAPI(request, response);
					if(status == false) {
						return false;						
					}					
				} else if(request.getMethod().equals("GET")) {
					return false;						
				}
			}
		}
		catch (Exception e) {
			logger.error("Exception({})", e);			
		}

		return super.preHandle(request, response, handler);
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mav)
			throws Exception {

		logger.debug("requestURL:" + request.getRequestURI());		
		try {
			String requestUrl = StringUtils.nvl(request.getRequestURI());
			String prefix = urlToPrefix(requestUrl);
			String transactionId = request.getHeader(HeaderConst.HD_NAME_TRANSACTION_ID);
			
			if (requestUrl.startsWith("/api/internal")) {
				return;
			}
						
			if (prefix.equals(CCSSConst.PREFIX_CUST)) {				
				if (transactionId != null && transactionId.length() > 0) {
					response.setHeader(HeaderConst.HD_NAME_TRANSACTION_ID, transactionId);
					logger.debug("response : ({})", response);
				}
			}
			// response.setHeader("Connection", "close");
			
			LogService logService = (LogService) logManager.getLogService(prefix);
			if (logService == null) {
				logger.error("logService is null. prefix({}) url({})", prefix, requestUrl);
			} else {
				logService.setResponseLog(request, response);
			}
		}
		catch (Exception e) {
			logger.error("Exception({})", e);
			
		} finally {
			MDC.clear();
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

	public String getTrId(HttpServletRequest httpServletRequest) {

		HttpSession session = httpServletRequest.getSession(true);
		String trId = "TR_" + DateUtils.getMillisecondsTime() + RandomUtil.getRandomStr('A', 'Z')
				+ RandomUtil.getRandomNum(1, 10000);
		session.setAttribute("TID", trId);
		return trId;
	}

	
	private boolean checkCustAPI(HttpServletRequest request, HttpServletResponse response) {
		String requestUrl = StringUtils.nvl(request.getRequestURI());
		String transactionId = StringUtils.nvl(request.getHeader(HeaderConst.HD_NAME_TRANSACTION_ID));		
		String UserAgent = StringUtils.nvl(request.getHeader("User-Agent"));
		//HttpSession session = request.getSession(true);
		if (logger.isDebugEnabled()) {
			logger.debug("checkSession IN. transactionId : ({}) | requestUrl : ({}) | UserAgent : ({}) ", transactionId, requestUrl, UserAgent);	
		}
		
		try {
			if (transactionId == null || transactionId.length() == 0 || transactionId.isEmpty()) {
				logger.error("abnormal transactionId value. transactionId({})", transactionId);				
				custFailMsg(request, response, ResultCodeUtil.RC_3C004000);
				return false;
			}
			
			if (UserAgent == null || UserAgent.length() == 0 || UserAgent.isEmpty()) {				
				logger.error("abnormal UserAgent value. User-Agent({})", UserAgent);
				custFailMsg(request, response, ResultCodeUtil.RC_3C004000);
				return false;
			}
			return true;			
		} catch (Exception e) {
			logger.error("requestUrl({}) Exception({})", requestUrl, e);
			return false;
		}
	}
	
	

	public boolean comparedSessionExpire(String sessExpDt) {

		Date nowDt = new Date();
		Date expDt = DateUtils.stringToDate(sessExpDt);

		if (DateUtils.comparedExpDate(nowDt, expDt) < 0) {
			return true;
		}
		return false;
	}
	
	public void custFailMsg(HttpServletRequest req, HttpServletResponse res, ResultCode resultCode) throws IOException {
		res.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		
		res.setHeader(HTTP.CONTENT_TYPE, HeaderConst.HD_VALUE_CONTENTTYPE_JSON);
		res.setHeader("transactionId", HeaderConst.HD_NAME_TRANSACTION_ID);
		
		String transactionId = req.getHeader(HeaderConst.HD_NAME_TRANSACTION_ID);
		if(transactionId != null && transactionId.length() > 0) {
			res.setHeader(HeaderConst.HD_NAME_TRANSACTION_ID, transactionId);
		}
		
		String userAgent = req.getHeader(HeaderConst.HD_NAME_USER_AGENT);
		if(userAgent != null && userAgent.length() > 0) {
			res.setHeader(HeaderConst.HD_NAME_USER_AGENT, userAgent);
		}
		
		res.getWriter().write(ResultCodeUtil.createResultMsgToJsonString(resultCode, MDC.get(MDCConst.API_ID)));
		
		Map<String, String> tlo = new HashMap<String, String>();
		tlo.put(TloData.RSP_TIME, TloData.getNowDate());
		tlo.put(TloData.RESULT_CODE, resultCode.getCode());

		TloUtil.setTloData(tlo);
		tloWriter.write(TloUtil.getTloData());
	}
		
	@Override
	public void setMessageSource(MessageSource messageSource) {
		// TODO Auto-generated method stub

	}
	
	private String urlToPrefix(String url) {		
		if (url.startsWith(CCSSConst.PREFIX_CUST)) {
			return CCSSConst.PREFIX_CUST;
		}
		
		return null;
	}
}