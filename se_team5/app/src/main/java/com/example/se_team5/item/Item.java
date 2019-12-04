package com.example.se_team5.item;

public class Item {
    private int imageResourceID;
    private String name1;
    private String name2;
    private int id;
    private String category;

    public Item (String name1, String name2, int imageResourceID){
        this.name1 = name1;
        this.name2 = name2;
        this.imageResourceID = imageResourceID;
    }

    public Item (String name1, String name2, int imageResourceID, int id){
        this.name1 = name1;
        this.name2 = name2;
        this.imageResourceID = imageResourceID;
        this.id = id;
    }

    public String getName1(){
        return name1;
    }

    public String getName2(){
        return name2;
    }

    public int getImageResourceID(){
        return imageResourceID;
    }

    public int getId() { return id; }
}