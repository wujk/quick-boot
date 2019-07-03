package com.wujk.mail.sendcloud.model;

import com.chinacareer.geek.common.utils.sendcloud.exception.ContentException;

public interface Content {
	public boolean useTemplate();

	public boolean validate() throws ContentException;
}