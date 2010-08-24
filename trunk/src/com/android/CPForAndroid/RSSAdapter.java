package com.android.CPForAndroid;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class RSSAdapter extends BaseAdapter {
	private Context context;
	private List<RSSItem> listRSSItems;
	
	public RSSAdapter(Context context, List<RSSItem> listrssitems){
		this.context = context;
		this.listRSSItems = listrssitems;
	}
	public int getCount() {
		return listRSSItems.size();
	}

	public Object getItem(int position) {
		return listRSSItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup viewGroup) {
		RSSItem entry = listRSSItems.get(position);
		return new RSSAdapterView(context,entry);
	}

}
