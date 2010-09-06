package org.android.CPForAndroid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LoungeActivity extends Activity implements OnItemClickListener{
	public final String TAG = "ArticlesActivity";
	public RSSFeed _MainFeed = null;
	public static TextView feedtitle ;
	public static ListView itemlist;
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	setContentView(R.layout.rsslistview);
    	
    	/* Second Tab Content */
        feedtitle = (TextView) findViewById(R.id.feedtitle);
        itemlist = (ListView) findViewById(R.id.itemlist);
    	DisplayRSS(CPForAndroid.Loungefeed);
    }
    
    public void DisplayRSS(RSSFeed rssfeed)
    {
        if (rssfeed == null)
        {
        	feedtitle.setText(rssfeed.getTitle() + " : No RSS Feed Available");
        	return;
        }
        
        feedtitle.setText(rssfeed.getTitle());
        
        //Get all items and remove the previously read items
        List<RSSItem> AllRSSlist = rssfeed.getAllItems();
        ArrayList<RSSItem> NewRSSlist = new ArrayList<RSSItem>();
        //Let's look at each item in the latest RSS
        for (RSSItem item : AllRSSlist) 
        {	//The URL Link is a unique enough field to check against.
        	if(!CPForAndroid.d.DoesExist("Link", item.getLink()))
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
    	if(CPForAndroid.Loungefeed.getAllItems().indexOf(c) == -1)
    	{
    		curFeed = CPForAndroid.Mainfeed;// Need to make copy
    		b.putString("feedtitle", CPForAndroid.Mainfeed.getTitle());
    	}
    	else
    	{
    		curFeed = CPForAndroid.Loungefeed;
    		b.putString("feedtitle", CPForAndroid.Loungefeed.getTitle());
    	}
    	 
    	Log.i(TAG,"item clicked! [" + curFeed.getItem(position).getTitle() + "]");
    	//Prepare the clicked item to be displayed
    	
    	b.putString("title", c.getTitle());
    	b.putString("author", c.getAuthor());
    	b.putString("description", c.getDescription());
    	b.putString("link", c.getLink());
    	b.putString("pubdate", c.getPubDate());

    	//Lets mark this as read OR Do not list the item
    	boolean bTest = CPForAndroid.d.DoesExist("Link", c.getLink());
    	if(!bTest)
    	{
    		CPForAndroid.d.AddRSSFeedAsRead(c.getTitle(), c.getLink(), c.getPubDate());
        	//Let the view know something changed and redraw
    	}
        //Let the view know something changed and redraw
    	DisplayRSS(curFeed);//DisplayRSS should only display what is not in the Read database
    	//Do the DetailsDialog
    	new DetailsDialog(this, b).show();
    }
}
    