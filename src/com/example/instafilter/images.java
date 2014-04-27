package com.example.instafilter;

import android.graphics.Bitmap;

public class images {
public Bitmap imageurl;
public String stringURL;
public String ID;
public String getStringURL() {
	return stringURL;
}

public String getID() {
	return ID;
}
public void setID(String id) {
	ID = id;
}

public void setStringURL(String stringURL) {
	this.stringURL = stringURL;
}

public Bitmap getImageurl() {
	return imageurl;
}

public void setImageurl(Bitmap imageurl) {
	this.imageurl = imageurl;
}
}
