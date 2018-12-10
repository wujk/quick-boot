package com.wujk.utils.express;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.wujk.utils.pojo.ObjectUtil;

public class ExpressParse {
	
	private String[] regrexs = {"IF", "SUM", "AVG"};
	private ExpressBean bean;

	public ExpressParse(String express, Map<String, Object> valuesMap) {
		express = express.trim().replaceAll(" ", "");
		bean = new ExpressBean();
		bean.setExpress(express);
		parse(bean, 0);
		System.out.println(bean);
		if (!ObjectUtil.isEmpty(valuesMap)) {
			setBeanValueMap(bean, valuesMap);
		}
	}
	
	public ExpressParse(String express) {
		this(express, null);
	}
	
	public void setValue(Map<String, Object> valuesMap) {
		setBeanValueMap(bean, valuesMap);
	}
	
	private void setBeanValueMap(ExpressBean bean, Map<String, Object> valuesMap) {
		bean.setValuesMap(valuesMap);
		Map<String, ExpressBean> nextBeans = bean.getNextBeans();
		if (!ObjectUtil.isEmpty(nextBeans)) {
			Collection<ExpressBean> list = nextBeans.values();
			for (ExpressBean eBean : list) {
				setBeanValueMap(eBean, valuesMap);
			}
		}
	}
	
	public void parse(ExpressBean bean, int count) {
		count ++;
		String express = bean.getExpress();
		//System.out.println(express);
		// 定位特殊计算表达式 如 IF SUM AVG
		// 1、获取第一个特殊符号
		int index = Integer.MAX_VALUE;
		String operate = null;
		for (String regrex : regrexs) {
			int i = express.indexOf(regrex);
			if (i == -1) {
				continue;
			}
			if (i < index) {
				index = i;
				operate = regrex;
			}
		}
        if (operate != null && index != Integer.MAX_VALUE) {
        	char leftKh = '(';
    		char rightKh = ')';
    		int left = 0;
			int right = 0;
    		char[] chs = express.toCharArray();
    		for (int i = index; i < chs.length; i++) {
				if (leftKh == chs[i]) {
					left ++;
				} else if (rightKh == chs[i]) {
					right ++;
				}
				if (left != 0 && right != 0 && left == right) {
					String key = "${"+ (count) +"}";
					String childExpress = express.substring(index, i + 1);
					express = express.substring(0, index) + key + express.substring(i + 1);
					bean.setExpress(express);
					// System.out.println(key + " : " + childExpress + " : " + express);
					ExpressBean newBean = new ExpressBean();
					newBean.setOperate(operate);
					newBean.setExpressClass(parseExpressClass(operate));
					childExpress = childExpress.substring(operate.length());
					newBean.setExpress(childExpress);
					bean.getNextBeans().put(key, newBean);
					parse(bean, count);
					break;
				}
			}
        }
        if (bean.getNextBeans().size() > 0) {
        	Collection<ExpressBean> beans = bean.getNextBeans().values();
        	for (ExpressBean _bean : beans) {
        		parse(_bean, 0);
        	}
        }
	}
	
	/**
	 * 
	* @Title: parseExpressClass
	* @Description: 通过重写该方法，可以根据不同操作符转化成需要对应的各种自定义计算方式（自定义计算方式可以实现Express接口）
	* @author kevin
	* @date 2018年12月10日 上午10:54:54
	* @param operate
	* @return Class<?>
	* @throws
	* 如：
	* switch (operate) {
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
	 */
	public Class<?> parseExpressClass(String operate) {
		return null;
	}
	
	public ExpressBean getBean() {
		return bean;
	}

	public Object evaluate() {
       return bean.getResult();
	}
	
	public static void main(String[] args) {
		Map<String, Object> valuesMap = new HashMap<>();
		valuesMap.put("a", 2);
		valuesMap.put("b", 4);
		ExpressParse p = new ExpressParse("IF(a+b>1,SUM(AVG(a,b),SUM(1,1)),b) + b / a");
		p.setValue(valuesMap);
		System.out.println(p.evaluate());
		valuesMap.put("b", 5);
		p.setValue(valuesMap);
		System.out.println(p.evaluate());
	}
	
}
