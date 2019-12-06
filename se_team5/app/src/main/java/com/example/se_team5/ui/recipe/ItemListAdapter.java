package com.example.se_team5.ui.recipe;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se_team5.HttpRequest;
import com.example.se_team5.MyGlobal;
import com.example.se_team5.R;
import com.example.se_team5.item.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter {
    private List<Item> recipeItemList;
    private String user_id;

    public ItemListAdapter(List<Item> recipeItemList,String user_id) {
        this.user_id = user_id;
        this.recipeItemList = recipeItemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_itemlist, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return recipeItemList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView item_name;
        private Button goBasket;

        public ItemViewHolder(final View itemView) {
            super(itemView);
            // 뷰 지정
            item_name = itemView.findViewById(R.id.item_in_list);
            goBasket = itemView.findViewById(R.id.go_basket);


            // 담기 버튼이 눌렸을 때
            goBasket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* 장바구니에 추가하기 */
                    Integer pos = (Integer) itemView.getTag();
                    int item_id = recipeItemList.get(pos).getId();

                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("username",user_id);

                        JSONArray items = new JSONArray();
                        items.put(item_id);
                        postData.put("items", items);

                        new putRefrigerator().execute("/user/basket",postData.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        // 해당 view의 position에 값 bind
        public void bindView(int position) {
            Item _item = recipeItemList.get(position);
            item_name.setText(_item.getName());
            itemView.setTag(position);

        }

        private class putRefrigerator extends AsyncTask<String, Void, String> {
            HttpRequest req = new HttpRequest(MyGlobal.getData());

            @Override
            protected String doInBackground(String... params) {
                return req.sendPost(params[0], params[1]);
            }

            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                try {
                    if (response.equals("")) return;
                    if (response.substring(0, 3).equals("200")) {
                        goBasket.setEnabled(false);
                    }

                } catch (Exception e) {
                    Log.e("Error", "Exception");
                    e.printStackTrace();
                }
            }
        }

    }


}

