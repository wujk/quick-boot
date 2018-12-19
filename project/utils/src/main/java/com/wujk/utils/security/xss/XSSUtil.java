package com.wujk.utils.security.xss;


import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class XSSUtil {
	
	private static final Whitelist DEFAULT_WHITELIST = Whitelist.none();
	
	/**
	 * 判断是否含有html等脚本
	 * @param value
	 * @return
	 */
	public boolean isValid(String value) {
		String unescapeValue = StringEscapeUtils.unescapeJava(value);
		return Jsoup.isValid(unescapeValue, DEFAULT_WHITELIST);
	}

}
