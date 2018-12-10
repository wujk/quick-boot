package com.wujk.utils.express;

import org.nfunk.jep.JEP;

public class DefaultExpress implements Express {

	@Override
	public Object result(ExpressBean bean) {
		String express = bean.getAfterReplaceExpress();
		String operate = bean.getOperate();
		try {
			JEP jep = new JEP();
			jep.parseExpression(express);
			Object result = jep.getValueAsObject();
			System.out.println(operate + "("+ express +")=" + result);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(operate + "("+ express +")表达式不正确");
		}
	}

	@Override
	public Class<?> returnClass() {
		return DefaultExpress.class;
	}

}
