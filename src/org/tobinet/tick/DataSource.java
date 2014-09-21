package org.tobinet.tick;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {
	private SQLiteDatabase database;
	private final MySQLite sqlite;
	private final String[] listcolumns = { "ID", "ListName" };
	private final String[] itemcolumns = { "ID", "ListID", "ItemName", "Ticks" };
	private final String[] tickcolumns = { "ID", "ListID", "ItemID", "Date",
			"Tick" };

	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public DataSource(final Context context) {
		this.sqlite = MySQLite.getInstance(context);
	}

	public void open() throws SQLException {
		this.database = this.sqlite.getWritableDatabase();
	}

	public void close() {
		this.sqlite.close();
	}

	private ItemList cursorToItemList(final Cursor cursor) {
		final ItemList l = new ItemList();

		l.setID(cursor.getInt(0));
		l.setListName(cursor.getString(1));

		return l;
	}

	private Item cursorToItem(final Cursor cursor) {
		final Item i = new Item();

		i.setID(cursor.getInt(0));
		i.setListID(cursor.getInt(1));
		i.setItemName(cursor.getString(2));
		i.setTicks(cursor.getInt(3));

		return i;
	}

	public ItemList createItemList(final String listname) {
		final ContentValues values = new ContentValues();
		values.put("Listname", listname);

		final long insertID = this.database.insert("ITEMLIST", null, values);

		final Cursor cursor = this.database.query("ITEMLIST", this.listcolumns,
				"ID = " + insertID, null, null, null, null);
		cursor.moveToFirst();

		return this.cursorToItemList(cursor);
	}

	public Item createItem(final int listid, final String itemname,
			final int ticks) {
		final ContentValues values = new ContentValues();
		values.put("ListID", listid);
		values.put("ItemName", itemname);
		values.put("Ticks", ticks);

		final long insertID = this.database.insert("ITEMS", null, values);

		final Cursor cursor = this.database.query("ITEMS", this.itemcolumns,
				"ID = " + insertID, null, null, null, null);
		cursor.moveToFirst();

		return this.cursorToItem(cursor);
	}

	private void createTick(final int listid, final int itemid, final int tick) {
		final ContentValues values = new ContentValues();
		values.put("ListID", listid);
		values.put("ItemID", itemid);
		values.put("Date", this.sdf.format(Calendar.getInstance().getTime()));
		values.put("Tick", tick);

		final long insertID = this.database.insert("TICKS", null, values);

		final Cursor cursor = this.database.query("TICKS", this.tickcolumns,
				"ID = " + insertID, null, null, null, null);
		cursor.moveToFirst();
	}

	protected ArrayList<ItemList> getAllItemLists() {
		final ArrayList<ItemList> ItemListArray = new ArrayList<ItemList>();

		final Cursor cursor = this.database.query("ITEMLIST", this.listcolumns,
				null, null, null, null, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0) {
			return ItemListArray;
		}

		while (cursor.isAfterLast() == false) {
			final ItemList il = this.cursorToItemList(cursor);
			ItemListArray.add(il);
			cursor.moveToNext();
		}

		return ItemListArray;
	}

	protected ArrayList<Item> getAllItems(final int ListID) {
		final ArrayList<Item> ItemArray = new ArrayList<Item>();

		final Cursor cursor = this.database.query("ITEMS", this.itemcolumns,
				"ListID = " + Integer.toString(ListID), null, null, null, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0) {
			return ItemArray;
		}

		while (cursor.isAfterLast() == false) {
			final Item i = this.cursorToItem(cursor);
			ItemArray.add(i);
			cursor.moveToNext();
		}

		return ItemArray;
	}

	protected String[] getAllItemListsAsString() {
		final ArrayList<ItemList> ItemListArray = new ArrayList<ItemList>();

		final Cursor cursor = this.database.query("ITEMLIST", this.listcolumns,
				null, null, null, null, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0) {
			return new String[0];
		}

		while (cursor.isAfterLast() == false) {
			final ItemList il = this.cursorToItemList(cursor);
			ItemListArray.add(il);
			cursor.moveToNext();
		}

		final String[] itemlist = new String[ItemListArray.size()];

		int elem = 0;

		for (final ItemList il : ItemListArray) {
			itemlist[elem] = il.getListName();
			elem++;
		}

		return itemlist;
	}

	public double getTicksperDay(final int ListID, final int ItemID) {
		int days = 1, ticks = 0;
		Date firstday, now;
		final Cursor cursor = this.database.query("TICKS", this.tickcolumns,
				"ListID = " + ListID + " AND ItemID = " + ItemID, null, null,
				null, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0) {
			return 0;
		}
		try {
			firstday = this.sdf.parse(cursor.getString(3));
			now = Calendar.getInstance().getTime();

			days = (int) ((now.getTime() - firstday.getTime()) / (1000 * 60 * 60 * 24));
			if (days == 0) {
				days = 1;
			}

			while (cursor.isAfterLast() == false) {
				ticks = ticks + cursor.getInt(4);
				cursor.moveToNext();
			}
		} catch (final ParseException ex) {
			ticks = days = 1;
		}

		return (double) ticks / (double) days;
	}

	public void TickPlus(final int ItemID, final int ListID) {
		final Cursor cursor = this.database.rawQuery(
				"UPDATE ITEMS SET Ticks=Ticks+1 WHERE ID=" + ItemID
						+ " AND ListID=" + ListID + ";", null);

		cursor.moveToFirst();

		this.createTick(ListID, ItemID, +1);
	}

	public void TickMinus(final int ItemID, final int ListID) {
		final Cursor cursor = this.database.rawQuery(
				"UPDATE ITEMS SET Ticks=Ticks-1 WHERE ID=" + ItemID
						+ " AND ListID=" + ListID + ";", null);

		cursor.moveToFirst();

		this.createTick(ListID, ItemID, -1);
	}

	public void RenameItem(final Item item, final String name) {
		final ContentValues values = new ContentValues();
		values.put("ListID", item.getListID());
		values.put("ItemName", name);
		values.put("Ticks", item.getTicks());

		this.database.update("ITEMS", values, "ID=" + item.getID(), null);
	}

	public void RenameList(final int ListID, final String name) {
		final ContentValues values = new ContentValues();
		values.put("ListName", name);

		this.database.update("ITEMLIST", values, "ID=" + ListID, null);
	}

	public void ResetItem(final int id) {
		final Cursor cursor = this.database.rawQuery(
				"UPDATE ITEMS SET Ticks = 0 WHERE ID = " + id + ";", null);
		cursor.moveToFirst();
		this.database.delete("TICKS", "ItemID=" + id, null);
	}

	public void RemoveItem(final int ID) {
		this.database.delete("ITEMS", "ID=" + ID, null);
		this.database.delete("TICKS", "ItemID=" + ID, null);
	}

	public void RemoveItemList(final int ID) {
		this.database.delete("ITEMLIST", "ID=" + ID, null);
		this.database.delete("ITEMS", "ListID=" + ID, null);
		this.database.delete("TICKS", "ListID=" + ID, null);
	}
}
