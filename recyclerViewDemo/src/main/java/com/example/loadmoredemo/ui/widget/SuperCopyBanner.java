package com.example.loadmoredemo.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.loadmoredemo.R;
import com.example.loadmoredemo.listener.OnBannerClickListener;
import com.example.loadmoredemo.ui.transformer.BGAPageTransformer;
import com.example.loadmoredemo.ui.transformer.TransitionEffect;

import java.util.ArrayList;
import java.util.List;

import static com.example.loadmoredemo.R.styleable.Banner;


/**
 * Created by dumingwei on 2016/10/22.
 * 简单的图片轮播
 */

public class SuperCopyBanner extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private static final int RMP = LayoutParams.MATCH_PARENT;
    private static final int RWC = LayoutParams.WRAP_CONTENT;
    private static final int LWC = LinearLayout.LayoutParams.WRAP_CONTENT;
    private String tag = SuperCopyBanner.class.getSimpleName();
    private int count;//图片轮播的数量
    private List imageUrls;//轮播的图片的加载地址
    private List<String> titles;//轮播的标题
    private List<ImageView> indicatorImages;//多个轮播图片时，底部的小圆点
    private BannerViewPager viewPager;
    private int duration = 800;//viewpager 切换页面的时间
    private BannerPagerAdapter adapter;
    private LinearLayout llIndicator;//使用线性布局放置轮播小圆点
    private int mPointGravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnBannerClickListener mOnBannerClickListener;
    //之前显示的图片
    private int preSelect = -1;
    //当前显示图片
    private int nowSelect = 0;
    private TextView textNumIndicator;
    private TextView mTipTv;//轮播的文字
    private Context context;
    private Handler handler = new Handler();
    //attrs 属性
    private int delayTime = 4000;//默认轮播时间3000毫秒
    private boolean isAutoPlay = false;//默认自动轮播为false
    private boolean isNumIndicator = false;//标志是否是数字指示
    private int mIndicatorMargin;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private TransitionEffect transitionEffect;
    private int mTipTextColor;//轮播文字的颜色
    private int mTipTextSize;
    private int numIndicatorTextColor;//数字指示器的文字的颜色
    private int numIndicatorTextSize;
    private Drawable mPointContainerBackgroundDrawable;//圆点指示器的背景
    private Drawable numIndicatorBackground;//数字指示器的背景
    // private int mIndicatorSelectedResId = R.drawable.selected_radius;//选中的时候小圆点
    //private int mIndicatorUnselectedResId = R.drawable.unselected_radius;//未选中时的小圆点
    private int mPointDrawableResId;

    public void setmOnBannerClickListener(OnBannerClickListener mOnBannerClickListener) {
        this.mOnBannerClickListener = mOnBannerClickListener;
    }

    public SuperCopyBanner(Context context) {
        this(context, null);
    }

    public SuperCopyBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperCopyBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        titles = new ArrayList<>();
        imageUrls = new ArrayList<>();
        indicatorImages = new ArrayList<>();
        //初始化属性
        initTypedArray(context, attrs);
        initView(context);
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, Banner);
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.Banner_indicator_width, 16);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.Banner_indicator_height, 16);
        mIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.Banner_indicator_margin, 8);
        //mIndicatorSelectedResId = typedArray.getResourceId(R.styleable.Banner_indicator_drawable_selected, R.drawable.selected_radius);
        //mIndicatorUnselectedResId = typedArray.getResourceId(R.styleable.Banner_indicator_drawable_unselected, R.drawable.unselected_radius);
        delayTime = typedArray.getInt(R.styleable.Banner_delay_time, delayTime);
        isAutoPlay = typedArray.getBoolean(R.styleable.Banner_is_auto_play, isAutoPlay);
        isNumIndicator = typedArray.getBoolean(R.styleable.Banner_is_num_indicator, false);
        numIndicatorTextColor = typedArray.getColor(R.styleable.Banner_number_indicator_text_color, Color.WHITE);
        numIndicatorTextSize = typedArray.getDimensionPixelSize(R.styleable.Banner_title_textsize, 14);
        numIndicatorBackground = typedArray.getDrawable(R.styleable.Banner_num_indicator_bg);
        mPointContainerBackgroundDrawable = typedArray.getDrawable(R.styleable.Banner_banner_pointContainerBackground);
        mPointDrawableResId = typedArray.getResourceId(R.styleable.Banner_banner_pointDrawable, R.drawable.bg_banner_selector_point_solid);
        mTipTextColor = typedArray.getColor(R.styleable.Banner_banner_tipTextColor, Color.WHITE);
        mTipTextSize = typedArray.getDimensionPixelSize(R.styleable.Banner_banner_tipTextSize, 16);
        mPointGravity = typedArray.getInt(R.styleable.Banner_banner_point_gravity, mPointGravity);
        int ordinal = typedArray.getInt(R.styleable.Banner_banner_transitionEffect, TransitionEffect.Default.ordinal());
        transitionEffect = TransitionEffect.values()[ordinal];
        typedArray.recycle();
    }

    private void initView(Context context) {
        RelativeLayout pointContainerRl = new RelativeLayout(context);
        if (Build.VERSION.SDK_INT >= 16) {
            pointContainerRl.setBackground(mPointContainerBackgroundDrawable);
        } else {
            pointContainerRl.setBackgroundDrawable(mPointContainerBackgroundDrawable);
        }
        LayoutParams pointContainerLp = new LayoutParams(RMP, RWC);
        // 处理圆点在顶部还是底部
        if ((mPointGravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.TOP) {
            pointContainerLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else {
            pointContainerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        addView(pointContainerRl, pointContainerLp);

        LayoutParams indicatorLp = new LayoutParams(RWC, RWC);
        indicatorLp.addRule(CENTER_VERTICAL);
        if (isNumIndicator) {
            textNumIndicator = new TextView(getContext());
            textNumIndicator.setId(R.id.banner_indicator_id);
            textNumIndicator.setGravity(Gravity.CENTER_VERTICAL);
            textNumIndicator.setSingleLine();
            textNumIndicator.setEllipsize(TextUtils.TruncateAt.END);
            textNumIndicator.setTextColor(numIndicatorTextColor);
            textNumIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, numIndicatorTextSize);
            textNumIndicator.setVisibility(INVISIBLE);
            if (numIndicatorBackground != null) {
                if (Build.VERSION.SDK_INT >= 16) {
                    textNumIndicator.setBackground(numIndicatorBackground);
                } else {
                    textNumIndicator.setBackgroundDrawable(numIndicatorBackground);
                }
            }
            pointContainerRl.addView(textNumIndicator, indicatorLp);
        } else {
            llIndicator = new LinearLayout(context);
            llIndicator.setId(R.id.banner_indicator_id);
            llIndicator.setOrientation(LinearLayout.HORIZONTAL);
            pointContainerRl.addView(llIndicator, indicatorLp);
        }

        LayoutParams tipLp = new LayoutParams(RMP, RWC);
        tipLp.addRule(CENTER_VERTICAL);
        mTipTv = new TextView(context);
        mTipTv.setGravity(Gravity.CENTER_VERTICAL);
        mTipTv.setSingleLine(true);
        mTipTv.setEllipsize(TextUtils.TruncateAt.END);
        mTipTv.setTextColor(mTipTextColor);
        mTipTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTipTextSize);
        pointContainerRl.addView(mTipTv, tipLp);

        int horizontalGravity = mPointGravity & Gravity.HORIZONTAL_GRAVITY_MASK;

        // 处理指示器在左边、右边还是水平居中
        if (horizontalGravity == Gravity.LEFT) {
            indicatorLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            tipLp.addRule(RelativeLayout.RIGHT_OF, R.id.banner_indicator_id);
            mTipTv.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        } else if (horizontalGravity == Gravity.RIGHT) {
            indicatorLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tipLp.addRule(RelativeLayout.LEFT_OF, R.id.banner_indicator_id);
        } else {
            indicatorLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            tipLp.addRule(RelativeLayout.LEFT_OF, R.id.banner_indicator_id);
        }
    }

    public SuperCopyBanner isNumberIndicator(boolean isNumIndicator) {
        this.isNumIndicator = isNumIndicator;
        return this;
    }

    public SuperCopyBanner isAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
        return this;
    }

    public SuperCopyBanner setDelayTime(int delayTime) {
        this.delayTime = delayTime;
        return this;
    }

    /*public SuperCopyBanner setPointGravity(int gravity) {
        this.mPointGravity = gravity;
        return this;
    }*/

    public SuperCopyBanner setBannerTitles(List<String> titles) {
        this.titles = titles;
        return this;
    }

    public SuperCopyBanner setImages(List<?> imagesUrl) {
        this.imageUrls = imagesUrl;
        return this;
    }

    public SuperCopyBanner setTransitionEffect(TransitionEffect transitionEffect) {
        this.transitionEffect = transitionEffect;
        return this;
    }

    public void start() {
        initPlay(titles, imageUrls);
        if (isAutoPlay) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(tag, "开始自动轮播");
                    startAutoPlay();
                }
            }, 500);

        }
    }

    /**
     * @param titles
     * @param imageUrls
     */
    private void initPlay(List<String> titles, List imageUrls) {
        if ((titles == null || titles.size() <= 0) || (titles == null || titles.size() <= 0)) {
            throw new IllegalStateException("when initPlay(List<String> titles, List<?> imageUrls) 标题和图片地址不能为空!");
        }
        count = imageUrls.size();
        //轮播图片大于一个,并且不是数字指示，才显示底部的小圆点
        if (count > 1) {
            initIndicator();
        }
        initViewPager();
    }

    /**
     * 添加轮播图片底部的小圆点
     */
    private void initIndicator() {
        if (llIndicator != null) {
            // indicatorImages.clear();
            llIndicator.removeAllViews();
            for (int i = 0; i < count; i++) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LWC, LWC);
                lp.leftMargin = mIndicatorMargin;
                lp.rightMargin = mIndicatorMargin;
                imageView.setLayoutParams(lp);
                imageView.setImageResource(mPointDrawableResId);
                //indicatorImages.add(imageView);
                llIndicator.addView(imageView);
            }
        }
        if (textNumIndicator != null) {
            textNumIndicator.setVisibility(VISIBLE);
        }
    }

    private void initViewPager() {
        if (viewPager != null && this.equals(viewPager.getParent())) {
            this.removeView(viewPager);
            viewPager = null;
        }
        viewPager = new BannerViewPager(getContext());
        viewPager.setOverScrollMode(OVER_SCROLL_ALWAYS);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setFocusable(true);
        adapter = new BannerPagerAdapter(imageUrls, titles);
        viewPager.setAdapter(adapter);
        if (count > 1) {
            viewPager.addOnPageChangeListener(this);
            viewPager.setPageTransformer(true, BGAPageTransformer.getPageTransformer(transitionEffect));
            viewPager.setScrollable(true);
            //设置页面切换的时间
            if (duration >= 0 && duration <= 2000) {
                viewPager.setPageChangeDuration(duration);
            }
            changeLoopPoint(nowSelect);
        } else {
            viewPager.setScrollable(false);
        }
        addView(viewPager, 0, new LayoutParams(RMP, RMP));
    }

    private void changeLoopPoint(int position) {
        preSelect = nowSelect;
        nowSelect = position;
        if (isNumIndicator) {
            textNumIndicator.setText((nowSelect + 1) + "/" + count);
        } else {
            for (int i = 0; i < llIndicator.getChildCount(); i++) {
                llIndicator.getChildAt(i).setEnabled(false);
            }
            llIndicator.getChildAt(nowSelect).setEnabled(true);
        }

        if (mTipTv != null && titles != null) {
            mTipTv.setText(titles.get(nowSelect));
        }
    }

    public void startAutoPlay() {
        handler.removeCallbacks(task);
        handler.postDelayed(task, delayTime);
    }

    public void stopAutoPlay() {
        handler.removeCallbacks(task);
    }

    /**
     * 处理触摸事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isAutoPlay) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_OUTSIDE) {
                startAutoPlay();
            } else if (action == MotionEvent.ACTION_DOWN) {
                stopAutoPlay();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.e(tag, "onPageSelected position=" + position);
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
        position %= count;
        //改变底部的小圆点
        if (count > 1) {
            changeLoopPoint(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private final Runnable task = new Runnable() {

        @Override
        public void run() {
            int num = adapter.getCount();
            if (num > 2) {
                int index = viewPager.getCurrentItem();
                index = index % (num - 2) + 1;
                Log.e(tag, "自动轮播");
                viewPager.setCurrentItem(index);
            }
            handler.postDelayed(task, delayTime);
        }
    };

    /*@Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            startAutoPlay();
        } else if (visibility == INVISIBLE) {
            stopAutoPlay();
        }
    }*/

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAutoPlay();
    }

    /**
     * ViewPager 的适配器
     */
    class BannerPagerAdapter extends PagerAdapter {

        private final int FAKE_BANNER_SIZE = 100;

        private List imgUrls;
        private List<String> titles;

        public BannerPagerAdapter(List imgUrls, List<String> titles) {
            this.imgUrls = imgUrls;
            this.titles = titles;
        }

        @Override
        public int getCount() {
            if (count == 1) {
                return 1;
            }
            return FAKE_BANNER_SIZE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position %= count;
            final int pos = position;
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.img_banner);
            // TextView textView = (TextView) view.findViewById(R.id.text_banner_title);
            // textView.setText(titles.get(position));
            Glide.with(context).load(imgUrls.get(position)).into(imageView);
            if (mOnBannerClickListener != null) {
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnBannerClickListener.OnBannerClick(pos);
                    }
                });
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (count > 1) {
                int position = viewPager.getCurrentItem();
                Log.e(tag, "finishUpdate" + position);
                if (position == 0) {
                    position = count;
                    viewPager.setCurrentItem(position, false);
                } else if (position == FAKE_BANNER_SIZE - 1) {
                    position = count - 1;
                    viewPager.setCurrentItem(position, false);
                }
            }
        }
    }
}
