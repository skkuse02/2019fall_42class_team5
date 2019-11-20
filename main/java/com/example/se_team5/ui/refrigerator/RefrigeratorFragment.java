package com.example.se_team5.ui.refrigerator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.*;


import com.example.se_team5.R;

public class RefrigeratorFragment extends Fragment {

    static final String[] LIST_MENU = {"ITEM1", "ITEM2", "ITEM3"} ;

    private RefrigeratorViewModel refrigeratorViewModel;

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        refrigeratorViewModel = ViewModelProviders.of(this).get(RefrigeratorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_refrigerator, container, false);
        ArrayAdapter Adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, LIST_MENU) ;
        ListView listview = (ListView) root.findViewById(R.id.listview1) ;
        listview.setAdapter(Adapter) ;
        final TextView textView = root.findViewById(R.id.text_refrigerator);
        refrigeratorViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {



            }
        });
        return root;
    }
}