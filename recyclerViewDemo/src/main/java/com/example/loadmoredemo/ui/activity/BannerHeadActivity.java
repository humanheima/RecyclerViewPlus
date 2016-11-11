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
import com.example.loadmoredemo.listener.OnBannerClickListener;
import com.example.loadmoredemo.listener.OnItemClickListener;
import com.example.loadmoredemo.listener.OnItemLongClickListener;
import com.example.loadmoredemo.listener.OnLoadMoreListener;
import com.example.loadmoredemo.ui.adapter.RvAdapter;
import com.example.loadmoredemo.ui.base.BaseActivity;
import com.example.loadmoredemo.ui.widget.SuperCopyBanner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class BannerHeadActivity extends BaseActivity {

    private RvAdapter adapter;
    private List<String> dataList = new ArrayList<>();
    private List<String> bannerImgs = new ArrayList<>();
    private List<String> bannerTitles = new ArrayList<>();
    private int page = 1;
    private int offsetY;//recyclerView 竖直方向上滑动的距离
    private SuperCopyBanner banner;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.myPtrFrameLayout)
    PtrClassicFrameLayout ptrFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_layout);
        ButterKnife.bind(this);
        bannerImgs.add(Constants.imageUrls[0]);
        bannerImgs.add(Constants.imageUrls[1]);
        bannerImgs.add(Constants.imageUrls[2]);
        bannerImgs.add(Constants.imageUrls[3]);
        bannerTitles.add("今天双十一");
        bannerTitles.add("没人发狗粮");
        bannerTitles.add("一点不敞亮");
        bannerTitles.add("自己打拳皇");
        headerView = LayoutInflater.from(this).inflate(R.layout.banner_layout, null);
        banner = (SuperCopyBanner) headerView.findViewById(R.id.super_banner);
        banner.setImages(bannerImgs)
                .setBannerTitles(bannerTitles)
                .isAutoPlay(true);
        banner.setmOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                Toast.makeText(BannerHeadActivity.this, "banner click position=" + position, Toast.LENGTH_SHORT).show();
            }
        });
        loadingView = LayoutInflater.from(this).inflate(R.layout.loading_view, null);
        loadAllView = LayoutInflater.from(this).inflate(R.layout.load_all_view, null);
        ptrFrameLayout.disableWhenHorizontalMove(true);
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            //检查是否可以刷新
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            //下拉刷新的时候会调用这个方法，每次下拉刷新都要把page重置为1
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 1;
                if (adapter != null) {
                    adapter.setLoadAll(false);
                }
                if (banner != null) {
                    banner.stopAutoPlay();
                }
                getData();
            }
        });
        /**
         * 延迟500毫秒后ptrFrameLayout自动刷新会调用checkCanDoRefresh(PtrFrameLayout frame, View content, View header)
         * 检查是否可以刷新，如果可以，就调用onRefreshBegin(PtrFrameLayout frame)
         */
        ptrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrameLayout.autoRefresh();
            }
        }, 500);
    }

    /**
     * 给recyclerView设置适配器
     */
    public void setAdapter() {
        Log.e("setAdapter", "size" + dataList.size());
        if (adapter == null) {
            adapter = new RvAdapter(dataList);
            adapter.addHeaderView(headerView);
            banner.start();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    offsetY += dy;
                    if (offsetY > headerView.getHeight()) {
                        banner.stopAutoPlay();
                    } else {
                        banner.startAutoPlay();
                    }
                }
            });
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(BannerHeadActivity.this, "你点击了position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
            adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public void onItemLongClick(View view, int position) {
                    Toast.makeText(BannerHeadActivity.this, "你长按了position:" + position, Toast.LENGTH_SHORT).show();
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

        //如果正在下拉刷新的话，结束下拉刷新
        if (ptrFrameLayout.isRefreshing()) {
            ptrFrameLayout.refreshComplete();
            if (banner != null) {
                banner.start();
            }
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
