package com.wujk.mail.sendcloud.model;

import com.wujk.mail.sendcloud.exception.ContentException;

public interface Content {
	public boolean useTemplate();

	public boolean validate() throws ContentException;
}