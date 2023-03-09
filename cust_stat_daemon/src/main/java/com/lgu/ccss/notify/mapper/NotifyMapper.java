package com.lgu.ccss.notify.mapper;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lgu.ccss.notify.model.NotifyVO;
import com.lgu.ccss.notify.model.SmsVO;
import com.lgu.ccss.notify.model.TargetVO;

@Component
public class NotifyMapper {
	private final Logger logger = LoggerFactory.getLogger(NotifyMapper.class);
	
	@Autowired
	NotifyMapperOracle notifyMapperOracle;
	@Autowired
	NotifyMapperAltibase notifyMapperAltibase;
	
	public ArrayList selectNotifyStat(NotifyVO notifyVo){
		ArrayList notifyStat = new ArrayList<>();
		notifyStat = notifyMapperOracle.selectNotifyStat(notifyVo);
		return notifyStat;
	}
	
	public int insertSmsSendQueue(SmsVO smsVo) {
		int result;
		result = notifyMapperAltibase.insertSmsSendQueue(smsVo);
		
		return result;
	}
	
//	public int insertSmsSendQueue(List<SmsVO> smsVo) {
//		int result;
//		logger.debug("Insert Sms Send Notify Queue!");
//		result = notifyMapperAltibase.insertSmsSendQueue(smsVo);
//		
//		return result;
//	}

	public List selectTargetList() {
		// TODO Auto-generated method stub
		List targetList =  new ArrayList<>();
		targetList = notifyMapperOracle.selectTargetList();
		return targetList;
	}
}
