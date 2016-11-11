package com.example.loadmoredemo;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Administrator on 2016/11/11.
 */
public class ImageUtil {
    public static void loadimg(Context context, String imgUrl, ImageView imgview) {
        Picasso.with(context).load(imgUrl).placeholder(R.drawable.bg).error(R.drawable.bg).into(imgview);
    }
}
