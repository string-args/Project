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

import com.ibm.watson.developer_cloud.language_translation.v2.LanguageTranslation;
import com.ibm.watson.developer_cloud.language_translation.v2.model.TranslationResult;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import java.io.IOException;

@WebServlet(name = "PServlet", urlPatterns = {"/PServlet"})
public class PServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
	
		Connector conn = new Connector();

		LanguageTranslation service = new LanguageTranslation();
		service.setUsernameAndPassword(conn.get_language_username(),conn.get_language_password());
		
		TextToSpeech service1 = new TextToSpeech();
		service1.setUsernameAndPassword(conn.get_t2s_username(),conn.get_t2s_password());
		
		TranslationResult result = service.translate((String) request.getParameter("input"),"en","es");
		
		try{
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result.toString());
			request.setAttribute("result",result.toString());
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	
		response.setContentType("text/html");
		response.setStatus(200); 
 		request.getRequestDispatcher("index.jsp").forward(request,response); 
	}
	
	
}