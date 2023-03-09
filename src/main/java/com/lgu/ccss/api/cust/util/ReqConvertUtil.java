package com.lgu.ccss.api.cust.util;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReqConvertUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ReqConvertUtil.class);
		
	@Value("#{config['custClient.reqConvert.convertParamName'].split(',')}")
	private List<String> convertList;
	
	@Autowired
	private ResConvertUtil resUtil;
			
	
	/*
	 *  고객센터 검색에 값을 더해 줄 경우, config의 reqConvert.convertParamName에 추가 하려는 값을 더해준다. [,고객센터검색이름:elasticSearch검색이름]으로 추가해 준다.
	 * 
	 */
	public LinkedMap requestConvert(Map<String, Object> map)
	{
		LinkedMap resMap = new LinkedMap();
		//int paramNum = map.size();
		logger.debug("convertList | ({})",convertList);
		for(String reqKey : map.keySet()) {
			for(String listKey : convertList)
			{
				if(listKey.indexOf(reqKey) > -1)
				{
					String[] convertName = listKey.split(":");
					resMap.put(convertName[1].trim(), map.get(reqKey));
				}
			}
		}
		logger.debug("resMap | ({})",resMap);
		return convertValue(resMap);
	}
		
	public LinkedMap convertValue(Map<String, Object> map) 
	{		
		String resString = null;
		LinkedMap resMap = new LinkedMap();
		try {
			for(String key : map.keySet()) 
			{
				if(StringUtils.isNotBlank((String)map.get(key))) {
					
					if(key.equalsIgnoreCase("from")) 
					{
						resString = calculationForFrom((String)map.get(key), (String)map.get("size"));
						resMap.put(key, URLEncoder.encode(resString, "UTF-8"));
					
					}
					else if(key.equalsIgnoreCase("appType"))
					{	
						resString = resUtil.convertSvcTypeToNameForEnglish((String)map.get(key));
						//logger.debug("resString | ({})",resString);
						resMap.put(key, URLEncoder.encode(resString, "UTF-8"));						
					}					
					else if(key.equalsIgnoreCase("gte") || key.equalsIgnoreCase("lte"))
					{	
						resString = convertFormatForLength((String)map.get(key));
						resMap.put(key, URLEncoder.encode(resString, "UTF-8"));				
					}					
					else
					{
						resString = (String)map.get(key);
						resMap.put(key, URLEncoder.encode(resString, "UTF-8"));
					}
					
				}
				else
				{
					if(key.equalsIgnoreCase("from")) 
					{	
						resMap.put(key, URLEncoder.encode("0", "UTF-8"));
					}
					if(key.equalsIgnoreCase("size")) 
					{						
						resMap.put(key, URLEncoder.encode("10", "UTF-8"));
					}
				}
				
			}			
			//logger.debug("resMap | ({})",resMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resMap;
	}
	
	public String calculationForFrom(String from, String size) 
	{
		String result = null;
		try 
		{
			int currentPage = Integer.parseInt(from.trim());
			int pageCount = Integer.parseInt(size.trim());
			currentPage = (currentPage-1)*pageCount;
			result = Integer.toString(currentPage);
			//logger.debug(result);
			return result;
			
		} 
		catch (Exception e) 
		{			
			int currentPage = 1;
			int pageCount = 10;
			currentPage = (currentPage-1)*pageCount;
			result = Integer.toString(currentPage);
			//logger.debug(result);
			return result;
		}
	}
	
	public String convertFormatForLength(String value) 
	{
		while(value.length() < 14) 
		{
			value = value+"0";
		}
		//logger.debug(value);
		return value;
	}
}
