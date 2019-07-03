package com.wujk.mail.sendcloud.model;

import com.wujk.mail.sendcloud.exception.ReceiverException;

public interface Receiver {
	public boolean useAddressList();
	
	public boolean validate() throws ReceiverException, ReceiverException;
	
	public String toString();
}