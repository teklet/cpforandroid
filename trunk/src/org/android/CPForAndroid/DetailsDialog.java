
package org.android.CPForAndroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.graphics.Color;

public class DetailsDialog {
	@SuppressWarnings("unused")
	private static final String TAG = "DetailsDialog";
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

    /** Expat parser strips all of the html tags out of <description> content, this decodes the hack preps for WebView */
    private String DecodeLine(String strLine)
    {
    	String newline = strLine;
    	//Lets go line by line for easy reading/debugging etc (keep in sequence order)
    	newline = newline.replace("{{a", "<a href=\"");
    	newline = newline.replace("[[", "<").replace("]]", ">");
    	newline = newline.replace("{hr}", "<hr>");
    	return newline;
    }
    
	private void innerUpdate(View dialogView)
	{
		TextView author = (TextView) dialogView.findViewById(R.id.author);
		WebView description = (WebView) dialogView.findViewById(R.id.webviewbody);
		description.getSettings().setJavaScriptEnabled(true); 
		description.setClickable(true);
		TextView link = (TextView) dialogView.findViewById(R.id.linkurl);

		// author
		author.setText("by "+(String)this._bundle.get("author"));
		
		// description (body)
		//description.setBackgroundColor(android.graphics.Color.parseColor("#99ffccff"));
		//description.loadData((String)this._bundle.get("description"), mimeType, encoding);
		String html = new String();
		html = ("<html><head></head><body>" + DecodeLine((String)this._bundle.get("description")) + "</body></html>" );
		Log.d(TAG, html);
		description.loadDataWithBaseURL("http://www.codeproject.com", html, mimeType, encoding, null);
		//description.loadDataWithBaseURL("http://google.com","<html><head></head><body><img src=\"http://www.google.com/intl/en_ALL/images/srpr/logo1w.png\"></body></html>","text/html","UTF-8","about:blank");//works


		// link
		link.setText("--\n" + (String)this._bundle.get("link"));
	}
}
