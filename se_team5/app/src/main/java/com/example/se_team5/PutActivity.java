package com.example.se_team5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se_team5.item.Item;
import com.example.se_team5.item.ItemsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class PutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemsAdapter myAdapter;
    private ArrayList<Item> AllItems_;

    private String user_id;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put);

        SharedPreferences sp = getSharedPreferences("userFile", MODE_PRIVATE);
        AllItems_ = Item.gsonParsing(sp.getString("allItems",""));
        user_id = sp.getString("username", "");

        Intent intent = getIntent();
        if(intent.getExtras().getInt("to")==1){
            url = "/user/basket";
        } else if(intent.getExtras().getInt("to")==0){
            url = "/user/refrigerator";
        }

        Button PutButton = findViewById(R.id.putButton);

        recyclerView = findViewById(R.id.putRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록

        myAdapter = new ItemsAdapter(AllItems_);
        recyclerView.setAdapter(myAdapter);  // Adapter 등록

        PutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SparseBooleanArray a = myAdapter.getmSelectedItems();
                if(a.size()>0){
                    JSONObject postData = new JSONObject();

                    try {
                        postData.put("username", user_id);

                        JSONArray temp = new JSONArray();

                        for(int i = 0; i < AllItems_.size(); i++){
                            if(a.get(i, false))
                                temp.put(i);
                        }

                        postData.put("items", temp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    new putItems(PutActivity.this).execute(url, postData.toString());
                    new getRefrigeratorItemList().execute("/user/refrigerator", "?username="+user_id);
                }

                finish();
            }
        });
    }

    private static class putItems extends AsyncTask<String, Void, String> {

        private WeakReference<PutActivity> activityReference;
        putItems(PutActivity context) {
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

            PutActivity activity = activityReference.get();

            if (activity == null) return;
            if(response == null) return;

            if (response.substring(0,3).equals("200")) {
                // 성공 시, 메인 화면 띄우기
                Toast.makeText(activity, "추가되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("result","OK");
                activity.setResult(2,intent);
                activity.finish();
            } else {
                Toast.makeText(activity, "오류: "+response.substring(0,3), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class getRefrigeratorItemList extends AsyncTask<String, Void, String> {

        private WeakReference<JoinActivity> activityReference;
        getRefrigeratorItemList() { }

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

                    SharedPreferences pref = getSharedPreferences("userFile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("userRefrigerator", jsonObject.toString());
                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences pref = getSharedPreferences("userFile", MODE_PRIVATE);
                myAdapter = new ItemsAdapter(jsonParsing(pref.getString("userRefrigerator", "")));
                recyclerView.setAdapter(myAdapter);

            } else {
                return;
            }
        }
    }

    private ArrayList<Item> jsonParsing(String json) {
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray ItemArray = jsonObject.getJSONArray("items");

            ArrayList<Item> li = new ArrayList<Item>();

            for(int i=0; i<ItemArray.length(); i++)
                li.add(AllItems_.get((int)ItemArray.get(i)));

            return li;
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}