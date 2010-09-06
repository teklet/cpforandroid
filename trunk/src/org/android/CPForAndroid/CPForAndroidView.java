package org.android.CPForAndroid;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class CPForAndroidView extends TabActivity{
	public String _LoungeFeed_en = "";
	public String _MainFeed_en = "";
	final static String TAG = "CPForAndroidView";
    /** TabHost will have Tabs */
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cporandroidview);
		//CPForAndroid.Mainfeed;
		try {
			TabHost tabHost = getTabHost();

	        /** TabSpec used to create a new tab.
	        /** tid1 is firstTabSpec Id. Its used to access outside. */
	        TabSpec firstTabSpec = tabHost.newTabSpec("tid1");
	        TabSpec secondTabSpec = tabHost.newTabSpec("tid1");

	        /** TabSpec setIndicator() is used to set name for the tab. */
	        /** TabSpec setContent() is used to set content for a particular tab. */
	        Resources res = getResources(); // Resource object to get Drawables
	        firstTabSpec.setIndicator("Latest Articles", res.getDrawable(R.drawable.articles)).setContent(new Intent(this,ArticlesActivity.class));
	        secondTabSpec.setIndicator("The Lounge", res.getDrawable(R.drawable.loungechair)).setContent(new Intent(this,LoungeActivity.class));

	        /** Add tabSpec to the TabHost to display. */
	        tabHost.addTab(firstTabSpec);
	        tabHost.addTab(secondTabSpec);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
		}
	}
	
    //*** ANDROID.VIEW.MENU START ***// 
    public boolean onCreateOptionsMenu(Menu menu) 
    {
    	// Create the menu from the menu_xml
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.refresh:
        	Intent myIntent = new Intent(CPForAndroidView.this, CPForAndroid.class);
        	CPForAndroidView.this.startActivity(myIntent);
        	finish();
            return true;
        case R.id.quit:
            finish();
            return true;
        case R.id.about:
        	new AboutDialog(this).show();
            return true;
        case R.id.viewread:
        	CPForAndroid.d.deleteAllinTable();
        	Intent myIntent2 = new Intent(CPForAndroidView.this, CPForAndroidView.class);
        	CPForAndroidView.this.startActivity(myIntent2);
        	finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    //*** ANDROID.VIEW.MENU END ***// 
}
