package org.tobinet.tick;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {
	private SQLiteDatabase database;
	private MySQLite sqlite;
	private String[] listcolumns = { "ID", "ListName" };
	private String[] itemcolumns = { "ID", "ListID", "ItemName", "Ticks" };
	
	public DataSource (Context context) {
		sqlite = MySQLite.getInstance(context);
	}
	
	public void open() throws SQLException {
		database = sqlite.getWritableDatabase();
	}

	public void close() {
		sqlite.close();
	}
	
	private ItemList cursorToItemList(Cursor cursor){
		ItemList l = new ItemList();
		
		l.setID(cursor.getLong(0));
		l.setListName(cursor.getString(1));
		
		return l;
	}
	
	private Item cursorToItem(Cursor cursor){
		Item i = new Item();
		
		i.setID(cursor.getLong(0));
		i.setListID(cursor.getInt(1));
		i.setItemName(cursor.getString(2));
		i.setTicks(cursor.getInt(3));
		
		return i;
	}
	
	public ItemList createItemList(String listname){
		ContentValues values = new ContentValues();
		values.put("Listname", listname);
		
		long insertID = database.insert("ITEMLIST", null, values);
		
		Cursor cursor = database.query("ITEMLIST", listcolumns, "ID = " + insertID, null, null, null, null);
		cursor.moveToFirst();
		
		return cursorToItemList(cursor);
	}
	
	public Item createItem(int listid, String itemname, int ticks){
		ContentValues values = new ContentValues();
		values.put("ListID", listid);
		values.put("ItemName", itemname);
		values.put("Ticks", ticks);
		
		long insertID = database.insert("ITEMS", null, values);
		
		Cursor cursor = database.query("ITEMS", itemcolumns, "ID = " + insertID, null, null, null, null);
		cursor.moveToFirst();
		
		return cursorToItem(cursor);		
	}
	
	protected List<ItemList> getAllItemLists() {
		List<ItemList> ItemListArray = new ArrayList<ItemList>();
		
		Cursor cursor = database.query("ITEMLIST", listcolumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		if(cursor.getCount() == 0) return ItemListArray;
		
		while(cursor.isAfterLast() == false){
			ItemList il = cursorToItemList(cursor);
			ItemListArray.add(il);
			cursor.moveToNext();
		}
		
		return ItemListArray;
	}
	
	protected List<Item> getAllItems(){
		List<Item> ItemArray = new ArrayList<Item>();
		
		Cursor cursor = database.query("ITEMS", itemcolumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		if(cursor.getCount() == 0) return ItemArray;
		
		while(cursor.isAfterLast() == false){
			Item i = cursorToItem(cursor);
			ItemArray.add(i);
			cursor.moveToNext();
		}
		
		return ItemArray;
	}
}
