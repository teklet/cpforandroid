//////////////////////////////////////////
//             CPForAndroid             //
//  http://cpforandroid.googlecode.com  //
//     Copyright (C) 2010 JPS III       //
//         and development team         //
// GNU General Public License version 3 //
//////////////////////////////////////////
package org.android.CPForAndroid;

import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class HttpClient {
	
    public static String makeRequest(String path) throws Exception 
    {
    	final String TAG = "HttpClient"; //Used in logging etc.
    	String str = "";
    	
    	try {
        	DefaultHttpClient httpclient = new DefaultHttpClient();
        	//HttpParams params = new BasicHttpParams();
        	//params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000L);
        	//httpclient.setParams(params);
        	HttpGet httpost = new HttpGet(path);

        	ResponseHandler<String> responseHandler = new BasicResponseHandler();
        	str = (String) httpclient.execute(httpost, responseHandler);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
    	return str;
    }
}
