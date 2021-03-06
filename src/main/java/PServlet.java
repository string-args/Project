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

import java.io.*;

import org.openstack4j.model.common.Payload;
import org.openstack4j.model.common.Payloads;

@WebServlet(name = "PServlet", urlPatterns = {"/PServlet"})
public class PServlet extends HttpServlet {
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		response.setContentType("text/html;charset=UTF-8");
	try (PrintWriter out = response.getWriter()) {	
		HttpSession session = request.getSession();
		Connector conn = (Connector) session.getAttribute("connector");

		LanguageTranslation service = new LanguageTranslation();
		service.setUsernameAndPassword(conn.get_language_username(),conn.get_language_password());
		
		TextToSpeech service1 = new TextToSpeech();
		service1.setUsernameAndPassword(conn.get_t2s_username(),conn.get_t2s_password());
		
		TranslationResult result = service.translate((String) request.getParameter("input"),"en","es");
		
		String translation = null;
		
		try{
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result.toString());
			
			JSONArray translations = (JSONArray) object.get("translations");
			for (Object o : translations){
				JSONObject a = (JSONObject) o;
				translation = (String) a.get("translation");
				break;
			}
			String format = "audio/wav";
			InputStream speech = service1.synthesize(translation, Voice.ES_ENRIQUE, format);
			/*
			OutputStream output = response.getOutputStream();
			byte[] buf = new byte[2046];
			int len;
			while ((len = speech.read(buf)) > 0){
				output.write(buf,0,len);
			}
			response.setContentType("audio/wav");
			response.setHeader("Content-disposition","attachment;filename=output.wav");
			output.flush();
			output.close();
			*/
			Payload upfile = Payloads.create(speech);
			if (!(upfile == null)){
				conn.uploadFile("sample","output["+translation+"].wav",upfile);
			}
			speech.close();
			session.setAttribute("connector", conn);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	
		response.sendRedirect("index.jsp");
	}
	}
	
	@Override 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
             throws ServletException, IOException { 
         processRequest(request, response); 
    } 

	@Override 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
             throws ServletException, IOException { 
         processRequest(request, response); 
    } 
	
	@Override 
    public String getServletInfo() { 
         return "Short description"; 
    } 


 

	
}