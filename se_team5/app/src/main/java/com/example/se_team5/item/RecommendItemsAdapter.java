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
    private ArrayList<Item> myDataList;
    private SparseBooleanArray mSelectedItems1 = new SparseBooleanArray(0);
    private SparseBooleanArray mSelectedItems2 = new SparseBooleanArray(0);


    public interface OnListItemLongSelectedInterface {
        void onItemLongSelected(View v, int position);
    }

    public interface OnListItemSelectedInterface {
        void onItemSelected(View v, int position);
    }

    private OnListItemSelectedInterface mListener;
    private OnListItemLongSelectedInterface mLongListener;


    public class ViewHolder extends RecyclerView.ViewHolder {
        //ImageView imageView;
        TextView name;
        ImageView img;

        ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (mSelectedItems1.get(position, false) == false && mSelectedItems2.get(position, false) == false) {
                        mSelectedItems1.put(position, true);
                        v.setBackgroundColor(Color.YELLOW);
                        //notifyItemChanged(position);
                    }else if (mSelectedItems1.get(position, false) == true && mSelectedItems2.get(position, false) == false) {
                        mSelectedItems1.delete(position);
                        mSelectedItems2.put(position, true);
                        v.setBackgroundColor(Color.CYAN);
                        //notifyItemChanged(position);
                    } else if(mSelectedItems2.get(position, false) == true && mSelectedItems1.get(position, false) == false) {
                        mSelectedItems2.delete(position);
                        v.setBackgroundColor(Color.WHITE);
                       // notifyItemChanged(position);
                    }else{
                        mSelectedItems1.delete(position);
                        mSelectedItems2.delete(position);
                        v.setBackgroundColor(Color.WHITE);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongListener.onItemLongSelected(v, getAdapterPosition());
                    return false;
                }
            });

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
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        viewHolder.img.setImageResource(myDataList.get(position).getImageResourceID());
        viewHolder.name.setText(myDataList.get(position).getName1() + " " + myDataList.get(position).getName2());
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
