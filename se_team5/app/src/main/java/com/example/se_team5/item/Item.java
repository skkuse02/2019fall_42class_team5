package com.example.se_team5.item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Item {
    /*item(재료)를 저장하고 다루는 객체*/

    private String name;            //아이템의 이름
    private String category;        //아이템의 카테고리
    private int id;                 //아이템의 id 1부터 시작하는 정수로 DB의 id와 같다.
    private int imageResourceID;    //아이템의 이미지의 drawable number


    public Item (String name, int imageResourceID){
        this.name = name;
        this.imageResourceID = imageResourceID;
    }

    public Item (String name, int imageResourceID, int id){
        this.name = name;
        this.imageResourceID = imageResourceID;
        this.id = id;
    }

    public Item (String name, String category, int imageResourceID, int id){
        this.name = name;
        this.category = category;
        this.imageResourceID = imageResourceID;
        this.id = id;
    }

    /*아이템의 이름을 반환하는 메소드*/
    public String getName(){ return name; }

    /*아이템의 image id number를 반환하는 메소드*/
    public int getImageResourceID(){
        return imageResourceID;
    }
    /*아이템의 id를 반환하는 메소드*/
    public int getId() { return id; }

    /*JSON 형태의 String을 Item ArrayList로 파싱하여 반환하는 메소드*/
    public static ArrayList<Item> gsonParsing(String gson){
        ArrayList<Item> itemList = new ArrayList<Item>();
        
        try{
            JSONObject jsonObject = new JSONObject(gson);
            JSONArray ItemArray = jsonObject.getJSONArray("items");

            /*아이템의 개수만큼 반복하여 item 객체를 생성하고 리스트에 추가*/
            for(int i=0; i<ItemArray.length(); i++){
                JSONObject temp = ItemArray.getJSONObject(i);
                itemList.add(new Item(temp.getString("name"),temp.getString("category"), temp.getInt("image"), temp.getInt("id")));
            }

            return itemList;
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}