package com.wujk.mail.sendcloud.builder;

import com.chinacareer.geek.common.utils.sendcloud.config.Config;
import com.chinacareer.geek.common.utils.sendcloud.core.SendCloud;
import com.chinacareer.geek.common.utils.sendcloud.model.MailAddressReceiver;
import com.chinacareer.geek.common.utils.sendcloud.model.MailBody;
import com.chinacareer.geek.common.utils.sendcloud.model.SendCloudMail;
import com.chinacareer.geek.common.utils.sendcloud.model.TextContent;
import com.chinacareer.geek.common.utils.sendcloud.model.TextContent.ScContentType;
import com.chinacareer.geek.common.utils.sendcloud.util.ResponseData;

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
		content.setContent_type(ScContentType.plain);
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