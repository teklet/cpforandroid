//////////////////////////////////////////
//             CPForAndroid             //
//  http://cpforandroid.googlecode.com  //
//     Copyright (C) 2010 JPS III       //
//         and development team         //
// GNU General Public License version 3 //
//////////////////////////////////////////
// http://www.sqlite.org/sqlite.html
// C:\android-sdk-windows\tools\adb.exe
//"DROP DATABASE


package com.android.CPForAndroid;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper
{
	private static final String DATABASE_NAME = "cpandroid.db";//CUSTOMIZE
	private static final int DATABASE_VERSION = 1;
	private String _sql = "";
	
	public DbHelper(Context context, String s) 
	{
	    super(context, DATABASE_NAME, null, 1);
	    this._sql = s;
	}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(this._sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	/*
		if (oldVersion >= newVersion)
			return;
		String sql = null;
		if (oldVersion == 1) 
			sql = "alter table " + TABLE + " add note text;";
		if (oldVersion == 2)
			sql = "";

		Log.d("EventsData", "onUpgrade	: " + sql);
		if (sql != null)
			db.execSQL(sql);
		*/
    }
    
    public void deleteAll(SQLiteDatabase db) 
    {
    	//this.db.delete(TABLE_NAME, null, null);
    }
    

    
}
