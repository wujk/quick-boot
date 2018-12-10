package com.wujk.utils.express;

public interface Express {
	
	Object result(ExpressBean bean);
	
	Class<?> returnClass();

}
