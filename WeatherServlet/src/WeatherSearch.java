import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;


public class WeatherSearch extends HttpServlet {
	
	
	 public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
throws ServletException, IOException {

request.setCharacterEncoding("UTF-8");
//response.setContentType("text/html");
  response.setContentType("application/json");
String loc = request.getParameter("location");
String type = request.getParameter("type");
String u = request.getParameter("u");

String urlString = null;
String productionURL = null;
if(u.equals("F")){
		urlString  = "http://cs-server.usc.edu:29223/yahoophp.php?location="+URLEncoder.encode(loc, "ISO-8859-1")+"&type="+type+"&u=f";
		productionURL = "http://default-environment-cxm9em94vm.elasticbeanstalk.com/?location="+URLEncoder.encode(loc, "ISO-8859-1")+"&type="+type+"&u=f";
}else{ 
		urlString  = "http://cs-server.usc.edu:29223/yahoophp.php?location="+URLEncoder.encode(loc, "ISO-8859-1")+"&type="+type+"&u=c";
		productionURL = "http://default-environment-cxm9em94vm.elasticbeanstalk.com/?location="+URLEncoder.encode(loc, "ISO-8859-1")+"&type="+type+"&u=c";
}
URL url = new URL(productionURL);

URLConnection yc = url.openConnection();
yc.setAllowUserInteraction(false);
	 BufferedReader in = new BufferedReader(new InputStreamReader( yc.getInputStream())); 
	 String read_xml_from_php; 
	 String xml="";
	 while ((read_xml_from_php = in.readLine()) != null) { 
		 xml=xml+read_xml_from_php; 
		 }
	 in.close();
PrintWriter out = response.getWriter();
// Use "out" to send content to browser

try {
	if(xml != null){
    JSONObject xmlJSONObj = XML.toJSONObject(xml);
    String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
    System.out.println(jsonPrettyPrintString);
    out.println(xmlJSONObj);
    
	}
} catch (JSONException je) {
	out.println("ERROR in Parsing JSON!");
    System.out.println(je.toString());
    je.printStackTrace();
}
	
}
}
