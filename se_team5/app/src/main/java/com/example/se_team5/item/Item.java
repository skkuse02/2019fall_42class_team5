package com.example.se_team5.item;

public class Item {
    private int imageResourceID;
    private String name;
    private int id;

    public Item (String name, int imageResourceID){
        this.name = name;
        this.imageResourceID = imageResourceID;
    }

    public Item (String name, int imageResourceID, int id){
        this.name = name;
        this.imageResourceID = imageResourceID;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public int getImageResourceID(){
        return imageResourceID;
    }

    public int getId() { return id; }
}

