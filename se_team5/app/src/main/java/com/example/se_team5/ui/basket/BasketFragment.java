package com.example.se_team5.ui.basket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.se_team5.item.Item;
import com.example.se_team5.item.ItemListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class BasketFragment extends Fragment {
    /*장바구니화면을 구성하는 fragment*/

    private static ArrayList<Item> ITEM_LIST = new ArrayList<Item>();   //사용자의 장바구니에 있는 아아템리스트

    public String user_id;              //사용자의 아이디

    ListView listview ;                 //아이템 리스트를 표현할 리스트 뷰
    ItemListViewAdapter adapter;        //리스트 뷰를 표현할 어댑터

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_basket, container, false);

        Button putButton = root.findViewById(R.id.putBasketButton);
        Button buyButton = root.findViewById(R.id.BuyButton);

        /*유저 아이디를 로컬에서 가져옴*/
        SharedPreferences sp = getActivity().getSharedPreferences("userFile", Context.MODE_PRIVATE);
        user_id = sp.getString("username","");

        /*리스트 뷰 초기화*/
        listview = root.findViewById(R.id.list_item);

        /*사용자의 장바구니에서 아이템리스트를 요청*/
        new getBasketItemList().execute("/user/basket", "?username="+user_id);


        /*putButton*/
        putButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*장바구니에 추가함을 명시하여 putActivity를 실행*/
                Intent intent = new Intent(getActivity().getApplicationContext(), PutActivity.class);
                intent.putExtra("to", 1);
                startActivityForResult(intent, 1);
            }
        });

        /*buyButton*/
        buyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*BrowseActivity를 실행*/
                Intent intent = new Intent(getActivity().getApplicationContext(), BrowseActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*putActivity 실행 후 사용자 장바구니를 업데이트*/
        if (requestCode == 1) {
            new getBasketItemList().execute("/user/basket", "?username="+user_id);
        }
    }

    private class getBasketItemList extends AsyncTask<String, Void, String> {
        /*사용자 장바구니를 업데이트하는 요청을 보내는 class*/
        getBasketItemList() { }

        @Override
        protected String doInBackground(String... params) {
            /*요청을 보낸 후 response를 반환*/
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            return req.sendGet(params[0], params[1]);
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(response==null) return;

            if (response.substring(0,3).equals("200")) {
                /*200을 수신하면 장바구니를 업데이트하고 화면에 표시*/
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
                ITEM_LIST = Item.jsonParsing(getActivity(), pref.getString("userBasket",""));
                adapter = new ItemListViewAdapter(ITEM_LIST, getActivity());
                listview.setAdapter(adapter);
            } else {
                return;
            }
        }
    }
}