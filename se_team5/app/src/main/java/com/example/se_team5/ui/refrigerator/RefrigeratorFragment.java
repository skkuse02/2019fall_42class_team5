package com.example.se_team5.ui.refrigerator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se_team5.HttpRequest;
import com.example.se_team5.MyGlobal;
import com.example.se_team5.PutActivity;
import com.example.se_team5.R;
import com.example.se_team5.ItemSelectActivity;
import com.example.se_team5.item.Item;
import com.example.se_team5.item.ItemsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class RefrigeratorFragment extends Fragment{
    /*사용자의 냉장고를 보여주는 fragment*/

    private static ArrayList<Item> ITEM_LIST = new ArrayList<Item>();       //냉장고의 아이템 리스트
    private RecyclerView recyclerView;      //리사이클러 뷰 선언
    private ItemsAdapter myAdapter;         //아이템어댑터 선언

    private int REQUEST_TEST = 1;           //putActivity가 종료 될때 값을 받기 위한 인자

    private String user_id;             //사용자 아이디

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_refrigerator, container, false);

        /*냉장고에 추가, 삭제, 추천으로 이동하는 버튼*/
        Button putButton = root.findViewById(R.id.putButton);
        Button removeButton = root.findViewById(R.id.removeButton);
        Button recommendButton = root.findViewById(R.id.recommendButton);

        /*로컬에서 사용자 아이디를 불러옴*/
        SharedPreferences pref = getActivity().getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = pref.getString("username", "");

        /*리사이클러 뷰 초기화 및 grid 형태로 설정*/
        recyclerView = root.findViewById(R.id.removeRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 5);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록

        /*냉장고 아이템 리스트를 요청*/
        new getRefrigeratorItemList(RefrigeratorFragment.this).execute("/user/refrigerator", "?username="+user_id);

        /*추가 버튼*/
        putButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*클릭시 PutActivity를 실행 냉장고에 대한 기능임을 의미하는 to 값에 0을 전달*/
                Intent intent = new Intent(getActivity().getApplicationContext(), PutActivity.class);
                intent.putExtra("to", 0);
                startActivityForResult(intent, REQUEST_TEST);
            }
        });

        /*삭제 버튼*/
        removeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*버튼 클릭시 뷰어에서 선택된 아이템의 객체를 불러오고 JSON 생성후 제거 요청 + 냉장고 아이텤 리스트 요청*/

                SparseBooleanArray a = myAdapter.getmSelectedItems();
                if(a.size()>0){
                    JSONObject postData = new JSONObject();
                    JSONArray temp = new JSONArray();
                    try {

                        postData.put("username", user_id);

                        for(int i = 0; i < ITEM_LIST.size(); i++){
                            if(a.get(i, false))
                                temp.put(ITEM_LIST.get(i).getId());
                        }

                        postData.put("items", temp);
                        new removeItemInRefrigerator(RefrigeratorFragment.this).execute("/user/refrigerator", postData.toString());
                        new getRefrigeratorItemList(RefrigeratorFragment.this).execute("/user/refrigerator", "?username="+user_id);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        /*추천 버튼*/
        recommendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*추천 버튼 클릭시 선호/비선호 아이템을 선택하는 화면으로 전환*/
                Intent intent = new Intent(getActivity().getApplicationContext(), ItemSelectActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*putActivity 가 실행된 후 아이템 리스트를 갱신하도록 한다.*/
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TEST) {
            new getRefrigeratorItemList(RefrigeratorFragment.this).execute("/user/refrigerator", "?username="+user_id);
        }
    }


    private class getRefrigeratorItemList extends AsyncTask<String, Void, String> {
        /*냉장고 아이템 리스트를 갱신해오도록 요청하는 class*/

        /*알림을 표시할 수 있도록 WeekReference 를 지정*/
        private WeakReference<RefrigeratorFragment> activityReference;
        getRefrigeratorItemList(RefrigeratorFragment context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            /*서버 경로를 지정하여 요청한 후 response 를 반환한다.*/
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            return req.sendGet(params[0], params[1]);
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            RefrigeratorFragment activity = activityReference.get();

            /*오류시 종료*/
            if (activity == null) return;
            if (response==null) return;

            if (response.substring(0,3).equals("200")) {
                /* 200 을 수신하면 JSON을 파싱하여 로컬에 저장하고 어댑터에 지정한다. */
                try {
                    JSONObject jsonObject = new JSONObject(response.substring(3));
                    SharedPreferences pref = getActivity().getSharedPreferences("userFile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("userRefrigerator", jsonObject.toString());
                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences pref = getActivity().getSharedPreferences("userFile", MODE_PRIVATE);
                ITEM_LIST = Item.jsonParsing(getActivity(), pref.getString("userRefrigerator",""));
                myAdapter = new ItemsAdapter(ITEM_LIST);
                recyclerView.setAdapter(myAdapter);
            } else {
                return;
            }
        }
    }


    private static class removeItemInRefrigerator extends AsyncTask<String, Void, String> {
        /*냉장고 리스트에서 아이템 삭제요청을 보내는 class*/

        /*알림을 표시할 수 있도록 WeekReference 를 지정*/
        private WeakReference<RefrigeratorFragment> activityReference;
        removeItemInRefrigerator(RefrigeratorFragment context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            /*서버 경로를 지정하여 요청한 후 response 를 반환한다.*/
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            return req.sendDelete(params[0], params[1]);
        }

        protected void onPostExecute(String response) {
            /*수신한 response 를 파싱하는 메소드*/
            super.onPostExecute(response);
            RefrigeratorFragment activity = activityReference.get();

            /*오류시 종료*/
            if (activity == null) return;
            if(response == null) return;

            if (response.substring(0,3).equals("200")) {
                /* 200 수신시 정상적인 작동을 알림*/
                Toast.makeText(activityReference.get().getActivity(), "냉장고에서 제거되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                /* 오류 발생시 오류코드를 알림*/
                Toast.makeText(activityReference.get().getActivity(), "오류: " + response.substring(0,3), Toast.LENGTH_SHORT).show();
            }
        }
    }
}