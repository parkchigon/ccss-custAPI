package com.lgu.ccss.common.log;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lgu.ccss.common.constant.CCSSConst;

@Component
public class LogManager {
	
	
	@Autowired
	CustLogServiceImpl custLogService;
	
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public LogService getLogService(String key) {
		if (map.size() == 0) {
			initLogService();
		}
				
		return (LogService) map.get(key);
	}
	
	private void initLogService() {
		map.put(CCSSConst.PREFIX_CUST, custLogService);		
	}
}
