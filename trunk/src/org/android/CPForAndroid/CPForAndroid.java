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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.DataInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.android.CPForAndroid.R.id;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
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
import android.net.ConnectivityManager;
import android.net.Uri;

public class CPForAndroid extends Activity{
	
	public final static String RSSFEED_LATESTARTICLES = "http://www.codeproject.com/webservices/articlerss.aspx?cat=1";
	public final static String RSSFEED_THELOUNGE = "http://www.codeproject.com/webservices/LoungeRss.aspx";
	public final String RSSFEED_TEST0 = "http://www.joeswammi.com/misc/latestrss.xml.htm";
	public static RSSFeed Mainfeed = null;
	public static RSSFeed Loungefeed = null;
	
	public final String TAG = "CPForAndroid";
    public static DbDataLayer d ;
    boolean FinishedDownloading = false;
    public static String LatestArticleHTML = "";
    public static String TheLoungeHTML = "";
    DownloaderThread progressThread;		//Used for DownloadedThread PRogressBar handling
    ProgressDialog progressDialog;			//Used for DownloadedThread PRogressBar handling
    static final int PROGRESS_DIALOG = 0;	//Used for DownloadedThread PRogressBar handling
    
    
    // Define the Handler that receives messages from the thread and update the progress
    final Handler Downloadhandler = new Handler() {
        public void handleMessage(Message msg) {
            try 
            {
                if(msg.obj == "true" && TheLoungeHTML != "")
                {
	            	progressThread.setState(DownloaderThread.STATE_DONE);
	                dismissDialog(PROGRESS_DIALOG);
                    ShowTabView();//Go to the main view
                    finish();//This view is finished (so we don't seeit again)!
                }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    };
    
    private void ShowTabView()
    {
    	try {
        	Intent itemintent = new Intent(this,CPForAndroidView.class);
        	Bundle b = new Bundle();
        	b.putString("mainfeed_en", LatestArticleHTML);
        	b.putString("loungefeed_en", TheLoungeHTML);
        	itemintent.putExtra("android.intent.extra.INTENT", b);
        	startActivity(itemintent);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
    }
    
    private class DownloaderThread extends Thread {
        Handler mHandler;
        final static int STATE_DONE = 0;
        final static int STATE_RUNNING = 1;
        int mState;
       
        DownloaderThread(Handler h) {
            mHandler = h;
        }
       
        public void run() {
        	Looper.prepare();
                Message msg = mHandler.obtainMessage();
                try {
                	//Do The Work [fyi:While statements have it run two times]
                	HttpClient browser = new HttpClient();
                	LatestArticleHTML = browser.makeRequest(RSSFEED_LATESTARTICLES);
                	TheLoungeHTML = browser.makeRequest(RSSFEED_THELOUNGE);

                    if(TheLoungeHTML != "")
                    {
                    	//Make sure the feeds are encoded
    	            	LatestArticleHTML = PrepAndEncodeLine(LatestArticleHTML);
    	            	TheLoungeHTML = PrepAndEncodeLine(TheLoungeHTML);
                    	Mainfeed = CreateRSS(LatestArticleHTML);
                    	Loungefeed = CreateRSS(TheLoungeHTML);
                    	msg.obj = "true";//FinishedDownloading = true;//We should be done
                    }
                } catch (Exception e) {
                    Log.e("ERROR", "Thread Interrupted Exception [" + e.getMessage() + "]");
                }
                mHandler.sendMessage(msg);
            Looper.loop();
        }

        /* sets the current state for the thread,
         * used to stop the thread */
        public void setState(int state) {
            mState = state;
        }
        
        private RSSFeed CreateRSS(String strToRssFeed)
        {
        	try
        	{
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
               ByteArrayInputStream baIS = new ByteArrayInputStream(strToRssFeed.getBytes());//MAYBE BREAK HERE IF SITE IS IN MAINTENANCE OR BAD FEED?
               // perform the synchronous parse   
               xmlreader.parse(new InputSource(baIS));
               // get the results - should be a fully populated RSSFeed instance, or null on error
               return theRssHandler.getFeed();
        	}
        	catch (Exception ee)
        	{
        		// if we have a problem, simply return null
        		Log.e("CreateRSS","getFeed Exception [" + ee.getMessage() + "]");
        		return null;
        	}
        }
    }
    
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case PROGRESS_DIALOG:
            progressDialog = new ProgressDialog(CPForAndroid.this);
            progressDialog.setMessage("Downloading Content...");
            progressThread = new DownloaderThread(Downloadhandler);
            progressThread.start();
            return progressDialog;
        default:
            return null;
        }
    }
    /** Expat parser strips all of the html tags out of <description> content, this hack preps for WebView */
    public static String PrepAndEncodeLine(String strLine)
    {
    	String newline = strLine;
    	//Lets go line by line for easy reading/debugging etc (keep in sequence order)
    	newline = newline.replace("&lt;a href=\"", "{{a");
    	//newline = newline.replace("&amp;", "&&&");
    	newline = newline.replace("&lt;", "[[").replace("&gt;", "]]");
    	newline = newline.replace("[[div class=\"signature\"]]", "{hr}");
    	return newline;
    }
    
    /** CHECK TO SEE IF CODEPROJECT IS AVAILABLE **/
    private boolean isCodeProjectUp() {
        try {
                String testUrl = RSSFEED_LATESTARTICLES;
                    URL url = new URL(testUrl);
                    URLConnection connection;
                    connection = url.openConnection();
                    connection.connect();
                    return true;
        } catch (IOException e) {
                Log.i(TAG, "Cannot connect to " + RSSFEED_LATESTARTICLES);
                new PopUpDialog(this, "CONNECTION INFO", "Cannot connect to " + RSSFEED_LATESTARTICLES).show();
                return false;
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.titlebg);//SplashScreen
        //setContentView(R.layout.main);//Default
        d = new DbDataLayer(getBaseContext());
        
        if(isCodeProjectUp())
        {
            //new PopUpDialog(this, "CONNECTION INFO", "CODEPROJECT ONLINE!").show();//TEST
        	showDialog(PROGRESS_DIALOG);
        }
    }
    
}