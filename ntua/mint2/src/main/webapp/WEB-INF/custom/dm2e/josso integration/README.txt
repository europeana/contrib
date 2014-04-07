In order to integrate dm2e mint instance with josso single sign on, you need to perform the following steps:

This list pertains to josso-1.8.7 and tomcat6

During installation we found that the josso gsh installer for tomcat6 doesn't work (completely) on the linux machine we used. So step by step
tomcat was modified like this:

1: Copy lot of libs into tomcat/lib folder 

FastInfoset-1.2.3.jar			cxf-tools-common-2.2.2.jar
XmlSchema-1.4.5.jar			ecj-4.2.2.jar
activation-1.1.jar			el-api.jar
annotations-api.jar			geronimo-activation_1.1_spec-1.0.2.jar
aopalliance-1.0.jar			geronimo-annotation_1.0_spec-1.1.1.jar
asm-2.2.3.jar				geronimo-javamail_1.4_spec-1.6.jar
axis-1.4-wl81fix.jar.bkp.1		geronimo-jaxws_2.1_spec-1.0.jar
axis-1.4.jar				geronimo-stax-api_1.0_spec-1.0.1.jar
axis-ant-1.4.jar			geronimo-ws-metadata_2.0_spec-1.1.2.jar
axis-jaxrpc-1.4.jar			jasper-el.jar
axis-saaj-1.4.jar			jasper.jar
axis-wsdl4j-1.5.1.jar			jaxb-api-2.1.jar
catalina-ant.jar			jaxb-impl-2.1.9.jar
catalina-ha.jar				josso-agent-config.xml
catalina-tribes.jar			josso-agent-shared-1.8.7.jar
catalina.jar				josso-agents-bin-1.8.7-axis.jar
commons-beanutils-1.6.1.jar		josso-tomcat60-agent-1.8.7.jar
commons-codec-1.3.jar			jsp-api.jar
commons-collections-3.0.jar		neethi-2.0.4.jar
commons-digester-1.5.jar		portal-identity-lib-2.7.1.GA.jar
commons-discovery-0.2.jar		saaj-api-1.3.jar
commons-httpclient-3.1.jar		saaj-impl-1.3.2.jar
commons-lang-2.0.jar			servlet-api.jar
commons-logging-1.1.1.jar		spring-aop-2.5.5.jar
commons-logging-api-1.0.4.jar		spring-beans-2.5.5.jar
commons-modeler-1.1.jar			spring-context-2.5.5.jar
cxf-api-2.2.2.jar			spring-core-2.5.5.jar
cxf-common-schemas-2.2.2.jar		tomcat-coyote.jar
cxf-common-utilities-2.2.2.jar		tomcat-dbcp.jar
cxf-rt-bindings-soap-2.2.2.jar		tomcat-i18n-es.jar
cxf-rt-bindings-xml-2.2.2.jar		tomcat-i18n-fr.jar
cxf-rt-core-2.2.2.jar			tomcat-i18n-ja.jar
cxf-rt-databinding-jaxb-2.2.2.jar	wsdl4j-1.6.2.jar
cxf-rt-frontend-jaxws-2.2.2.jar		wstx-asl-3.2.8.jar
cxf-rt-frontend-simple-2.2.2.jar	xbean-spring-3.4.3.jar
cxf-rt-ws-addr-2.2.2.jar		xml-resolver-1.2.jar


well, some of those were already there, but due to this, we would not recommend to modify an existing server but use a separate one for josso.

2: Copy the josso-agent-config.xml into the tomcat/lib dir. It might need modification, if the app name is not dm2e.
3: tomcat/conf/server.xml needs some additions:

<Realm className="org.josso.tc60.agent.jaas.CatalinaJAASRealm"
      appName="josso"
      userClassNames="org.josso.gateway.identity.service.BaseUserImpl"
      roleClassNames="org.josso.gateway.identity.service.BaseRoleImpl"
      debug="1" />
 
 and
 
 <Valve className="org.josso.tc60.agent.SSOAgentValve" debug="9" />
 
 at appropriate places, along with the removal of the normal Realm for user database.
 
 4: Put the jaas.conf into a place, maybe tomcat/conf. You'll need to reference it in 5:
 
 5: Start tomcat with -Djava.security.auth.login.config=tomcat/conf/jaas.conf
 tomcat/bin/setenv.sh might be a good place to add this option.
 (use absolute path, its safer)
 
 
 
 
 
 
