//////////////////////////////////////////
//             CPForAndroid             //
//  http://cpforandroid.googlecode.com  //
//     Copyright (C) 2010 JPS III       //
//         and development team         //
// GNU General Public License version 3 //
//////////////////////////////////////////
//ToDo: 
// - Add More SQL Functions to DbHelper (JoeSox working on 8/21/10)
// - Work on a 'reboot of database' delete allrecords at least for debugging
// - Account Login
// - Message board interface
// - Simple Article viewer so it is easier to read. (Takes the original article html and only grabs the article content)
// - Make interface similar to DroidX social networking widget http://www.youtube.com/watch?v=Z0fmVL11TVY
// - Fix ProgressBar when loading rss
// - Bug ShowDescription does not scroll when text too long
//
package com.android.CPForAndroid;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.android.CPForAndroid.ShowDescription;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class CPForAndroid extends Activity implements OnItemClickListener{
	
	public final String RSSFEED_LATESTARTICLES = "http://www.codeproject.com/webservices/articlerss.aspx?cat=1";
	public final String RSSFEED_THELOUNGE = "http://www.codeproject.com/webservices/LoungeRss.aspx";
	
	public final String tag = "CPForAndroid";
	private RSSFeed Mainfeed = null;
	private RSSFeed Loungefeed = null;
	private ProgressDialog progressDialog;
    
    public DbDataLayer d ;

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
        	RefreshCurrentRSSFeeds();
            return true;
        case R.id.articles:
        	DisplayRSS(Mainfeed);
            return true;
        case R.id.lounge:
        	DisplayRSS(Loungefeed);
            return true;
        case R.id.quit:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
  //*** ANDROID.VIEW.MENU END ***// 
    
    @Override
    public void finishFromChild (Activity child)
    {
    	finish();//http://blog.henriklarsentoft.com/2010/07/android-tabactivity-nested-activities/#comment-7
    }

    //*** ANDROID.VIEW.MENU END ***//
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //SQLiteDatabase Maintenance
        //ToDo d.Trim(500) : Read current database, if more than 500, then remove the oldest ones.
        d = new DbDataLayer(getBaseContext());
        
        //Go get the RSSFeeds
        RefreshCurrentRSSFeeds();
        
        // display Main UI / The Latest Articles
        DisplayRSS(Mainfeed);
        
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
    
    private void RefreshCurrentRSSFeeds()
    {
    	try 
    	{
            progressDialog = ProgressDialog.show(CPForAndroid.this, "", "Loading. Please wait...");
            // go get our Mainfeed!
            Mainfeed = getFeed(RSSFEED_LATESTARTICLES);
            //Cache the Lounge feed
            Loungefeed = getFeed(RSSFEED_THELOUNGE);
            progressDialog.dismiss();
            
            // display Main UI / The Latest Articles
            DisplayRSS(Mainfeed);
		} catch (Exception e)
		{
			Log.i(tag,"RefreshCurrentRSSFeeds [" + e.getMessage() + "]");
		}
    	
    }
    
    private RSSFeed getFeed(String urlToRssFeed)
    {
    	try
    	{
    		// setup the url
    	   URL url = new URL(urlToRssFeed);

           // create the factory
           SAXParserFactory factory = SAXParserFactory.newInstance();
           // create a parser
           SAXParser parser = factory.newSAXParser();

           // create the reader (scanner)
           XMLReader xmlreader = parser.getXMLReader();
           // instantiate our handler
           RSSHandler theRssHandler = new RSSHandler();
           // assign our handler
           xmlreader.setContentHandler(theRssHandler);
           // get our data via the url class
           InputSource is = new InputSource(url.openStream());
           // perform the synchronous parse           
           xmlreader.parse(is);
           // get the results - should be a fully populated RSSFeed instance, or null on error
           return theRssHandler.getFeed();
    	}
    	catch (Exception ee)
    	{
    		// if we have a problem, simply return null
    		return null;
    	}
    }

    public void DisplayRSS(RSSFeed rssfeed)
    {
        TextView feedtitle = (TextView) findViewById(R.id.feedtitle);
        TextView feedpubdate = (TextView) findViewById(R.id.feedpubdate);
        ListView itemlist = (ListView) findViewById(R.id.itemlist);
        if (rssfeed == null)
        {
        	feedtitle.setText(rssfeed.getTitle() + " : No RSS Feed Available");
        	return;
        }
        
        feedtitle.setText(rssfeed.getTitle());
        feedpubdate.setText(rssfeed.getPubDate());
        
        //Get all items and remove the previously read items
        List<RSSItem> AllRSSlist = rssfeed.getAllItems();
        ArrayList<RSSItem> NewRSSlist = new ArrayList<RSSItem>();
        //Let's look at each item in the latest RSS
        for (RSSItem item : AllRSSlist) 
        {	//The URL Link is a unique enough field to check against.
        	if(!d.DoesExist("Link", item.getLink()))
        	{
        		//This is a new unread item not found in SQLite db
        		RSSItem thisitem = new RSSItem();
        		thisitem = item;
        		NewRSSlist.add(thisitem);
        	}
		}
      //Now the unread items are created time to create the view.
        int s = NewRSSlist.size();
        //ArrayAdapter<RSSItem> adapter = new ArrayAdapter<RSSItem>(this,android.R.layout.simple_list_item_1, NewRSSlist);
        RSSAdapter adapter = new RSSAdapter(this, NewRSSlist);
        
        adapter.notifyDataSetChanged();
        itemlist.setAdapter(adapter);
        itemlist.setOnItemClickListener(this);
        itemlist.setSelection(0);
        if(s == 0)
        {
        	feedtitle.setText(rssfeed.getTitle() + " : No recent unread updates.");
        }
    }
    
    //Used for debugging
    public void DisplayPopUp(View v)
    {
   	    LayoutInflater inflater = (LayoutInflater)
        this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
   	    PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.popup, null, false), 100, 100, true);
   	    // The code below assumes that the root container has an id called 'main'
   	    pw.showAtLocation(v, Gravity.CENTER, 0, 0); 
    }
    
    public void onItemClick(AdapterView parent, View v, int position, long id)
    {
    	//Modify so works with Latest or Lounge
    	RSSFeed curFeed = null;
    	RSSItem c = (RSSItem)parent.getAdapter().getItem(position);//What RSSItem was selected
    	//Found out what View this _link this is and this tells us what the user is looking at
    	if(Loungefeed.getAllItems().indexOf(c) == -1)
    	{
    		curFeed = Mainfeed;
    	}
    	else
    	{
    		curFeed = Loungefeed;
    	}
    	 
    	Log.i(tag,"item clicked! [" + curFeed.getItem(position).getTitle() + "]");
    	//Prepare the clicked item to be displayed
    	Intent itemintent = new Intent(this,ShowDescription.class);
    	Bundle b = new Bundle();
    	b.putString("title", curFeed.getItem(position).getTitle());
    	b.putString("author", curFeed.getItem(position).getAuthor());
    	b.putString("description", curFeed.getItem(position).getDescription());
    	b.putString("link", curFeed.getItem(position).getLink());
    	b.putString("pubdate", curFeed.getItem(position).getPubDate());
    	itemintent.putExtra("android.intent.extra.INTENT", b);

    	//Lets mark this as read OR Do not list the item
    	boolean bTest = d.DoesExist("Link", curFeed.getItem(position).getLink());
    	if(!bTest)
    	{
	    	d.AddRSSFeedAsRead(curFeed.getItem(position).getTitle(), curFeed.getItem(position).getLink(), curFeed.getItem(position).getPubDate());
	    	//Since we just read it, let's remove it from the current view
	    	//Delete from data source
	    	curFeed.removeItem(curFeed.getItem(position));
    	}

    	//Let the view know something changed and redraw
    	DisplayRSS(curFeed);
    	//Do the ShowDescription
    	startActivity(itemintent);
    }
    

}