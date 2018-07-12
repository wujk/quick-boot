package com.wujk.wechat.openid;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wujk.utils.http.HttpRequestor;
import com.wujk.utils.http.URLUtil;
import com.wujk.utils.json.JsonMapper;
import com.wujk.utils.pojo.ObjectUtil;
import com.wujk.wechat.constant.Constants;
import com.wujk.wechat.inteface.AbstractDataWechat;

public abstract class DefaultFetchOpendId extends AbstractDataWechat  {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultFetchOpendId.class);
	
	public DefaultFetchOpendId(HttpServletRequest request, HttpServletResponse response) {
		super(request, response);
	}
	
	/**
	 * 获取腾讯转发的code
	 * @return
	 */
	public String initCode() {
		if (getRequest() == null) 
			return null;
		String code = getRequest().getParameter("code");
		logger.info("code='{}'", code);
		if (ObjectUtil.isEmpty(code)) {
			try {
				getResponse().sendRedirect(URLUtil.url(Constants.CODE_URL, getLocalAppid(), getRedictUrl(), getScope()));
				return null;
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return code;
	}

	/**
	 * 通过腾讯的code通过ACCESS_TOKEN获取openid
	 * @param code
	 * @return
	 */
	public String getAccessToken(String code) {
		String result = "{}";
		try {
			result = new HttpRequestor().doGet(URLUtil.url(Constants.ACCESS_TOKEN, getLocalAppid(), getLocalAppsecret(), code));
			logger.info("result：'{}'", result);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		 JsonMapper jsonMapper = new JsonMapper();
		 Map<String, String> resultMap = jsonMapper.fromJson(result, jsonMapper.contructMapType(Map.class, String.class, String.class));
		 return resultMap.get("openid");
	}
	
	/**
	 * 转发的url
	 * @return
	 */
	public abstract String getRedictUrl();

	public String getScope() {
		return "snsapi_base";
	}

}
