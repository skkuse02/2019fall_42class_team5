package com.example.se_team5.ui.recipe;

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

public class StepAdapter extends RecyclerView.Adapter {
    private List<Step> stepList; // Adapter에 추가된 데이터를 저장하기 위한 ArrayList

    public StepAdapter(List<Step> stepList) {
        this.stepList = stepList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_info,parent,false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((StepViewHolder)holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    private class StepViewHolder extends RecyclerView.ViewHolder{
        private TextView step_order;
        private ImageView step_image;
        private TextView description;

        public StepViewHolder(View itemView){
            super(itemView);
            // view 지정
            step_order  = itemView.findViewById(R.id.step_order);
            step_image = itemView.findViewById(R.id.step_image);
            description = itemView.findViewById(R.id.step_description);

        }

        // 해당 view의 position에 값 bind
        public void bindView(int position){
            Step step = stepList.get(position);
            step_order.setText("STEP "+position);
            description.setText(step.description);
            // 이미지 로드
            Picasso.get().load(MyGlobal.getData()+"/image/"+step.imagepath).into(step_image);


        }

    }


}