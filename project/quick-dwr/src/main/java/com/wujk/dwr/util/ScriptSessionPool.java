package com.wujk.dwr.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.directwebremoting.ScriptSession;

import com.wujk.dwr.beans.ConnectBean;
import com.wujk.dwr.constant.Constants;
/**
 * 用来存放scripSession的池子单列模式（利用静态代码块只初始化加载一次保证单例）
 * @author admin
 *
 */
public class ScriptSessionPool {
	
	private static class InitPool {
		private static ScriptSessionPool INSTANCE = new ScriptSessionPool();
	}
	
	private ConcurrentHashMap<String, ScriptSession> scriptSessions;
	
	private ScriptSessionPool() {
		scriptSessions = new ConcurrentHashMap<String, ScriptSession>();
	}
	
	public static ScriptSessionPool getInstance() {
		return InitPool.INSTANCE;
	}

	public ConcurrentHashMap<String, ScriptSession> getScriptSessions() {
		return scriptSessions;
	}

	public void setScriptSessions(ConcurrentHashMap<String, ScriptSession> scriptSessions) {
		this.scriptSessions = scriptSessions;
	}
	
	public ScriptSession addScriptSession(ScriptSession scriptSession) {
		if (scriptSession != null) {
			String id = (String) scriptSession.getAttribute(Constants.HTTPSESSIONID);
			if (id != null) {
				System.out.println("添加scriptSession：" + id);
				return scriptSessions.putIfAbsent(id, scriptSession);
			}
		} 
		return scriptSession;
	}
	
	public ScriptSession removeScriptSession(ScriptSession scriptSession) {
		if (scriptSession != null) {
			String id = (String) scriptSession.getAttribute(Constants.HTTPSESSIONID);
			if (id != null) {
				System.out.println("删除scriptSession：" + id);
				return scriptSessions.remove(id);
			}
		}
		return scriptSession;
	}
	
	
	public List<ConnectBean> managerScriptSession() {
		List<ConnectBean> list = new ArrayList<ConnectBean>();
		Set<Entry<String, ScriptSession>> entries = scriptSessions.entrySet();
		for (Entry<String, ScriptSession> entry : entries) {
			ScriptSession scriptSession = entry.getValue();
			ConnectBean connectBean = new ConnectBean();
			connectBean.setId(scriptSession.getId());
			connectBean.setIp((String)scriptSession.getAttribute(Constants.VISITIP));
			connectBean.setGroup((String)scriptSession.getAttribute(Constants.GROUP));
			connectBean.setSessionId((String)scriptSession.getAttribute(Constants.HTTPSESSIONID));
			list.add(connectBean);
		}
		return list;
	}
	
	public Collection<ScriptSession> getAllScriptSession() {
		return scriptSessions.values();
	}
	
	public Collection<ScriptSession> getGroupScriptSession(String group) {
		List<ScriptSession> list = new ArrayList<ScriptSession>();
		for (ScriptSession scriptSession : getAllScriptSession()) {
			if (group.equals(scriptSession.getAttribute(Constants.GROUP))) {
				list.add(scriptSession);
			}
		}
		return list;
	}
	
	public ScriptSession getSessionScriptSession(String httpSessionId) {
		return scriptSessions.get(httpSessionId);
	}
}
