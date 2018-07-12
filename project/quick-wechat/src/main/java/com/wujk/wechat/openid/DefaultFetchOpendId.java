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
import com.wujk.wechat.inteface.DataWechatListener;

public abstract class DefaultFetchOpendId implements DataWechatListener {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultFetchOpendId.class);

	private HttpServletRequest request;
	private HttpServletResponse response;

	public DefaultFetchOpendId(HttpServletRequest request, HttpServletResponse response) {
		super();
		this.request = request;
		this.response = response;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public String initCode() {
		if (request == null) 
			return null;
		String code = request.getParameter("code");
		logger.info("code='{}'", code);
		if (ObjectUtil.isEmpty(code)) {
			try {
				response.sendRedirect(URLUtil.url(Constants.CODE_URL, getAppid(), getAppid(), getRedictUrl(), getScope()));
				return null;
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return code;
	}

	@Override
	public String getAccessToken(String code) {
		String result = "{}";
		try {
			result = new HttpRequestor().doGet(URLUtil.url(Constants.ACCESS_TOKEN, getAppid(), getAppsecret(), code));
			logger.info("result：'{}'", result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 JsonMapper jsonMapper = new JsonMapper();
		 Map<String, String> resultMap = jsonMapper.fromJson(result, jsonMapper.contructMapType(Map.class, String.class, String.class));
		 return resultMap.get("openid");
	}

	@Override
	public String getScope() {
		return "snsapi_base";
	}

}
