package com.example.se_team5.ui.refrigerator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se_team5.HttpRequest;
import com.example.se_team5.MyGlobal;
import com.example.se_team5.PutActivity;
import com.example.se_team5.R;
import com.example.se_team5.RecommendActivity;
import com.example.se_team5.item.AllItems;
import com.example.se_team5.item.Item;
import com.example.se_team5.item.ItemsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;


public class RefrigeratorFragment extends Fragment{

    private static ArrayList<Item> ITEM_LIST = new ArrayList<Item>();
    private static ArrayList<Item> SELECTED_ITEMS = new ArrayList<Item>();
    private RecyclerView recyclerView;
    private ItemsAdapter myAdapter;

    private String user_id;

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_refrigerator, container, false);

        Button putButton = root.findViewById(R.id.putButton);
        Button removeButton = root.findViewById(R.id.removeButton);
        Button recommendButton = root.findViewById(R.id.recommendButton);

        SharedPreferences user = this.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        user_id = user.getString("user","");
        Log.d("user", user_id);

        recyclerView = root.findViewById(R.id.removeRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 5);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록

        new getRefrigeratorItemList(RefrigeratorFragment.this).execute("/user/refrigerator", "?user_id=hj323");
        //myAdapter = new ItemsAdapter(ITEM_LIST);//new AllItems().getAllItem()
        //recyclerView.setAdapter(myAdapter);  // Adapter 등록

        //put
        putButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), PutActivity.class);
                startActivity(intent);


//                new putItemInRefrigerator(RefrigeratorFragment.this).execute("/user/refrigerator", "{\"username\":\"hj323\",\"items\":[1,2]}");
            }
        });

        //remove
        removeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // JSON으로 로그인 데이터 보냄
                JSONObject postData = new JSONObject();
                JSONArray arr = new JSONArray();

                try {

                    for (Item i : SELECTED_ITEMS){
                        arr.put(i.getId());
                    }

                    postData.put("items", arr);
                    //Log.i("Dd", String.valueOf(postData));
                    new removeItemInRefrigerator(RefrigeratorFragment.this).execute("/user/refrigerator", "{\"username\":\"hj323\",\"items\":[1,2]}");
                    new getRefrigeratorItemList(RefrigeratorFragment.this).execute("/user/refrigerator", "?user_id=hj323");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        //recommend
        recommendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), RecommendActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private class getRefrigeratorItemList extends AsyncTask<String, Void, String> {

        private WeakReference<RefrigeratorFragment> activityReference;
        // only retain a weak reference to the activity
        getRefrigeratorItemList(RefrigeratorFragment context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            return req.sendGet(params[0], params[1]);
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            RefrigeratorFragment activity = activityReference.get();

            if (activity == null) return;
            if(response==null) return;

            if (response.substring(0,3).equals("200")) {

            } else {

            }
            //Toast.makeText(response.substring(3),Toast.LENGTH_SHORT).show();
            ITEM_LIST = jsonParsing(response.substring(3));
            myAdapter = new ItemsAdapter(new AllItems().getAllItem());
            recyclerView.setAdapter(myAdapter);
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

    private static class removeItemInRefrigerator extends AsyncTask<String, Void, String> {

        private WeakReference<RefrigeratorFragment> activityReference;
        // only retain a weak reference to the activity
        removeItemInRefrigerator(RefrigeratorFragment context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            return req.sendDelete(params[0], params[1]);
        }
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            RefrigeratorFragment activity = activityReference.get();

            if (activity == null) return;
            if(response == null) return;

            if (response.substring(0,3).equals("200")) {
                // 성공 시, 메인 화면 띄우기
                //Toast.makeText(activity, "냉장고에 추가되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(activity, "오류: " + response.substring(0,3), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class putItemInRefrigerator extends AsyncTask<String, Void, String> {

        private WeakReference<RefrigeratorFragment> activityReference;
        putItemInRefrigerator(RefrigeratorFragment context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {

            String response;

            HttpURLConnection httpURLConnection = null;
            try {
                HttpRequest req = new HttpRequest(MyGlobal.getData());
                return req.sendPost(params[0], params[1]);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // server와의 connection 해제
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return null;

        }
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            RefrigeratorFragment activity = activityReference.get();

            if (activity == null) return;

            if(response == null){
                //
            }else{
                //
            }
        }
    }
}