/**
 * @Author Coders4Africa
 * Demonstrates the client server architecture where data is fetched from a remote database (MySQL)
 * and rendered to the device.
 * The server side language is php.
 * @References  http://blog.sptechnolab.com/2011/02/10/android/android-connecting-to-mysql-using-php
 * */
package com.app.taita;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.*;

import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
//import org.apache.http.*;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.client.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.taita.lib.UserFunctions;
//import org.json.*;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ListCitiesActivity extends ListActivity {
	//variables
	JSONArray jArray;
	String result=null;
	InputStream is=null;
	StringBuilder sb=null;
	int ct_id;
    String[] ct_name=null;
    Button btnLogout;
	Button btnDash;
	UserFunctions userFunctions;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_cities);
        btnLogout = (Button) findViewById(R.id.btnLogout);
    	btnDash = (Button) findViewById(R.id.btnBack);
        
        ArrayList<NameValuePair>nameValuePairs=new ArrayList<NameValuePair> ();
        //attempt a http post:
        try{
        	HttpClient httpClient=new DefaultHttpClient();
        	HttpPost httpPost=new HttpPost("http://10.0.2.2/lab/android/city.php");
        	HttpResponse response=httpClient.execute(httpPost);
        	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	HttpEntity entity=response.getEntity();
        	is=entity.getContent();
        }catch (Exception e){
        	//Log.e("log_tag","Error in http connnection: "+e.toString());
        	e.printStackTrace();
        }
        
        //convert response to string
        try{
        	BufferedReader reader=new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
        	sb=new StringBuilder();
        	sb.append(reader.readLine()+"\n");
        	
        	//String line="0";
        	while((reader.readLine() !=null)){
        		sb.append(reader.readLine()+"\n");
        	}
        	is.close();
        	result=sb.toString();
        }catch(Exception e){
        	Log.e("Log_Tag","Error converting result "+e.toString());
        }
        
        //paring the data
        try{
        	jArray=new JSONArray(result);
        	JSONObject json_data=null;
        	ct_name=new String[jArray.length()];
        	for(int i=0;i<jArray.length();i++){
        		json_data=jArray.getJSONObject(i);
        		ct_id=json_data.getInt("cityId");
        		ct_name[i]=json_data.getString("cityName").toString();
        	}
        }catch(JSONException el){
        	Toast.makeText(getBaseContext(), "No City Found", Toast.LENGTH_LONG).show();
        	//el.printStackTrace();
        }catch(ParseException el){
        	el.printStackTrace();
        }
        
        //render the data to the device
        setListAdapter(new ArrayAdapter<String>(this,R.layout.city_list_item,ct_name));
        
        ListView cityListView=getListView();
        cityListView.setTextFilterEnabled(true);
        cityListView.setOnItemClickListener(new OnItemClickListener(){
        	public void onItemClick(AdapterView<?> parent, View view, int pos,long id){
        		//do something on list view item click
        		Toast.makeText(getApplicationContext(),"You selected '"+ct_name[pos]+"' city",Toast.LENGTH_SHORT).show();
        	}
        });
        
        //register button listeners
      //register listeners for the 2 buttons
    	btnLogout.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				userFunctions = new UserFunctions();
				userFunctions.logoutUser(getApplicationContext());
				Intent login = new Intent(getApplicationContext(), LoginActivity.class);
	        	login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	startActivity(login);
	        	// Closing dashboard screen
	        	finish();
			}
		});
    	
            btnDash.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent viewCity = new Intent(getApplicationContext(), DashboardActivity.class);
				viewCity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	startActivity(viewCity);
	        	// Closing dashboard screen, and move to the dashboard
	        	finish();
			}
		});
    }
}