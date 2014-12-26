package org.tobinet.tick;

public class Tick {

    private int iD;
    private int listID;
    private int itemID;
    private String date;
    private int tick;

    public int getID() {
        return this.iD;
    }

    public void setID(final int iD) {
        this.iD = iD;
    }

    public int getListID() {
        return this.listID;
    }

    public void setListID(final int listID) {
        this.listID = listID;
    }

    public int getItemID() {
        return this.itemID;
    }

    public void setItemID(final int itemID) {
        this.itemID = itemID;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public int getTick() {
        return this.tick;
    }

    public void setTick(final int tick) {
        this.tick = tick;
    }

}
