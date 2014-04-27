package com.example.instafilter;


import java.net.URI;
import java.util.ArrayList;




import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class myadapter extends ArrayAdapter<images>{
	   Context context; 
	    int layoutResourceId;    
	    ArrayList<images> data = null;
	public myadapter(Context context, int resource ,ArrayList<images> data) {
		super(context, resource,data);
		this.context=context;
		this.layoutResourceId=resource;
		this.data=data;
		
		// TODO Auto-generated constructor stub
	}

	 public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        contactHolder holder = null;
	        
	        if(row == null)
	        {
	            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            holder = new contactHolder();
	            row = inflater.inflate(layoutResourceId, parent, false);
	            holder.image = (ImageView)row.findViewById(R.id.rowimage);
	            
	            
	            row.setTag(holder);
	        	
} else
{
    holder = (contactHolder)row.getTag();
}

images image = data.get(position);
//image.imageurl.
holder.image.setImageBitmap(image.imageurl);
//holder.image.setImageResource(image.imageurl);

			return row;

}
	 static class contactHolder
 	{
 		ImageView image;
 	}
}