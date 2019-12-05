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

import com.example.se_team5.item.AllItems;
import com.example.se_team5.item.ItemsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

public class PutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemsAdapter myAdapter;

    private String user_id;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put);

        Intent intent = getIntent();
        if(intent.getExtras().getInt("to")==1){
            url = "/user/basket";
        } else if(intent.getExtras().getInt("to")==0){
            url = "/user/refrigerator";
        }

        SharedPreferences user = getSharedPreferences("user", Context.MODE_PRIVATE);

        user_id = user.getString("user","");

        Button PutButton = findViewById(R.id.putButton);


        recyclerView = findViewById(R.id.putRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록

        myAdapter = new ItemsAdapter(new AllItems().getAllItem());
        recyclerView.setAdapter(myAdapter);  // Adapter 등록

        PutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("username", "hj323");

                SparseBooleanArray a = myAdapter.getmSelectedItems();
                JSONArray temp = new JSONArray();
                for(int i = 0; i < new AllItems().getItemNum(); i++){
                    if(a.get(i, false))
                        temp.put(i);
                }
                postData.put("items", temp);
                Log.d("ddddd", String.valueOf(temp));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new putItems(PutActivity.this).execute(url, postData.toString());
            //new getRefrigeratorItemList().execute("/user/refrigerator", "?username=hj323");
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
                activity.finish();
            } else {
                Toast.makeText(activity, "오류: "+response.substring(0,3), Toast.LENGTH_SHORT).show();
            }
        }
    }
/*
    private class getRefrigeratorItemList extends AsyncTask<String, Void, String> {

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

            } else {

            }
            //Toast.makeText(response.substring(3),Toast.LENGTH_SHORT).show();
            ITEM_LIST = jsonParsing(response.substring(3));
            myAdapter = new ItemsAdapter(ITEM_LIST);
            recyclerView.setAdapter(myAdapter);
        }
    }*/
}