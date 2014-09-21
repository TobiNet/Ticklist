package org.tobinet.tick;

public class Tick {

	private int iD;
	private int listID;
	private int itemID;
	private String date;
	private int tick;

	public void setID(final int ID) {
		this.iD = ID;
	}

	public int getID() {
		return this.iD;
	}

	public void setListID(final int ListID) {
		this.listID = ListID;
	}

	public int getListID() {
		return this.listID;
	}

	public void setItemID(final int ItemID) {
		this.itemID = ItemID;
	}

	public int getItemID() {
		return this.itemID;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public String getDate() {
		return this.date;
	}

	public void setTick(final int tick) {
		this.tick = tick;
	}

	public int getTick() {
		return this.tick;
	}

}
