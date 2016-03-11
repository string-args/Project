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
import java.io.InputStream;
import java.io.OutputStream;

import com.ibm.watson.developer_cloud.language_translation.v2.LanguageTranslation;
import com.ibm.watson.developer_cloud.language_translation.v2.model.TranslationResult;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import org.openstack4j.model.common.Payload; 
import org.openstack4j.model.common.Payloads; 

@WebServlet(name = "PServlet", urlPatterns = {"/PServlet"})
public class PServlet extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Connector conn = new Connector();
		LanguageTranslation service = new LanguageTranslation();
		
		service.setUsernameAndPassword(conn.get_language_username(),conn.get_language_password());
		TranslationResult result = service.translate(request.getParameter("input"),"en","es");
		
		TextToSpeech service1 = new TextToSpeech();
		service1.setUsernameAndPassword(conn.get_t2s_username(),conn.get_t2s_password());
		
		String translate = null;

		Payload upfile = null;
		
		try{
			JSONParser parser = new JSONParser();
			JSONObject a = (JSONObject) parser.parse(result.toString());
			JSONArray o = (JSONArray) a.get("translations");
			
			for (Object c : o ){
				JSONObject p = (JSONObject) c;
				
				translate = (String) p.get("translation");		
				break;
			}
			
			String outputformat = "audio/wav";
			InputStream speech = service1.synthesize(translate,Voice.ES_ENRIQUE,outputformat);
			
			/*OutputStream output = response.getOutputStream();
			byte[] buf = new byte[2046];
			int len;
			while ((len = speech.read(buf)) > 0){
				output.write(buf,0,len);
			}

			OutputStream os = output;
			*/
			upfile = Payloads.create(speech);
			if (!(upfile == null)){
				conn.uploadFile("sample", "output["+translate+"].wav", upfile);
			}
			//os.flush();
			//os.close();
		} catch (Exception e){
			e.printStackTrace(System.err);
		}
		response.setContentType("text/html");
		response.setStatus(200); 
 		request.getRequestDispatcher("index.jsp").forward(request,response); 
	}
	
	
}