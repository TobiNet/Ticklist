package org.tobinet.tick;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class MySQLite extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "ticklist.db";
	private static final int DATABASE_VERSION = 1;
	private static MySQLite mInstance = null;
	
	private static final String TABLE_CREATE_LIST = "create table ITEMLIST("
			+ "ID integer primary key autoincrement, "
			+ "ListName string"
			+ ")";
	
	private static final String TABLE_CREATE_LISTITEMS = "create table ITEMS("
			+ "ID integer primary key autoincrement, "
			+ "ListID integer,"
			+ "ItemName string,"
			+ "Ticks int"
			+ ")";
	
	public static MySQLite getInstance(Context context) {
		if (mInstance == null){
			mInstance = new MySQLite(context.getApplicationContext());
		}
		return mInstance;
	}
	
	private MySQLite (Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE_LIST);
		database.execSQL(TABLE_CREATE_LISTITEMS);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
