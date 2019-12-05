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

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


public class RefrigeratorFragment extends Fragment{

    private static ArrayList<Item> ITEM_LIST = new ArrayList<Item>();
    private RecyclerView recyclerView;
    private ItemsAdapter myAdapter;

    private int REQUEST_TEST = 1;

    private String user_id;

    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_refrigerator, container, false);

        Button putButton = root.findViewById(R.id.putButton);
        Button removeButton = root.findViewById(R.id.removeButton);
        Button recommendButton = root.findViewById(R.id.recommendButton);

        SharedPreferences pref = getActivity().getSharedPreferences("userFile", MODE_PRIVATE);
        user_id = pref.getString("username", "");

        recyclerView = root.findViewById(R.id.removeRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 5);
        recyclerView.setLayoutManager(manager); // LayoutManager 등록

        new getRefrigeratorItemList(RefrigeratorFragment.this).execute("/user/refrigerator", "?username="+user_id);

        //put
        putButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity().getApplicationContext(), PutActivity.class);
                intent.putExtra("to", 0);
                startActivityForResult(intent, REQUEST_TEST);
            }
        });

        //remove
        removeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TEST) {
            new getRefrigeratorItemList(RefrigeratorFragment.this).execute("/user/refrigerator", "?username="+user_id);
        }
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
                ITEM_LIST = jsonParsing(pref.getString("userRefrigerator",""));
                myAdapter = new ItemsAdapter(ITEM_LIST);
                recyclerView.setAdapter(myAdapter);
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
}