package com.wujk.dwr.push;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContextFactory;

import com.wujk.dwr.constant.Constants;
import com.wujk.dwr.util.DwrScriptbufferUtil;
import com.wujk.dwr.util.ScriptSessionPool;

public class PushGroup {

	public void sendGroup(String group, String funcName, String msg) {

		HttpSession httpSession = WebContextFactory.get().getSession();
		ScriptSessionPool pool = ScriptSessionPool.getInstance();
		Collection<ScriptSession> sessions = pool.getAllScriptSession();
		for (ScriptSession session : sessions) {
			String type = (String) session.getAttribute(Constants.GROUP);
			boolean result = group.equals(type)
					&& !httpSession.getId().equals(session.getAttribute(Constants.HTTPSESSIONID));
			if (result) {
				session.addRunnable(new Runnable() {

					@Override
					public void run() {
						ScriptBuffer script = DwrScriptbufferUtil.genScriptBuffer(funcName, msg);
						session.addScript(script);
					}
				});
			}

		}

		/*
		 * Browser.withAllSessionsFiltered(new ScriptSessionFilter() {
		 * 
		 * @Override public boolean match(ScriptSession session) { HttpSession
		 * httpSession = WebContextFactory.get().getSession();
		 * System.out.println("当前访问sessionId：" + httpSession.getId());
		 * System.out.println("id:" + session.getId() + "组：" +
		 * session.getAttribute(Constants.GROUP) + " 访问ip：" +
		 * session.getAttribute(Constants.VISITIP) + " httpSessionID：" +
		 * session.getAttribute(Constants.HTTPSESSIONID)); String type = (String)
		 * session.getAttribute(Constants.GROUP); boolean result = group.equals(type) &&
		 * !httpSession.getId().equals(session.getAttribute(Constants.HTTPSESSIONID));
		 * System.out.println("是否需要发送：" + result); return result; // 不发给自己; } }, new
		 * Runnable() {
		 * 
		 * @Override public void run() { ScriptBuffer script =
		 * DwrScriptbufferUtil.genScriptBuffer(funcName, msg); Collection<ScriptSession>
		 * sessions = Browser.getTargetSessions(); for (ScriptSession scriptSession :
		 * sessions) { scriptSession.addScript(script); } } });
		 */
	}
}
