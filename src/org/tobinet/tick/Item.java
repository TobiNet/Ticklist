package org.tobinet.tick;

public class Item {
	private int iD;
	private int itemListID;
	private String itemname;
	private int ticks;
	private int color;

	public int getID() {
		return this.iD;
	}

	public void setID(final int ID) {
		this.iD = ID;
	}

	public int getListID() {
		return this.itemListID;
	}

	public void setListID(final int ItemListID) {
		this.itemListID = ItemListID;
	}

	public void setItemName(final String itemname) {
		this.itemname = itemname;
	}

	public void setColor(final int color) {
		this.color = color;
	}

	public String getItemName() {
		return this.itemname;
	}

	public void setTicks(final int ticks) {
		this.ticks = ticks;
	}

	public int getTicks() {
		return this.ticks;
	}

	public int getColor() {
		return this.color;
	}
}
