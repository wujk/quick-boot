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
	
	private DefaultFetchOpendId listener;

	public DataWechatListener getListener() {
		return listener;
	}

	public void setListener(DataWechatListener listener) {
		this.listener = (DefaultFetchOpendId) listener;
	}
	
	public String openid() {
		String openId = listener.getLocalOpenId();
		boolean noExit = ObjectUtil.isEmpty(openId);
		if (noExit) {
			String code = listener.initCode(); // 转发腾讯获取code
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
