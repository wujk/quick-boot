package com.wujk.mail.sendcloud.builder;


import com.wujk.mail.sendcloud.config.Config;
import com.wujk.mail.sendcloud.core.SendCloud;
import com.wujk.mail.sendcloud.model.MailAddressReceiver;
import com.wujk.mail.sendcloud.model.MailBody;
import com.wujk.mail.sendcloud.model.SendCloudMail;
import com.wujk.mail.sendcloud.model.TextContent;
import com.wujk.mail.sendcloud.util.ResponseData;

public class SendCloudBuilder {

	public static SendCloud build() {
		SendCloud sc = new SendCloud();
		sc.setServer(Config.server);
		sc.setMailAPI(Config.send_api);
		sc.setTemplateAPI(Config.send_template_api);
		sc.setMailCountDayAPI(Config.send_mail_count_day);
		sc.setMailCountHourAPI(Config.send_mail_count_hour);
		sc.setInvalidMailCountAPI(Config.send_mail_count_invalid);
		return sc;
	}
	
	public static void main(String[] args) throws Throwable {
		SendCloudMail mail = new SendCloudMail();
		MailBody body = new MailBody();
		body.setFrom(Config.from);
		body.setSubject("HRSAAS测试邮件");
		body.setFromName("HRSAAS");
		mail.setBody(body);
		
		TextContent content = new TextContent();
		content.setContent_type(TextContent.ScContentType.plain);
		content.setText("测试邮件内容");
		mail.setContent(content);
		
		MailAddressReceiver receiver = new MailAddressReceiver();
		receiver.addTo("qin.zhu@autoliv.com");
		receiver.addCc("1021815383@qq.com");
		mail.setTo(receiver);
		ResponseData responseData = SendCloudBuilder.build().sendMail(mail);
		System.out.println(responseData.toString());
	}
}