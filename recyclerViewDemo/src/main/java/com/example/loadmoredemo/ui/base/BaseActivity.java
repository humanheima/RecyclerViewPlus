package com.example.loadmoredemo.ui.base;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Administrator on 2016/11/11.
 */
public class BaseActivity extends AppCompatActivity {

    protected View headerView;
    protected View loadingView;
    protected View loadAllView;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }
}
