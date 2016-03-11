 
<%@page import="java.util.List"%> 

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head> 
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 
         <title>Project Sample</title> 	 
    </head> 
	<body>
		<form action="PServlet" method="post">
			<input type="text" name="input">
			<input type="submit" value="submit">
		</form>
		<%
			if (request.getAttribute("result") != null){
				out.println("<h1>"+request.getAttribute("result")+"</h1>");
			}
		%>
	<body>
<html>