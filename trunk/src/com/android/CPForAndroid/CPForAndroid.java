//////////////////////////////////////////
//             CPForAndroid             //
//  http://cpforandroid.googlecode.com  //
//     Copyright (C) 2010 JPS III       //
//         and development team         //
// GNU General Public License version 3 //
//////////////////////////////////////////
package com.android.CPForAndroid;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.android.CPForAndroid.ShowDescription;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;

public class CPForAndroid extends Activity implements OnItemClickListener{
	
	public final String RSSFEED_LATESTARTICLES = "http://www.codeproject.com/webservices/articlerss.aspx?cat=1";
	
	public final String tag = "RSSReader";
	private RSSFeed feed = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // go get our feed!
        feed = getFeed(RSSFEED_LATESTARTICLES);
        
        // display UI
        UpdateDisplay();
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
    
    private void UpdateDisplay()
    {
        TextView feedtitle = (TextView) findViewById(R.id.feedtitle);
        TextView feedpubdate = (TextView) findViewById(R.id.feedpubdate);
        ListView itemlist = (ListView) findViewById(R.id.itemlist);
  
        
        if (feed == null)
        {
        	feedtitle.setText("No RSS Feed Available");
        	return;
        }
        
        feedtitle.setText(feed.getTitle());
        feedpubdate.setText(feed.getPubDate());

        ArrayAdapter<RSSItem> adapter = new ArrayAdapter<RSSItem>(this,android.R.layout.simple_list_item_1, feed.getAllItems());
        itemlist.setAdapter(adapter);
        itemlist.setOnItemClickListener(this);
        itemlist.setSelection(0);
        
    }
    
    public void onItemClick(AdapterView parent, View v, int position, long id)
    {
   	 Log.i(tag,"item clicked! [" + feed.getItem(position).getTitle() + "]");

   	 Intent itemintent = new Intent(this,ShowDescription.class);
        
   	 Bundle b = new Bundle();
   	 b.putString("title", feed.getItem(position).getTitle());
   	 b.putString("author", feed.getItem(position).getAuthor());
   	 b.putString("description", feed.getItem(position).getDescription());
   	 b.putString("link", feed.getItem(position).getLink());
   	 b.putString("pubdate", feed.getItem(position).getPubDate());
   	
   	 itemintent.putExtra("android.intent.extra.INTENT", b);
        
   	startActivity(itemintent);
    }
}