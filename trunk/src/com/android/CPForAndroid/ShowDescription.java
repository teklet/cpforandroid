//////////////////////////////////////////
//             CPForAndroid             //
//  http://cpforandroid.googlecode.com  //
//     Copyright (C) 2010 JPS III       //
//         and development team         //
// GNU General Public License version 3 //
//////////////////////////////////////////
package com.android.CPForAndroid;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.view.*;

public class ShowDescription extends Activity 
{
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        setContentView(R.layout.showdescription);
        
        String theTitle = null;
        String theStory = null;
        
        Intent startingIntent = getIntent();
        
        if (startingIntent != null)
        {
        	Bundle b = startingIntent.getBundleExtra("android.intent.extra.INTENT");
        	if (b == null)
        	{
        		theStory = "bad bundle?";
        	}
        	else
    		{
        		try {
        		//Format the Date java.sql.Date.parse(b.getString("pubdate"));
        		// Mon, 16 Aug 2010 23:35:00 GMT
        		//create SimpleDateFormat object with source string date format
        		SimpleDateFormat sdfSource = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss zzz");
        		Date date;
        		date = sdfSource.parse(b.getString("pubdate"));
        		
        		//create SimpleDateFormat object with desired date format
        		SimpleDateFormat sdfDestination = new SimpleDateFormat("dd MMM yyyy hh:mm");
        		//parse the date into another format
        		theTitle =  b.getString("title");
        		theStory = "By " + b.getString("author") + " | " + sdfDestination.format(date) + "\n\n" + b.getString("description").replace('\n',' ') + "\n\nMore information:\n" + b.getString("link") + "\n\n\n\n";
        		
				} 
        		catch (ParseException e) 
        		{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
        }
        else
        {
        	theTitle = "";
        	theStory = "Information Not Found.";
        
        }
        
        //TextView for the article 'title' found in the RSS
        TextView titledb= (TextView) findViewById(R.id.titlebox);
        titledb.setText(theTitle);
        
        //TextView for the 'author', 'description', and 'link' found in the RSS
        TextView db= (TextView) findViewById(R.id.storybox);
        db.setText(theStory);
        
        //Provide easy go back button
        Button backbutton = (Button) findViewById(R.id.back);
        backbutton.setOnClickListener(new Button.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	finish();
            }
        });       
        
        //Provide menu button
        Button menubutton = (Button) findViewById(R.id.menu);
        menubutton.setOnClickListener(new Button.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	  openOptionsMenu();
            }
        });  
    }
}

