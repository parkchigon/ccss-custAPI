package com.lgu.ccss.common.tlo;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lgu.ccss.api.cust.model.ElasticSearchErrorCode;
import com.lgu.ccss.common.model.ResponseJSON;

@Aspect
public class TloAop {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Pointcut("execution(* com.lgu.ccss.api.cust.service.CustListServiceImpl.doService(..))")
	private void pointCutElsticSearchGetList() {
		
	}
	@Around("pointCutElsticSearchGetList()")
	public Object pointCutElsticSearchGetList(ProceedingJoinPoint joinPoint) throws Throwable {
		Map<String, String> tlo = new HashMap<String, String>();
		tlo.put(TloData.CUST_SVC_CLASS, TloConst.EC01);
		tlo.put(TloData.CUST_REQ_TIME, TloData.getNowDate());
		
		Object obj = null;
		try {
			obj = joinPoint.proceed();
			return obj;
		} finally {
			
			if(obj == null) {
				tlo.put(TloData.CUST_RESULT_CODE,ElasticSearchErrorCode.RC_60000000.getCode());
			}
			else {
				ResponseJSON result = (ResponseJSON) obj;
				tlo.put(TloData.CUST_RESULT_CODE, result.getResultCode());
				logger.debug("pointCutElsticSearchGetList : {}", obj);
			}
			tlo.put(TloData.CUST_RES_TIME, TloData.getNowDate());
			TloUtil.setTloData(tlo);
		}
	}
	
	@Pointcut("execution(* com.lgu.ccss.api.cust.service.CustDetailServiceImpl.doService(..))")
	private void pointCutElsticSearchGetDetail() {
		
	}
	@Around("pointCutElsticSearchGetDetail()")
	public Object pointCutElsticSearchGetDetail(ProceedingJoinPoint joinPoint) throws Throwable {
		Map<String, String> tlo = new HashMap<String, String>();
		tlo.put(TloData.CUST_SVC_CLASS, TloConst.EC02);
		tlo.put(TloData.CUST_REQ_TIME, TloData.getNowDate());
		
		Object obj = null;
		try {
			obj = joinPoint.proceed();
			return obj;
		} finally {
			
			if(obj == null) {
				tlo.put(TloData.CUST_RESULT_CODE,ElasticSearchErrorCode.RC_60000000.getCode());
			}
			else {
				ResponseJSON result = (ResponseJSON) obj;
				tlo.put(TloData.CUST_RESULT_CODE, result.getResultCode());
				logger.debug("pointCutElsticSearchGetDetail : {}", obj);
			}
			tlo.put(TloData.CUST_RES_TIME, TloData.getNowDate());
			TloUtil.setTloData(tlo);
		}
	}
}
