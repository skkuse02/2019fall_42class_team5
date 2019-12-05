package com.example.se_team5.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.se_team5.R;

import java.util.ArrayList;


public class ItemListViewAdapter extends BaseAdapter {

    private ArrayList<Item> myBasketList = new ArrayList<Item>() ;

    public ItemListViewAdapter(ArrayList<Item> dataList) {
        myBasketList = dataList;
    }

    @Override
    public int getCount() {
        return myBasketList.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        ImageView iconImageView = convertView.findViewById(R.id.imageView1) ;
        TextView titleTextView = convertView.findViewById(R.id.textView1) ;


        Item listViewItem = myBasketList.get(position);

        iconImageView.setImageResource(listViewItem.getImageResourceID());
        titleTextView.setText(listViewItem.getName());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return myBasketList.get(position) ;
    }
}