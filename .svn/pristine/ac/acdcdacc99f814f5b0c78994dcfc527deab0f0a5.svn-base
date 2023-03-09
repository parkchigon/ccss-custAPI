package com.lgu.ccss.api.cust.util;


import java.io.IOException;
import java.util.*;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lgu.ccss.api.cust.model.RequestCustJSON;
import com.lgu.ccss.common.constant.CCSSConst;
import com.lgu.ccss.common.model.ResponseJSON;
import com.lgu.ccss.common.util.ResultCodeUtil;
import com.lgu.common.util.AES256LogUtil;
import com.lgu.common.util.AES256Util;

@Component
public class RestClientUtil 
{	
	private static final Logger logger = LoggerFactory.getLogger(RestClientUtil.class);
		
	@Value("#{config['custClient.firstHostName']}")
	private String firstHostName;
	
	@Value("#{config['custClient.secondHostName']}")
	private String secondHostName;
	
	@Value("#{config['custClient.port']}")
	private int port;
	
	@Value("#{config['custClient.method']}")
	private String method;
	
	@Value("#{config['custClient.eindex']}")
	private String index;
	
	@Value("#{config['custClient.etype']}")
	private String type;

	@Value("#{config['custClient.query.category0']}")
	private String category;
			
	@Value("#{config['custClient.query.defSort']}")
	private String defSort;
	
	@Autowired
	private ResConvertUtil resUtil;
	
	@Autowired
	private ReqConvertUtil reqUtil;
		
	public ResponseJSON doClient(RequestCustJSON reqCusJSON) throws IOException 
	{		
		ResponseJSON resJSON;
		Object resOBJ = null;
		Map<String, Object> map = new HashMap<>();
		String apiId = reqCusJSON.getCommon().getApiId();
		String endPoint = "/"+index+"/"+type+"/_search?pretty=true";
		RestClient restClient = checkReq();
		try
		{				
			Request reqData = new Request(method, endPoint);
			
			reqData = (Request)addParamToReq(reqCusJSON, reqData);				
			
			//logger.debug("reqData : {}", reqData.getParameters());
			
			Response resData = restClient.performRequest(reqData);
			
			//logger.debug("request line : {}", resData.getRequestLine());
			
			if(apiId.equals(CCSSConst.API_ID_CUST_LOG_SERVICE_DETAIL)) 
			{				
				resOBJ = resUtil.convertResForDetail(reqCusJSON, resData);					
				//logger.debug("resOBJ : {}", resOBJ);
			} 
			else 
			{
				resOBJ = resUtil.convertResForList(reqCusJSON, resData);						
				//logger.debug("resOBJ : {}", resOBJ);
			}
			logger.debug("Response Data : ({})", resOBJ);
			logger.debug("resultCode : {}", resData.getStatusLine());
			restClient.close();					 
			resJSON = ResultCodeUtil.createResultMsg(ResultCodeUtil.RC_2S000000, resOBJ, reqCusJSON.getCommon().getApiId());					
			return resJSON;
		}
		catch (ResponseException re)
		{
			logger.debug("resultCode : {}", re.getResponse().getStatusLine());
			resJSON = ResultCodeUtil.createResultMsg(ResultCodeUtil.RC_5E000001, reqCusJSON.getCommon().getApiId());
			return resJSON;
		}
		catch (NullPointerException ne)
		{			
			resJSON = ResultCodeUtil.createResultMsg(ResultCodeUtil.RC_4C005001, reqCusJSON.getCommon().getApiId());
			return resJSON;
		}
		catch (Exception e) 
		{
			resJSON = ResultCodeUtil.createResultMsg(ResultCodeUtil.RC_4C005000, reqCusJSON.getCommon().getApiId());
			e.printStackTrace();
			return resJSON;
		}
	}
	
	/*
	 * elastic search에 입력할 쿼리에 param을 추가하는 메소드
	 */
	public Request addParamToReq(RequestCustJSON reqCusJSON, Request reqData) throws Exception 
	{
		String apiId = reqCusJSON.getCommon().getApiId();
		Map<String, Object> reqCusJsonMap = reqCusJSON.getParam();
		LinkedMap convertMap = (LinkedMap) reqUtil.requestConvert(reqCusJsonMap);
		if(apiId.equals(CCSSConst.API_ID_CUST_LOG_SERVICE_DETAIL)) 
		{			
			String _id = (String) convertMap.get("_id");			
			reqData.addParameter("q", "_id:"+_id);			
			return reqData;
		} 
		else
		{				
			String from = (String) convertMap.get("from");
			String size = (String) convertMap.get("size");
			String deviceType = (String) convertMap.get("deviceType");
			String appType = (String) convertMap.get("appType");
			String ctn = (String) convertMap.get("ctn");
			String userCtn = (String) convertMap.get("userCtn");
			String gte = (String) convertMap.get("gte");
			String lte = (String) convertMap.get("lte");
			String query = "";			
			String sort = (String)reqCusJsonMap.get(RequestCustJSON.PARAM_SORT);
			String cate0 = category;
			String ignoreList = "size, from, gte, lte, sort";
			logger.debug("reqConvertData | from : ({}) size : ({}) deviceType : ({}) appType : ({}) ctn : ({}) userCtn : ({}) gte : ({}) lte : ({}) sort : ({}) category0 : ({})" ,
					from, size, deviceType, appType, ctn, userCtn, gte, lte, sort, cate0
					);
			if(CCSSConst.SVC_TYPE_ALL.equalsIgnoreCase(appType) /*|| CCSSConst.SVC_TYPE_ETC.equalsIgnoreCase(appType)*/ || CCSSConst.SVC_TYPE_NULL.equalsIgnoreCase(appType))
			{
				ignoreList += ", appType";
			}
			
			if(CCSSConst.TYPE_MANAGER_APP.equalsIgnoreCase(appType)) 
			{
				ignoreList += ", ctn";
			}
			else
			{
				ignoreList += ", userCtn";
			}
			
			for(Object key : convertMap.keySet())
			{
				if(ignoreList.indexOf((String)key) == -1 && StringUtils.isNotBlank((String)convertMap.get(key)))
				{
					query = setQuery(query, (String)key, (String)convertMap.get(key));
				}				
			}
			
			if(StringUtils.isBlank(sort))
			{
				sort = "requestTime:"+ defSort;
			}
			
			if(StringUtils.isNotBlank(category) && CCSSConst.TYPE_MANAGER_APP.equalsIgnoreCase(appType))
			{
				query += " AND category0:"+ category;
			}		
			query += " AND requestTime: ["+gte+" TO "+lte+"]";
			reqData.addParameter("q", query);				
			reqData.addParameter("sort", sort);	
			reqData.addParameter("from", from);
			reqData.addParameter("size", size);		
			logger.debug("RequestData : ({})", reqData);			
			return reqData;	
		}		
	}
	
	public String setQuery(String query, String keyName, String value)
	{
		if(StringUtils.isBlank(query)) 
		{
			query += keyName + ":" + value;
		}
		else
		{
			query += " AND " + keyName + ":" + value;
		}
		return query;
	}
	
	public RestClient checkReq()
	{	
		RestClient restClient = null;
		LinkedMap map = new LinkedMap();
		map.put("firstHostName", firstHostName);
		map.put("secondHostName", secondHostName);
		for(Object key : map.keySet())
		{	
			Map<String, Object> resultMap = restClient((String)map.get(key));
			if((boolean) resultMap.get("result"))
			{
				restClient = (RestClient) resultMap.get("restClient");
			}				
		}
		return restClient;		
	}
	
	public Map<String, Object> restClient(String hostName) 
	{
		String checkHealth = "/_cat/health?v";
		Map<String, Object> map = new HashMap<>();
		
		try
		{
			RestClientBuilder builder = RestClient.builder(
					new HttpHost(hostName, port, "http")).setRequestConfigCallback(
							new RequestConfigCallback() {
								@Override
								public Builder customizeRequestConfig(Builder requestConfigBuilder) {
									return requestConfigBuilder
											.setConnectTimeout(1000);
								}
							});			
			
			Header[] defaultHeaders = new Header[]
					{
							new BasicHeader("Content-Type", "application/json")
					};			
			builder.setDefaultHeaders(defaultHeaders);	
			
			Request reqData = new Request(method, checkHealth);	
			
			RestClient restClient = builder.build();
			
			Response resData = restClient.performRequest(reqData);
			if(resData.getStatusLine().getStatusCode() == 200)
			{
				map.put("result", true);
				map.put("restClient", restClient);
			}
			return map;
		} catch (Exception e) {
			map.put("result", false);
			return map;
		}
	}	
}
