package com.wujk.wechat.sign;

import com.wujk.wechat.inteface.DataWechatListener;
import com.wujk.wechat.utils.SignUtil;

public class WechatSign {
	
	private DefaultFetchSign listener;

	public DataWechatListener getListener() {
		return listener;
	}

	public void setListener(DataWechatListener listener) {
		this.listener = (DefaultFetchSign) listener;
	}
	
	public String sign() {
		SignUtil.sign(listener.getJsTicket(), listener.getUrl());
		return null;
	}

}
