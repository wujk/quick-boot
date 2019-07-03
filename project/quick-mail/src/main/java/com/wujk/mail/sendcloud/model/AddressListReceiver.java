package com.wujk.mail.sendcloud.model;

import com.chinacareer.geek.common.utils.sendcloud.config.Config;
import com.chinacareer.geek.common.utils.sendcloud.exception.ReceiverException;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 邮件列表收件人
 * 
 * @author Sam
 *
 */
public class AddressListReceiver implements Receiver {

	public boolean useAddressList() {
		return true;
	}

	/**
	 * 地址列表
	 */
	private List<String> invokeNames = new ArrayList<String>();

	public List<String> getInvokeNames() {
		return invokeNames;
	}

	/**
	 * 增加地址列表的调用名称
	 * 
	 * @param to
	 */
	public void addTo(String to) {
		invokeNames.addAll(Arrays.asList(to.split(";")));
	}

	public boolean validate() throws ReceiverException {
		if (CollectionUtils.isEmpty(invokeNames))
			throw new ReceiverException("地址列表为空");
		if (invokeNames.size() > Config.MAX_MAILLIST)
			throw new ReceiverException("地址列表超过上限");
		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String address : invokeNames) {
			if (sb.length() > 0)
				sb.append(";");
			sb.append(address);
		}
		return sb.toString();
	}
}