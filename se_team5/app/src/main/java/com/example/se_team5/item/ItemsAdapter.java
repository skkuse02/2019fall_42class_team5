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

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder>{
    /*아이템리스트를 리사이클러 뷰의 형태로 변환해주는 어댑터*/

    private ArrayList<Item> myDataList;         //뷰로 전환할 아이템 리스트
    private SparseBooleanArray mSelectedItems = new SparseBooleanArray(0);  //선택된 아이템의 포지션을 저장할 객체

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

            itemView.setOnClickListener(new View.OnClickListener() {
                /*뷰가 클릭되면 selectedItem에 추가하고 색을 변화한다.*/
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (mSelectedItems.get(position, false) == false) {
                        mSelectedItems.put(position, true);

                        v.setBackgroundColor(Color.rgb(255,204,153));
                    } else {
                        mSelectedItems.delete(position);
                        v.setBackgroundColor(Color.rgb(255,255,255));
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

    public ItemsAdapter(ArrayList<Item> dataList)
    {
        /*아이템리스트를 받아와 저장*/
        myDataList = dataList;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        /*뷰를 보여주는 context를 지정*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewer_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        /*뷰에 이미지와 텍스트를 지정*/
        viewHolder.img.setImageResource(myDataList.get(position).getImageResourceID());
        viewHolder.name.setText(myDataList.get(position).getName());
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

    public SparseBooleanArray getmSelectedItems(){
        return mSelectedItems;
    }
}