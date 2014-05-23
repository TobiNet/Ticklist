package org.tobinet.tick;

public class ItemList {
	private int ID;
	private String itemlistname;
	
	public int getID() {
		return ID;
	}
	public void setID(int ID) {
		this.ID = ID;
	}
	public void setListName(String itemlistname){
		this.itemlistname = itemlistname;
	}
	public String getListName(){
		return itemlistname;
	}
}
