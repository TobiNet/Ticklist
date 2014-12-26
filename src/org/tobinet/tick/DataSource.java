package org.tobinet.tick;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataSource {

    private static final String[] LISTCOLUMNS = {"ID", "ListName"};
    private static final String[] ITEMCOLUMNS = {"ID", "ListID", "ItemName",
            "Ticks", "Color"};
    private static final String[] TICKCOLUMNS = {"ID", "ListID", "ItemID",
            "Date", "Tick"};
    private static final String ITEMLIST = "ITEMLIST";
    private static final String ITEMS = "ITEMS";
    private static final String TICKS = "TICKS";
    private final MySQLite sqlite;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private SQLiteDatabase database;

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

        final Cursor cursor = this.database.query(ITEMLIST, LISTCOLUMNS,
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

        final Cursor cursor = this.database.query(ITEMS, ITEMCOLUMNS, "ID = "
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

        final Cursor cursor = this.database.query(TICKS, TICKCOLUMNS, "ID = "
                + insertID, null, null, null, null);
        cursor.moveToFirst();
    }

    protected List<ItemList> getAllItemLists() {
        final List<ItemList> itemListArray = new ArrayList<ItemList>();

        final Cursor cursor = this.database.query(ITEMLIST, LISTCOLUMNS, null,
                null, null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return itemListArray;
        }

        while (!cursor.isAfterLast()) {
            final ItemList il = this.cursorToItemList(cursor);
            itemListArray.add(il);
            cursor.moveToNext();
        }

        return itemListArray;
    }

    protected List<Item> getAllItems(final int listID) {
        final List<Item> itemArray = new ArrayList<Item>();

        final Cursor cursor = this.database.query(ITEMS, ITEMCOLUMNS,
                "ListID = " + Integer.toString(listID), null, null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return itemArray;
        }

        while (!cursor.isAfterLast()) {
            final Item i = this.cursorToItem(cursor);
            itemArray.add(i);
            cursor.moveToNext();
        }

        return itemArray;
    }

    protected String[] getAllItemListsAsString() {
        final List<ItemList> itemListArray = new ArrayList<ItemList>();

        final Cursor cursor = this.database.query(ITEMLIST, LISTCOLUMNS, null,
                null, null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return new String[0];
        }

        while (!cursor.isAfterLast()) {
            final ItemList il = this.cursorToItemList(cursor);
            itemListArray.add(il);
            cursor.moveToNext();
        }

        final String[] itemlist = new String[itemListArray.size()];

        int elem = 0;

        for (final ItemList il : itemListArray) {
            itemlist[elem] = il.getListName();
            elem++;
        }

        return itemlist;
    }

    public double getTicksperDay(final int listID, final int itemID) {
        int days = 1, ticks = 0;
        Date firstday, now;
        final Cursor cursor = this.database.query(TICKS, TICKCOLUMNS,
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

            while (!cursor.isAfterLast()) {
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

    public void renameList(final int listID, final String name) {
        final ContentValues values = new ContentValues();
        values.put("ListName", name);

        this.database.update(ITEMLIST, values, "ID=" + listID, null);
    }

    public void resetItem(final int id) {
        final Cursor cursor = this.database.rawQuery(
                "UPDATE ITEMS SET Ticks = 0 WHERE ID = " + id + ";", null);
        cursor.moveToFirst();
        this.database.delete(TICKS, "ItemID=" + id, null);
    }

    public void removeItem(final int iD) {
        this.database.delete(ITEMS, "ID=" + iD, null);
        this.database.delete(TICKS, "ItemID=" + iD, null);
    }

    public void removeItemList(final int iD) {
        this.database.delete(ITEMLIST, "ID=" + iD, null);
        this.database.delete(ITEMS, "ListID=" + iD, null);
        this.database.delete(TICKS, "ListID=" + iD, null);
    }
}
