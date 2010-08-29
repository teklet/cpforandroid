package com.android.CPForAndroid;
 
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
 
/***
 * Helper singleton class to manage SQLiteDatabase Create and Restore
 * 
 * @author alessandrofranzi
 * http://www.androidworld.it/dev/tutorials/sqlite_implementazione_di_sqliteopenhelper
 * http://creativecommons.org/licenses/by-sa/3.0/
 */
public class SQLiteDatabaseAdapter extends SQLiteOpenHelper{
	private static SQLiteDatabase sqliteDb;  
	private static SQLiteDatabaseAdapter instance;
	private static final int DATABASE_VERSION = 1;  
	// the default database path is : /data/data/pkgNameOfYourApplication/databases/
	private static String DB_PATH_PREFIX = "/data/data/";
	private static String DB_PATH_SUFFIX = "/databases/";
	private static final String TAG = "SQLiteDatabaseAdapter";  
	private Context context;
	private String _sql = "";//- JPSIII 8/28/10
 
	/***
	 * Contructor
	 * 
	 * @param context	: app context
	 * @param name		: database name
	 * @param factory	: cursor Factory
	 * @param version	: DB version
	 * removed private - JPSIII 8/28/10
	 * added @param sql	: create table sql statement string - JPSIII 8/28/10
	 */
	SQLiteDatabaseAdapter(Context context, String name, CursorFactory factory, int version, String sql) {  
		super(context, name, factory, version);  
		this.context = context;
		this._sql = sql;
		Log.i(TAG, "Create or Open database : "+name);
	} 
 
	/***
	 * Initialize method
	 * 
	 * @param context		: application context
	 * @param databaseName	: database name
	 * added @param s	: create table sql statement string - JPSIII 8/28/10
	 */
	private static void initialize(Context context,String databaseName, String s) {  
		if(instance == null) {  
			/**
			 * Try to check if there is an Original copy of DB in asset Directory
			 */
			if (!checkDatabase(context,databaseName)){
				// if not exists, I try to copy from asset dir 
				try {
					copyDataBase(context,databaseName);
				} catch (IOException e) {
					Log.e(TAG,"Database "+databaseName+" does not exists and there is no Original Version in Asset dir");
				}
			}
 
			Log.i(TAG, "Try to create instance of database ("+databaseName+")");
			instance = new SQLiteDatabaseAdapter(context, databaseName, null, DATABASE_VERSION, "");
			sqliteDb = instance.getWritableDatabase();
			Log.i(TAG, "instance of database ("+databaseName+") created !");
		}  
	}
 
	/***
	 * Static method for getting singleton instance
	 * 
	 * @param context		: application context
	 * @param databaseName	: database name
	 * @return				: singleton instance
	 * added @param s	: create table sql statement string - JPSIII 8/28/10
	 */
	public static final SQLiteDatabaseAdapter getInstance(Context context,String databaseName, String s) {  
		initialize(context,databaseName, s);  
		return instance;  
	}  
	/***
	 * Method to get database instance 
	 * @return	database instance
	 */
	public SQLiteDatabase getDatabase() {  
		return sqliteDb;  
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		Log.d(TAG, "onCreate : nothing to do");
    	/* this method modified by JPSIII 8/28/10 with below optional code
    	// THIS IS AN UPGRADE TEMPLATE BELOW; YOU WILL NEED TO CUSTOMIZE THIS FOR YOUR DB UPGRADE
    	String table_name = "MY_NEWNAME";		//CUSTOMIZE
    	String col_name   = "MY_NEWCOLNAME";	//CUSTOMIZE
		if (oldVersion >= newVersion)
			return;
		String sql = null;
		if (oldVersion == 1) 
			sql = "alter table " + table_name + " add "+ col_name +" text;";//Changes table name to table_name and adds a column col_name
		if (oldVersion == 2)
			sql = "";
		Log.d("EventsData", "onUpgrade	: " + sql);
		if (sql != null)
			db.execSQL(sql);
			
		//OR TO DELETE THE TABLE AND REPLACE IT:
		//Log.w(tag, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		//db.execSQL("DROP TABLE IF EXISTS " + table_name);
	    //onCreate(db);
		*/
	} 
 
	/***
	 * Method for Copy the database from asset directory to application's data directory
	 * @param databaseName	: database name
	 * @throws IOException	: exception if file does not exists
	 */
	private void copyDataBase (String databaseName) throws IOException{
		copyDataBase(context,databaseName);
	}
 
	/***
	 * Static method for copy the database from asset directory to application's data directory
	 * @param aContext		: application context
	 * @param databaseName	: database name
	 * @throws IOException	: exception if file does not exists
	 */
	private static void copyDataBase(Context aContext,String databaseName) throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = aContext.getAssets().open(databaseName);
 
    	// Path to the just created empty db
    	String outFileName = getDatabasePath(aContext,databaseName);
 
    	Log.i(TAG,"Trying to copy local DB to : "+outFileName);
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    	Log.i(TAG,"DB ("+databaseName+") copied!");
	}
 
	/***
	 * Method to check if database exists in application's data directory
	 * @param databaseName	: database name
	 * @return				: boolean (true if exists)
	 */
	public boolean checkDatabase(String databaseName){
		return checkDatabase(context,databaseName);
	}
 
	/***
	 * Static Method to check if database exists in application's data directory
	 * @param aContext		: application context
	 * @param databaseName	: database name
	 * @return				: boolean (true if exists)
	 */
	public static boolean checkDatabase(Context aContext,String databaseName){
		SQLiteDatabase checkDB = null;
 
		try{
			String myPath = getDatabasePath(aContext,databaseName);
 
	    	Log.i(TAG,"Trying to conntect to : "+myPath);
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
			Log.i(TAG,"Database "+databaseName+" found!");
			checkDB.close();
		}catch(SQLiteException e){
			Log.i(TAG,"Database "+databaseName+" does not exists!");
 
		}
 
		return checkDB != null ? true : false;
	}
 
	/***
	 * Method that returns database path in the application's data directory
	 * @param databaseName	: database name
	 * @return				: complete path
	 */
	private String getDatabasePath(String databaseName){
		return getDatabasePath(context,databaseName);
	}
 
	/***
	 * Static Method that returns database path in the application's data directory
	 * @param aContext		: application context
	 * @param databaseName	: database name
	 * @return				: complete path
	 */
	private static String getDatabasePath(Context aContext,String databaseName){
		return DB_PATH_PREFIX + aContext.getPackageName() +DB_PATH_SUFFIX+ databaseName;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(this._sql);//- JPSIII 8/28/10
		
	}	
}
