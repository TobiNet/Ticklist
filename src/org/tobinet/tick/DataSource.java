package org.tobinet.tick;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {

	private SQLiteDatabase database;
	private final MySQLite sqlite;
	private static final String[] listcolumns = { "ID", "ListName" };
	private static final String[] itemcolumns = { "ID", "ListID", "ItemName",
			"Ticks", "Color" };
	private static final String[] tickcolumns = { "ID", "ListID", "ItemID",
			"Date", "Tick" };

	private static final String ITEMLIST = "ITEMLIST";
	private static final String ITEMS = "ITEMS";
	private static final String TICKS = "TICKS";

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
		i.setColor(cursor.getInt(4));

		return i;
	}

	public ItemList createItemList(final String listname) {
		final ContentValues values = new ContentValues();
		values.put("Listname", listname);

		final long insertID = this.database.insert(ITEMLIST, null, values);

		final Cursor cursor = this.database.query(ITEMLIST, listcolumns,
				"ID = " + insertID, null, null, null, null);
		cursor.moveToFirst();

		return this.cursorToItemList(cursor);
	}

	public Item createItem(final int listid, final String itemname,
			final int ticks, final int color) {
		final ContentValues values = new ContentValues();
		values.put("ListID", listid);
		values.put("ItemName", itemname);
		values.put("Ticks", ticks);
		values.put("Color", color);

		final long insertID = this.database.insert(ITEMS, null, values);

		final Cursor cursor = this.database.query(ITEMS, itemcolumns, "ID = "
				+ insertID, null, null, null, null);
		cursor.moveToFirst();

		return this.cursorToItem(cursor);
	}

	private void createTick(final int listid, final int itemid, final int tick) {
		final ContentValues values = new ContentValues();
		values.put("ListID", listid);
		values.put("ItemID", itemid);
		values.put("Date", this.sdf.format(Calendar.getInstance().getTime()));
		values.put("Tick", tick);

		final long insertID = this.database.insert(TICKS, null, values);

		final Cursor cursor = this.database.query(TICKS, tickcolumns, "ID = "
				+ insertID, null, null, null, null);
		cursor.moveToFirst();
	}

	protected List<ItemList> getAllItemLists() {
		final List<ItemList> ItemListArray = new ArrayList<ItemList>();

		final Cursor cursor = this.database.query(ITEMLIST, listcolumns, null,
				null, null, null, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0) {
			return ItemListArray;
		}

		while (!cursor.isAfterLast()) {
			final ItemList il = this.cursorToItemList(cursor);
			ItemListArray.add(il);
			cursor.moveToNext();
		}

		return ItemListArray;
	}

	protected List<Item> getAllItems(final int ListID) {
		final List<Item> ItemArray = new ArrayList<Item>();

		final Cursor cursor = this.database.query(ITEMS, itemcolumns,
				"ListID = " + Integer.toString(ListID), null, null, null, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0) {
			return ItemArray;
		}

		while (!cursor.isAfterLast()) {
			final Item i = this.cursorToItem(cursor);
			ItemArray.add(i);
			cursor.moveToNext();
		}

		return ItemArray;
	}

	protected String[] getAllItemListsAsString() {
		final List<ItemList> ItemListArray = new ArrayList<ItemList>();

		final Cursor cursor = this.database.query(ITEMLIST, listcolumns, null,
				null, null, null, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0) {
			return new String[0];
		}

		while (!cursor.isAfterLast()) {
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

	public double getTicksperDay(final int listID, final int itemID) {
		int days = 1, ticks = 0;
		Date firstday, now;
		final Cursor cursor = this.database.query(TICKS, tickcolumns,
				"ListID = " + listID + " AND ItemID = " + itemID, null, null,
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

	public void tickPlus(final int itemID, final int listID) {
		final Cursor cursor = this.database.rawQuery(
				"UPDATE ITEMS SET Ticks=Ticks+1 WHERE ID=" + itemID
						+ " AND ListID=" + listID + ";", null);

		cursor.moveToFirst();

		this.createTick(listID, itemID, +1);
	}

	public void tickMinus(final int itemID, final int listID) {
		final Cursor cursor = this.database.rawQuery(
				"UPDATE ITEMS SET Ticks=Ticks-1 WHERE ID=" + itemID
						+ " AND ListID=" + listID + ";", null);

		cursor.moveToFirst();

		this.createTick(listID, itemID, -1);
	}

	public void setColor(final Item item, final int color) {
		final ContentValues values = new ContentValues();

		values.put("ListID", item.getListID());
		values.put("ItemName", item.getItemName());
		values.put("Ticks", item.getTicks());
		values.put("Color", color);

		this.database.update(ITEMS, values, "ID=" + item.getID(), null);
	}

	public void renameItem(final Item item, final String name) {
		final ContentValues values = new ContentValues();
		values.put("ListID", item.getListID());
		values.put("ItemName", name);
		values.put("Ticks", item.getTicks());
		values.put("Color", item.getColor());

		this.database.update(ITEMS, values, "ID=" + item.getID(), null);
	}

	public void renameList(final int ListID, final String name) {
		final ContentValues values = new ContentValues();
		values.put("ListName", name);

		this.database.update(ITEMLIST, values, "ID=" + ListID, null);
	}

	public void resetItem(final int id) {
		final Cursor cursor = this.database.rawQuery(
				"UPDATE ITEMS SET Ticks = 0 WHERE ID = " + id + ";", null);
		cursor.moveToFirst();
		this.database.delete(TICKS, "ItemID=" + id, null);
	}

	public void removeItem(final int ID) {
		this.database.delete(ITEMS, "ID=" + ID, null);
		this.database.delete(TICKS, "ItemID=" + ID, null);
	}

	public void removeItemList(final int ID) {
		this.database.delete(ITEMLIST, "ID=" + ID, null);
		this.database.delete(ITEMS, "ListID=" + ID, null);
		this.database.delete(TICKS, "ListID=" + ID, null);
	}
}
