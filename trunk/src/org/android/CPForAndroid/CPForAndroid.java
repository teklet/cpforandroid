//////////////////////////////////////////
//             CPForAndroid             //
//  http://cpforandroid.googlecode.com  //
//     Copyright (C) 2010 JPS III       //
//         and development team         //
// GNU General Public License version 3 //
//////////////////////////////////////////
//ToDo: 
// - http://code.google.com/p/cpforandroid/issues/list
//
package org.android.CPForAndroid;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.DataInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
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
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import android.net.Uri;


public class CPForAndroid extends Activity implements OnItemClickListener{
	
	public final String RSSFEED_LATESTARTICLES = "http://www.codeproject.com/webservices/articlerss.aspx?cat=1";
	public final String RSSFEED_THELOUNGE = "http://www.codeproject.com/webservices/LoungeRss.aspx";
	public final String RSSFEED_TEST0 = "http://www.joeswammi.com/misc/latestrss.xml.htm";
	
	public final String tag = "CPForAndroid";
	private RSSFeed Mainfeed = null;
	private RSSFeed Loungefeed = null;
	
    static final int PROGRESS_DIALOG = 0;	//Used for DownloadedThread PRogressBar handling
    DownloaderThread progressThread;			//Used for DownloadedThread PRogressBar handling
    ProgressDialog progressDialog;			//Used for DownloadedThread PRogressBar handling
    
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
        	RefreshCurrentRSSFeeds(true);//if we don't wait, the progressbar appear frozen
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
        case R.id.about:
        	new AboutDialog(this).show();
            return true;
        case R.id.viewread:
        	d.deleteAllinTable();
        	DisplayRSS(Mainfeed);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    //*** ANDROID.VIEW.MENU END ***// 
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //SQLiteDatabase Maintenance
        //ToDo d.Trim(500) : Read current database, if more than 500, then remove the oldest ones.
        d = new DbDataLayer(getBaseContext());

        //Go get the RSSFeeds
        RefreshCurrentRSSFeeds(false);
        
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
    
    private void RefreshCurrentRSSFeeds(boolean bForcePB)
    {
    	try 
    	{
    		//The first time this app is run Mainfeed equals null 
    		//we must delay before trying to update the screen or errors happen
    		while(Mainfeed == null)
    		{
    			//Possible redesign here needed as this may end in endless loop
                // go get our feeds! because this calls onCreateDialog which creates the second thread to call getFeed
        		showDialog(PROGRESS_DIALOG);
    		}
    		if(bForcePB)
    		{
        		//showDialog(PROGRESS_DIALOG);//I don't understand why this hangs the progressbar
            	Mainfeed = getFeed(RSSFEED_LATESTARTICLES);
            	//Mainfeed = getFeed(RSSFEED_TEST0);//DEBUG
            	Loungefeed = getFeed(RSSFEED_THELOUNGE);
            	
    		}
            // display Main UI / The Latest Articles
            DisplayRSS(Mainfeed);
		} catch (Exception e)
		{
			Log.e(tag,"RefreshCurrentRSSFeeds Exception [" + e.getMessage() + "]");
		}
    }
    
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case PROGRESS_DIALOG:
            progressDialog = new ProgressDialog(CPForAndroid.this);
            progressDialog.setMessage("Loading...");
            progressThread = new DownloaderThread(handler);
            progressThread.start();
            return progressDialog;
        default:
            return null;
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
           ////xmlreader.
           // get our data via the url class
           //InputSource is = new InputSource(url.openStream());
           BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
           StringBuilder sb = new StringBuilder();
           String line;
           while ((line = in.readLine()) != null)
           {
        	   String newline = line.replace("&lt;br /&gt;", " ").replace("&lt;", "<").replace("&gt;", ">").replace("/script/Forums/Images/", "http://www.codeproject.com/script/Forums/Images/");
        	   sb.append(newline);
        	   //DEBUGGING
        	   //if(line.indexOf("Forums/Images") > 0)
        	   //{
        	   //   Log.d(tag, newline);
        	   //}
           }
           ByteArrayInputStream baIS = new ByteArrayInputStream(sb.toString().getBytes());
           // perform the synchronous parse   
           xmlreader.parse(new InputSource(baIS));
           // get the results - should be a fully populated RSSFeed instance, or null on error
           return theRssHandler.getFeed();
    	}
    	catch (Exception ee)
    	{
    		// if we have a problem, simply return null
    		Log.e(tag,"getFeed Exception [" + ee.getMessage() + "]");
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
    
    public void onItemClick(AdapterView parent, View v, int position, long id)
    {
        //List<RSSItem> curFeed = null;//used for DEBUG
        //ArrayList<RSSItem> NewRSSlist = new ArrayList<RSSItem>();//used for DEBUG
    	RSSFeed curFeed = null;//used as a temp value to decide what list user clicked on
    	RSSItem c = (RSSItem)parent.getAdapter().getItem(position);//What RSSItem was selected
    	//Found out what View this _link this is and this tells us what the user is looking at
    	Bundle b = new Bundle();
    	if(Loungefeed.getAllItems().indexOf(c) == -1)
    	{
    		curFeed = Mainfeed;// Need to make copy
    		b.putString("feedtitle", Mainfeed.getTitle());
    	}
    	else
    	{
    		curFeed = Loungefeed;
    		b.putString("feedtitle", Loungefeed.getTitle());
    	}
    	 
    	Log.i(tag,"item clicked! [" + curFeed.getItem(position).getTitle() + "]");
    	//Prepare the clicked item to be displayed
    	
    	b.putString("title", c.getTitle());
    	b.putString("author", c.getAuthor());
    	b.putString("description", c.getDescription());
    	b.putString("link", c.getLink());
    	b.putString("pubdate", c.getPubDate());

    	//Lets mark this as read OR Do not list the item
    	boolean bTest = d.DoesExist("Link", c.getLink());
    	if(!bTest)
    	{
	    	d.AddRSSFeedAsRead(c.getTitle(), c.getLink(), c.getPubDate());
        	//Let the view know something changed and redraw
    	}
        //Let the view know something changed and redraw
    	DisplayRSS(curFeed);//DisplayRSS should only display what is not in the Read database
    	//Do the DetailsDialog
    	new DetailsDialog(this, b).show();
    }
    
    
    // Define the Handler that receives messages from the thread and update the progress
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            dismissDialog(PROGRESS_DIALOG);
            progressThread.setState(DownloaderThread.STATE_DONE);
        }
    };

    
    /** Nested class that performs progress calculations (counting) */
    private class DownloaderThread extends Thread {
        Handler mHandler;
        final static int STATE_DONE = 0;
        final static int STATE_RUNNING = 1;
        int mState;
        int total;
       
        DownloaderThread(Handler h) {
            mHandler = h;
        }
       
        public void run() {
            mState = STATE_RUNNING;   
            while (mState == STATE_RUNNING) 
            {
                Message msg = mHandler.obtainMessage();
                try {
                	//Do The Work
                	Mainfeed = getFeed(RSSFEED_LATESTARTICLES);
                	//Mainfeed = getFeed(RSSFEED_TEST0);//DEBUG
                	Loungefeed = getFeed(RSSFEED_THELOUNGE);
                } catch (Exception e) {
                    Log.e("ERROR", "Thread Interrupted Exception [" + e.getMessage() + "]");
                }
                mHandler.sendMessage(msg);
            }
        }
        
        /* sets the current state for the thread,
         * used to stop the thread */
        public void setState(int state) {
            mState = state;
        }
    }
    
}