package org.tobinet.tick;

public class Tick {

	private int ID;
	private int ListID;
	private int ItemID;
	private String date;
	private int tick;

	public void setID(final int ID) {
		this.ID = ID;
	}

	public int getID() {
		return this.ID;
	}

	public void setListID(final int ListID) {
		this.ListID = ListID;
	}

	public int getListID() {
		return this.ListID;
	}

	public void setItemID(final int ItemID) {
		this.ItemID = ItemID;
	}

	public int getItemID() {
		return this.ItemID;
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
