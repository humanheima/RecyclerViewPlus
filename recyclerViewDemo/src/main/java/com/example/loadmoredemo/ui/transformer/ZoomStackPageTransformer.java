package com.example.loadmoredemo.ui.transformer;

import android.view.View;

import static android.support.v4.view.ViewCompat.setAlpha;
import static android.support.v4.view.ViewCompat.setPivotX;
import static android.support.v4.view.ViewCompat.setPivotY;
import static android.support.v4.view.ViewCompat.setScaleX;
import static android.support.v4.view.ViewCompat.setScaleY;
import static android.support.v4.view.ViewCompat.setTranslationX;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/6/19 上午8:41
 * 描述:
 */
public class ZoomStackPageTransformer extends BGAPageTransformer {

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
        setTranslationX(view, -view.getWidth() * position);

        setPivotX(view, view.getWidth() * 0.5f);
        setPivotY(view, view.getHeight() * 0.5f);
        setScaleX(view, 1 + position);
        setScaleY(view, 1 + position);

        if (position < -0.95f) {
            setAlpha(view, 0);
        } else {
            setAlpha(view, 1);
        }
    }

    @Override
    public void handleRightPage(View view, float position) {
        setTranslationX(view, -view.getWidth() * position);

        setPivotX(view, view.getWidth() * 0.5f);
        setPivotY(view, view.getHeight() * 0.5f);
        setScaleX(view, 1 + position);
        setScaleY(view, 1 + position);

        if (position > 0.95f) {
            setAlpha(view, 0);
        } else {
            setAlpha(view, 1);
        }
    }

}