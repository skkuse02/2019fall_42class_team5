package com.example.se_team5.item;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.se_team5.HttpRequest;
import com.example.se_team5.MyGlobal;
import com.example.se_team5.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;


public class ItemListViewAdapter extends BaseAdapter {

    public String user_id;

    private ArrayList<Item> myBasketList;

    public ItemListViewAdapter(ArrayList<Item> dataList) {
        myBasketList = dataList;
    }

    @Override
    public int getCount() {
        if(myBasketList.size()==0)
            return 0;
        return myBasketList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        SharedPreferences sp = context.getSharedPreferences("userFile", Context.MODE_PRIVATE);
        user_id = sp.getString("username","");

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        ImageView iconImageView = convertView.findViewById(R.id.imageView1) ;
        TextView titleTextView = convertView.findViewById(R.id.textView1) ;


        Item listViewItem = myBasketList.get(position);

        iconImageView.setImageResource(listViewItem.getImageResourceID());
        titleTextView.setText(listViewItem.getName());

        Button deleteBasketButton = (Button) convertView.findViewById(R.id.deleteBasketButton);
        deleteBasketButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                JSONObject deleteData = new JSONObject();
                JSONArray temp = new JSONArray();

                try {

                    deleteData.put("username", user_id);


                    temp.put(myBasketList.get(pos).getId());
                    deleteData.put("items", temp);
                    new removeItemInRefrigerator().execute("/user/basket/remove", deleteData.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button buyItemButton = (Button) convertView.findViewById(R.id.BuyItemButton);
        buyItemButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                JSONObject data1 = new JSONObject();

                try {

                    data1.put("username", user_id);
                    JSONArray temp = new JSONArray();
                    temp.put(myBasketList.get(pos).getId());
                    data1.put("items", temp);
                    new removeItemInRefrigerator().execute("/user/basket/move", data1.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return myBasketList.get(position) ;
    }

    private static class removeItemInRefrigerator extends AsyncTask<String, Void, String> {

        removeItemInRefrigerator() { }

        @Override
        protected String doInBackground(String... params) {
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            return req.sendDelete(params[0], params[1]);
        }
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(response == null) return;

            if (response.substring(0,3).equals("200")) {
                // 성공 시, 메인 화면 띄우기
                //Toast.makeText(activity, "냉장고에 추가되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(activity, "오류: " + response.substring(0,3), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class putItems extends AsyncTask<String, Void, String> {

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

            if(response == null) return;

            if (response.substring(0,3).equals("200")) {

            } else {

            }
        }
    }
}