package com.wujk.utils.express;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExpressBean {
	
	private static final String DEFAULT_OPERATE = "default";  // 默认操作符一般 + - * /
	
	private String express;
	
	private String operate = DEFAULT_OPERATE;
	
	private Map<String, ExpressBean> nextBeans = new HashMap<String, ExpressBean>();
	
	private Object result;
	
	private Class<?> expressClass;

	public String getExpress() {
		return express;
	}

	public void setExpress(String express) {
		this.express = express;
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
		if (express.startsWith("(") && express.endsWith(")") && !operate.equals(DEFAULT_OPERATE)) {
			express = express.substring(1, express.length() - 1);
		}
		if (nextBeans.size() > 0) {
			Set<String> keys = nextBeans.keySet();
			for (String key : keys) {
				ExpressBean bean = nextBeans.get(key);
				String res = bean.getResult() + "";
				System.out.println("替换：" + key + "====" + res);
				express = express.replace(key, res);
			}
		}
		System.out.println("express: " + express);
		try {
			Express e = (Express) getExpressClass().newInstance();
			return e.result(this);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public String toString() {
		return "Bean [express=" + express + ", operate=" + operate + ", nextBeans=" + nextBeans + "]";
	}

	
}
