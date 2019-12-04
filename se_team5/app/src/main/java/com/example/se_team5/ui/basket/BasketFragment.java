package com.example.se_team5.ui.basket;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.se_team5.PutActivity;
import com.example.se_team5.R;
import com.example.se_team5.item.AllItems;
import com.example.se_team5.item.ItemListViewAdapter;

import static androidx.lifecycle.ViewModelProviders.*;

public class BasketFragment extends Fragment {

    ListView listview ;
    ItemListViewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_basket, container, false);

        Button putButton = root.findViewById(R.id.putBasketButton);

        listview = (ListView) root.findViewById(R.id.list_item);
        adapter = new ItemListViewAdapter(new AllItems().getAllItem()) ;
        listview.setAdapter(adapter);

        //put
        putButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), PutActivity.class);
                startActivity(intent);


//                new putItemInRefrigerator(RefrigeratorFragment.this).execute("/user/refrigerator", "{\"username\":\"hj323\",\"items\":[1,2]}");
            }
        });

        return root;
    }
}