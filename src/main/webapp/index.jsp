 
<%@page import="java.util.List"%> 
<%@page import="org.openstack4j.model.storage.object.SwiftObject"%> 


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
			Connector conn = new Connector();
			if (conn.listAllObjects("sample").isEmpty()){
				conn.createContainer("sample");
			} else {
				List<? extends SwiftObject> objectlist = connect.listAllObjects("sample");	
				for (int i = 0; i < objectlist.size(); i++) { 
 					out.println("<tr>"); 
 					out.println("<td><label for=\"filename\">"+ objectlist.get(i).getName() +"</label></td>"); 
 					out.println("<td><form action=\"Download\" method=\"GET\">"); 
 					out.println("<input type=\"hidden\" readonly name=\"filename\" value=\"" + objectlist.get(i).getName() + "\"/>"); 
 					out.println("<input type=\"submit\" value=\"Download\" name=\"" + i + "\">"); 
 					out.println("</form></td>"); 
 					out.println("</tr>"); 
 				} 
 				out.println("</table>"); 
			}
		%>
	<body>
<html>