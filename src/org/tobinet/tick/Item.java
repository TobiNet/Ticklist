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

    public void setID(final int iD) {
        this.iD = iD;
    }

    public int getListID() {
        return this.itemListID;
    }

    public void setListID(final int itemListID) {
        this.itemListID = itemListID;
    }

    public String getItemName() {
        return this.itemname;
    }

    public void setItemName(final String itemname) {
        this.itemname = itemname;
    }

    public int getTicks() {
        return this.ticks;
    }

    public void setTicks(final int ticks) {
        this.ticks = ticks;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(final int color) {
        this.color = color;
    }
}
