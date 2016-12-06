package com.example.loadmoredemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.loadmoredemo.ui.activity.BannerActivity;
import com.example.loadmoredemo.ui.activity.BannerHeadActivity;
import com.example.loadmoredemo.ui.activity.GridLayoutActivity;
import com.example.loadmoredemo.ui.activity.ItemDecorationActivity;
import com.example.loadmoredemo.ui.activity.LinearLayoutActivity;
import com.example.loadmoredemo.ui.activity.ScrollViewWithRecyclerViewActivity;
import com.example.loadmoredemo.ui.activity.StaggeredGridLayoutActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.btn_linear_layout)
    Button btnLinearLayout;
    @BindView(R.id.btn_grid_layout)
    Button btnGridLayout;
    @BindView(R.id.btn_staggered_layout)
    Button btnStaggeredLayout;
    @BindView(R.id.btn_banner_head)
    Button btnBannerHead;
    @BindView(R.id.btn_recycler_view_divider)
    Button btnRecyclerViewDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_linear_layout, R.id.btn_grid_layout, R.id.btn_staggered_layout, R.id.btn_banner_head, R.id.btn_test_banner, R.id.btn_scroll_recycler})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_linear_layout:
                startActivity(new Intent(this, LinearLayoutActivity.class));
                break;
            case R.id.btn_grid_layout:
                startActivity(new Intent(this, GridLayoutActivity.class));
                break;
            case R.id.btn_staggered_layout:
                startActivity(new Intent(this, StaggeredGridLayoutActivity.class));
                break;
            case R.id.btn_banner_head:
                startActivity(new Intent(this, BannerHeadActivity.class));
                break;
            case R.id.btn_test_banner:
                startActivity(new Intent(this, BannerActivity.class));
                break;
            case R.id.btn_scroll_recycler:
                startActivity(new Intent(this, ScrollViewWithRecyclerViewActivity.class));
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.btn_recycler_view_divider)
    public void onClick() {
        startActivity(new Intent(this, ItemDecorationActivity.class));
    }
}
