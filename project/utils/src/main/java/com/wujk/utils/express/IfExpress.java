package com.wujk.utils.express;

import org.nfunk.jep.JEP;

public class IfExpress implements Express {

	@Override
	public Object result(ExpressBean bean) {
		String express = bean.getExpress();
		String operate = bean.getOperate();
		String[] strs = express.split(",");
		if (strs.length == 3) {
			try {
				JEP jep = new JEP();
				jep.parseExpression(strs[0]);
				if (Boolean.parseBoolean(jep.getValueAsObject() + "")) {
					jep.parseExpression(strs[1]);
				} else {
					jep.parseExpression(strs[2]);
				}
				Object result = jep.getValueAsObject();
				System.out.println(operate + "("+ express +")=" + result);
				return result;
			} catch (Exception e) {
				throw new RuntimeException(operate + "("+ express +")表达式不正确");
			}
		} else {
			throw new RuntimeException(operate + "("+ express +")表达式不正确");
		}
	}

	@Override
	public Class<?> returnClass() {
		return IfExpress.class;
	}

}
