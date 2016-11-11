package com.example.loadmoredemo.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.loadmoredemo.ImageUtil;
import com.example.loadmoredemo.R;
import com.example.loadmoredemo.ui.base.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/10.
 */
public class RvAdapter extends BaseAdapter {

    private List<String> dataList;
    private List<Integer> heights;
    private Context context;
    private String tag = "RvAdapter";

    public RvAdapter(List<String> list) {
        this.dataList = list;
        Log.e("tag", "dataList.size()" + dataList.size());
        heights = new ArrayList<>();
        initRandomHeight(300);
    }

    private void initRandomHeight(int size) {
        heights = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            heights.add((int) (160 + Math.random() * 160));
        }
    }

    @Override
    protected int getDataSize() {
        if (dataList == null || dataList.isEmpty()) {
            return 0;
        } else {
            return dataList.size();
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyHolder(itemView);
    }

    @Override
    protected void onBindView(RecyclerView.ViewHolder holder, int position) {
        Log.e(tag, "onBindView" + position);
        if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();//得到item的LayoutParams布局参数
            params.height = heights.get(position);//把随机的高度赋予itemView布局
            holder.itemView.setLayoutParams(params);//把params设置给itemView布局
        }
        ImageUtil.loadimg(context, dataList.get(position), ((MyHolder) holder).imgView);
        //Picasso.with(context).load(dataList.get(position)).into(((MyHolder) holder).imgView);
    }

    private class MyHolder extends RecyclerView.ViewHolder {

        ImageView imgView;

        public MyHolder(View itemView) {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(R.id.imgView);
        }
    }
}
