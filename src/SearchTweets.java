/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class SearchTweets {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("java twitter4j.examples.search.SearchTweets [query]");
            System.exit(-1);
        }
        Twitter twitter = new TwitterFactory().getInstance();
        String[] languages = {"en", "fr", "es", "hi", "th"};
        double[][] cities = {{40.730610,-73.935242}, {48.8566, 2.3522}, {19.432608, -99.133209}, {28.7041, 77.1025}, {13.7563, 100.5018}};
        String[] cityNames = {"nyc", "paris", "mexico city", "delhi", "bangkok"};
        String[][] topics = {{"crime" , "Murder", "Robbery", "Extortion", "Burglary", "Smuggling", "Pickpocketing"},
        						{"environment", "air quality", "floods", "droughts", "dust storms", "smog" , "pollution"},
        						{"politics" , "government", "governance" , "civics", "trump", "kejriwal", "Vejjajiva", "Panyarachun", "dalberto Madero"},
        						{"social unrest", "strikes", "protests", "riots", "turmoil", "violence", "agitation"},
        						{"infra", "roads", "power", "water", "sanitation"}};
      for(int cityName = 0 ; cityName < 4; cityName++)  {
        for (int currTopic = 1; currTopic < 2; currTopic++) {
			for (int currSubTopic = 0; currSubTopic < topics[currTopic].length; currSubTopic++) {
		        try {
		        	Query query = new Query(topics[currTopic][currSubTopic]+" -filter:retweets");
					GeoLocation geoloc = new GeoLocation(cities[cityName][0],cities[cityName][1]);
					query.setCount(100);
					query.setLang(languages[0]);
					query.setGeoCode(geoloc, 50, Query.MILES);
					QueryResult result;
					 String fileName = cityNames[cityName]+ "_" + topics[currTopic][0]+ "_"+ languages[0] + ".json";
		            do {
		                result = twitter.search(query);
		                List<Status> tweets = result.getTweets();
		                for (Status tweet : tweets) {
		                	String rawJSON = TwitterObjectFactory.getRawJSON(tweet);
		                	//String myJSONString = "{'test': '100.00'}";
		                	JsonObject jobj = new Gson().fromJson(rawJSON, JsonObject.class);
							try {
			                	DateFormat twitterDateFormat = new SimpleDateFormat(
			            	            "EEE MMM dd HH:mm:ss ZZZZ yyyy");
			                	Date tweetDate;
								tweetDate = twitterDateFormat.parse(jobj.get("created_at").getAsString());
			                	DateFormat solrdateFormat = new SimpleDateFormat(
			            	            "yyyy-MM-dd'T'HH:00:00'Z'");
			            		 solrdateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			                	jobj.addProperty("created_at", solrdateFormat.format(tweetDate));
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} ;
							jobj.addProperty("topic",topics[currTopic][0]);
		                	jobj.addProperty("city",cityNames[cityName]);
		                	jobj.addProperty("tweet_loc",cities[cityName][0] + "," + cities[cityName][1]);
		                	String jsonInString = new Gson().toJson(jobj);
		                	try {
								storeJSON(jsonInString, fileName);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
		                }
		/*    	        try {
						//	Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
		            } while ((query = result.nextQuery()) != null);
		         } catch (TwitterException te) {
		            te.printStackTrace();
		            System.out.println("Failed to search tweets: " + te.getMessage());
		            System.exit(-1);
		        }
			}
		}
    }
    }
    private static void storeJSON(String rawJSON, String fileName) throws IOException {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(fileName, true);
            osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
           bw.write(rawJSON);
            bw.newLine();
           // bw.write(rawJSON);
            bw.flush();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ignore) {
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException ignore) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
}


