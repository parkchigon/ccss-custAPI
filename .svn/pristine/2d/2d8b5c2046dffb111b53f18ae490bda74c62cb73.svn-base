package com.lgu.ccss.statistics.mapper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lgu.ccss.statistics.model.StatVO;

@Component
public class StatMapper {
	private final Logger logger = LoggerFactory.getLogger(StatMapper.class);
	
	@Autowired
	StatMapperOracle statMapperOracle;
	
	public int insertStatistics(List<StatVO> statList) {
		return statMapperOracle.insertStatHistory(statList);
	}
}
