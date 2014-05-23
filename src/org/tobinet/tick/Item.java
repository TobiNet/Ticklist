package org.tobinet.tick;

public class Item {
	private int ID;
	private int ItemListID;
	private String itemname;
	private int ticks;
	
	public int getID() {
		return ID;
	}	
	public void setID(int ID) {
		this.ID = ID;
	}
	public int getListID() {
		return ItemListID;
	}	
	public void setListID(int ItemListID) {
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
