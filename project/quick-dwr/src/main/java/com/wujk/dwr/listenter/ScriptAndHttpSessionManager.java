package com.wujk.dwr.listenter;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.directwebremoting.Browser;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContextFactory;

import com.wujk.dwr.constant.Constants;
import com.wujk.dwr.util.DwrScriptbufferUtil;
import com.wujk.dwr.util.ScriptSessionPool;

public class ScriptAndHttpSessionManager {

	public static synchronized void init(String group) {
		ScriptSessionPool pool = ScriptSessionPool.getInstance();
		HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
		String ip = request.getHeader("X-Real-IP");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		HttpSession httpSession = WebContextFactory.get().getSession(true);
		httpSession.setAttribute(Constants.GROUP, group);
		ScriptSession scriptSession = pool.getSessionScriptSession(httpSession.getId());
		if (scriptSession == null) {
			Collection<ScriptSession> scriptSessions = Browser.getTargetSessions();
			for (ScriptSession session : scriptSessions) {
				session.setAttribute(Constants.HTTPSESSIONID, httpSession != null ? httpSession.getId() : null);
				if (group == null) {
					group = (String) httpSession.getAttribute(Constants.GROUP);
				}
				if (group != null) {
					session.setAttribute(Constants.GROUP, group);
				}
				if (ip != null) {
					session.setAttribute(Constants.VISITIP, ip);
				}
				System.out.println("id:" + session.getId() + "组：" + session.getAttribute(Constants.GROUP) + " 访问ip："
						+ session.getAttribute(Constants.VISITIP) + " httpSessionID："
						+ session.getAttribute(Constants.HTTPSESSIONID));
				pool.addScriptSession(session);
				ScriptBuffer script = DwrScriptbufferUtil.genScriptBuffer("connectSuccess", ip);
				session.addScript(script);
				if (group != null) {
					script = DwrScriptbufferUtil.genScriptBuffer("showGroup",
							pool.getSessionScriptSession(httpSession.getId()).getAttribute(Constants.GROUP));
					session.addScript(script);
				}
			}
		} else {
			scriptSession.setAttribute(Constants.HTTPSESSIONID, httpSession != null ? httpSession.getId() : null);
			if (group == null) {
				group = (String) httpSession.getAttribute(Constants.GROUP);
			}
			if (group != null) {
				String oldGroup = (String) scriptSession.getAttribute(Constants.GROUP);
				if (oldGroup != null && !group.equals(oldGroup)) {
					ScriptBuffer script = DwrScriptbufferUtil.genScriptBuffer("changeGroup", oldGroup, group);
					scriptSession.addScript(script);
				}
				scriptSession.setAttribute(Constants.GROUP, group);
			}
			if (ip != null) {
				scriptSession.setAttribute(Constants.VISITIP, ip);
			}
			System.out.println("id:" + scriptSession.getId() + "组：" + scriptSession.getAttribute(Constants.GROUP) + " 访问ip："
					+ scriptSession.getAttribute(Constants.VISITIP) + " httpSessionID："
					+ scriptSession.getAttribute(Constants.HTTPSESSIONID));
			if (group != null) {
				ScriptBuffer script = DwrScriptbufferUtil.genScriptBuffer("showGroup",
						pool.getSessionScriptSession(httpSession.getId()).getAttribute(Constants.GROUP));
				scriptSession.addScript(script);
			}
		}
	}
}
