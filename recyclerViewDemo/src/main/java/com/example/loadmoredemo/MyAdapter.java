package com.example.loadmoredemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.loadmoredemo.listener.OnLoadMoreListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/10.
 */
public class MyAdapter extends BaseAdapter {

    List<String> dataList;
    private Context context;
    private List<Integer> heights;

    public MyAdapter(RecyclerView recyclerView, List<String> list, OnLoadMoreListener onloadMoreListener) {
        super(recyclerView, onloadMoreListener);
        this.dataList = list;
        Log.e("tag", "dataList.size()" + dataList.size());
        context = recyclerView.getContext();
        heights = new ArrayList<>();
        initRandomHeight(300);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LOAD_MORE_ITEM) {
            return super.onCreateViewHolder(parent, viewType);
        } else {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
            return new MyHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();//得到item的LayoutParams布局参数
                params.height = heights.get(position);//把随机的高度赋予itemView布局
                holder.itemView.setLayoutParams(params);//把params设置给itemView布局
            }
            Picasso.with(context).load(dataList.get(position)).into(((MyHolder) holder).imgView);
        }
    }

    private void initRandomHeight(int size) {
        heights = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            heights.add((int) (160 + Math.random() * 160));
        }
    }

    /**
     * 获取随机高度,可能会越界
     *
     * @return
     */
    private int getRandomHeight(int position) {//得到随机item的高度
        return heights.get(position);
    }

    @Override
    int getDataSize() {
        if (dataList == null) {
            return 0;
        } else {
            return dataList.size();
        }
    }


    class MyHolder extends RecyclerView.ViewHolder {

        ImageView imgView;

        public MyHolder(View itemView) {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(R.id.imgView);
        }
    }
}
