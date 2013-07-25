<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<html>
<head><title>Logout</title></head>

<body>
  <% 
    HttpSession ses = request.getSession(false);
  	ses.invalidate();
   %>

   <p>You are now logged out.</p> 
   <a href="${pageContext.request.contextPath}"/>Home Page</a>
</body>

</html>
