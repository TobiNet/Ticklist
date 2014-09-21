package org.tobinet.tick;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLite extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "ticklist.db";
	private static final int DATABASE_VERSION = 2;
	private static MySQLite mInstance = null;

	// @formatter:off
	private static final String TABLE_CREATE_ITEMLIST = "create table ITEMLIST("
			+ "ID integer primary key autoincrement, "
			+ "ListName string"
			+ ")";

	private static final String TABLE_CREATE_ITEMS = "create table ITEMS("
			+ "ID integer primary key autoincrement, "
			+ "ListID integer,"
			+ "ItemName string,"
			+ "Ticks integer"
			+ ")";

	private static final String TABLE_CREATE_TICK = "create table TICKS("
			+ "ID integer primary key autoincrement,"
			+ "ListID integer,"
			+ "ItemID integer,"
			+ "Date string,"
			+ "Tick integer"
			+ ")";
	// @formatter:on

	private static final String EXAMPLE_VALUES_1 = "insert into ITEMLIST (ID, ListName) values (1, 'TickList');";
	private static final String EXAMPLE_VALUES_2 = "insert into ITEMS (ID, ListID, ItemName, Ticks) values (1, 1, 'Element 1', 42);";
	private static final String EXAMPLE_VALUES_3 = "insert into ITEMS (ID, ListID, ItemName, Ticks) values (2, 1, 'Element 2', 1337);";
	private static final String EXAMPLE_VALUES_4 = "insert into ITEMS (ID, ListID, ItemName, Ticks) values (3, 1, 'Element 3', 13);";

	public static MySQLite getInstance(final Context context) {
		if (mInstance == null) {
			mInstance = new MySQLite(context.getApplicationContext());
		}
		return mInstance;
	}

	private MySQLite(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase database) {
		database.execSQL(TABLE_CREATE_ITEMLIST);
		database.execSQL(TABLE_CREATE_ITEMS);
		database.execSQL(TABLE_CREATE_TICK);

		database.execSQL(EXAMPLE_VALUES_1);
		database.execSQL(EXAMPLE_VALUES_2);
		database.execSQL(EXAMPLE_VALUES_3);
		database.execSQL(EXAMPLE_VALUES_4);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
			final int newVersion) {
		if ((newVersion == 2) && (oldVersion == 1)) {
			db.execSQL(TABLE_CREATE_TICK);
		}
	}

}
