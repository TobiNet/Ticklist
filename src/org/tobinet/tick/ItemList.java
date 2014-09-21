package org.tobinet.tick;

public class ItemList {
	private int ID;
	private String itemlistname;

	public int getID() {
		return this.ID;
	}

	public void setID(final int ID) {
		this.ID = ID;
	}

	public void setListName(final String itemlistname) {
		this.itemlistname = itemlistname;
	}

	public String getListName() {
		return this.itemlistname;
	}
}
