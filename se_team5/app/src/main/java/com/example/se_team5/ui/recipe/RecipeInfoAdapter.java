package com.example.se_team5.ui.recipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se_team5.MyGlobal;
import com.example.se_team5.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeInfoAdapter extends RecyclerView.Adapter {
    private Context context; // fragment context
    private List<RecipeInfo> recipeList; // Adapter에 추가된 데이터를 저장하기 위한 ArrayList

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
        private Integer recipe_id;

        public RecipeViewHolder(View itemView){
            super(itemView);

            // view 지정 및 listener 달기
            mealImage = (ImageView) itemView.findViewById(R.id.main_image);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            likes = (TextView) itemView.findViewById(R.id.likes);
            items = (TextView) itemView.findViewById(R.id.items);

            itemView.setOnClickListener(this);

        }

        // 해당 view의 position에 값 bind
        public void bindView(int position){
            RecipeInfo recipeinfo = recipeList.get(position);

            recipe_id = recipeinfo.recipe_id;
            title.setText(recipeinfo.title);
            likes.setText(recipeinfo.like.toString());
            description.setText(recipeinfo.description);

            // items 받기
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<recipeinfo.items.size();i++) sb.append(recipeinfo.items.get(i)+" ");
            items.setText(sb.toString());

            // 이미지 로드
            Picasso.get().load(MyGlobal.getData()+"/image/"+recipeinfo.imagepath).into(mealImage);


        }

        // 리스트 클릭 시 Detailed Recipe로 이동
        @Override
        public void onClick(View v) {
            // open할 activity
            Intent toRecipe = new Intent(context, RecipeDetailedActivity.class);

            // main image, title, id 정보 넘기기
            toRecipe.putExtra("image",((BitmapDrawable)mealImage.getDrawable()).getBitmap());
            toRecipe.putExtra("title",title.getText());
            toRecipe.putExtra("recipe_id",recipe_id);

            // activity 시작
            context.startActivity(toRecipe);

        }
    }


}