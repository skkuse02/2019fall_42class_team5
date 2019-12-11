package com.example.se_team5.ui.recipe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se_team5.MyGlobal;
import com.example.se_team5.R;
import com.example.se_team5.item.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/* 레시피 검색 또는 추천 시, RecipeInfo들을 리스트로 뷰에 보여주는 어댑터 */
public class RecipeInfoAdapter extends RecyclerView.Adapter {
    private Context context; // fragment context
    private List<RecipeInfo> recipeList; // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Item> AllItems_; // 아이템 전체 정보 리스트

    public RecipeInfoAdapter(Context context,List<RecipeInfo> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_info,parent,false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecipeViewHolder)holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    private class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mealImage;
        private TextView title;
        private TextView likes;
        private TextView items;
        private TextView description;

        public RecipeViewHolder(View itemView){
            super(itemView);

            // view 지정 및 listener 달기
            mealImage = itemView.findViewById(R.id.main_image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            likes = itemView.findViewById(R.id.likes);
            items = itemView.findViewById(R.id.items);
            itemView.setOnClickListener(this);

        }

        // 해당 view의 position에 값 bind
        public void bindView(int position){
            RecipeInfo recipeinfo = recipeList.get(position);
            title.setText(recipeinfo.title);
            likes.setText(recipeinfo.like.toString());
            description.setText(recipeinfo.description);

            // items 받기
            SharedPreferences sp = context.getSharedPreferences("userFile", MODE_PRIVATE);
            AllItems_ = Item.gsonParsing(sp.getString("allItems",""));
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<recipeinfo.items.size();i++) {
                sb.append(AllItems_.get(recipeinfo.items.get(i)-1).getName()+" ");
            }
            items.setText(sb.toString());

            // 이미지 로드
            Picasso.get().load(MyGlobal.getData()+"/image/"+recipeinfo.imagepath).into(mealImage);
            // tag 달기
            itemView.setTag(position);

        }

        // 리스트 클릭 시 Detailed Recipe로 이동
        @Override
        public void onClick(View v) {
            // tag로 클릭된 view 찾기
            Integer pos = (Integer)v.getTag();
            RecipeInfo thisRecipe = recipeList.get(pos);

            // 불러올 activity 생성
            Intent DetailedRecipe = new Intent(context, RecipeDetailedActivity.class);

            // main image uri, title, id 정보 넘기기
            DetailedRecipe.putExtra("imagepath",thisRecipe.imagepath);
            Log.i("path",thisRecipe.imagepath);
            DetailedRecipe.putExtra("title",thisRecipe.title);
            DetailedRecipe.putExtra("recipe_id",thisRecipe.recipe_id);

            // RecipeDetailedActivity 시작
            context.startActivity(DetailedRecipe);

        }
    }


}