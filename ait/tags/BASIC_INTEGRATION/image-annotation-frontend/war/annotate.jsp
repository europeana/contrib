<html>
	<head>			
		<meta name="gwt:property" content="locale=<%= request.getLocale().getLanguage() %>">
		<link href="css/annotation.css" rel="stylesheet" type="text/css" />     
		<script src="js/wz_jsgraphics.js" type="text/javascript"></script>
		<%
		if (request.getParameter("tileView") != null) {
			out.println("<script src=\"openlayers/OpenLayers.js\" type=\"text/javascript\"></script>");
		}
		%>
		<!-- use the base tag for IE so that GWT is able to detect the path even if any request 
		parameter contains unescaped slashes. if a base tag exists GWT is not trying to get
		the path from location.href! -->
		<%
			String base = request.getScheme()+ "://" + 
				request.getServerName()+ ":" + request.getServerPort();
			if(request.getContextPath()!=null&&request.getContextPath().length()!=0) {
				base+= request.getContextPath();
			} else if(request.getPathInfo()!=null&&request.getPathInfo().length()!=0) {
				int lastSlashPos = request.getPathInfo().lastIndexOf("/");
				if(lastSlashPos>0)
					base+= request.getPathInfo().substring(0,lastSlashPos);
			}
			base+="/";
		%>
		<base href="<%=base%>" />							
					
		<script language='javascript' src='imageannotation/imageannotation.nocache.js'>
		</script>
	</head>

	<body>
		<!--  store the request parameters  -->	         
		<script language='javascript'>
			var parameters = {
                         objectURL:	"<%=request.getParameter("objectURL")%>",
                         imageURL:	"<%=request.getParameter("imageURL")%>",                         
                         user: 		"<%=request.getParameter("user")%>",
                         db: 	 	"<%=request.getParameter("db")%>",
                         id: 		"<%=request.getParameter("id")%>",
                         authToken:	"<%=request.getParameter("authToken")%>",
                         appSign: 	"<%=request.getParameter("appSign")%>",
                         bbox: 		"<%=request.getParameter("bbox")%>",
                         tileView: 	"<%=request.getParameter("tileView")%>",                                                  
                         baseURL:	"<%=base%>"                         
             }; 
		</script>	
	</body>
</html>
