package com.wujk.utils.express;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nfunk.jep.JEP;

public class ExpressBean {
	
	private static final String DEFAULT_OPERATE = "default";  // 默认操作符一般 + - * /
	
	private String express;
	
	private String operate = DEFAULT_OPERATE;
	
	private Map<String, ExpressBean> nextBeans = new HashMap<String, ExpressBean>();
	
	private Object result;

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
		switch (operate) {
		case "IF":
			result = ifExpress();
			break;
		case "SUM":
			result = sum();
			break;
		case "AVG":
			result = avg();
			break;
		default:
			result = defaultExpress();
			break;
		}
		return result;
	}

	/**
	 * 
	* @Title: ifExpress
	* @Description: if计算
	* @author kevin
	* @date 2018年11月29日 下午4:43:53
	* @return
	* @throws RuntimeException Object
	* @throws
	 */
	protected Object ifExpress() throws RuntimeException {
		String[] strs = express.split(",");
		if (strs.length == 3) {
			try {
				
				JEP jep = new JEP();
				jep.parse(strs[0]);
				if (Boolean.parseBoolean(jep.getValueAsObject() + "")) {
					jep.parse(strs[1]);
				} else {
					jep.parse(strs[2]);
				}
				result = jep.getValueAsObject();
				System.out.println(operate + "("+ express +")=" + result);
				return result;
			} catch (Exception e) {
				throw new RuntimeException(operate + "("+ express +")表达式不正确");
			}
		} else {
			throw new RuntimeException(operate + "("+ express +")表达式不正确");
		}
	}

	/**
	 * 
	* @Title: sum
	* @Description: 求和
	* @author kevin
	* @date 2018年11月29日 下午4:42:33
	* @return
	* @throws RuntimeException Object
	* @throws
	 */
	protected Object sum() throws RuntimeException {
		String[] strs;
		strs = express.split(",");
		if (strs.length > 0) {
			try {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < strs.length; i++) {
					sb.append(strs[i]).append("+");
				}
				if (sb.length() > 0) {
					sb.deleteCharAt(sb.length()-1);
				}
				JEP jep = new JEP();
				jep.parse(sb.toString());
				result = jep.getValueAsObject();
				System.out.println(operate + "("+ express +")=" + result);
				return result;
			} catch (Exception e) {
				throw new RuntimeException(operate + "("+ express +")表达式不正确");
			}
		} else {
			throw new RuntimeException(operate + "("+ express +")表达式不正确");
		}
	}

	/**
	 * 
	* @Title: avg
	* @Description: 平均值
	* @author kevin
	* @date 2018年11月29日 下午4:42:50
	* @return
	* @throws RuntimeException Object
	* @throws
	 */
	protected Object avg() throws RuntimeException {
		String[] strs;
		strs = express.split(",");
		if (strs.length > 0) {
			try {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < strs.length; i++) {
					sb.append(strs[i]).append("+");
				}
				if (sb.length() > 0) {
					sb.deleteCharAt(sb.length()-1);
				}
				JEP jep = new JEP();
				jep.parse("(" + sb.toString() + ")" + "/" + strs.length);
				result = jep.getValueAsObject();
				System.out.println(operate + "("+ express +")=" + result);
				return result;
			} catch (Exception e) {
				throw new RuntimeException(operate + "("+ express +")表达式不正确");
			}
		} else {
			throw new RuntimeException(operate + "("+ express +")表达式不正确");
		}
	}

	/**
	 * 
	* @Title: defaultExpress
	* @Description: 默认计算
	* @author kevin
	* @date 2018年11月29日 下午4:41:03
	* @return
	* @throws RuntimeException Object
	* @throws
	 */
	private Object defaultExpress() throws RuntimeException {
		try {
			JEP jep = new JEP();
			jep.parse(express);
			result = jep.getValueAsObject();
			System.out.println(operate + "("+ express +")=" + result);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(operate + "("+ express +")表达式不正确");
		}
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "Bean [express=" + express + ", operate=" + operate + ", nextBeans=" + nextBeans + "]";
	}

	
}
