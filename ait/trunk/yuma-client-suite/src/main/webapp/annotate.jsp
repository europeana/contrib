<!DOCTYPE html>
<html>
	<head>
		<title>YUMA - Your Universal Multimedia Annotator</title>	
	    <meta http-equiv="content-type" content="text/html; charset=UTF-8">		
		<meta name="gwt:property" content="locale=<%= request.getLocale().getLanguage() %>">
		<link href="css/annotation.css" rel="stylesheet" type="text/css" />
		<link href="css/tileview.css" rel="stylesheet" type="text/css" />
		    		
		<%
			if (request.getParameter("tileView") == null) {
				out.println("\t\t<script src=\"js/wz_jsgraphics.js\" type=\"text/javascript\"></script>");
			} else {
				out.println("\t\t<script src=\"js/openlayers/OpenLayers.js\" type=\"text/javascript\"></script>");
			}
		%>
		<script src="js/raphael/raphael-min.js"></script>
		
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
					
		<script language='javascript' src='image/image.nocache.js'>
		</script>
		
		<script src="js/ait-jsutils.js"></script> 
	</head>

	<body>
		<!--  store the request parameters  -->	         
		<script language='javascript'>
			var parameters = {
                         objectURL:	"<%=request.getParameter("objectURL")%>",
                         htmlURL:	"<%=request.getParameter("htmlURL")%>",                             
                         imageURL:	"<%=request.getParameter("imageURL")%>",                                                  
                         user: 		"<%=request.getParameter("user")%>",
                         db: 	 	"<%=request.getParameter("db")%>",
                         id: 		"<%=request.getParameter("id")%>",
                         authToken:	"<%=request.getParameter("authToken")%>",
                         appSign: 	"<%=request.getParameter("appSign")%>",
                         bbox: 		"<%=request.getParameter("bbox")%>",
                         tileView: 	"<%=request.getParameter("tileView")%>",
                         flaggedId:	"<%=request.getParameter("flaggedId")%>",                                                                          
                         baseURL:	"<%=base%>"                         
             }; 
		</script>	
	</body>
</html>
