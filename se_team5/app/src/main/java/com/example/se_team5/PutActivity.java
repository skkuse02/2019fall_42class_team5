package com.example.se_team5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.ArrayList;

/* 모든 아이템 리스트에서 일부를 선택하여 냉장고 혹은 장바구니에 추가하도록 하는 액티비티 */
public class PutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;  //리사이클러 뷰 선언
    private ItemsAdapter myAdapter;     //어댑터 선언
    private ArrayList<Item> AllItems_;  //모든 아이템 리스트 선언

    private String user_id;         //사용자 아이디 선언
    private String url;             //서버에 송신할 경로 선언
    private int to;                 // 0 - 장바구니에 추가, 1 - 냉장고에 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put);

        /*로컬에서 모든아이템 리스트와 사용자 아이디를 불러옴*/
        SharedPreferences sp = getSharedPreferences("userFile", MODE_PRIVATE);
        AllItems_ = Item.gsonParsing(sp.getString("allItems",""));
        user_id = sp.getString("username", "");

        Button PutButton = findViewById(R.id.putButton);

        /* 전달받은 인자(to)를 통해 url 을 지정*/
        Intent intent = getIntent();
        if(intent.getExtras().getInt("to")==1){
            url = "/user/basket";
            PutButton.setText("장바구니에 추가");
        } else if(intent.getExtras().getInt("to")==0){
            url = "/user/refrigerator";
            PutButton.setText("냉장고에 추가");
        }

        /* 리사이클러 뷰 및 매니저, 어댑터를 지정 */
        recyclerView = findViewById(R.id.putRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        myAdapter = new ItemsAdapter(AllItems_);
        recyclerView.setAdapter(myAdapter);  // Adapter 등록

        PutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*버튼 클릭 시 선택된 아이템들을 JSON 형태로 변환하여 서버에 요청*/
                SparseBooleanArray a = myAdapter.getmSelectedItems();
                if(a.size()>0){
                    JSONObject postData = new JSONObject();

                    try {
                        postData.put("username", user_id);

                        JSONArray temp = new JSONArray();
                        for(int i = 0; i < AllItems_.size(); i++){
                            if(a.get(i, false))
                                temp.put(i+1);
                        }
                        postData.put("items", temp);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 서버에 요청
                    new putItems(PutActivity.this).execute(url, postData.toString());

                    // 요청후 putActivity 를 불러온 context 가 정보를 불러올 수 있도록 아이템 리스트 최신화
                    Intent intent = getIntent();
                    to = intent.getExtras().getInt("to");
                    if(to==1){
                        new getBasketItemList().execute("/user/basket", "?username="+user_id);
                    } else if(to==0){
                        new getRefrigeratorItemList().execute("/user/refrigerator", "?username="+user_id);
                    }
                }
                finish();//액티비티 종료
            }
        });
    }
    /* 추가하는 아이템 리스트를 서버에 송신하는 method */
    private class putItems extends AsyncTask<String, Void, String> {

        private WeakReference<PutActivity> activityReference;
        putItems(PutActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            return req.sendPost(params[0], params[1]);
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            PutActivity activity = activityReference.get();

            if (activity == null) return;
            if(response == null) return;

            if (response.substring(0,3).equals("200")) {
                Intent intent = new Intent();
                if(to==1){
                    Toast.makeText(activity, "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                } else if(to==0){
                    Toast.makeText(activity, "냉장고에 추가되었습니다.", Toast.LENGTH_SHORT).show();
                }
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
                myAdapter = new ItemsAdapter(Item.jsonParsing(getParent(), pref.getString("userRefrigerator", "")));
                recyclerView.setAdapter(myAdapter);

            } else {
                return;
            }
        }
    }

    private class getBasketItemList extends AsyncTask<String, Void, String> {

        private WeakReference<JoinActivity> activityReference;
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

                    SharedPreferences pref = getSharedPreferences("userFile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("userBasket", jsonObject.toString());
                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences pref = getSharedPreferences("userFile", MODE_PRIVATE);
                myAdapter = new ItemsAdapter(Item.jsonParsing(getParent(), pref.getString("userBasket", "")));
                recyclerView.setAdapter(myAdapter);

            } else {
                return;
            }
        }
    }
}