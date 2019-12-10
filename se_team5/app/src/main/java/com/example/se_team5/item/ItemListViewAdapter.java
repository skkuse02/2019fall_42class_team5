package com.example.se_team5.item;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    /*아이템을 리스트 뷰의 형태로 바꾸어주는 뷰 어댑터*/

    public String user_id;                  //사용자의 아이디
    private ArrayList<Item> myBasketList;   //표시할 아이템 리스트
    private Activity mActivity;             //표시되는 액티비티

    public ItemListViewAdapter(ArrayList<Item> dataList, Activity mActivity) {
        /*객체 선언시 아이템 리스트와 액티비티를 받아와 전역변수로 저장*/
        myBasketList = dataList;
        this.mActivity = mActivity;

        /*사용자 아이디를 로컬에서 가져옴*/
        SharedPreferences sp = mActivity.getSharedPreferences("userFile", Context.MODE_PRIVATE);
        user_id = sp.getString("username","");
    }

    @Override
    public int getCount() {
        /*표시할 아이템리스트의 길이를 반환하는 메소드*/
        if(myBasketList.size()==0)
            return 0;
        return myBasketList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*하나의 리스트 뷰를 구성하는 메소드*/
        final int pos = position;                   //이번에 표시할 리스트 뷰의 position
        final Context context = parent.getContext();//표시될 context


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        /*표시할 아이템 선언 및 초기화*/
        Item listViewItem = myBasketList.get(position);

        /*이미지 뷰와 텍스트 뷰 선언 및 초기화*/
        ImageView iconImageView = convertView.findViewById(R.id.imageView1) ;
        TextView titleTextView = convertView.findViewById(R.id.textView1) ;

        /*이미지 뷰와 텍스트 뷰에 값 설정*/
        iconImageView.setImageResource(listViewItem.getImageResourceID());
        titleTextView.setText(listViewItem.getName());

        /*장바구니에서 삭제(삭제) 버튼*/
        Button deleteBasketButton = convertView.findViewById(R.id.deleteBasketButton);
        deleteBasketButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                /*아이템 제거에 대한 JSON 생성*/
                JSONObject deleteData = new JSONObject();
                JSONArray temp = new JSONArray();

                try {
                    deleteData.put("username", user_id);
                    temp.put(myBasketList.get(pos).getId());
                    deleteData.put("items", temp);

                    /*제거 요청 및 장바구니 리스트 불러오기*/
                    new removeItemInRefrigerator().execute("/user/basket/remove", deleteData.toString());
                    new getBasketItemList().execute("/user/basket", "?username="+user_id);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        /*냉장고에 추가(담기) 버튼*/
        Button buyItemButton = convertView.findViewById(R.id.BuyItemButton);
        buyItemButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                /*담기 요청에 대한 JSON 생성*/
                JSONObject data1 = new JSONObject();
                JSONArray temp = new JSONArray();

                try {
                    data1.put("username", user_id);
                    temp.put(myBasketList.get(pos).getId());
                    data1.put("items", temp);

                    /*담기 요청 및 장바구니 리스트 불러오기*/
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
        /*뷰의 position을 반환하는 메소드*/
        return position ;
    }

    @Override
    public Object getItem(int position) {
        /*뷰에 표시된 아이템을 반환하는 메소드*/
        return myBasketList.get(position) ;
    }

    private class removeItemInRefrigerator extends AsyncTask<String, Void, String> {
        /*아이템 삭제 및 담기에 대한 요청을 보내는 class*/

        boolean justRemove = false; //담기, 삭제를 구분하는 값 ( true - 삭제 / false - 담기 )

        removeItemInRefrigerator() { }

        @Override
        protected String doInBackground(String... params) {
            /*서버의 경로를 정하고 삭제/담기를 결정하여 DELETE를 송신, 그에 대한 response를 반환*/
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            if(params[0].contains("remove")) justRemove = true;
            return req.sendDelete(params[0], params[1]);
        }

        protected void onPostExecute(String response) {
            /*송신 후 response에 대한 반응을 하는 메소드*/
            super.onPostExecute(response);

            /*response가 없을 경우 종료*/
            if(response == null) return;

            if (response.substring(0,3).equals("200")) {
                /*200을 수신한 경우 삭제, 담기 요청을 구분하여 알림*/
                if(justRemove)
                    Toast.makeText(mActivity, "장바구니에서 제거되었습니다.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mActivity, "냉장고에 추가되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                /*오류 시 수신한 코드를 알림*/
                Toast.makeText(mActivity, "오류: " + response.substring(0,3), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class getBasketItemList extends AsyncTask<String, Void, String> {
        /*장바구니의 아이템 리스트를 업데이트하는 class*/
        getBasketItemList() { }

        @Override
        protected String doInBackground(String... params) {
            /*서버의 경로를 정하고 아이템 리스트 요청*/
            HttpRequest req = new HttpRequest(MyGlobal.getData());
            return req.sendGet(params[0], params[1]);
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            /*response가 없을 경우 종료*/
            if(response==null) return;

            if (response.substring(0,3).equals("200")) {
                /*200을 수신한 경우 로컬에 수신한 아이템 리스트를 저장*/
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

                /*표시되는 뷰를 초기화*/
                notifyDataSetChanged();

                /*basketFragment에 아이템 리스트가 갱신되었음을 알림*/
                Intent intent = new Intent();
                intent.putExtra("result","OK");
                mActivity.setResult(1,intent);
            } else {
                return;
            }
        }
    }

    private ArrayList<Item> jsonParsing(String json) {
        /*수신한 JSON을 Item ArrayList로 파싱하는 메소드*/

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