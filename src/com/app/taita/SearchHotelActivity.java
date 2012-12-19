package com.app.taita;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.app.taita.lib.AlertDialogManager;
import com.app.taita.lib.JSONParser;

public class SearchHotelActivity extends ListActivity {

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();

	ArrayList<HashMap<String, String>> hotelsList;

	// albums JSONArray
	JSONArray hotels = null;

	// albums JSON url
	private static final String URL_HOTELS = "http://192.168.74.1/lab/egerton/get_all_hotels.php";

	// ALL JSON node names
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_hotel);
		

		// Hashmap for ListView
		hotelsList = new ArrayList<HashMap<String, String>>();

		// Loading Hotels JSON in Background Thread
		new LoadHotels().execute();
		
		// get listview
		ListView lv = getListView();
		//ListView lv = (ListView) findViewById(R.id.list_view);
		
		/**
		 * Listview item click listener
		 * HotelDetailsActivity will be lauched by passing hotel id
		 * */
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				// on selecting a single hotel
				// HotelDetailsActivity will be launched to show details of the hotel
				Intent i = new Intent(getApplicationContext(), HotelDetailsActivity.class);
				
				// send hotel id to HotelDetails activity to get details of that hotel
				String hotel_id = ((TextView) view.findViewById(R.id.hotel_id)).getText().toString();
				i.putExtra("album_id", hotel_id);				
				
				startActivity(i);
			}
		});		
	}

	/**
	 * Background Async Task to Load all Hotels via http request
	 * */
	class LoadHotels extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SearchHotelActivity.this);
			pDialog.setMessage("Retreving Hotel Names ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Hotels JSON
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// getting JSON string from URL
			String json = jsonParser.makeHttpRequest(URL_HOTELS, "GET",
					params);

			// Check your log cat for JSON reponse
			Log.d("Hotels JSON: ", "> " + json);

			try {				
				hotels = new JSONArray(json);
				
				if (hotels != null) {
					// looping through all hotel names
					for (int i = 0; i < hotels.length(); i++) {
						JSONObject c = hotels.getJSONObject(i);

						// Storing each json item values in variable
						String id = c.getString(TAG_ID);
						String name = c.getString(TAG_NAME);
						

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_ID, id);
						map.put(TAG_NAME, name);
						

						// adding HashList to ArrayList
						hotelsList.add(map);
					}
				}else{
					Log.d("Hotels: ", "null");
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all albums
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							SearchHotelActivity.this, hotelsList,
							R.layout.hotel_list_item_a, new String[] { TAG_ID,
									TAG_NAME,}, new int[] {
									R.id.hotel_id, R.id.hotel_name });
					
					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}
}