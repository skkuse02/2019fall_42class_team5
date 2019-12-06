package com.example.se_team5;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se_team5.item.Item;
import com.example.se_team5.item.RecommendItemsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class RecommendActivity extends AppCompatActivity {

    private static ArrayList<Item> SELECTED_ITEMS1 = new ArrayList<Item>();
    private static ArrayList<Item> SELECTED_ITEMS2 = new ArrayList<Item>();
    private RecyclerView recyclerView;
    private RecommendItemsAdapter myAdapter;
    private ArrayList<Item> AllItems_;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        SharedPreferences sp = getSharedPreferences("userFile", Context.MODE_PRIVATE);
        AllItems_ = Item.gsonParsing(sp.getString("allItems",""));

        Button recommendButton = findViewById(R.id.recommendButton);

        recyclerView = findViewById(R.id.recommendRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록

        myAdapter = new RecommendItemsAdapter(AllItems_);
        recyclerView.setAdapter(myAdapter);  // Adapter 등록
        recommendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                JSONObject postData = new JSONObject();
                try {
                    postData.put("username", "hj323");

                    SparseBooleanArray a = myAdapter.getmSelectedItems1();
                    JSONArray temp = new JSONArray();
                    for(int i = 0; i < AllItems_.size(); i++){
                        if(a.get(i, false))
                            temp.put(i);
                    }
                    postData.put("good", temp);
                    Log.d("good", String.valueOf(temp));

                    a = myAdapter.getmSelectedItems2();
                    temp = new JSONArray();
                    for(int i = 0; i < AllItems_.size(); i++){
                        if(a.get(i, false))
                            temp.put(i);
                    }
                    postData.put("bad", temp);

                    Log.d("bad", String.valueOf(temp));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new recommendRecipe(RecommendActivity.this).execute("/recipe/refrigerator", "{\"good\":[3,4],\"bad\":[1,2]}");
            }
        });
    }

    private static class recommendRecipe extends AsyncTask<String, Void, String> {

        private WeakReference<RecommendActivity> activityReference;
            recommendRecipe(RecommendActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {

            String success;

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
        protected void onPostExecute(String success) {
            super.onPostExecute(success);

            RecommendActivity activity = activityReference.get();

            if (activity == null) return;
            if(success == null) return ;

            if (success.substring(0,3).equals("200")) {
                activity.finish();
                //Intent intent = new Intent(activity, MainActivity.class);
                //activity.startActivity(intent);
            } else {

            }
        }
    }
}