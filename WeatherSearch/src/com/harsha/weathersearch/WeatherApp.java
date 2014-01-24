package com.harsha.weathersearch;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;


public class WeatherApp extends Activity implements OnClickListener {
	
	
	private final static String TAG = "DEBUG";
	private static final int IO_BUFFER_SIZE = 4 * 1024;
	public String URL = "http://cs-server.usc.edu:29227/examples/servlet/weathersearch?";
	public static final String testURL = "http://upload.wikimedia.org/wikipedia/commons/4/45/Terra_globe_icon.png";
	public static final String locPattern = "\\w+[\\.'-]?(\\s*\\w+)?(\\s*\\w+)?\\s*,\\s*\\w+\\s*(,\\s*\\w+)?";							//Regex: \\w+(\\s*\\w+)?(\\s*\\w+)?\\s*,\\s*\\w+\\s*(,\\s*\\w+)?
	public static final String imgSrc = "http://l.yimg.com/a/i/us/we/52/";
	
	TextView test;
	EditText loc;
	Button search;
	RadioGroup tempScale;
	String setTemp;
	String strLocation;
	RelativeLayout dynamicLayout;
	Drawable drawable;
	int type;
	Bundle params;
	
	
	//Var for weather template.
	TextView tvCity, tvRegion, tvCountry;
	TextView tvCondition, tvTemp;
	ImageView ivWeatherIcon;
	
	TextView tCell10, tCell11, tCell12, tCell13,
			 tCell20, tCell21, tCell22, tCell23,
			 tCell30, tCell31, tCell32, tCell33,
			 tCell40, tCell41, tCell42, tCell43,
			 tCell50, tCell51, tCell52, tCell53;
	
	TextView tvShareWeather, tvShareForecast;
	
	//Values retrieved.
	String woeid;
	String city, region, country;
	String wcondition, temp, img;
	String imgUrl;
	String link, feed;
	
	JSONObject property, properties;
	
	String fbCell12, fbCell22, fbCell32, fbCell42, fbCell52;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initialize();
		
		try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.harsha.weathersearch", 
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
        } catch (NameNotFoundException e) {
        		
        } catch (NoSuchAlgorithmException e) {
        		
        }
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	

	private void initialize(){
		loc = (EditText) findViewById(R.id.etLocation);
		search = (Button) findViewById(R.id.bSearch);
		tempScale = (RadioGroup) findViewById(R.id.rgTemp);
		dynamicLayout = (RelativeLayout) findViewById(R.id.rvDynamic);
		
		setTemp = "F";
		
		//Init viewID for Template variables.
		tvCity = (TextView) findViewById(R.id.tvCity);
		tvRegion = (TextView) findViewById(R.id.tvRegion);
		tvCondition = (TextView) findViewById(R.id.tvWeather);
		tvTemp = (TextView) findViewById(R.id.tvTemp);
		ivWeatherIcon = (ImageView) findViewById(R.id.ivWeatherIcon);
		
		
		tvShareWeather = (TextView) findViewById(R.id.tvShareWeather);
		tvShareForecast = (TextView) findViewById(R.id.tvShareForecast);
		
		params = new Bundle();
		
		//Forecast Table init.
		tCell10 = (TextView) findViewById(R.id.tCell10);
		tCell20 = (TextView) findViewById(R.id.tCell20);
		tCell30 = (TextView) findViewById(R.id.tCell30);
		tCell40 = (TextView) findViewById(R.id.tCell40);
		tCell50 = (TextView) findViewById(R.id.tCell50);
		
		tCell11 = (TextView) findViewById(R.id.tCell11);
		tCell21 = (TextView) findViewById(R.id.tCell21);
		tCell31 = (TextView) findViewById(R.id.tCell31);
		tCell41 = (TextView) findViewById(R.id.tCell41);
		tCell51 = (TextView) findViewById(R.id.tCell51);
		
		tCell12 = (TextView) findViewById(R.id.tCell12);
		tCell22 = (TextView) findViewById(R.id.tCell22);
		tCell32 = (TextView) findViewById(R.id.tCell32);
		tCell42 = (TextView) findViewById(R.id.tCell42);
		tCell52 = (TextView) findViewById(R.id.tCell52);
		
		tCell13 = (TextView) findViewById(R.id.tCell13);
		tCell23 = (TextView) findViewById(R.id.tCell23);
		tCell33 = (TextView) findViewById(R.id.tCell33);
		tCell43 = (TextView) findViewById(R.id.tCell43);
		tCell53 = (TextView) findViewById(R.id.tCell53);
		
		
		
		
		search.setOnClickListener(this);
		tvShareWeather.setOnClickListener(this);
		tvShareForecast.setOnClickListener(this);
		
		
		
		
	}
	
	public static boolean isNumeric(String str){  
	  try{  
	    int digits = Integer.parseInt(str);  
	  }  
	  catch(NumberFormatException nfe){  
	    return false;  
	  }  
	  return true;  
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.bSearch:
			Log.d(TAG, "Button Clickey");
			
			if(dynamicLayout.getVisibility() == View.VISIBLE){
				dynamicLayout.setVisibility(View.INVISIBLE); 
		}
			
			
			strLocation = loc.getText().toString();
			 //VALIDATIONS FOR INPUT. 
	        if(loc.getText().toString().length() == 0){
	        	Toast.makeText(getApplicationContext(), "Text is empty!!", Toast.LENGTH_LONG).show();
	        	break;
	        }
	        if(isNumeric(strLocation)){
	        	if(strLocation.length() == 5){
	        	type = 1;
	        	Log.i(TAG, "Zipcode Format");
	        	}else{
	        		Toast.makeText(getApplicationContext(), "Invalid Zipcode! Zipcode length must consist of 5 digits. Eg.: 90001.", Toast.LENGTH_LONG).show();
	        		break;
	        	}
	        }else{
	        	if(strLocation.matches(locPattern)){
	        	type = 2;
	        	}else{
	        		Toast.makeText(getApplicationContext(), "Location must be of the format city followed by state or country. Eg.: Los Angeles, CA", Toast.LENGTH_LONG).show();
	        		break;
	        	}
	        }  
	        
			String formURL = null;
			
			int checked = tempScale.getCheckedRadioButtonId();
			if(checked == R.id.rbC){
				setTemp = "C";
			}else{
				setTemp = "F";
			}
			
			try {
				formURL = URL + "location="+URLEncoder.encode(strLocation, "ISO-8859-1")+"&type="+type+"&u="+setTemp;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			//Toast.makeText(getApplicationContext(), "Search: "+ strLocation+" Temp: "+ setTemp, Toast.LENGTH_LONG).show();
			 GetJSON task = new GetJSON();
		        task.execute(new String[] { formURL });
			break;
			
			
		case R.id.tvShareWeather:
			//Toast.makeText(getApplicationContext(), "ShareWeather toast click works!", Toast.LENGTH_LONG).show();
			
			
			final Dialog dialog = new Dialog(WeatherApp.this);
			dialog.setContentView(R.layout.dialog_layout);
			dialog.setTitle("Post to Facebook");
			Button post = (Button) dialog.findViewById(R.id.bPost);
			Button cancel = (Button) dialog.findViewById(R.id.bCancel);
			post.setText("Post Current Weather");
			cancel.setOnClickListener(new OnClickListener()
			{
			    @Override
			    public void onClick(View v) 
			    {
			          dialog.cancel();
			    }   
			});
			post.setOnClickListener(new OnClickListener()
			{
			    @Override
			    public void onClick(View v) 
			    {
			          sessionCreator(1);
			          dialog.dismiss();
			    }   
			});
			dialog.show();
			
			
			break;
			
		case R.id.tvShareForecast:
			//Toast.makeText(getApplicationContext(), "ShareForecast toast click works!", Toast.LENGTH_LONG).show();
			final Dialog dialog2 = new Dialog(WeatherApp.this);
			dialog2.setContentView(R.layout.dialog_layout);
			dialog2.setTitle("Post to Facebook");
			Button post2 = (Button) dialog2.findViewById(R.id.bPost);
			Button cancel2 = (Button) dialog2.findViewById(R.id.bCancel);
			post2.setText("Post Weather Forecast");
			cancel2.setOnClickListener(new OnClickListener()
			{
			    @Override
			    public void onClick(View v) 
			    {
			          dialog2.cancel();
			    }   
			});
			post2.setOnClickListener(new OnClickListener()
			{
			    @Override
			    public void onClick(View v) 
			    {
			          sessionCreator(2);
			          dialog2.dismiss();
			    }   
			});
			dialog2.show();
			
			
			
		 
			break;
			
		}
		
	}
	
	private class GetJSON extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String output = null;
            for (String url : urls) {
            	Log.i("INFO",url);
                output = getOutputFromUrl(url);
                try {
					JSONObject jobj = new JSONObject(output);
					img = jobj.getJSONObject("weather").getString("img");
					imgUrl = imgSrc + img;
					Log.i(TAG, "imgUrl: "+ imgUrl);
					drawable = LoadImageFromWebOperations(imgUrl);
				} catch (JSONException e) {					
					e.printStackTrace();
				}
            }
            
            return output;
        }
 
        private String getOutputFromUrl(String url) {
            StringBuffer output = new StringBuffer("");
            try {
                InputStream stream = getHttpConnection(url);
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(stream));
                String s = "";
                while ((s = buffer.readLine()) != null)
                    output.append(s);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return output.toString();
        }
 
        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
 
            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();
 
                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        } 
 
        @Override
        protected void onPostExecute(String output) {
        	//Toast.makeText(getApplicationContext(), "RETURN: "+ output, Toast.LENGTH_LONG).show();
        	try {
				JSONObject jobj = new JSONObject(output);
				Log.i("INFO",jobj.toString());
				
				//Retrieve JSON values into variables.
				woeid = jobj.getJSONObject("weather").getString("woeid");
				Log.i(TAG, "woeid: "+ woeid);
				
				if(woeid.contentEquals("0")){
					Toast.makeText(getApplicationContext(), "No Weather Data Found!", Toast.LENGTH_LONG).show();
					
					return;
				}else{
												
				city = jobj.getJSONObject("weather").getJSONObject("location").getString("city");
				region = jobj.getJSONObject("weather").getJSONObject("location").getString("region");
				country = jobj.getJSONObject("weather").getJSONObject("location").getString("country");
				wcondition = jobj.getJSONObject("weather").getJSONObject("condition").getString("text");
				temp = jobj.getJSONObject("weather").getJSONObject("condition").getString("temp");
				link = jobj.getJSONObject("weather").getString("link");
				feed = jobj.getJSONObject("weather").getString("feed");
				
				String day = jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(3).getString("day");
				Log.i("INFO", day);
				
				//SetText to weather template.
				tvCity.setText(city);
				tvRegion.setText(region+","+country);
				tvCondition.setText(wcondition);
				tvTemp.setText(temp+"º"+setTemp);
				ivWeatherIcon.setImageDrawable(drawable);
				
				//Forecast: Day.
				tCell10.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(0).getString("day"));
				tCell20.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(1).getString("day"));
				tCell30.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(2).getString("day"));
				tCell40.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(3).getString("day"));
				tCell50.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(4).getString("day"));
				
				//Forecast: Condition.
				tCell11.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(0).getString("text"));
				tCell21.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(1).getString("text"));
				tCell31.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(2).getString("text"));
				tCell41.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(3).getString("text"));
				tCell51.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(4).getString("text"));
				
				//Forecast: High.
				tCell12.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(0).getString("high")+"º"+setTemp);
				tCell22.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(1).getString("high")+"º"+setTemp);
				tCell32.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(2).getString("high")+"º"+setTemp);
				tCell42.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(3).getString("high")+"º"+setTemp);
				tCell52.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(4).getString("high")+"º"+setTemp);
				
				//Forecast: Low.
				tCell13.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(0).getString("low")+"º"+setTemp);
				tCell23.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(1).getString("low")+"º"+setTemp);
				tCell33.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(2).getString("low")+"º"+setTemp);
				tCell43.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(3).getString("low")+"º"+setTemp);
				tCell53.setText(jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(4).getString("low")+"º"+setTemp);
				Log.i("INFO", city+country);
				
				//for fb format stuff.
				fbCell12 = jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(0).getString("high").toString();
				fbCell22 = jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(1).getString("high").toString();
				fbCell32 = jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(2).getString("high").toString();
				fbCell42 = jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(3).getString("high").toString();
				fbCell52 = jobj.getJSONObject("weather").getJSONArray("forecast").getJSONObject(4).getString("high").toString();
				
				}
				
				} catch (JSONException e) {
					Log.e("ERR", "Error in JSON Parsing");
				e.printStackTrace();
			}
        	renderDynamicLayout();
        }
    }
	

	public Drawable LoadImageFromWebOperations(String url) {
	    try {
	        InputStream is = (InputStream) new URL(url).getContent();
	        Drawable d = Drawable.createFromStream(is, url);
	        return d;
	    } catch (Exception e) {
	        return null;
	    }
	}
	
	
	void renderDynamicLayout(){
		
		if(dynamicLayout.getVisibility() == View.INVISIBLE){
				dynamicLayout.setVisibility(View.VISIBLE); 
		}
	}
	
	public void publishFeedWeather(){
		
		try {
			 property = new JSONObject(); 
			 property.put("text", "here"); 
			 property.put("href", feed); 
			 properties = new JSONObject(); 
			 properties.put("Look at details", property);			 
			 
		} catch (JSONException e) {
		
			e.printStackTrace();
		}
		
		 params.putString("name", city+", "+region+", "+country);
		    params.putString("caption", "The current condition for "+city+" is "+wcondition);
		    params.putString("description", "Temperature is "+temp+"º"+setTemp);
		    params.putString("link", link);
		    params.putString("picture", imgUrl);
		    params.putString("properties", properties.toString());
		    publishFeedDialog();
	}
	
	public void publishFeedForecast(){
		
		JSONObject propobj = null;
		String description = tCell10.getText().toString()+": "+tCell11.getText().toString()+" "+fbCell12+"/"+tCell13.getText().toString()+";";
		description += tCell20.getText().toString()+": "+tCell21.getText().toString()+" "+fbCell22+"/"+tCell23.getText().toString()+";";
		description += tCell30.getText().toString()+": "+tCell31.getText().toString()+" "+fbCell32+"/"+tCell33.getText().toString()+";";
		description += tCell40.getText().toString()+": "+tCell41.getText().toString()+" "+fbCell42+"/"+tCell43.getText().toString()+";";
		description += tCell50.getText().toString()+": "+tCell51.getText().toString()+" "+fbCell52+"/"+tCell53.getText().toString()+";";
		Log.i(TAG, "Dialog Forecast Feed: "+ description);
		
		try {
			 property = new JSONObject(); 
			 property.put("text", "here"); 
			 property.put("href", feed); 
			 properties = new JSONObject(); 
			 properties.put("Look at details", property);			 
			 
		} catch (JSONException e) {
		
			e.printStackTrace();
		}
		
		 params.putString("name", city+", "+region+", "+country);
		    params.putString("caption", "Weather forecast for "+city);
		    params.putString("description", description);
		    params.putString("link", link);
		    params.putString("picture", "http://3.s3.envato.com/files/30465562/2250.jpg");
		    params.putString("properties", properties.toString());
		    publishFeedDialog();
	}
	
	
	private void publishFeedDialog() {
	    
	   // params.putString("properties", "{\"text\": \"Look at details here\", \"href\": \""+feed+"\"}");

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(WeatherApp.this,
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(WeatherApp.this,
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(WeatherApp.this.getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(WeatherApp.this.getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(WeatherApp.this.getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}

void sessionCreator(final int id){
	Session.openActiveSession(this, true, new Session.StatusCallback() {

	      // callback when session changes state
	      @Override
	      public void call(Session session, SessionState state, Exception exception) {
	        if (session.isOpened()) {

	          // make request to the /me API
	          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

	            // callback after Graph API response with user object
	            @Override
	            public void onCompleted(GraphUser user, Response response) {
	              if (user != null) {
	                //TextView welcome = (TextView) findViewById(R.id.welcome);
	                //welcome.setText("Hello " + user.getName() + "!");
	            	  //Toast.makeText(getApplicationContext(), "Hello, "+ user.getName(), Toast.LENGTH_LONG).show();
	            	  if(id == 1){
	            		  publishFeedWeather();
	            	  }else{
	            	  publishFeedForecast();
	            	  }
	              }
	            }
	          });
	        }
	      }
	    });
	
	}
}
