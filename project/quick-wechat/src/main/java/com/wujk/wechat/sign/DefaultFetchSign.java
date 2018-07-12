package com.wujk.wechat.sign;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wujk.utils.http.HttpRequestor;
import com.wujk.utils.http.URLUtil;
import com.wujk.utils.json.JsonMapper;
import com.wujk.utils.pojo.ObjectUtil;
import com.wujk.wechat.constant.Constants;
import com.wujk.wechat.inteface.AbstractDataWechat;

public abstract class DefaultFetchSign extends AbstractDataWechat {

	private static final Logger logger = LoggerFactory.getLogger(DefaultFetchSign.class);
	
	public DefaultFetchSign() {
		super(null, null);
	}

	/**
	 * 获取access_token,区别于获取openid时的token
	 * @return
	 */
	public String getNorAccessToken() {
		String accessToken = getLocalNorAccessToken();
		if (ObjectUtil.isEmpty(accessToken)) {
			accessToken = parseWx(URLUtil.url(Constants.accessToken, getLocalAppid(), getLocalAppsecret()), "access_token");
		}
		return accessToken;
	}

	/**
	 * 调用js时需要的ticket
	 * @return
	 */
	public String getJsTicket() {
		String jsTicket = getLocalNorAccessToken();
		if (ObjectUtil.isEmpty(jsTicket)) {
			jsTicket = parseWx(URLUtil.url(Constants.jsTicket, getNorAccessToken()), "ticket");
		}
		return jsTicket;
	}
	
	private String parseWx(String url, String key) {
		String result = "{}";
		String value;
		try {
			result = new HttpRequestor().doGet(url);
			logger.info("result='{}'", result);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		 JsonMapper jsonMapper = new JsonMapper();
		 Map<String, Object> resultMap = jsonMapper.fromJson(result, jsonMapper.contructMapType(Map.class, String.class, String.class));
		 value = (String) resultMap.get(key);
		 logger.info("key={}, value={}", key, result);
		 Integer expiresIn = (Integer) resultMap.get("expires_in");
		 if ("".equals(key)) {
			 setLocalJsTicket(value, expiresIn);
		 } else if ("".equals(key)) {
			 setLocalNorAccessToken(value, expiresIn);
		 }
		 return value;
	}

	public abstract String getUrl();
	
	/**
	 * 从本地项目中获取access_token，调用有限制需要保存
	 * @return
	 */
	public abstract String getLocalNorAccessToken();
	
	/**
	 * 保存access_token
	 * @param value  access_token
	 * @param expiresIn 超时时间
	 * @return
	 */
	public abstract String setLocalNorAccessToken(String value, Integer expiresIn);
	
	
	/**
	 * 从本地项目中获取ticket，调用有限制需要保存
	 * @return
	 */
	public abstract String getLocalJsTicket();
	
	/**
	 * 保存ticket
	 * @param value  ticket
	 * @param expiresIn 超时时间
	 * @return
	 */
	public abstract String setLocalJsTicket(String value, Integer expiresIn);

}
