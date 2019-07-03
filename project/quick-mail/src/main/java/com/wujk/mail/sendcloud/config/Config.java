package com.wujk.mail.sendcloud.config;

public class Config {

	public static final String CHARSET = "utf-8";
	
	public static String server = "http://www.sendcloud.net";
	// 普通邮件发送
	public static String send_api = "http://api.sendcloud.net/apiv2/mail/send";
	// 地址列表发送
	public static String send_template_api = "http://api.sendcloud.net/apiv2/mail/sendtemplate";
	//邮件发送数据统计(每天)
	public static String send_mail_count_day = "http://api.sendcloud.net/apiv2/statday/list";
	//邮件发送数据统计(每小时)
	public static String send_mail_count_hour = "http://api.sendcloud.net/apiv2/stathour/list";
	//无效邮件统计
	public static String send_mail_count_invalid = "http://api.sendcloud.net/apiv2/invalidstat/list";
	// 邮件user
	public static String api_user = "geetool_trigger";
	// 邮件key
	public static String api_key = "lreljckwMzkzdms2";
	
	public static String from = "market@geetemp.com";
	// 最大收件人数
	public static final int MAX_RECEIVERS = 100;
	// 最大地址列表数
	public static final int MAX_MAILLIST = 5;
	// 邮件内容大小
	public static final int MAX_CONTENT_SIZE = 1024 * 1024;

	public static String getSend_api() {
		return send_api;
	}
	
}
