package org.tobinet.tick;

public class Item {
	private long ID;
	private long ItemListID;
	private String itemname;
	private int ticks;
	
	public long getID() {
		return ID;
	}	
	public void setID(long ID) {
		this.ID = ID;
	}
	public long getListID() {
		return ItemListID;
	}	
	public void setListID(long ItemListID) {
		this.ItemListID = ItemListID;
	}	
	public void setItemName(String itemname){
		this.itemname = itemname;
	}
	public String getItemName(){
		return itemname;
	}
	public void setTicks(int ticks){
		this.ticks = ticks;
	}
	public int getTicks(){
		return ticks;
	}
}
