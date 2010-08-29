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
import android.widget.ViewFlipper;
import android.content.Intent;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ShowDescription extends Activity 
{
    //Swiping vars
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
    private Animation slideRightOut;
    private ViewFlipper viewFlipper;
    
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        setContentView(R.layout.showdescription);
        //SWIPING
        viewFlipper = (ViewFlipper)findViewById(R.id.flipper);
        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        slideLeftOut = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
        slideRightIn = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
        slideRightOut = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);
        
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        
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
    
    /** Nested class that performs swiping */
    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideLeftOut);
                	viewFlipper.showNext();
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                    finish();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
	        return true;
	    else
	    	return false;
    }
}

