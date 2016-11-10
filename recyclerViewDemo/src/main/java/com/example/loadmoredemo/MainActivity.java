package com.example.loadmoredemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.loadmoredemo.ui.GridLayoutActivity;
import com.example.loadmoredemo.ui.LinearLayoutActivity;
import com.example.loadmoredemo.ui.StaggeredGridLayoutActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_linear_layout, R.id.btn_grid_layout, R.id.btn_staggered_layout})
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
            default:
                break;
        }
    }
}
