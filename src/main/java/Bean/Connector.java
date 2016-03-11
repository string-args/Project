package Bean;


import java.util.Map; 
import java.util.List;
import java.text.ParseException; 
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser; 


public class Connector {
    
	private String tradeoff_username;
	private String tradeoff_password;

	
	public Connector(){
		set_credentials();
	}
	
	private void set_credentials(){
		Map<String,String> env = System.getenv();
		
		if (env.containsKey("VCAP_SERVICES")){
			try{
				JSONParser parser = new JSONParser();
				JSONObject vcap = (JSONObject) parser.parse(env.get("VCAP_SERVICES"));
				JSONObject tradeoff_service = null;

				for (Object key: vcap.keySet()){
					String keyStr = (String) key;
					if (keyStr.toLowerCase().contains("tradeoff_analytics")){
						tradeoff_service = (JSONObject) ((JSONArray) vcap.get(keyStr)).get(0);
						break;
					}
				}
				
				if (tradeoff_service != null){
					JSONObject creds = (JSONObject) tradeoff_service.get("credentials");
					String username = (String) creds.get("username");
					String password = (String) creds.get("password");
					this.tradeoff_username = username;
					this.tradeoff_password = password;
				}

			} catch(Exception e){
				e.printStackTrace(System.err);
			}
		}
	}
	
	public String get_tradeoff_username(){
		return this.tradeoff_username;
	}
	
	public String get_tradeoff_password(){
		return this.tradeoff_username;
	}
}

