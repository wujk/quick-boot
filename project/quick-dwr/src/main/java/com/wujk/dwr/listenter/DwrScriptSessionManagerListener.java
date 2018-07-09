package com.wujk.dwr.listenter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.event.ScriptSessionEvent;
import org.directwebremoting.event.ScriptSessionListener;
import org.directwebremoting.impl.DefaultScriptSessionManager;

import com.wujk.dwr.constant.Constants;
import com.wujk.dwr.util.DwrScriptbufferUtil;
import com.wujk.dwr.util.ScriptSessionPool;
import com.wujk.utils.pojo.ObjectUtil;
/**
 * 创建初始化监听，此时session可能未被创建或还是上次创建的session 需要更新session需要调用ScriptAndHttpSessionManager此方法主要更新新session到scriptSession
 * 如果在jsp页面可以在java脚本中先调用session赋值
 * @author admin
 *
 */
public class DwrScriptSessionManagerListener extends DefaultScriptSessionManager {
	
	public DwrScriptSessionManagerListener() {
		ScriptSessionPool pool = ScriptSessionPool.getInstance();
		this.addScriptSessionListener(new ScriptSessionListener() {

			@Override
			public void sessionDestroyed(ScriptSessionEvent ev) {
				System.out.println("ScriptSession销毁");
				ScriptSession scriptSession = ev.getSession();
				if (ObjectUtil.isEmpty(scriptSession))
					return;
				System.out.println("id:" + scriptSession.getId() + "组：" + scriptSession.getAttribute(Constants.GROUP) + " 访问ip：" + scriptSession.getAttribute(Constants.VISITIP) + " httpSessionID：" + scriptSession.getAttribute(Constants.HTTPSESSIONID));
				pool.removeScriptSession(scriptSession);
			}

			@Override
			public void sessionCreated(ScriptSessionEvent ev) {
				System.out.println("ScriptSession创建");
				HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
				HttpSession httpSession = WebContextFactory.get().getSession();
				String ip = request.getHeader("X-Real-IP");
				if (ObjectUtil.isEmpty(ip)) {
					ip = request.getRemoteAddr();
				}
				ScriptSession scriptSession = ev.getSession();
				if (ObjectUtil.isEmpty(scriptSession))
					return;
				if (ObjectUtil.isEmpty(scriptSession)) {
					return;
				}
				
				scriptSession.setAttribute(Constants.HTTPSESSIONID, !ObjectUtil.isEmpty(scriptSession) ? httpSession.getId() : null);
				String group = (String) request.getAttribute(Constants.GROUP);
				if (ObjectUtil.isEmpty(group)) {
					group = (String) httpSession.getAttribute(Constants.GROUP);
				}
				if (!ObjectUtil.isEmpty(group)) {
					scriptSession.setAttribute(Constants.GROUP, group);
					ScriptSession _scriptSession = pool.getSessionScriptSession(httpSession.getId());
					if (!ObjectUtil.isEmpty(_scriptSession)) {
						String oldGroup = (String) _scriptSession.getAttribute(Constants.GROUP);
						if (!ObjectUtil.isEmpty(oldGroup) && !group.equals(oldGroup)) {
							ScriptBuffer script = DwrScriptbufferUtil.genScriptBuffer("changeGroup", oldGroup, group);
							_scriptSession.addScript(script);
						}
					}
				}
				if (ip != null) {
					scriptSession.setAttribute(Constants.VISITIP, ip);
				}
				
				System.out.println("id:" + scriptSession.getId() + "组：" + scriptSession.getAttribute(Constants.GROUP) + " 访问ip：" + scriptSession.getAttribute(Constants.VISITIP) + " httpSessionID：" + scriptSession.getAttribute(Constants.HTTPSESSIONID));
				pool.addScriptSession(scriptSession);
				ScriptBuffer script = DwrScriptbufferUtil.genScriptBuffer("connectSuccess", ip);
				scriptSession.addScript(script);
				if (group != null) {
					script = DwrScriptbufferUtil.genScriptBuffer("hasConnect",
							pool.getSessionScriptSession(httpSession.getId()).getAttribute(Constants.GROUP));
					scriptSession.addScript(script);
				}
				
				
			}
		});
	}

}
