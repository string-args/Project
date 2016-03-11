import Bean.Connector;
import javax.servlet.ServletException; 
import javax.servlet.annotation.WebServlet; 
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse; 
import javax.servlet.http.HttpSession; 

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser; 

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import java.io.IOException;
import java.io.InputStreamReader;

import com.ibm.watson.developer_cloud.tradeoff_analytics.v1.TradeoffAnalytics;
import com.ibm.watson.developer_cloud.tradeoff_analytics.v1.model.Dilemma;
import com.ibm.watson.developer_cloud.tradeoff_analytics.v1.model.Problem;
import com.ibm.watson.developer_cloud.tradeoff_analytics.v1.model.Option;
import com.ibm.watson.developer_cloud.tradeoff_analytics.v1.model.column.Column;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet(name = "PServlet", urlPatterns = {"/PServlet"})
public class PServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
	
		Connector conn = new Connector();
	
		TradeoffAnalytics service = new TradeoffAnalytics();
		service.setUsernameAndPassword(conn.get_tradeoff_username(),conn.get_tradeoff_password());
		
		try{
			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
			for (FileItem item: items) {
				if (!item.isFormField()){
					Scanner scn = new Scanner(new InputStreamReader(item.getInputStream(),"UTF-8"));
					List<String> output = new ArrayList<String>();
					while (scn.hasNextLine()){
						String line = scn.nextLine().trim();
						if (line.length() > 0){
							output.add(line);
						}
					}
					scn.close();
					
					//parse the output
					JSONParser parser = new JSONParser();
					JSONObject result = (JSONObject) parser.parse(output.toString());
					
					Problem problem = new Problem((String) result.get("subject"));
					
					List<Column> columns = new ArrayList<Column>();
					problem.setColumns(columns);
					
					JSONObject column_names = (JSONObject) ((JSONArray) result.get("columns")).get(0);
					request.setAttribute("result",column_names.toString());
				}
			}
			
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
		
		
	
	
		response.setContentType("text/html");
		response.setStatus(200); 
 		request.getRequestDispatcher("index.jsp").forward(request,response); 
	}
	
	
}