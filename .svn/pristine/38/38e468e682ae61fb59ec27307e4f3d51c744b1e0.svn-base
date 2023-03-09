package com.lgu.ccss.api.cust.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.lgu.ccss.api.cust.model.CustResultDetailJSON;
import com.lgu.ccss.api.cust.model.RequestCustJSON;
import com.lgu.ccss.common.constant.CCSSConst;
import com.lgu.ccss.common.tlo.TloData;
import com.lgu.ccss.common.tlo.TloUtil;
import com.lgu.ccss.common.util.CCSSUtil;
import com.lgu.common.util.AES256CustUtil;
import com.lgu.common.util.AES256LogUtil;
import com.lgu.common.util.AES256Util;

@Component
public class ResConvertUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ResConvertUtil.class);
	
	@Value("${spring.profiles.active}")
	private String profiles;
	
	@Value("#{config['custClient.enc_key']}")
	private String encKey;
	
	@Value("#{config['custClient.dec_key']}")
	private String decKey;
	
	public Object convertResForList(RequestCustJSON reqCusJSON, Response response) throws Exception 
	{	
		List<Map<String, Object>> resultList;
		LinkedMap resultDataMap;
		LinkedMap logMap;		
		Gson gson = new Gson();		
		JSONParser jsonParser = new JSONParser();
				
		String entityRes = EntityUtils.toString(response.getEntity());
		
		if(StringUtils.isNotBlank(entityRes) == false) 
		{	
			resultList = new ArrayList<>();
			return resultList;
		}
		
		int pageNo = Integer.parseInt((String) reqCusJSON.getParam().get("pageNo"));
		int pageCnt = Integer.parseInt((String)  reqCusJSON.getParam().get("pageCnt"));		
		
		JSONObject entityResObj = (JSONObject) jsonParser.parse(entityRes);		
		JSONObject hitsObj = (JSONObject) entityResObj.get("hits");			
		ArrayList hitsArray = (ArrayList)hitsObj.get("hits");
			
		int total = (int) (long) hitsObj.get("total");
		int rowNum = calculationRowNum(pageNo, pageCnt, total);
		
		resultList = new ArrayList<>();
			
		if(hitsArray.size() == 0) 
		{	
			return resultDataMap = new LinkedMap();
		} 
		else 
		{
			logger.debug("ctn : {}", (String)reqCusJSON.getParam().get(RequestCustJSON.PARAM_CAR_CTN));
			CCSSUtil.setMembInfo((String)reqCusJSON.getParam().get(RequestCustJSON.PARAM_CAR_CTN));
			logger.debug("dec key length : ({}) enc key length : ({})", decKey.length(), encKey.length());
			for(Object key : hitsArray) 
			{
				Map<String, Object> linkedHashMap = new LinkedHashMap<String,Object>();
				linkedHashMap = gson.fromJson(key.toString(), linkedHashMap.getClass());
				Map<String, Object> _sourceMap = (Map<String, Object>) linkedHashMap.get("_source");
				String appType = (String) _sourceMap.get("appType");
				String category0 = Integer.toString((int) (double)_sourceMap.get("category0")); 
				resultDataMap = new LinkedMap();
				logMap = new LinkedMap();
				String posX = (String) setPosData(_sourceMap).get("posX");
				String posY = (String) setPosData(_sourceMap).get("posY");
				resultDataMap.put("reqCarCtn", (String) _sourceMap.get("ctn"));
				resultDataMap.put("reqDeviceType", (String) _sourceMap.get("deviceType"));
				
				if(checkParamForNotNull((String) _sourceMap.get("userCtn")) == true) 
				{
					resultDataMap.put("reqCusCtn", (String) _sourceMap.get("userCtn"));						
				}
				
				resultDataMap.put("reqPageNo", pageNo);
				resultDataMap.put("reqPageCnt", pageCnt);
				String reqSvcType = convertSvcTypeToNumber((String) _sourceMap.get("appType"));
				/*if(reqSvcType.length() > 0 && !reqSvcType.equals("000"))
				{	
					resultDataMap.put("reqSvcType", reqSvcType);
				}*/
				if(reqSvcType.length() > 0)
				{	
					resultDataMap.put("reqSvcType", reqSvcType);
				}
				resultDataMap.put("reqSvcTypeName", convertSvcTypeToNameForKorean(appType));
				resultDataMap.put("totalCnt", total);
				
				logMap.put("rowNum", rowNum--);
				logMap.put("seq", (String) linkedHashMap.get("_id"));
				logMap.put("svcReqDatetime", (String) _sourceMap.get("requestTime"));
				
				if(checkParamForNotNull((String) _sourceMap.get("appVer")) == true) 
				{				
					logMap.put("appVer", (String) _sourceMap.get("appVer"));
				}
				if(StringUtils.isNotEmpty((String)_sourceMap.get("saVer")))	
				{
					logMap.put("saVer", (String) _sourceMap.get("saVer"));
				}
				logMap.put("carOem", (String) _sourceMap.get("carOem"));
				if(StringUtils.isNotBlank((String)_sourceMap.get("devModel")))
				{
					logMap.put("devModel", (String) _sourceMap.get("devModel"));
				}
				//logger.debug("useType : {}", (String) _sourceMap.get("useType"));
				if(checkParamForNotNull(convertParamToAbbreviation((String) _sourceMap.get("useType"))) == true) 
				{
					logMap.put("useType", convertParamToAbbreviation((String) _sourceMap.get("useType")));
				}
				if(checkParamForNotNull(convertParamToAbbreviation((String) _sourceMap.get("result"))) == true) 
				{				
					logMap.put("svcReqResult", convertParamToAbbreviation((String) _sourceMap.get("result")));
				}
				
				logMap.put("posX", posX);
				logMap.put("posY", posY);
				//logger.debug("svcReqInfo : ({})", (String)_sourceMap.get("svcReqInfo"));
				if(StringUtils.isNotBlank((String)_sourceMap.get("svcReqInfo")));
				{
					//logMap.put("loginStatus", convertParamToAbbreviation((String)_sourceMap.get("svcReqInfo")));
				}
				resultDataMap.put("log", logMap);
				logger.debug("resultDataMap : {}",resultDataMap);
				resultList.add(resultDataMap);
			}
			return resultList;
		} 
	}	
	
	public Object convertResForDetail(RequestCustJSON reqCusJSON, Response response) throws Exception 
	{	
		CustResultDetailJSON resJSON = new CustResultDetailJSON();
		Gson gson = new Gson();		
		String apiId = reqCusJSON.getCommon().getApiId();
		
		try
		{		
			if(response.getStatusLine().getStatusCode() == 200) 
			{
				String entityRes = EntityUtils.toString(response.getEntity());
				
				JSONParser jsonParser = new JSONParser();
				JSONObject entityResObj = (JSONObject) jsonParser.parse(entityRes);
								
				JSONObject hitsObj = (JSONObject) entityResObj.get("hits");
				ArrayList hitsArray = (ArrayList)hitsObj.get("hits");			
				
				if(apiId.equals(CCSSConst.API_ID_CUST_LOG_SERVICE_DETAIL)) 
				{
					Map<String, Object> resMap;
					for(Object key : hitsArray) 
					{
						Map<String, Object> maps = new LinkedHashMap<String,Object>();
						resMap = new HashMap<String, Object>();
						maps = gson.fromJson(key.toString(), maps.getClass());
						
						String seq = (String) maps.get("_id");
						Map<String, Object> sourceMap = (Map<String, Object>) maps.get("_source");
						String appType = (String)sourceMap.get("appType");
						String userCtn = (String)sourceMap.get("userCtn");
						String ctn = (String)sourceMap.get("ctn");
						int category0 = (int) (double)sourceMap.get("category0");
						logger.debug("ctn : ({})", ctn);
						logger.debug("userCtn : ({})", userCtn);
						
						setTloMap(sourceMap);
						
						if(!ctn.isEmpty()) 
						{
							CCSSUtil.setMembInfo(ctn);
						}
						List<String> addList = setPropertyList(appType);
						String detailInfo = "";											
						for(String value : addList) 
						{
							if(sourceMap.get(value.trim()) != null) 
							{								
								detailInfo += "<div>"+value.trim()+" : "+ sourceMap.get(value.trim())+"</div>";								
							}
						}
						logger.debug("detailInfo : {}", detailInfo);
						if(checkParamForNotNull(seq))
						{
							resJSON.setSeq(seq);	
						}
						else
						{
							resJSON.setSeq((String)reqCusJSON.getParam().get("seq"));
						}						
						String svcDetailInfo = detailInfo;
						resJSON.setSvcDetailInfo(svcDetailInfo);	
						logger.debug("resJSON : {}", resJSON);
						return resJSON;
					}
				} 
				else 
				{	
					return resJSON;			
				}	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return resJSON;	
	}
	
	public static int calculationRowNum(int from, int size, int total) 
	{		
		return (total - (size*(from-1)));
	}
	
	public Map<String, Object> setPosData(Map<String, Object> map)
	{
		Map<String, Object> resultMap = new HashMap<>();
		String posX = null;
		String posY = null;
		int posNum = 0;		
		for(Map.Entry<String, Object> entryKey : map.entrySet())
		{
			if(entryKey.getKey().indexOf("pos") > -1)
			{
				posNum++;
			}			
		}
		if(posNum == 1) 
		{
			String[] splitPos = ((String)map.get("pos")).split(",");
			posX = splitPos[0];
			posY = splitPos[1];
		}
		else
		{
			posX = (String)map.get("posX");
			posY = (String)map.get("posY");			
		}
		try {			
			if(posX.length() == 24 && posY.length() == 24)
			{
				posX = AES256Util.AESDecode(decKey, posX);
				posY = AES256Util.AESDecode(decKey, posY);	
			}			
			String encodePosX = AES256CustUtil.AESEncode(encKey, posX);
			String encodePosY = AES256CustUtil.AESEncode(encKey, posY);
			resultMap.put("posX", encodePosX);
			resultMap.put("posY", encodePosY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	public static boolean checkParamForNotNull(String value) 
	{
		boolean result = true;

		if(StringUtils.isBlank(value)) 
		{
			return false;
		}
		return result; 
	}
	
	public String convertSvcTypeToNameForEnglish(String value) 
	{
		String returnType;
		
		if(StringUtils.isNotBlank(value) == false) 
		{
			value = "ALL";
		}
		switch (value) 
		{
		case "001": returnType = CCSSConst.SVC_TYPE_001;			
		break;
		case "002": returnType = CCSSConst.SVC_TYPE_002;		
		break;
		case "003": returnType = CCSSConst.SVC_TYPE_003;		
		break;
		case "004": returnType = CCSSConst.SVC_TYPE_004;		
		break;
		case "005": returnType = CCSSConst.SVC_TYPE_005;		
		break;
		case "006": returnType = CCSSConst.SVC_TYPE_006;		
		break;
		case "007": returnType = CCSSConst.SVC_TYPE_007;		
		break;
		case "008": returnType = CCSSConst.SVC_TYPE_008;		
		break;
		case "009": returnType = CCSSConst.SVC_TYPE_009;		
		break;
		case "010": returnType = CCSSConst.SVC_TYPE_010;		
		break;
		case "011": returnType = CCSSConst.SVC_TYPE_011;		
		break;
		case "012": returnType = CCSSConst.SVC_TYPE_012;		
		break;
		case "013": returnType = CCSSConst.SVC_TYPE_013;		
		break;
		case "014": returnType = CCSSConst.SVC_TYPE_014;		
		break;
		default : returnType = CCSSConst.SVC_TYPE_ALL;	
		}
		
		return returnType;
	}
	
	public static String convertSvcTypeToNameForKorean(String value) 
	{
		String returnName = null;	
		
		switch (value) 
		{
		case CCSSConst.SVC_TYPE_001: returnName = CCSSConst.SVC_TYPE_NAME_001; 		
		break;
		case CCSSConst.SVC_TYPE_002: returnName = CCSSConst.SVC_TYPE_NAME_002;		
		break;
		case CCSSConst.SVC_TYPE_003: returnName = CCSSConst.SVC_TYPE_NAME_003;		
		break;
		case CCSSConst.SVC_TYPE_004: returnName = CCSSConst.SVC_TYPE_NAME_004;		
		break;
		case CCSSConst.SVC_TYPE_005: returnName = CCSSConst.SVC_TYPE_NAME_005;		
		break;
		case CCSSConst.SVC_TYPE_006: returnName = CCSSConst.SVC_TYPE_NAME_006;		
		break;
		case CCSSConst.SVC_TYPE_007: returnName = CCSSConst.SVC_TYPE_NAME_007;		
		break;
		case CCSSConst.SVC_TYPE_008: returnName = CCSSConst.SVC_TYPE_NAME_008;		
		break;
		case CCSSConst.SVC_TYPE_009: returnName = CCSSConst.SVC_TYPE_NAME_009;		
		break;
		case CCSSConst.SVC_TYPE_010: returnName = CCSSConst.SVC_TYPE_NAME_010;		
		break;
		case CCSSConst.SVC_TYPE_011: returnName = CCSSConst.SVC_TYPE_NAME_011;		
		break;
		case CCSSConst.SVC_TYPE_012: returnName = CCSSConst.SVC_TYPE_NAME_012;		
		break;
		case CCSSConst.SVC_TYPE_013: returnName = CCSSConst.SVC_TYPE_NAME_013;		
		break;
		case CCSSConst.SVC_TYPE_014: returnName = CCSSConst.SVC_TYPE_NAME_014;		
		break;
		default : returnName = CCSSConst.SVC_TYPE_ALL;			
		}
		return returnName;
	}
	
	public static String convertSvcTypeToNumber(String value) 
	{
		String returnType = null;
		
		switch (value) 
		{
		case CCSSConst.SVC_TYPE_001: returnType = "001";			
		break;
		case CCSSConst.SVC_TYPE_002: returnType = "002";		
		break;
		case CCSSConst.SVC_TYPE_003: returnType = "003";		
		break;
		case CCSSConst.SVC_TYPE_004: returnType = "004";		
		break;
		case CCSSConst.SVC_TYPE_005: returnType = "005";		
		break;
		case CCSSConst.SVC_TYPE_006: returnType = "006";		
		break;
		case CCSSConst.SVC_TYPE_007: returnType = "007";		
		break;
		case CCSSConst.SVC_TYPE_008: returnType = "008";		
		break;
		case CCSSConst.SVC_TYPE_009: returnType = "009";		
		break;
		case CCSSConst.SVC_TYPE_010: returnType = "010";		
		break;
		case CCSSConst.SVC_TYPE_011: returnType = "011";		
		break;
		case CCSSConst.SVC_TYPE_012: returnType = "012";		
		break;
		case CCSSConst.SVC_TYPE_013: returnType = "013";		
		break;
		case CCSSConst.SVC_TYPE_014: returnType = "014";		
		break;
		default : returnType = "000";
		}		
		return returnType;
	}
	
	public static String convertParamToAbbreviation(String value) 
	{
		String result = null;
		if(StringUtils.isBlank(value)) 
		{
			return result;
		}
		switch (value) 
		{
		case "success": result = "S";			
			return result;
		case "fail": result = "F";			
			return result;
		case "voice": result = "1";			
			return result;
		case "touch": result = "2";			
			return result;
		case "login": result = "Y";			
			return result;
		case "logout": result = "N";			
			return result;
		}
		return result;
	}
	
	public List setPropertyList(String value) 
	{
		Resource resource;
	    Properties props;
	    List<String> addList = new ArrayList<String>();
	    try 
	    {	    	
	    	//logger.debug("profiles : ({})",profiles);
			resource = new ClassPathResource("/config/"+ profiles +"/config.properties");
			logger.debug("resource : {}", resource);
			props = PropertiesLoaderUtils.loadProperties(resource);
			String appMandatoryColumnList = props.getProperty("custClient.mandatory."+value.toUpperCase());
			logger.debug("value : {}", value.toUpperCase());
			logger.debug("appMandatoryColumnList : {}", appMandatoryColumnList);
			if(StringUtils.isNotBlank(appMandatoryColumnList)) 
			{
				//addList = new ArrayList(Arrays.asList(appMandatoryColumnList.split(",")));
				return new ArrayList(Arrays.asList(appMandatoryColumnList.split(",")));				
			}			
			return addList;	
	    } 
	    catch (Exception e) 
	    {
			e.printStackTrace();
	    	return addList;
		}
	}
	
	public static void setTloMap(Map<String, Object> map) 
	{
		Map<String, String> tloMap = new HashMap<String, String>();
		String deviceType = (String)map.get("deviceType");
		String deviceModel = (String)map.get("devModel");
		logger.debug("deviceType : {} | deviceModel : {}",deviceType,deviceModel);
		tloMap.put(TloData.DEVICE_TYPE, deviceType);
		tloMap.put(TloData.DEV_MODEL, deviceModel);
		logger.debug("tloMap : {}",tloMap);
		TloUtil.setTloData(tloMap);
	}
}
