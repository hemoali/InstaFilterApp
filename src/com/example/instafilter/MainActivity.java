package com.example.instafilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.R.string;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class MainActivity extends Activity implements OnClickListener {

	public static final String TAG = "InstaFilter";
	private EditText cityET;
	private ImageView searchIV;
	private ListView citiesListView, albumsList, historyList;
	LinearLayout albums_tabLL, history_tab;
	TabHost tabHost;
	static Boolean isSDPresent = android.os.Environment // Check if SD card is
														// mounted
			.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Define Vars */

		cityET = (EditText) findViewById(R.id.cityNameET);
		searchIV = (ImageView) findViewById(R.id.searchIV);

		citiesListView = (ListView) findViewById(R.id.citiesListVIew);

		albumsList = (ListView) findViewById(R.id.albumsList);
		albums_tabLL = (LinearLayout) findViewById(R.id.albums_tab);

		history_tab = (LinearLayout) findViewById(R.id.history_tab);

		historyList = (ListView) findViewById(R.id.historyList);

		/* Add History */
		String his1 = "", his2 = "", his3 = "", his4 = "", his5 = "";

		SharedPreferences sharedPreferences = getApplicationContext()
				.getSharedPreferences("com.example.instafilter", 0);
		List<String> his_list = new ArrayList<String>();

		Map<String, ?> keys = sharedPreferences.getAll();

		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			Log.d("map values", entry.getKey() + ": "
					+ entry.getValue().toString());
			if (!his_list.contains(entry.getValue().toString())) {
				
				his_list.add(entry.getValue().toString());

			}
		}

		/*if (sharedPreferences.contains("his1")) {
			his1 = sharedPreferences.getString("his1", "");
			if (!his1.equals("") && his1.length() > 0) {
				his_list.add(his1);
			}
		} else if (sharedPreferences.contains("his2")) {
			his2 = sharedPreferences.getString("his2", "");
			if (!his2.equals("") && his2.length() > 0) {
				his_list.add(his2);
			}
		} else if (sharedPreferences.contains("his3")) {
			his3 = sharedPreferences.getString("his3", "");
			if (!his3.equals("") && his3.length() > 0) {
				his_list.add(his3);
			}
		} else if (sharedPreferences.contains("his4")) {
			his4 = sharedPreferences.getString("his4", "");
			if (!his4.equals("") && his4.length() > 0) {
				his_list.add(his4);
			}
		} else if (sharedPreferences.contains("his5")) {
			his5 = sharedPreferences.getString("his5", "");
			if (!his5.equals("") && his5.length() > 0) {
				his_list.add(his5);
			}
		}
*/
		ArrayAdapter<String> historyAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				his_list.toArray(new String[his_list.size()]));

		historyList.setAdapter(historyAdapter);
		
		historyList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				tabHost.setCurrentTab(0);
				cityET.setText(((TextView)arg1).getText().toString());
				
			}
		
		});
		/* Add Albums */

		File file = new File(Environment.getExternalStorageDirectory()
				+ "/InstaFilter/");
		final String[] albumNames = file.list();

		ArrayAdapter<String> albumsAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, albumNames);

		albumsList.setAdapter(albumsAdapter);

		albumsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				

			}

		});
		/* Add Cities */

		new TheTask().execute("");

		final String city_names[] = { "Jeddah", "Riyadh", "Madinah", "Makkah",
				"Dammam", "Al-Ahsa", "Ta'if", "Khamis Mushait", "Buraidah",
				"Khobar", "Tabuk", "Ha'il", "Hafar Al-Batin", "Jubail",
				"Al-Kharj", "Qatif", "Abha", "Najran", "Yanbu", "Al Qunfudhah" };

		final String empty_city_names[] = {};

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, city_names);

		citiesListView.setAdapter(adapter);

		/* Define Tabs */
		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();

		TabSpec spec1 = tabHost.newTabSpec("Tab 1");
		spec1.setContent(R.id.cities_tab);
		spec1.setIndicator("Cities");

		TabSpec spec2 = tabHost.newTabSpec("Tab 2");
		spec2.setIndicator("Albums");
		spec2.setContent(R.id.albums_tab);

		TabSpec spec3 = tabHost.newTabSpec("Tab 3");
		spec3.setIndicator("History");
		spec3.setContent(R.id.history_tab);

		// onItemClickListener
		citiesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				SharedPreferences myPrefs = getApplicationContext()
						.getSharedPreferences("com.example.instafilter", 0);
				SharedPreferences.Editor prefsEditor = myPrefs.edit();
				String txt = cityET.getText().toString().trim();
				if (!myPrefs.contains("his1")) {
					if (!txt.equals("") && txt != null) {
						prefsEditor.putString("his1", txt);

					}
				} else if (!myPrefs.contains("his2")) {
					if (!txt.equals("") && txt != null) {
						prefsEditor.putString("his2", txt);

					}
				} else if (!myPrefs.contains("his3")) {
					if (!txt.equals("") && txt != null) {
						prefsEditor.putString("his3", txt);

					}
				} else if (!myPrefs.contains("his4")) {
					if (!txt.equals("") && txt != null) {
						prefsEditor.putString("his4", txt);

					}
				} else if (!myPrefs.contains("his5")) {
					if (!txt.equals("") && txt != null) {
						prefsEditor.putString("his5", txt);

					}
				}

				prefsEditor.commit();

				Intent cityImagesIntent = new Intent(MainActivity.this,
						CityImages.class);

				cityImagesIntent.putExtra("cityName", citiesListView
						.getItemAtPosition(arg2).toString());
				startActivity(cityImagesIntent);
				finish();

			}

		});

		albumsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Intent albumIntent = new Intent(MainActivity.this,
						AlbumImages.class);

				albumIntent.putExtra("albumName",
						albumsList.getItemAtPosition(arg2).toString());
				startActivity(albumIntent);
				finish();

			}

		});

		/* OnCLick */
		searchIV.setOnClickListener(this);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);

		/* textChangeListener */
		cityET.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.d("asd", "asfas");

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				ArrayAdapter<String> empty_adapter = new ArrayAdapter<String>(
						MainActivity.this, android.R.layout.simple_list_item_1,
						empty_city_names);
				Log.d(TAG, s.toString());
				if (s.toString().trim() != null && s.toString().trim() != "") {
					if (tabHost.getCurrentTab() == 0) {
						citiesListView.setAdapter(empty_adapter);

						ArrayList<String> newArrayString = new ArrayList<String>();

						ArrayAdapter<String> new_adapter = new ArrayAdapter<String>(
								MainActivity.this,
								android.R.layout.simple_list_item_1,
								newArrayString);

						for (String city_name : city_names) {
							if (city_name.contains(s.toString())) {
								Log.d(TAG, city_name);
								newArrayString.add(city_name);
							}
						}
						citiesListView.setAdapter(new_adapter);
						((BaseAdapter) citiesListView.getAdapter())
								.notifyDataSetChanged();
					} else if (tabHost.getCurrentTab() == 1) {
						albumsList.setAdapter(empty_adapter);

						ArrayList<String> newArrayString = new ArrayList<String>();

						ArrayAdapter<String> new_adapter = new ArrayAdapter<String>(
								MainActivity.this,
								android.R.layout.simple_list_item_1,
								newArrayString);

						for (String album_name : albumNames) {
							if (album_name.contains(s.toString())) {
								Log.d(TAG, album_name);
								newArrayString.add(album_name);
							}
						}
						albumsList.setAdapter(new_adapter);
						((BaseAdapter) albumsList.getAdapter())
								.notifyDataSetChanged();
					}

				}

			}
		});
		/* Image Folder */

		if (isSDPresent) {
			Log.d(TAG, "sd");
			// Check Dir

			File InstaFilterDir = new File(
					Environment.getExternalStorageDirectory() + "/InstaFilter/");

			if (!InstaFilterDir.isDirectory()) {
				Log.d(TAG, "3");
				InstaFilterDir.mkdirs();
				return;
			} else { // Folder exists
				Log.d(TAG, "4");
				// Check if folder has files

				File[] InstaFilterDirFiles = InstaFilterDir.listFiles();

				if (InstaFilterDirFiles.length == 0
						|| InstaFilterDirFiles == null) {
					TextView noFilesTV = new TextView(MainActivity.this);
					noFilesTV.setTextSize(18f);
					noFilesTV.setText("No Albums");
					albums_tabLL.setGravity(Gravity.CENTER_HORIZONTAL
							| Gravity.CENTER_VERTICAL);
					albums_tabLL.addView(noFilesTV);

				} else { // There are some photos

				}

			}

		} else {
			Toast.makeText(MainActivity.this, "Please, Check your SD Card.",
					Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.searchIV:
			Toast.makeText(MainActivity.this, "Type to Search",
					Toast.LENGTH_LONG).show();
			break;

		}

	}

	class TheTask extends AsyncTask<String, String, String> {

		private final String NAMESPACE = "http://tempuri.org/";
		private final String URL = "http://holomws.elm.sa/WS/CitiesWS.svc?wsdl";
		private final String SOAP_ACTION = "http://tempuri.org/ICitiesWS/GetAllCountries";
		private final String METHOD_NAME = "GetAllCountries";

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Log.d("Web service result", result); // No enough time :)
			// Toast.makeText(MainActivity.this, result,
			// Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.d(TAG, "The Task");
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
				// Property which holds input parameters
				PropertyInfo PI2 = new PropertyInfo();
				// Set Name
				PI2.setName("username");
				// Set Value
				PI2.setValue("i.radwan1996@gmail.com");
				// Set dataType
				PI2.setType(string.class);
				// Add the property to request object

				PropertyInfo PI3 = new PropertyInfo();
				// Set Name
				PI3.setName("password");
				// Set Value
				PI3.setValue(20061996);
				// Set dataType
				PI3.setType(int.class);
				// Add the property to request object

				request.addProperty(PI2);
				request.addProperty(PI3);
				// Create envelope
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.dotNet = true;

				// Set output SOAP object
				envelope.setOutputSoapObject(request);
				// Create HTTP call object

				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

				// Invole web service
				androidHttpTransport.call(SOAP_ACTION, envelope);
				// Get the response
				SoapObject response = (SoapObject) envelope.getResponse();
				// Assign it to fahren static variable
				return response.toString();
			} catch (Exception e) {
				// Google maps api
			}

			return null;

		}
	}
}
