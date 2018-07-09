package com.wujk.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;


public class SAXmlUtil {
	
	/**
	 * 解析xml
	 * @param file
	 * @return
	 */
	public static Map<String, Object> parseXML(File file) {
		SAXParserFactory saxfac = SAXParserFactory.newInstance();
		InputStream is = null;
		final Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
        	if (!file.exists()) {
        		return resultMap;
        	}
        	is = new FileInputStream(file);
            SAXParser saxparser = saxfac.newSAXParser();
            
            saxparser.parse(is, new DefaultHandler(){
            	private String value;

				@SuppressWarnings("unchecked")
				@Override
				public void endElement(String uri, String localName, String qName) {
					Map<String, Object> map = new HashMap<String, Object>();
					if (value.length() > 0) {
						Object obj = resultMap.get(qName);
						if (obj == null) {
							resultMap.put(qName, value);
						} else {
							List<Map<String, Object>> result = null;
							if (List.class.isAssignableFrom(obj.getClass())) {
								result = (List<Map<String, Object>>)obj;
								map.put(qName, value);
								result.add(map);
							} else if (String.class.isAssignableFrom(obj.getClass())) {
								result = new ArrayList<Map<String, Object>>();
								map.put(qName, obj);
								result.add(map);
								map.put(qName, value);
								result.add(map);
								resultMap.put(qName, result);
							} else if (Map.class.isAssignableFrom(obj.getClass())) {
								result = new ArrayList<Map<String, Object>>();
								result.add((Map<String, Object>) obj);
								map.put(qName, value);	
								result.add(map);
								resultMap.put(qName, result);
							}
						}
					}
						
				}

				@Override
				public void characters(char[] ch, int start, int length) {
					value = new String(ch, start, length).trim();	
				}
				
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try{
        		if (is != null)
            		is.close();
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	
        }
        return resultMap;
	}
	
	/**
	 * sax解析
	 * @param path
	 * @return
	 */
	public static Map<String, Object> parseXML(String path) {
		return parseXML(new File(path));
	}
	
}
