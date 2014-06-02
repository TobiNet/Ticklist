package org.tobinet.tick;

public class Tick {

	private int ID;
	private int ListID;
	private int ItemID;
	private String date;
	private int tick;
	
	public void setID (int ID){
		this.ID = ID;
	}
	public int getID (){
		return ID;
	}

	public void setListID (int ListID){
		this.ListID = ListID;
	}
	public int getListID (){
		return ListID;
	}

	public void setItemID (int ItemID){
		this.ItemID = ItemID;
	}
	public int getItemID (){
		return ItemID;
	}

	public void setDate (String date){
		this.date = date;
	}
	public String getDate (){
		return date;
	}
	
	public void setTick (int tick){
		this.tick = tick;
	}
	public int getTick (){
		return tick;
	}
	
	
}
