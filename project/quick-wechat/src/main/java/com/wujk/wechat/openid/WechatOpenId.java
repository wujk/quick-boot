package com.wujk.wechat.openid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wujk.utils.pojo.ObjectUtil;
import com.wujk.wechat.inteface.DataWechatListener;

/**
 * 获取openid
 * @author CI11951
 *
 */
public class WechatOpenId {
	
	private static final Logger logger = LoggerFactory.getLogger(WechatOpenId.class);
	
	private String appId;
	
	private String appsecret;
	
	private String accessToken;
	
	private String openId;
	
	private String code;
	
	private DataWechatListener listener;

	public String getAppId() {
		return (appId == null && listener != null) ? listener.getAppid() : appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppsecret() {
		return (appsecret == null && listener != null) ? listener.getAppsecret() : appsecret;
	}

	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getOpenId() {
		return (openId == null && listener != null) ? listener.getOpenId() : openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public DataWechatListener getListener() {
		return listener;
	}

	public void setListener(DataWechatListener listener) {
		this.listener = listener;
	}
	
	public String openid() {
		String openId = getOpenId();
		boolean noExit = ObjectUtil.isEmpty(openId);
		if (noExit) {
			code = listener.initCode(); // 转发腾讯获取code
			if (ObjectUtil.isEmpty(code)) {
				return null;
			} else {
				openId = listener.getAccessToken(code);  // 通过code获取openid
			}
		}
		logger.info("exits openid： {}， openid： {}", noExit, openId);
		return openId;
	}
	
}
