import javax.servlet.ServletException; 
import javax.servlet.annotation.WebServlet; 
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse; 
import javax.servlet.http.HttpSession; 

import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser; 

import java.io.IOException;

import com.ibm.watson.developer_cloud.language_translation.v2.LanguageTranslation;
import com.ibm.watson.developer_cloud.language_translation.v2.model.TranslationResult;

@WebServlet(name = "PServlet", urlPatterns = {"/PServlet"})
public class PServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Connector conn = new Connector();
		LanguageTranslation service = new LanguageTranslation();
		service.setUsernameAndPassword(conn.get_language_username(),conn.get_language_password());
		TranslationResult result = service.translate(request.getParameter("input"),"en","es");
		
		try{
			JSONParser parser = new JSONParser();
			JSONObject a = (JSONObject) parser.parse(result.toString());
			JSONArray o = (JSONArray) a.get("translations");
			
			for (Object c : o ){
				JSONObject p = (JSONObject) c;
				
				String translate = (String) p.get("translation");		
				request.setAttribute("result",translate);
			}

		} catch (Exception e){
			e.printStackTrace(System.err);
		}
		response.setContentType("text/html"); 
		response.setStatus(200); 
 		request.getRequestDispatcher("index.jsp").forward(request,response); 
	}
	
	
}