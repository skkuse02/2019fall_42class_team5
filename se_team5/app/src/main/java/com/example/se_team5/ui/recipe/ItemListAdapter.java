package com.example.se_team5.ui.recipe;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se_team5.R;
import com.example.se_team5.item.Item;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter {
    private List<Item> recipeItemList;

    public ItemListAdapter(List<Item> recipeItemList) {
        this.recipeItemList = recipeItemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_itemlist,parent,false);
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

    private class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView item_name;
        private Button goBasket;

        public ItemViewHolder(View itemView) {
            super(itemView);
            // 뷰 지정
            item_name = itemView.findViewById(R.id.item_in_list);
            goBasket = itemView.findViewById(R.id.go_basket);


            // 담기 버튼이 눌렸을 때
            goBasket.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    /* 장바구니에 추가하기 */

                    goBasket.setEnabled(false);
                }
            });

        }
        // 해당 view의 position에 값 bind
        public void bindView(int position) {
            Item _item = recipeItemList.get(position);
            Log.i("bind",_item.getId()+", "+_item.getName());

            item_name.setText(_item.getName());
            itemView.setTag(position);

        }

    }
}

