//////////////////////////////////////////
//             CPForAndroid             //
//  http://cpforandroid.googlecode.com  //
//     Copyright (C) 2010 JPS III       //
//         and development team         //
// GNU General Public License version 3 //
//////////////////////////////////////////
package com.android.CPForAndroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.provider.BaseColumns;
import android.util.Log;
public class DbDataLayer 
{
		private static final String DATABASE_NAME = "cpandroid.db";	//CUSTOMIZE
		private static final String TABLE_NAME = "FeedsRead";//CUSTOMIZE
	    private SQLiteDatabaseAdapter _dbHelper;
	    public final String tag = "DbDataLayer";

	    public DbDataLayer(Context c) 
	    {
	        String sql = "create table "+ TABLE_NAME + " " +
            "("+ BaseColumns._ID + " integer primary key autoincrement, " +
            "Link text not null, " +
            "Title text not null, " +
            "Pubdate text not null); ";

	        _dbHelper = new SQLiteDatabaseAdapter(c, DATABASE_NAME, null , 1, sql);
	    }

	    public void AddRSSFeedAsRead(String title, String link, String pubdate) {
	        SQLiteDatabase db = _dbHelper.getWritableDatabase();
	        try {
	            ContentValues values = new ContentValues();
	            values.put("Link", link);
	            values.put("Title", title);
	            values.put("Pubdate", pubdate);

	            db.insert("FeedsRead", "", values);
	        }catch (Exception e)
			{
				Log.e(tag,"AddRSSFeedAsRead Exception [" + e.getMessage() + "]");
			} finally {
	            if (db != null)
	                db.close();
	        }
	    }
	    
	    public boolean DoesExist(String columkey, String findvalue)
	    {
	    	boolean bExists = false;
	        SQLiteDatabase db = _dbHelper.getWritableDatabase();
	        try {
		    	//select * FROM FeedsRead WHERE Link LIKE 'http://www.codeproject.com/KB/library/MatchingFramework.aspx%';
		    	Cursor cur = db.rawQuery("select * FROM " + TABLE_NAME + " WHERE " + columkey +" LIKE '" + findvalue + "%'", null);
		    	bExists = cur.moveToFirst();
	        }catch (Exception e)
			{
				Log.e(tag,"DoesExist Exception [" + e.getMessage() + "]");
			} finally {
	            if (db != null)
	                db.close();
	        }
	        return bExists;
	    }
	    
	    public void deleteAllinTable() 
	    {
	    	SQLiteDatabase db = _dbHelper.getWritableDatabase();
	        try 
	        {
	        	db.delete(TABLE_NAME, null, null);
	        }catch (Exception e)
			{
				Log.e(tag,"deleteAllinTable Exception [" + e.getMessage() + "]");
			} finally {
	            if (db != null)
	                db.close();
	        }
	    	
	    	
	    }
	}
