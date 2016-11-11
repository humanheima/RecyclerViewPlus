package com.example.loadmoredemo.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.loadmoredemo.Constants;
import com.example.loadmoredemo.R;
import com.example.loadmoredemo.ui.widget.SuperCopyBanner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BannerActivity extends AppCompatActivity {

    private List<String> bannerImgs = new ArrayList<>();
    private List<String> bannerTitles = new ArrayList<>();
    @BindView(R.id.test_banner)
    SuperCopyBanner testBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        ButterKnife.bind(this);
        bannerImgs.add(Constants.imageUrls[0]);
        bannerImgs.add(Constants.imageUrls[1]);
        bannerImgs.add(Constants.imageUrls[2]);
        bannerImgs.add(Constants.imageUrls[3]);
        bannerTitles.add("今天双十一");
        bannerTitles.add("没人发狗粮");
        bannerTitles.add("一点不敞亮");
        bannerTitles.add("自己打拳皇");
        testBanner.setImages(bannerImgs)
                .setBannerTitles(bannerTitles)
                .isAutoPlay(true)
                .start();
    }
}
