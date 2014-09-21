package org.tobinet.tick;

public class Item {
	private int ID;
	private int ItemListID;
	private String itemname;
	private int ticks;
	private int color;

	public int getID() {
		return this.ID;
	}

	public void setID(final int ID) {
		this.ID = ID;
	}

	public int getListID() {
		return this.ItemListID;
	}

	public void setListID(final int ItemListID) {
		this.ItemListID = ItemListID;
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
