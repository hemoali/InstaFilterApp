package com.example.instafilter;

import java.io.File;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class AlbumImages extends Activity implements
		AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {

	static Boolean isSDPresent = android.os.Environment
			.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);

	Bitmap[] mThumbIds = null;
	File[] mImageIds = null;
	String albumName;
	Bundle city;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		setContentView(R.layout.albums);

		city = getIntent().getExtras();

		albumName = city.get("albumName").toString();

		((TextView) findViewById(R.id.albumNameTV)).setText(albumName);
		mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
		mSwitcher.setFactory(this);
		mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));

		Gallery g = (Gallery) findViewById(R.id.gallery);
		g.setAdapter(new ImageAdapter(this));
		g.setOnItemSelectedListener(this);

	}

	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		Log.d("asd", "4");
		mSwitcher.setImageURI(Uri.fromFile(mImageIds[position]));
	}

	public void onNothingSelected(AdapterView<?> parent) {
	}

	public View makeView() {
		city = getIntent().getExtras();

		albumName = city.get("albumName").toString();

		((TextView) findViewById(R.id.albumNameTV)).setText(albumName);
		
		albumName = city.get("albumName").toString();
		if (isSDPresent) {
			Log.d("asd", "1");
			/* Check Dir */
			File AlbumDir = new File(Environment.getExternalStorageDirectory()
					+ "/InstaFilter/" + albumName + "/");

			if (!AlbumDir.isDirectory()) {
				Log.d("asd", "2");

				AlbumDir.mkdirs();
			} else {

				Log.d("asd", "3");
				mThumbIds = new Bitmap[AlbumDir.listFiles().length];
				mImageIds = new File[AlbumDir.listFiles().length];
				File[] AlbumFiles = AlbumDir.listFiles();
				if (AlbumDir.listFiles().length > 0) {
					for (int i = 0; i < AlbumDir.listFiles().length; i++) {
						Bitmap myBitmap = BitmapFactory
								.decodeFile(AlbumFiles[i].getAbsolutePath());
						mThumbIds[i] = myBitmap;
						mImageIds[i] = AlbumFiles[i];
					}

				}
			}

		}
		Log.d("asd", "5");
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		i.setBackgroundColor(Color.TRANSPARENT);
		return i;
	}

	private ImageSwitcher mSwitcher;

	public class ImageAdapter extends BaseAdapter {
		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mThumbIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d("asd", "8");
			ImageView i = new ImageView(mContext);

			i.setImageBitmap(mThumbIds[position]);
			i.setAdjustViewBounds(true);
			i.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			// i.setBackgroundResource(R.drawable.picture_frame);
			return i;
		}

		private Context mContext;

	}

	@Override
	public void onBackPressed() {

		Intent backHomeIntent = new Intent(AlbumImages.this, MainActivity.class);

		startActivity(backHomeIntent);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {

		onBackPressed();

		return true;
	}
}