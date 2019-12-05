package com.example.se_team5.item;

import java.util.HashMap;

public class ItemHashMap {
    private final HashMap<Integer,String> Item = new HashMap<Integer, String>();
    public ItemHashMap(){
        Item.put(13,"대파");
        Item.put(16,"간장");
        Item.put(17,"설탕");
        Item.put(18,"참기름");
        Item.put(20,"마늘");
        Item.put(24,"닭가슴살");
        Item.put(25,"꿀");
    }
    public String itemName(Integer id){
        return Item.get(id);
    }

    public int getItemNum(){
        return Item.size();
    }
}
