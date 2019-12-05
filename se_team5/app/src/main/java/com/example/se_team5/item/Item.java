package com.example.se_team5.item;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Item {
    private int imageResourceID;
    private String name;
    private int id;
    private String category;

    public Item (String name, int imageResourceID){
        this.name = name;
        this.imageResourceID = imageResourceID;
    }

    public Item (String name, String name2, int imageResourceID, int id){
        this.name = name;
        this.imageResourceID = imageResourceID;
        this.id = id;
    }

    public String getName(){ return name; }

    public int getImageResourceID(){
        return imageResourceID;
    }

    public int getId() { return id; }

    public static ArrayList<Item> gsonParsing(String gson){
        ArrayList<Item> itemList = new ArrayList<Item>();
        
        try{
            JSONObject jsonObject = new JSONObject(gson);
            JSONArray ItemArray = jsonObject.getJSONArray("items");
            Log.d("name", String.valueOf(ItemArray.length()));
            Log.d("name", "dggdgdsdddddddddddddddddddddddddddddddd");

            for(int i=0; i<ItemArray.length(); i++){
                JSONObject temp = ItemArray.getJSONObject(i);
                itemList.add(new Item(temp.getString("name"), temp.getInt("image")));
                Log.d("name", temp.getString("name"));
                Log.d("image", temp.getString("image"));
            }

            return itemList;
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}