package com.example.instafilter;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class image extends Activity{
ImageView imageselcted;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image);

		imageselcted=(ImageView)findViewById(R.id.imageselected);
		Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);		
		createDirectoryAndSaveFile(largeIcon, "Riyadh.PNG");
	}
	private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

	    File direct = new File(Environment.getExternalStorageDirectory() + "/InstaFilter/");

	    if (!direct.exists()) {
	    	
           
	      }
	    
	    File direct2 = new File(Environment.getExternalStorageDirectory() + "/InstaFilter/City/");

	    if (!direct2.exists()) {
	    	
             direct2.mkdirs();
        
	      }
        
	    Log.d("exists", "exists");
	        File file = new File(new File(Environment.getExternalStorageDirectory()+"/InstaFilter/City/"), fileName);
	        file.mkdir();
	        if (file.exists ()) file.delete (); 
	        try {
	            FileOutputStream out = new FileOutputStream(file);
	            imageToSave.compress(Bitmap.CompressFormat.PNG, 100, out);
	            out.flush();
	            out.close();
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	}
}
