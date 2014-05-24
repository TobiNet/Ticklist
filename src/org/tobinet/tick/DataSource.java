package org.tobinet.tick;


import java.util.ArrayList;

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
		
		l.setID(cursor.getInt(0));
		l.setListName(cursor.getString(1));
		
		return l;
	}
	
	private Item cursorToItem(Cursor cursor){
		Item i = new Item();
		
		i.setID(cursor.getInt(0));
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
	
	protected ArrayList<ItemList> getAllItemLists() {
		ArrayList<ItemList> ItemListArray = new ArrayList<ItemList>();
		
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
	
	protected ArrayList<Item> getAllItems(int ListID){
		ArrayList<Item> ItemArray = new ArrayList<Item>();
		
		Cursor cursor = database.query("ITEMS", itemcolumns, "ListID = " + Integer.toString(ListID), null, null, null, null);
		cursor.moveToFirst();
		
		if(cursor.getCount() == 0) return ItemArray;
		
		while(cursor.isAfterLast() == false){
			Item i = cursorToItem(cursor);
			ItemArray.add(i);
			cursor.moveToNext();
		}
		
		return ItemArray;
	}

	public void TickPlus(int ItemID, int ListID){
		Cursor cursor = database.rawQuery("UPDATE ITEMS SET Ticks=Ticks+1 WHERE ID="+ItemID+" AND ListID="+ListID+";", null);
		
		cursor.moveToFirst();
	}
	
	public void TickMinus(int ItemID, int ListID){
		Cursor cursor = database.rawQuery("UPDATE ITEMS SET Ticks=Ticks-1 WHERE ID="+ItemID+" AND ListID="+ListID+";", null);
		
		cursor.moveToFirst();
	}
	
	public void RenameItem(Item item, String name){
		ContentValues values = new ContentValues();
		values.put("ListID", item.getListID());
		values.put("ItemName", name);
		values.put("Ticks", item.getTicks());
		
		database.update("ITEMS", values, "ID=" + item.getID(), null);
	}
	
	public void RenameList(ItemList itemlist, String name){
		ContentValues values = new ContentValues();
		values.put("ListName", name);
			
		database.update("ITEMLIST", values, "ID=" + itemlist.getID(), null);
	}
	
	public void RemoveItem(int ID){
		database.delete("ITEMS", "ID="+ID, null);
	}

	public void RemoveItemList(int ID) {
		database.delete("ITEMLIST", "ID="+ID, null);
		database.delete("ITEMS", "ListID="+ID, null);
		
	}
}
