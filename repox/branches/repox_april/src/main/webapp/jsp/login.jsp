<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ page import="org.springframework.security.ui.AbstractProcessingFilter" %>
<%@ page import="org.springframework.security.ui.webapp.AuthenticationProcessingFilter" %>
<%@ page import="org.springframework.security.AuthenticationException" %>

<html><head><title><fmt:message key="authentication" /></title></head>

  <body onload="document.f.j_username.focus();">

<%-- 
	<P>Valid users:
	<P>
	<P>username <b>admin</b>, password <b>admin</b>
	<P>username <b>rod</b>, password <b>koala</b>
	<br>username <b>dianne</b>, password <b>emu</b>
	<br>username <b>scott</b>, password <b>wombat</b>
	<br>username <b>peter</b>, password <b>opal</b>
	<p>
--%>

    <%-- this form-login-page form is also used as the form-error-page to ask for a login again. --%>
    <c:if test="${not empty param.login_error}">
    	<span style="color: red;"><fmt:message key="error.authenticationFailed" /></span>
    </c:if>
  	
    <form name="f" action="<c:url value='/j_spring_security_check'/>" method="POST">
      <table style="margin-left: auto; margin-right: auto;">
        <tr><td><fmt:message key="authentication.username" />:</td><td><input type='text' name='j_username' value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>'/></td></tr>
        <tr><td><fmt:message key="authentication.password" />:</td><td><input type='password' name='j_password'></td></tr>
        <tr><td><input type="checkbox" name="_spring_security_remember_me"></td><td><fmt:message key="authentication.dontAskPassword" /></td></tr>

        <tr><td colspan='2'><input name="submit" type="submit"></td></tr>
        <tr><td colspan='2'><input name="reset" type="reset"></td></tr>
      </table>

    </form>

  </body>
</html>
