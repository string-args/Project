

import Bean.Connector;
import javax.servlet.ServletException; 
import javax.servlet.annotation.WebServlet; 
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse; 
import javax.servlet.http.HttpSession; 

import java.util.List;

import java.io.IOException; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.util.List; 


import org.apache.commons.io.IOUtils; 
import org.openstack4j.model.storage.object.SwiftObject; 


@WebServlet(name = "Download", urlPatterns = {"/Download"})
public class Download extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Connector conn = new Connector();
		SwiftObject swiftObj; 
        InputStream inStr = null; 
        OutputStream outStr = null; 
 
        List<? extends SwiftObject> objectlist = conn.listAllObjects("sample"); 
        String filename = request.getParameter("filename"); 

		for (int i = 0; i < objectlist.size(); i++){
			if (filename.equals(objectlist.get(i).getName())) { 
                     swiftObj = conn.getFile("sample", filename); 
                     response.setContentType(swiftObj.getMimeType()); 
                     response.setHeader("Content-Disposition", "attachment; filename=\""+filename+"\""); 
                     inStr = swiftObj.download().getInputStream(); 
                     outStr = response.getOutputStream(); 

                     IOUtils.copy(inStr, outStr); 
                     inStr.close(); 
                     outStr.flush(); 
                     outStr.close(); 
            } 

		}
	
	}

}