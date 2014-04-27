package com.example.instafilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CityImages extends Activity implements OnClickListener {
	TextView cityNameTV;
	GridView gridView1;
	InputStream is;
	ArrayList<images> images;
	Bitmap bitmap;
	Button refresh;
	myadapter myadapter;
	int instaCount = 0;
	String[] instaURL;
	String[] instaID;
	List<String> selected_picsID = new ArrayList<String>();
	List<String> selected_picsURL = new ArrayList<String>();
	int currPic = 0;
	private ProgressDialog PD;
	boolean selectMode = false;
    double lat, lng;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_images);

		ActionBar actionBar = getActionBar();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			actionBar.setHomeButtonEnabled(true);
		}
		actionBar.setDisplayHomeAsUpEnabled(true);

		/* Define vars */
		cityNameTV = (TextView) findViewById(R.id.cityNameTV);

		Bundle city = getIntent().getExtras();

		String cityName = city.get("cityName").toString();

		cityNameTV.setText(cityName);

		Log.d(MainActivity.TAG, cityName);

		
		/* GET COOR*/
		
		try {
			Geocoder geocoder = new Geocoder(CityImages.this);
			List<Address> addresses;

			addresses = geocoder.getFromLocationName(cityName, 1);

			if (addresses.size() > 0) {
				double latitude = lat = addresses.get(0).getLatitude();
				double longitude = lng = addresses.get(0).getLongitude();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PD = new ProgressDialog(CityImages.this);

		PD.setTitle("Loading...");

		PD.setMessage("Please wait...");

		PD.setCancelable(false);

		/* Initialize Gallery */

		gridView1 = (GridView) findViewById(R.id.gridView1);

		refresh = (Button) findViewById(R.id.refresh);

		refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getImages(currPic);

			}

		});

		// get Some Images

		// new getPhotos().execute("");

		images = new ArrayList<images>();

		myadapter = new myadapter(this, R.layout.row, images);

		gridView1.setAdapter(myadapter);

		Animation animation = AnimationUtils.loadAnimation(CityImages.this,
				R.anim.flyin);

		gridView1.startAnimation(animation);

		getImages(0);

		gridView1.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (!selectMode) {
					for (int i = 0; i < gridView1.getChildCount(); i++) {
						gridView1.getChildAt(i).setAlpha(0.5f);

					}
					arg1.setAlpha(1);
					if (arg1.getAlpha() == 1.0) {
						selected_picsID.add(images.get(arg2).getID());
						selected_picsURL.add(images.get(arg2).getStringURL());
					} else {
						selected_picsID.remove(images.get(arg2).getID());
						selected_picsURL.add(images.get(arg2).getStringURL());
					}
					selectMode = true;

				}
				((ImageView) findViewById(R.id.addAlbumIV))
						.setVisibility(View.VISIBLE);

				return false;

			}

		});

		gridView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (selectMode) {
					if (arg1.getAlpha() != 1) {
						arg1.setAlpha(1f);
					} else {
						arg1.setAlpha(0.5f);
					}

				}
				if (arg1.getAlpha() == 1.0) {
					selected_picsID.add(images.get(arg2).getID());
					selected_picsURL.add(images.get(arg2).getStringURL());

				} else {
					selected_picsID.remove(images.get(arg2).getID());
					selected_picsURL.add(images.get(arg2).getStringURL());
				}

			}

		});

		((ImageView) findViewById(R.id.addAlbumIV)).setOnClickListener(this);
	}

	private void getImages(int i) {
		if (i == 0) {
			new getPhotos().execute("");

		}

		else {
			new RetrieveFeedTask(i).execute("");
		}
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	} // Author: silentnuke

	public class getPhotos extends AsyncTask<String, Void, String> {

		String coor;

		public getPhotos() {
			super();
		}

		@Override
		protected String doInBackground(String... arg0) {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					"https://api.instagram.com/v1/media/search?lat="
							+ lat
							+ "&lng="
							+ lng
							+ "&access_token=265244079.d5378de.d48c1a5c768f4f3fa29071ed326a64dc&distance=5000&count=33");
			HttpResponse response;
			try {
				response = httpclient.execute(httpget);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				in.close();
				String result = sb.toString();
				// Log.v("My Response :: ", result);
				try {
					JSONObject jsonObj = new JSONObject(result);

					// Getting JSON Array node
					// results = jsonObj.getJSONObject(TAG_RESULTS);
					JSONArray images = jsonObj.getJSONArray("data");

					// looping through All Contacts
					instaCount = images.length();
					instaID = new String[instaCount];
					instaURL = new String[instaCount];
					for (int i = 0; i < images.length(); i++) {
						JSONObject c = images.getJSONObject(i);
						JSONObject image = c.getJSONObject("images");
						JSONObject standardRes = image
								.getJSONObject("thumbnail");

						String url = standardRes.getString("url");

						
						String imageID = c.getString("id");

						Log.d(MainActivity.TAG, imageID);
						instaID[i] = imageID;
						instaURL[i] = url;
						if(new File(Environment.getExternalStorageDirectory()
								+ "/InstaFilter/"
								+ cityNameTV.getText().toString() + "/" + imageID + ".jpg").exists()){
							
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			new RetrieveFeedTask().execute("", result);

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!PD.isShowing()) {
				PD.show();
			}
		}

	}

	class RetrieveFeedTask extends AsyncTask<String, Void, Bitmap> {

		private Exception exception;
		private int currInstaPic;

		public RetrieveFeedTask() {
			currInstaPic = 0;
		}

		public RetrieveFeedTask(int curr) {
			currInstaPic = curr;
		}

		protected void onPostExecute(Bitmap feed) {
			myadapter.notifyDataSetChanged();

			PD.dismiss();

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!PD.isShowing()) {
				PD.show();
			}
		}

		protected Bitmap doInBackground(String... params) {
			try {

				if (currInstaPic == 0) {
					for (int j = 0; j < ((instaCount > 10) ? 10 : instaCount); j++) {

						bitmap = getBitmapFromURL(instaURL[j]);
						images image3 = new images();
						image3.imageurl = bitmap;

						image3.setStringURL(instaURL[j]);
						image3.setID(instaID[j]);

						images.add(image3);
						Log.d(MainActivity.TAG, instaURL[j]);
						Log.d(MainActivity.TAG, instaID[j]);

						currPic = j;
					}

				} else {
					for (int j = currInstaPic + 1; j < currInstaPic + 10; j++) {

						bitmap = getBitmapFromURL(instaURL[j]);
						images image3 = new images();
						image3.imageurl = bitmap;

						images.add(image3);
						Log.d(MainActivity.TAG, instaURL[j]);

						currPic = j;
					}
				}
				if (currPic == instaCount - 1) {
					refresh.setVisibility(View.GONE);
				} else {
					refresh.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
				this.exception = e;
				e.printStackTrace();
			}
			return null;
		}

	}

	private class DownloadTask extends AsyncTask<String, Integer, String> {

		ProgressDialog PD = new ProgressDialog(CityImages.this);

		public DownloadTask() {
			super();
			PD.setTitle("Downloading...");
			PD.setMessage("Please wait...");
			PD.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... sUrl) {
			Log.d(MainActivity.TAG, sUrl[0] + "---" + sUrl[1]);
			InputStream input = null;
			OutputStream output = null;
			HttpURLConnection connection = null;
			try {
				URL url = new URL(sUrl[1]);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();

				// expect HTTP 200 OK, so we don't mistakenly save error
				// report
				// instead of the file
				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					return "Server returned HTTP "
							+ connection.getResponseCode() + " "
							+ connection.getResponseMessage();
				}

				// this will be useful to display download percentage
				// might be -1: server did not report the length
				int fileLength = connection.getContentLength();
				// download the file
				File InstaFilterCityDir = new File(
						Environment.getExternalStorageDirectory()
								+ "/InstaFilter/"
								+ cityNameTV.getText().toString() + "/");

				if (!InstaFilterCityDir.isDirectory()) {
					InstaFilterCityDir.mkdirs();
				}
				File f = new File(Environment.getExternalStorageDirectory()
						+ "/InstaFilter/" + cityNameTV.getText().toString()
						+ "/", sUrl[0] + ".jpg");
				
				if(f.exists()) f.delete();

				input = connection.getInputStream();
				output = new FileOutputStream(f);

				byte data[] = new byte[4096];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					// allow canceling with back button
					if (isCancelled()) {
						input.close();
						return null;
					}
					total += count;
					// publishing the progress....
					if (fileLength > 0) // only if total length is known
						publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}

			} catch (Exception e) {
				e.printStackTrace();
				return e.toString();
			} finally {
				try {
					if (output != null)
						output.close();
					if (input != null)
						input.close();
				} catch (IOException ignored) {
				}

				if (connection != null)
					connection.disconnect();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			PD.dismiss();
			Toast.makeText(CityImages.this, "Done", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			PD.show();
		}

	}

	@Override
	public void onBackPressed() {
		for (int i = 0; i < gridView1.getChildCount(); i++) {
			gridView1.getChildAt(i).setAlpha(1);
		}
		Intent backHomeIntent = new Intent(CityImages.this, MainActivity.class);

		startActivity(backHomeIntent);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {

		onBackPressed();

		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.addAlbumIV) {
			// remove duplicates
			HashSet hs = new HashSet();
			hs.addAll(selected_picsID);
			selected_picsID.clear();
			selected_picsID.addAll(hs);

			// Remove duplicate (url)

			HashSet hs2 = new HashSet();
			hs2.addAll(selected_picsURL);
			selected_picsURL.clear();
			selected_picsURL.addAll(hs2);

			for (int i = 0; i < selected_picsID.toArray().length; i++) {
				Log.d(MainActivity.TAG, selected_picsID.get(i));
				new DownloadTask().execute(selected_picsID.get(i),
						selected_picsURL.get(i));
			}

		}

	}
}
