package com.example.se_team5.ui.basket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.se_team5.BrowseActivity;
import com.example.se_team5.HttpRequest;
import com.example.se_team5.MyGlobal;
import com.example.se_team5.PutActivity;
import com.example.se_team5.R;
import com.example.se_team5.item.AllItems;
import com.example.se_team5.item.Item;
import com.example.se_team5.item.ItemListViewAdapter;
import com.example.se_team5.item.ItemsAdapter;
import com.example.se_team5.ui.refrigerator.RefrigeratorFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static androidx.lifecycle.ViewModelProviders.*;

public class BasketFragment extends Fragment {

    private static ArrayList<Item> ITEM_LIST = new ArrayList<Item>();

    ListView listview ;
    ItemListViewAdapter adapter;

    public String user_id;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_basket, container, false);

        Button putButton = root.findViewById(R.id.putBasketButton);
        Button buyButton = root.findViewById(R.id.BuyButton);

        SharedPreferences sp = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        user_id = sp.getString("username","");

        listview = (ListView) root.findViewById(R.id.list_item);

        new getBasketItemList().execute("/user/basket", "?username="+user_id);


        //put
        putButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), PutActivity.class);
                intent.putExtra("to", 1);
                startActivity(intent);
            }
        });

        //buy
        buyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), BrowseActivity.class);
                startActivity(intent);

            }
        });

        return root;
    }

    private class getBasketItemList extends AsyncTask<String, Void, String> {

        getBasketItemList() { }

        @Override
        protected String doInBackground(String... params) {
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            return req.sendGet(params[0], params[1]);
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(response==null) return;

            if (response.substring(0,3).equals("200")) {
                try {
                    JSONObject jsonObject = new JSONObject(response.substring(3));
                    SharedPreferences pref = getActivity().getSharedPreferences("userFile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("userBasket", jsonObject.toString());
                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences pref = getActivity().getSharedPreferences("userFile", MODE_PRIVATE);
                ITEM_LIST = jsonParsing(pref.getString("userBasket",""));
                adapter = new ItemListViewAdapter(ITEM_LIST);
                listview.setAdapter(adapter);
            } else {
                return;
            }
        }
    }

    private static ArrayList<Item> jsonParsing(String json) {
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray ItemArray = jsonObject.getJSONArray("items");

            ArrayList<Item> li = new ArrayList<Item>();

            for(int i=0; i<ItemArray.length(); i++)
                li.add(new AllItems().findItem((int)ItemArray.get(i)));

            return li;
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}