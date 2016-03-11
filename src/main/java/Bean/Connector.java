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
	
	private String auth_url = ""; 
    private String project = ""; 
    private String projectId = ""; 
    private String region = ""; 
    private String userId = ""; 
    private String username = ""; 
    private String password = ""; 
    private String domainId = ""; 
    private String domainName = ""; 
    private Identifier domainIdent = null; 
    private Identifier projectIdent = null; 
    private OSClient os = null; 
 	private SwiftAccount account = null; 

	
	public Connector(){
		set_credentials();
		object_storage_connection();
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
	
	private void object_storage_connection(){
		try{
			 String envApp = System.getenv("VCAP_APPLICATION"); 
             String envServices = System.getenv("VCAP_SERVICES"); 
              
             JSONParser parser = new JSONParser(); 
             Object obj = parser.parse(envServices); 
             JSONObject jsonObject = (JSONObject) obj; 
             JSONArray vcapArray = (JSONArray) jsonObject.get("Object-Storage"); 
             JSONObject vcap = (JSONObject) vcapArray.get(0); 
             JSONObject credentials = (JSONObject) vcap.get("credentials"); 
             auth_url = credentials.get("auth_url").toString() + "/v3"; 
             project = credentials.get("project").toString(); 
             projectId = credentials.get("projectId").toString(); 
             region = credentials.get("region").toString(); 
             userId = credentials.get("userId").toString(); 
             username = credentials.get("username").toString(); 
             password = credentials.get("password").toString(); 
             domainId = credentials.get("domainId").toString(); 
             domainName = credentials.get("domainName").toString(); 
              
             Identifier domainIdent = Identifier.byName(domainName); 
             Identifier projectIdent = Identifier.byName(project); 
              
             os = OSFactory.builderV3() 
                 .endpoint(auth_url) 
                 .credentials(userId, password) 
                 .scopeToProject(projectIdent, domainIdent) 
                 .authenticate(); 
              
             account = os.objectStorage().account().get(); 

		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	}
	
	public boolean createContainer(String cName){
		return os.objectStorage().containers().create(cName).isSuccess();
	}
	
	public boolean deleteContainer(String cName) { 
        return os.objectStorage().containers().delete(cName).isSuccess(); 
    } 

	public String uploadFile(String cName, String fName, Payload payload) { 
        return os.objectStorage().objects().put(cName, fName, payload); 
    } 

	public boolean deleteFile(String cName, String fName) { 
		return os.objectStorage().objects().delete(cName, fName).isSuccess(); 
    } 

	  
    public SwiftObject getFile(String cName, String fName) { 
		return os.objectStorage().objects().get(cName, fName); 
    } 
 	 
 	public SwiftAccount getAccount() { 
        account = os.objectStorage().account().get(); 
        return account; 
    } 
	
	public List listAllObjects(String containerName) { 
         return os.objectStorage().objects().list(containerName); 
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
