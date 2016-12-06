package com.example.loadmoredemo.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.loadmoredemo.R;

import java.util.List;

public class ItemDecorationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //定义一个集合，接收从Activity中传递过来的数据和上下文
    private List<String> mList;
    private Context mContext;

    public ItemDecorationAdapter(Context context, List<String> list){
        this.mContext = context;
        this.mList = list;
    }

    //得到child的数量
    @Override
    public int getItemCount() {
        return mList.size();
    }

    //创建ChildView
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(mContext).inflate(R.layout.item_view, parent, false);
        return new MyHolder(layout);
    }

    //将数据绑定到每一个childView中
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder){
            final String itemText = mList.get(position);
            ((MyHolder)holder).tv.setText(itemText);
        }
    }

    // 通过holder的方式来初始化每一个ChildView的内容
    class MyHolder extends RecyclerView.ViewHolder{
        TextView tv;
        public MyHolder(View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.list_item);
        }
    }
}