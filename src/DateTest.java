import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;


public class DateTest {

	public static void main(String[] args) {/*
		// TODO Auto-generated method stub
		DateFormat twitterDateFormat = new SimpleDateFormat(
	            "EEE MMM dd HH:mm:ss ZZZZ yyyy");
		DateFormat solrdateFormat;
		Date date ;
	try {
		 date = twitterDateFormat.parse("Sat Sep 15 18:26:36 +0000 2018");
		// System.out.println(date.getMinutes());
		 solrdateFormat = new SimpleDateFormat(
	            "yyyy-MM-dd'T'HH:00:00'Z'");
		 solrdateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		 //"2018-09-13T13:00:00Z",527,
		 System.out.println(solrdateFormat.format(date));
		
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	//Fri Sep 14 07:45:27 +0000 2018
	*/
		
    	//String rawJSON = TwitterObjectFactory.getRawJSON(tweet);
    	//String myJSONString = "{'test': '100.00'}";
  /*      InputStream is =  whatever ;
        	       Reader r = new InputStreamReader(is, "UTF-8");
        	       Gson gson = new GsonBuilder().create();*/
		String content = "";
		try {
			content = new Scanner(new File("nyc_droughts_fr.json")).useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(content);
        JsonStreamParser p = new JsonStreamParser(content);

        	       while (p.hasNext()) {
        	          JsonElement rawJSON = p.next();
        	          if (rawJSON.isJsonObject()) {
        	        	  JsonObject jobj = new Gson().fromJson(rawJSON, JsonObject.class);
        	      		try {
        	      			System.out.println(jobj.get("full_text").getAsString());
        	              	//jobj.addProperty("tweet_emoticons", "");
        	      		} catch (Exception e1) {
        	      			// TODO Auto-generated catch block
        	      			e1.printStackTrace();
        	      		} ;
        	              /* do something useful with JSON object .. */
        	          }
        	          /* handle other JSON data structures */
        	       }
    	


	
	
	}
	
	public static String getEmojis(String sentence)
	{
		 String regexPattern = "[\uD83C-\uDBFF\uDC00-\uDFFF]+";
	        byte[] utf8;
			try {
				utf8 = sentence.getBytes("UTF-8");
				 String string1;
				string1 = new String(utf8, "UTF-8");
		        Pattern pattern = Pattern.compile(regexPattern);
		        Matcher matcher = pattern.matcher(string1);
		        List<String> matchList = new ArrayList<String>();

		        while (matcher.find()) {
		            matchList.add(matcher.group());
		        }
		        StringBuffer output = new StringBuffer();
		        for(int i=0;i<matchList.size();i++){
		        	output.append(matchList.get(i));
		            System.out.println(i+":"+matchList.get(i));

		        }
		        return output.toString();
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
	}

}
