package Bean;


import java.util.Map; 
import java.util.List;
import java.text.ParseException; 
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser; 

import org.openstack4j.api.OSClient;
import org.openstack4j.model.storage.object.SwiftAccount;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.common.Payload;
import org.openstack4j.openstack.OSFactory;

public class Connector {
    
	private String language_username;
	private String language_password;
	
	private String t2s_username;
	private String t2s_password;
	
	private String auth_url;
	private String project;
	private String project_id;
	private String region;
	private String user_id;
	private String username;
	private String password;
	private String domain_id;
	private String domain_name;
	private Identifier domain_ident;
	private Identifier project_ident;
	private OSClient os;
	private SwiftAccount account;
	
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
				JSONObject os_service = null;

				for (Object key: vcap.keySet()){
					String keyStr = (String) key;
					if (keyStr.toLowerCase().contains("language_translation")){
						language_service = (JSONObject) ((JSONArray) vcap.get(keyStr)).get(0);
					}
					if (keyStr.toLowerCase().contains("text_to_speech")){
						t2s_service = (JSONObject) ((JSONArray) vcap.get(keyStr)).get(0);
					}
					if (keyStr.toLowerCase().contains("Object-Storage")){
						os_service = (JSONObject) ((JSONArray) vcap.get(keyStr)).get(0);
					}
				}
				
				//Language Translation
				if (language_service != null){
					JSONObject creds = (JSONObject) language_service.get("credentials");
					String username = (String) creds.get("username");
					String password = (String) creds.get("password");
					this.language_username = username;
					this.language_password = password;
				}
				
				//Text To Speech
				if (t2s_service != null){
					JSONObject creds = (JSONObject) t2s_service.get("credentials");
					String username = (String) creds.get("username");
					String password = (String) creds.get("password");
					this.t2s_username = username;
					this.t2s_password = password;
				}
				
				//Object Storage
				if (os_service != null){
					JSONObject creds = (JSONObject) os_service.get("credentials");
					this.auth_url = creds.get("auth_url").toString() + "/v3";
					this.project = creds.get("project").toString();
					this.project_id = creds.get("projectId").toString();
					this.region = creds.get("region").toString();
					this.user_id = creds.get("userId").toString();
					this.username = creds.get("username").toString();
					this.password = creds.get("password").toString();
					this.domain_id = creds.get("domainId").toString();
					this.domain_name = creds.get("domainName").toString();
					
					this.domain_ident = Identifier.byName(this.domain_name);
					this.project_ident = Identifier.byName(this.project);
					
					this.os = OSFactory.builderV3()
						.endpoint(this.auth_url)
						.credentials(this.user_id,this.password)
						.scopeToProject(this.project_ident,this.domain_ident)
						.authenticate();
					this.account = this.os.objectStorage().account().get();	
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
	
	public boolean createContainer(String cName){
		return this.os.objectStorage().containers().create(cName).isSuccess();
	}
	
	public boolean deleteContainer(String cName){
		return this.os.objectStorage().containers().delete(cName).isSuccess();
	}
	
	public String uploadFile(String cName, String fName, Payload payload){
		return this.os.objectStorage().objects().put(cName, fName, payload);
	}
	
	public boolean deleteFile(String cName, String fName){
		return this.os.objectStorage().objects().delete(cName,fName).isSuccess();
	}
	
	public SwiftObject getFile(String cName, String fName){
		return this.os.objectStorage().objects().get(cName, fName);
	}
	
	public SwiftAccount getAccount(){
		return this.account;
	}
	
	public List listAllObjects(String cName){
		return this.os.objectStorage().objects().list(cName);
	}
	
}

