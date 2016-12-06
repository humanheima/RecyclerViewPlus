package com.example.loadmoredemo.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.loadmoredemo.Constants;
import com.example.loadmoredemo.R;
import com.example.loadmoredemo.listener.OnItemClickListener;
import com.example.loadmoredemo.listener.OnItemLongClickListener;
import com.example.loadmoredemo.listener.OnLoadMoreListener;
import com.example.loadmoredemo.ui.adapter.RvAdapter;
import com.example.loadmoredemo.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class ScrollViewWithRecyclerViewActivity extends BaseActivity {

    @BindView(R.id.ptr_layout)
    PtrClassicFrameLayout ptrLayout;
    private RvAdapter adapter;
    private List<String> dataList = new ArrayList<>();
    int page = 1;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private float deltY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view_with_recycler_view);
        ButterKnife.bind(this);
        headerView = LayoutInflater.from(this).inflate(R.layout.head_view, null);
        loadingView = LayoutInflater.from(this).inflate(R.layout.loading_view, null);
        loadAllView = LayoutInflater.from(this).inflate(R.layout.load_all_view, null);

        ptrLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return deltY == 0 && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getData();
            }
        });
        ptrLayout.post(new Runnable() {
            @Override
            public void run() {
                ptrLayout.autoRefresh();
            }
        });
    }

    /**
     * 给recyclerView设置适配器
     */
    public void setAdapter() {
        Log.e("setAdapter", "size" + dataList.size());
        if (adapter == null) {
            adapter = new RvAdapter(dataList);
            //adapter.addHeaderView(headerView);
            linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    deltY += dy;
                }
            });
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(ScrollViewWithRecyclerViewActivity.this, "你点击了position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
            adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public void onItemLongClick(View view, int position) {
                    Toast.makeText(ScrollViewWithRecyclerViewActivity.this, "你长按了position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    //每次上拉加载更多之前要把page++
                    page++;
                    getData();
                }
            });
            adapter.addLoadingView(loadingView);
        }
        //更新适配器
        adapter.reset();
        if (ptrLayout.isRefreshing()) {
            ptrLayout.refreshComplete();
        }
    }


    public void getData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (page == 1) {
                    dataList.clear();
                    for (int i = 0; i < 10; i++) {
                        dataList.add(Constants.imageUrls[i]);
                    }
                    setAdapter();
                } else {
                    if (page > 3) {
                        if (adapter != null) {
                            adapter.setLoadAll(true);
                            //加载完成用Footer
                            adapter.addFooterView(loadAllView);
                        }
                    } else {
                        for (int i = 0; i < 10; i++) {
                            dataList.add(Constants.imageUrls[i]);
                        }
                        setAdapter();
                    }
                }
            }
        }, 2000);
    }
}
