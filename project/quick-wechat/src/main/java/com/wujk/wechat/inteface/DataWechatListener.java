package com.wujk.wechat.inteface;

public interface DataWechatListener {
	
	/**
	 * 获取项目中的appid
	 * @return
	 */
	public String getAppid();
	
	/**
	 * 获取项目中的appsecret
	 * @return
	 */
	public String getAppsecret();
	
	/**
	 * 获取腾讯转发的code
	 * @return
	 */
	public String initCode();
	
	/**
	 * 获取项目中的openid
	 * @return
	 */
	public String getOpenId();
	
	/**
	 * 通过腾讯的code通过ACCESS_TOKEN获取openid
	 * @param code
	 * @return
	 */
	public String getAccessToken(String code);
	
	/**
	 * 转发的url
	 * @return
	 */
	public String getRedictUrl();
	
	public String getScope();

}
