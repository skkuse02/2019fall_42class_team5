package com.example.se_team5.ui.recipe;

import android.content.Context;
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
    private Context context; // fragment context
    private List<Step> stepList; // Adapter에 추가된 데이터를 저장하기 위한 ArrayList

    public StepAdapter(Context context,List<Step> stepList) {
        this.context = context;
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
        private Integer order;
        private ImageView image;
        private TextView title;
        private TextView description;

        public StepViewHolder(View itemView){
            super(itemView);

            // view 지정
            image = (ImageView) itemView.findViewById(R.id.step_image);
            title = (TextView) itemView.findViewById(R.id.step);
            description = (TextView) itemView.findViewById(R.id.step_description);

        }

        // 해당 view의 position에 값 bind
        public void bindView(int position){
            Step step = stepList.get(position);

            order = step.order;
            title.setText(step.title);
            description.setText(step.description);

            // 이미지 로드
            Picasso.get().load(MyGlobal.getData()+"/image/"+step.imagepath).into(image);


        }

    }


}