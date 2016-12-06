package com.example.loadmoredemo.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.GridLayout;

import com.example.loadmoredemo.R;
import com.example.loadmoredemo.ui.adapter.ItemDecorationAdapter;
import com.example.loadmoredemo.ui.widget.CcDividerGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * RecyclerView分割线
 */
public class ItemDecorationActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    //定义一个List集合，用于存放RecyclerView中的每一个数据
    private List<String> mData = null;
    private ItemDecorationAdapter adapter;
    //定义一个LinearLayoutManager
    private LinearLayoutManager mLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_decoration);
        ButterKnife.bind(this);
        //RecyclerView三步曲+LayoutManager
        initData();
        adapter = new ItemDecorationAdapter(this, mData);
        /*recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));*/
        gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(GridLayout.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new CcDividerGridItemDecoration(this,0));
        //这句就是添加我们自定义的分隔线
        // recyclerView.addItemDecoration(new CcDividerGridItemDecoration(ItemDecorationActivity.this,0));
    }

    //初始化加载到RecyclerView中的数据, 我这里只是给每一个Item添加了String类型的数据
    private void initData() {
        mData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mData.add("Item" + i);
        }
    }
}
