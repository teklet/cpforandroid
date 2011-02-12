//////////////////////////////////////////
//             CPForAndroid             //
//  http://cpforandroid.googlecode.com  //
//     Copyright (C) 2010 JPS III       //
//         and development team         //
// GNU General Public License version 3 //
//////////////////////////////////////////
package org.android.CPForAndroidPlusPlus;


import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class RSSFeed 
{
	private String _title = null;
	private String _pubdate = null;
	private String _author = null;
	private int _itemcount = 0;
	private List<RSSItem> _itemlist;
	
	
	RSSFeed()
	{
		_itemlist = new Vector(0); 
	}
	int addItem(RSSItem item)
	{
		_itemlist.add(item);
		_itemcount++;
		return _itemcount;
	}
	int removeItem(RSSItem item)
	{
		_itemlist.remove(item);
		_itemcount--;
		return _itemcount;
	}
	RSSItem getItem(int location)
	{
		return _itemlist.get(location);
	}
	List<RSSItem> getAllItems()
	{
		return _itemlist;
	}
	int getItemCount()
	{
		return _itemcount;
	}
	void setTitle(String title)
	{
		_title = title;
	}
	void setPubDate(String pubdate)
	{
		_pubdate = pubdate;
	}
	void setAuthor(String author)
	{
		_author = author;
	}
	String getTitle()
	{
		return _title;
	}
	String getPubDate()
	{
		return _pubdate;
	}
	String getAuthor()
	{
		return _author;
	}
	
	
}
