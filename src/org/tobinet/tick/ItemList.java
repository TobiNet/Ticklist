package org.tobinet.tick;

public class ItemList {
	private long ID;
	private String itemlistname;
	
	public long getID() {
		return ID;
	}
	public void setID(long ID) {
		this.ID = ID;
	}
	public void setListName(String itemlistname){
		this.itemlistname = itemlistname;
	}
	public String getListName(){
		return itemlistname;
	}
}
