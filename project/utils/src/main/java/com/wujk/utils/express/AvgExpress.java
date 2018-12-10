package com.wujk.utils.express;

import org.nfunk.jep.JEP;

public class AvgExpress implements Express {

	@Override
	public Object result(ExpressBean bean) {
		String express = bean.getAfterReplaceExpress();
		String operate = bean.getOperate();
		String[] strs = express.split(",");
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
				jep.parseExpression("(" + sb.toString() + ")" + "/" + strs.length);
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
		return AvgExpress.class;
	}

}
