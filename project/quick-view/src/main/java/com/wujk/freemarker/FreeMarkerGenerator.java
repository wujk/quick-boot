package com.wujk.freemarker;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wujk.utils.pojo.ObjectUtil;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * freemarker生成
 * @author CI11951
 *
 */
public class FreeMarkerGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(FreeMarkerGenerator.class);
	
	private String key = "template";
	
	private static final String UTF_8 = "UTF-8";
	
	private Object model;
	
	private String filePath;
	
	private String text;
	
	private File file;
	
	private InputStream inputStream;
	
	private Map<String, Object> mutiData = new HashMap<>();
	
	private Map<String, Object> mutiModel = new HashMap<>();
	
	public FreeMarkerGenerator(String filePath, Object model) {
		super();
		this.model = model;
		this.filePath = filePath;
		getTemplete(filePath);
	}
	
	public FreeMarkerGenerator(File file, Object model) {
		super();
		this.model = model;
		this.file = file;
		getTemplete(file);
	}
	
	public FreeMarkerGenerator(InputStream inputStream, Object model) {
		super();
		this.model = model;
		this.inputStream = inputStream;
		getTemplete(inputStream);
	}

	public FreeMarkerGenerator(Map<String, Object> mutiData, Map<String, Object> mutiModel) {
		super();
		this.mutiData = mutiData;
		this.mutiModel = mutiModel;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
		getTemplete(filePath);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		getTemplete(file);
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		getTemplete(inputStream);
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Map<String, Object> getMutiData() {
		return mutiData;
	}

	public void setMutiData(Map<String, Object> mutiData) {
		this.mutiData = mutiData;
	}
	
	public Map<String, Object> getMutiModel() {
		return mutiModel;
	}

	public void setMutiModel(Map<String, Object> mutiModel) {
		this.mutiModel = mutiModel;
	}

	public Map<String, String> makeHtmls() {
		Map<String, String> result = null;
		if (ObjectUtil.isEmpty(mutiData)) {
			return result;
		}
		result = new HashMap<>();
		Set<Entry<String, Object>> entries = mutiData.entrySet();
		for (Entry<String, Object> entry : entries) {
			key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof String) {
				getTemplete((String)value); 
			} else if (value instanceof File) {
				getTemplete((File)value); 
			} else if (value instanceof InputStream) {
				getTemplete((InputStream)value); 
			}
			result.put(key, makeHtml());
		}
		return result;
	}

	public String makeHtml() {
		String result = null;
		try {
			if (ObjectUtil.isEmpty(text)) 
				return result;
			String templete = text;
			Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
			configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_28));
			configuration.setDefaultEncoding(UTF_8); // 这个一定要设置，不然在生成的页面中 会乱码
			StringTemplateLoader stringLoader = new StringTemplateLoader();
			stringLoader.putTemplate(key, templete);
			configuration.setTemplateLoader(stringLoader);
			Template template = configuration.getTemplate(key, UTF_8);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024 * 5);
			Writer writer = new OutputStreamWriter(outputStream, UTF_8);
			template.process(model, writer);
			result = new String(outputStream.toByteArray());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	private void getTemplete(String filePath) {
		try {
			getTemplete(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void getTemplete(InputStream inputStream) {
		BufferedReader br = null;
		String result = "";
		try {
			InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
			br = new BufferedReader(isr);
			String str = null;
			while ((str = br.readLine()) != null) {
				result += str;
			}
			text = result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	private void getTemplete(File file) {
		try {
			getTemplete(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
