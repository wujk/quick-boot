# quick-dwr

# 1��pom.xml��������
      <dependencies>
    		<dependency>
    			<groupId>org.directwebremoting</groupId>
    			<artifactId>dwr</artifactId>
    			<version>3.0.2-RELEASE</version>
    		</dependency>
    		<dependency>
    			<groupId>javax.servlet</groupId>
    			<artifactId>servlet-api</artifactId>
    			<version>2.5</version>
    			<scope>provided</scope>
    		</dependency>
    		<dependency>
    			<groupId>commons-logging</groupId>
    			<artifactId>commons-logging</artifactId>
    			<version>1.1.1</version>
    		</dependency>
    	</dependencies>
    	<build>
    		<plugins>
    			<plugin>
    				<groupId>org.apache.maven.plugins</groupId>
    				<artifactId>maven-compiler-plugin</artifactId>
    				<version>3.3</version>
    				<configuration>
    					<source>1.8</source>
    					<target>1.8</target>
    					<encoding>UTF-8</encoding>
    				</configuration>
    			</plugin>
    		</plugins>
    	</build>

# 1��web.xml����
        <servlet>
		<servlet-name>dwr-invoker</servlet-name>
		<servlet-class>org.directwebremoting.servlet.DwrServlet</servlet-class>
		<init-param>
			<param-name>org.directwebremoting.extend.ScriptSessionManager</param-name>
			<param-value>com.wujk.dwr.listenter.DwrScriptSessionManagerListener</param-value>
		</init-param>
		<init-param>
			<param-name>crossDomainSessionSecurity</param-name>
			<param-value>false</param-value>
		</init-param>
		<init-param>
			<param-name>allowScriptTagRemoting</param-name>
			<param-value>true</param-value>
		</init-param>
		<init-param>
			<param-name>classes</param-name>
			<param-value>java.lang.Object</param-value>
		</init-param>
		<init-param>
			<param-name>activeReverseAjaxEnabled</param-name>
			<param-value>true</param-value>
		</init-param>
		<init-param>
			<param-name>initApplicationScopeCreatorsAtStartup</param-name>
			<param-value>true</param-value>
		</init-param>
		<init-param>
			<param-name>maxWaitAfterWrite</param-name>
			<param-value>3000</param-value>
		</init-param>
		<init-param>
			<param-name>debug</param-name>
			<param-value>true</param-value>
		</init-param>
		<init-param>
			<param-name>logLevel</param-name>
			<param-value>WARN</param-value>
		</init-param>
	</servlet>
	<servlet-mapping>
		<servlet-name>dwr-invoker</servlet-name>
		<url-pattern>/dwr/*</url-pattern>
	</servlet-mapping>
	
# 3��dwr.xml������web.xmlͬ��Ŀ¼
	<?xml version="1.0" encoding="UTF-8"?>
	<!DOCTYPE dwr PUBLIC
	          "-//GetAhead Limited//DTD Direct Web Remoting 3.0//EN"
	          "http://getahead.org/dwr/dwr30.dtd">
	<dwr>
		<allow>
			<create creator="new" javascript="">
				<param name="class" value="" />
			</create>
			<create creator="new" javascript="***">
				<param name="class" value="com.wujk.***" />
			</create>
			<create creator="new" javascript="xxx">
				<param name="class" value="com.wujk.xxx" />
			</create>
		</allow>
	</dwr>

# 4������Ҫ���͵����������js
	<script type="text/javascript" src="dwr_web/engine.js"></script>
	<script type="text/javascript" src="dwr_web/util.js"></script>
	<script type="text/javascript" src="dwr_web/interface/xxx.js"></script>
	<script type="text/javascript" src="dwr_web/interface/***.js"></script>
	
# 5��д��Ҫִ�еĻص�js
	<script type="text/javascript">  
    function testPush() {  
    	var value = document.getElementById("text").value;
    	PushGroupClient.sendGroup('super', "showMessage",value);  
    } 
    function showMessage(msg) {  
    	alert(msg);
    }
    function onPageLoad() { // ҳ���ʼ��ʱ���� 
    	dwr.engine.setActiveReverseAjax(true);
    	dwr.engine.setNotifyServerOnPageUnload(true);
    	ScriptSessionGroup.initData("common");
    }
    function connectSuccess(ip) {  
    	var div = document.getElementById("ip");
    	while (div.hasChildNodes()) //��div�»������ӽڵ�ʱ ѭ������
		{
			div.removeChild(div.firstChild);
		}
    	var p = document.createElement("p");
    	p.setAttribute("name", ip);
    	p.innerHTML = ip;
    	div.appendChild(p);
    }
    function showGroup(msg) {
    	alert("�Ѿ�������"+ msg);
    }
    function changeGroup(oldGroup, newGroup) {
    	alert("����"+ oldGroup + "��" + newGroup);
    }
    </script>  