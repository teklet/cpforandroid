
package org.android.CPForAndroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.graphics.Color;


public class DetailsDialog {
	@SuppressWarnings("unused")
	private static final String tag = "DetailsDialog";
	private final Context mCtx;
	private Bundle _bundle = null;
    final String mimeType = "text/html";
    final String encoding = "utf-8";


	public DetailsDialog(Context ctx, Bundle b) {
		super();
		this.mCtx = ctx;
		this._bundle = b;
	}
	
    DialogInterface.OnClickListener doneListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };
	
	public void show()
	{
		final LayoutInflater factory = LayoutInflater.from(mCtx);
		View dialogView = factory.inflate(R.layout.detailsdialog, null);
		innerUpdate(dialogView);
		
		String feedType = (String)this._bundle.get("feedtitle");
		if(feedType.indexOf("Articles") > 0)
		{
			AlertDialog.Builder adBuilder = new AlertDialog.Builder(mCtx).setTitle(
					(String)this._bundle.get("title")).setIcon(R.drawable.articles)
					.setView(dialogView).setPositiveButton(android.R.string.ok, doneListener);
			adBuilder.show();
		}
		else
		{
			AlertDialog.Builder adBuilder = new AlertDialog.Builder(mCtx).setTitle(
					(String)this._bundle.get("title")).setIcon(R.drawable.loungechair)
					.setView(dialogView).setPositiveButton(android.R.string.ok, doneListener);
			adBuilder.show();
		}
	}

	private void innerUpdate(View dialogView)
	{
		TextView author = (TextView) dialogView.findViewById(R.id.author);
		WebView description = (WebView) dialogView.findViewById(R.id.webviewbody);
		TextView link = (TextView) dialogView.findViewById(R.id.linkurl);

		// author
		author.setText("by "+(String)this._bundle.get("author"));
		
		// description (body)
		//description.setBackgroundColor(android.graphics.Color.parseColor("#99ffccff"));
		description.loadData((String)this._bundle.get("description"), mimeType, encoding);

		// link
		link.setText((String)this._bundle.get("link"));
	}
}
