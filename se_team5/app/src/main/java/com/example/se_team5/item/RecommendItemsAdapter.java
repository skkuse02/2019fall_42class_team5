package com.example.se_team5.item;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.se_team5.R;

import java.util.ArrayList;

public class RecommendItemsAdapter extends RecyclerView.Adapter<RecommendItemsAdapter.ViewHolder>{
    /*추천을 위한 리사이클러 뷰에 아이템 리스트를 보여주는 어댑터*/

    private ArrayList<Item> myDataList;         //뷰에 보여줄 아이템 리스트
    private SparseBooleanArray mSelectedItems1 = new SparseBooleanArray(0); //선호하는 재료를 저장하는 객체
    private SparseBooleanArray mSelectedItems2 = new SparseBooleanArray(0); //비호하는 재료를 저장하는 객체


    public interface OnListItemLongSelectedInterface {
        void onItemLongSelected(View v, int position);
    }

    public interface OnListItemSelectedInterface {
        void onItemSelected(View v, int position);
    }

    private OnListItemSelectedInterface mListener;
    private OnListItemLongSelectedInterface mLongListener;


    /*뷰를 구성하는 뷰홀더*/
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView img;

        ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
        }
    }

    public RecommendItemsAdapter(ArrayList<Item> dataList)
    {
        myDataList = dataList;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewer_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position)
    {
        viewHolder.img.setImageResource(myDataList.get(position).getImageResourceID());
        viewHolder.name.setText(myDataList.get(position).getName());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            /*클릭 시 해당 아이템을 선택된 아이템에 반영*/
            @Override
            public void onClick(View view) {
                if (mSelectedItems1.get(position, false) == false && mSelectedItems2.get(position, false) == false) {
                    mSelectedItems1.put(position, true);
                }else if (mSelectedItems1.get(position, false) == true && mSelectedItems2.get(position, false) == false) {
                    mSelectedItems1.delete(position);
                    mSelectedItems2.put(position, true);
                } else if(mSelectedItems2.get(position, false) == true && mSelectedItems1.get(position, false) == false) {
                    mSelectedItems2.delete(position);
                }
                notifyDataSetChanged();
            }
        });

        /*선택된 아이템들의 색을 지정*/
        if(mSelectedItems1.get(position, false) == true){
            viewHolder.itemView.setBackgroundColor(Color.rgb(255,204,153));
        }else if(mSelectedItems2.get(position, false) == true){
            viewHolder.itemView.setBackgroundColor(Color.rgb(153,204,255));
        }else{
            viewHolder.itemView.setBackgroundColor(Color.rgb(255,255,255));
        }
    }

    @Override
    public int getItemCount()
    {
        return (null != myDataList ? myDataList.size() : 0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public SparseBooleanArray getmSelectedItems1(){
        return mSelectedItems1;
    }

    public SparseBooleanArray getmSelectedItems2(){
        return mSelectedItems2;
    }
}
