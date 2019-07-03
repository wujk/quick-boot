package com.wujk.mail.sendcloud.model;

import com.chinacareer.geek.common.utils.sendcloud.exception.ReceiverException;

public interface Receiver {
	public boolean useAddressList();
	
	public boolean validate() throws ReceiverException;
	
	public String toString();
}