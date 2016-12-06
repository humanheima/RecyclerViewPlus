package com.example.loadmoredemo.ui.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/11/17.
 * 用来演示如何当ScrollView和RecyclerView嵌套的时候，如何解决滑动冲突
 */
public class MyScrollView extends ScrollView {

    private int lastX, lastY;
    private TextView textView;
    private RecyclerView recyclerView;

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            Log.e("top_bottom", textView.getTop() + "," + textView.getBottom());
            float y = ev.getRawY();
            if (y >= textView.getBottom()) {
                return false;
            }
            float endY = recyclerView.getHeight();
            if (y <= endY) {
                return false;
            }
        }
        return true;
    }
}
