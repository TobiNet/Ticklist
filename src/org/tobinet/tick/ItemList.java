package org.tobinet.tick;

public class ItemList {
	private int iD;
	private String itemlistname;

	public int getID() {
		return this.iD;
	}

	public void setID(final int ID) {
		this.iD = ID;
	}

	public void setListName(final String itemlistname) {
		this.itemlistname = itemlistname;
	}

	public String getListName() {
		return this.itemlistname;
	}
}
