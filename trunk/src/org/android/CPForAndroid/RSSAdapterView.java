package org.android.CPForAndroid;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RSSAdapterView extends LinearLayout {

	public RSSAdapterView(Context context, RSSItem entry) {
		super(context);
		
		this.setOrientation(VERTICAL);
		this.setTag(entry);
		
		View v = inflate(context, R.layout.rss_row, null);
		
		TextView tvContact = (TextView)v.findViewById(R.id.tvTitle);
		tvContact.setText(entry.getTitle());
		
		TextView tvPhone = (TextView)v.findViewById(R.id.tvAuthor);
		tvPhone.setText(entry.getAuthor());
		
		TextView tvMail = (TextView)v.findViewById(R.id.tvTime);
		tvMail.setText(entry.getPubDate());
		
		addView(v);
	}
}
