package com.lgu.ccss.dbschd.scheduler;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;



@Service
public class AltibaseDBWorkerScheduler {
	private final Logger logger = LoggerFactory.getLogger(AltibaseDBWorkerScheduler.class);
	
	/*
	@Autowired
	private AltiDatabase altiDatabase;

	//@Scheduled(cron = "${alti.cron.partition.time}")
	public void processPartition() {
		logger.info("### [ALTIBASE] CCSS SERVICE PROCESS PARTITION TASK DAEMON START ...");
		long startTime = System.currentTimeMillis();
		try {
			altiDatabase.processPartition();
			logger.info("### [ALTIBASE] CCSS SERVICE CREATE PROCESS TASK DAEMON END {}ms", (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	*/
	
	
	
	
}
