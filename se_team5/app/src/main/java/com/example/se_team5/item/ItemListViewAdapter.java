package com.example.se_team5.item;

import android.app.Activity;
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
import android.widget.Toast;

import com.example.se_team5.HttpRequest;
import com.example.se_team5.MyGlobal;
import com.example.se_team5.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class ItemListViewAdapter extends BaseAdapter {

    public String user_id;

    private ArrayList<Item> myBasketList;
    private Activity mActivity;

    public ItemListViewAdapter(ArrayList<Item> dataList, Activity mActivity) {
        myBasketList = dataList;
        this.mActivity = mActivity;
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

        Button deleteBasketButton = convertView.findViewById(R.id.deleteBasketButton);
        deleteBasketButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                JSONObject deleteData = new JSONObject();
                JSONArray temp = new JSONArray();

                try {

                    deleteData.put("username", user_id);


                    temp.put(myBasketList.get(pos).getId());
                    deleteData.put("items", temp);
                    new removeItemInRefrigerator().execute("/user/basket/remove", deleteData.toString());
                    new getBasketItemList().execute("/user/basket", "?username="+user_id);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button buyItemButton = convertView.findViewById(R.id.BuyItemButton);
        buyItemButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                JSONObject data1 = new JSONObject();

                try {

                    data1.put("username", user_id);
                    JSONArray temp = new JSONArray();
                    temp.put(myBasketList.get(pos).getId());
                    data1.put("items", temp);
                    new removeItemInRefrigerator().execute("/user/basket/move", data1.toString());
                    new getBasketItemList().execute("/user/basket", "?username="+user_id);


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

    private class removeItemInRefrigerator extends AsyncTask<String, Void, String> {

        boolean justRemove = false;

        removeItemInRefrigerator() { }

        @Override
        protected String doInBackground(String... params) {
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            if(params[0].contains("remove")) justRemove = true;
            return req.sendDelete(params[0], params[1]);
        }
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(response == null) return;

            if (response.substring(0,3).equals("200")) {
                if(justRemove)
                    Toast.makeText(mActivity, "장바구니에서 제거되었습니다.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mActivity, "냉장고에 추가되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(activity, "오류: " + response.substring(0,3), Toast.LENGTH_SHORT).show();
            }
        }
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
                    SharedPreferences pref = mActivity.getSharedPreferences("userFile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("userBasket", jsonObject.toString());
                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences pref = mActivity.getSharedPreferences("userFile", MODE_PRIVATE);
                myBasketList = jsonParsing(pref.getString("userBasket",""));
                notifyDataSetChanged();
            } else {
                return;
            }
        }
    }

    private ArrayList<Item> jsonParsing(String json) {
        SharedPreferences sp = mActivity.getSharedPreferences("userFile", Context.MODE_PRIVATE);
        ArrayList<Item> AllItems_ = Item.gsonParsing(sp.getString("allItems",""));
        try{
            JSONObject jsonObject = new JSONObject(json);
            JSONArray ItemArray = jsonObject.getJSONArray("items");

            ArrayList<Item> li = new ArrayList<Item>();

            for(int i=0; i<ItemArray.length(); i++)
                li.add(AllItems_.get((int)ItemArray.get(i)-1));

            return li;
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}