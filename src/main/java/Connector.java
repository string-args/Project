
import java.util.Map; 
import java.text.ParseException; 
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser; 

public class Connector {
    
	private String language_username;
	private String language_password;
	
	private String t2s_username;
	private String t2s_password;
	
	public Connector(){
		set_credentials();
	}
	
	private void set_credentials(){
		Map<String,String> env = System.getenv();
		
		if (env.containsKey("VCAP_SERVICES")){
			try{
				JSONParser parser = new JSONParser();
				JSONObject vcap = (JSONObject) parser.parse(env.get("VCAP_SERVICES"));
				JSONObject language_service = null;
				JSONObject t2s_service = null;
				
				for (Object key: vcap.keySet()){
					String keyStr = (String) key;
					if (keyStr.toLowerCase().contains("language_translation")){
						language_service = (JSONObject) ((JSONArray) vcap.get(keyStr)).get(0);
					}
					if (keyStr.toLowerCase().contains("text_to_speech")){
						t2s_service = (JSONObject) ((JSONArray) vcap.get(keyStr)).get(0);
					}
				}
				
				if (language_service != null){
					JSONObject creds = (JSONObject) language_service.get("credentials");
					String username = (String) creds.get("username");
					String password = (String) creds.get("password");
					this.language_username = username;
					this.language_password = password;
				}
				if (t2s_service != null){
					JSONObject creds = (JSONObject) t2s_service.get("credentials");
					String username = (String) creds.get("username");
					String password = (String) creds.get("password");
					this.t2s_username = username;
					this.t2s_password = password;
				}
			} catch(Exception e){
				e.printStackTrace(System.err);
			}
		}
	}
	
	public String get_language_username(){
		return this.language_username;
	}
	public String get_language_password(){
		return this.language_password;
	}
	public String get_t2s_username(){
		return this.t2s_username;
	}
	public String get_t2s_password(){
		return this.t2s_password;
	}
}
