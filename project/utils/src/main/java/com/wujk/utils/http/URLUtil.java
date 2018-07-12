package com.wujk.utils.http;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wujk.utils.pojo.ObjectUtil;

public class URLUtil {

	public static final String PROTOCOL = "PROTOCOL";
	public static final String HOST = "HOST";
	public static final String PORT = "PORT";
	public static final String PATH = "PATH";
	public static final String QUERY = "QUERY";

	private static final Logger logger = LoggerFactory.getLogger(URLUtil.class);

	public static String url(String url, Object... obj) {
		String dest = String.format(url, obj);
		logger.info("完整url：{}", dest);
		return dest;
	}

	public static Map<String, String> parseUrl(String url) {
		Map<String, String> map = new HashMap<>();
		try {
			URL Url = new URL(url);
			map.put(PROTOCOL, Url.getProtocol());
			map.put(HOST, Url.getHost());
			map.put(PORT, String.valueOf(-1 == Url.getPort() ? Url.getDefaultPort() : Url.getPort()));
			map.put(PATH, Url.getPath());
			map.put(QUERY, Url.getQuery());
			logger.info(PROTOCOL + "： {}," + HOST + "： {}," + PORT + "： {}," + PATH + "： {}," + QUERY + "： {}",
					map.get(PROTOCOL), map.get(HOST), map.get(PORT), map.get(PATH), map.get(QUERY));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return map;
	}

	public static String getUrlProtocol(String url) {
		return parseUrl(url).get(PROTOCOL);
	}

	public static String getUrlHost(String url) {
		return parseUrl(url).get(HOST);
	}

	public static String getUrlPort(String url) {
		return parseUrl(url).get(PORT);
	}

	public static String getUrlPath(String url) {
		return parseUrl(url).get(PATH);
	}

	public static String getUrlQuery(String url) {
		return parseUrl(url).get(QUERY);
	}

	public static Map<String, String> parseUrlQuery(String url) {
		Map<String, String> map = new HashMap<>();
		String query = parseUrl(url).get(QUERY);
		if (!ObjectUtil.isEmpty(query)) {
			int index = 0;
			while ((index = query.indexOf("&")) != -1) {
				String param = query.substring(0, index);
				if (index + 1 > query.length()) {
					break;
				}
				query = query.substring(index + 1);
				if (!ObjectUtil.isEmpty(param)) {
					String[] strs = param.split("=");
					if (strs.length == 2) {
						map.put(strs[0], strs[1]);
					}
				}
			}
			if (!ObjectUtil.isEmpty(query)) {
				String[] strs = query.split("=");
				if (strs.length == 2) {
					map.put(strs[0], strs[1]);
				}
			}
		}
		return map;
	}

}
