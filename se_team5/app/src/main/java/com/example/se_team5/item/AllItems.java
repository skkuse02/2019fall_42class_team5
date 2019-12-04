package com.example.se_team5.item;

import com.example.se_team5.R;

import java.util.ArrayList;
import java.util.Arrays;

public class AllItems {

    private final int itemNum = 8;
    private final ArrayList<Item> AllItem = new ArrayList<Item>(Arrays.asList(
            new Item("계란", "", R.drawable.egg, 0),
            new Item("김", "", R.drawable.gim, 1),
            new Item("닭고기", "가슴살", R.drawable.chicken_breast, 2),
            new Item("대파", "", R.drawable.green_onion, 3),
            new Item("양파", "", R.drawable.onion, 4),
            new Item("삼겹살", "", R.drawable.pork_belly, 5),
            new Item("쪽파", "", R.drawable.sect, 6),
            new Item("소고기", "국거리", R.drawable.soup_beef, 7)
    ));

    public ArrayList<Item> getAllItem(){
        return AllItem;
    }

    public Item findItem(int id){
        return AllItem.get(id);
    }

    public int getItemNum(){
        return itemNum;
    }
}
