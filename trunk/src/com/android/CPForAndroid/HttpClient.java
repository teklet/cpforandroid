//////////////////////////////////////////
//             CPForAndroid             //
//  http://cpforandroid.googlecode.com  //
//     Copyright (C) 2010 JPS III       //
//         and development team         //
// GNU General Public License version 3 //
//////////////////////////////////////////
package com.android.CPForAndroid;

import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HttpClient {
    
    public static String makeRequest(String path) throws Exception 
    {
    	DefaultHttpClient httpclient = new DefaultHttpClient();
    	HttpGet httpost = new HttpGet(path);

    	ResponseHandler<String> responseHandler = new BasicResponseHandler();
    	String str = (String) httpclient.execute(httpost, responseHandler);
    	 
    	return str;
    }
}
