package com.wujk.utils.express;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExpressBean {
	
	private static final String DEFAULT_OPERATE = "default";  // 默认操作符一般 + - * /
	
	private String express;
	
	private String afterReplaceExpress;
	
	private String operate = DEFAULT_OPERATE;
	
	Map<String, Object> valuesMap;
	
	private Map<String, ExpressBean> nextBeans = new HashMap<String, ExpressBean>();
	
	private Object result;
	
	private Class<?> expressClass;

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
	}

	public String getAfterReplaceExpress() {
		return afterReplaceExpress;
	}

	public void setAfterReplaceExpress(String afterReplaceExpress) {
		this.afterReplaceExpress = afterReplaceExpress;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public Map<String, ExpressBean> getNextBeans() {
		return nextBeans;
	}

	public void setNextBeans(Map<String, ExpressBean> nextBeans) {
		this.nextBeans = nextBeans;
	}

	public Map<String, Object> getValuesMap() {
		return valuesMap;
	}

	public void setValuesMap(Map<String, Object> valuesMap) {
		this.valuesMap = valuesMap;
	}

	public Class<?> getExpressClass() {
		if (expressClass != null) {
			return expressClass;
		}
		switch (operate) {
		case "IF":
			expressClass = IfExpress.class;
			break;
		case "SUM":
			expressClass = SumExpress.class;
			break;
		case "AVG":
			expressClass = AvgExpress.class;
			break;
		default:
			expressClass = DefaultExpress.class;
			break;
		}
		return expressClass;
	}

	public void setExpressClass(Class<?> expressClass) {
		this.expressClass = expressClass;
	}

	public Object getResult() {
		replaceValue();
		if (afterReplaceExpress.startsWith("(") && afterReplaceExpress.endsWith(")") && !operate.equals(DEFAULT_OPERATE)) {
			afterReplaceExpress = afterReplaceExpress.substring(1, afterReplaceExpress.length() - 1);
		}
		if (nextBeans.size() > 0) {
			Set<String> keys = nextBeans.keySet();
			for (String key : keys) {
				ExpressBean bean = nextBeans.get(key);
				String res = bean.getResult() + "";
				System.out.println("替换：" + key + "====" + res);
				afterReplaceExpress = afterReplaceExpress.replace(key, res);
			}
		}
		System.out.println("express: " + afterReplaceExpress);
		try {
			Express e = (Express) getExpressClass().newInstance();
			return e.result(this);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	void replaceValue() {
		afterReplaceExpress = express;
		if (valuesMap != null) {
			Set<String> keys = valuesMap.keySet();
	    	for (String key : keys) {
	    		afterReplaceExpress = afterReplaceExpress.replaceAll(key, String.valueOf(valuesMap.get(key)));
	    	}
		}
	}

	@Override
	public String toString() {
		return "ExpressBean [express=" + express + ", operate=" + operate + ", nextBeans=" + nextBeans
				+ ", expressClass=" + expressClass + "]";
	}
	
}
