package com.example.loadmoredemo;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.loadmoredemo.listener.OnItemClickListener;
import com.example.loadmoredemo.listener.OnLoadMoreListener;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Administrator on 2016/8/10.
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final int LOAD_MORE_ITEM = -100;
    protected static final int HEAD_ITEM = -200;
    protected RecyclerView mRecyclerView;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading = true;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    //正在加载中item position
    private int loadMorePos = -1;

    //是否全部加载完成
    private boolean isLoadAll = false;

    private FootViewHolder footViewHolder;
    private OnItemClickListener onItemClickListener;
    private View headerView;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public BaseAdapter(RecyclerView recyclerView, final OnLoadMoreListener onloadMoreListener) {
        this.mRecyclerView = recyclerView;
        this.onLoadMoreListener = onloadMoreListener;
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = layoutManager.getItemCount();
                //lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                lastVisibleItem = getLastVisiblePosition(recyclerView.getLayoutManager());
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)
                        && dy > 0 && !isLoadAll) {
                    loading = true;
                    loadMorePos = getItemCount() - 1;
                    notifyItemInserted(getItemCount() - 1);
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }

    public final void reset() {
        loading = false;
        if (loadMorePos != -1) {
            notifyItemRemoved(loadMorePos);
        } else {
            notifyDataSetChanged();
        }
    }

    /**
     * 判读数据是否加载完毕，显示不同的布局
     *
     * @param loadAll
     */
    public final void setLoadAll(boolean loadAll) {
        if (footViewHolder == null) {
            View view = LayoutInflater.from(mRecyclerView.getContext()).inflate(R.layout.footer_view_load_more, null);
            footViewHolder = new FootViewHolder(view);
        }
        isLoadAll = loadAll;
        if (isLoadAll && footViewHolder != null) {
            footViewHolder.mLLLoadNow.setVisibility(View.INVISIBLE);
            footViewHolder.mTxtLoadMore.setVisibility(View.VISIBLE);
        } else if (footViewHolder != null) {
            footViewHolder.mLLLoadNow.setVisibility(View.VISIBLE);
            footViewHolder.mTxtLoadMore.setVisibility(View.INVISIBLE);
        }
        if (!isLoadAll) {
            loadMorePos = -1;
        }
    }

    /**
     * 添加Header
     *
     * @param view
     */
    public void addHeaderView(View view) {
        if (view == null) {
            throw new NullPointerException("HeadView is null");
        }
        if (headerView != null) {
            return;
        }
        headerView = view;
        headerView.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        notifyDataSetChanged();
    }

    /**
     * 如果position大于List的大小，说明正在加载
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position >= getDataSize()) {
            return LOAD_MORE_ITEM;
        }
        return super.getItemViewType(position);
    }

    //抽象出一个方法用来返回和adapter绑定的List的size的大小
    abstract int getDataSize();

    @Override
    public int getItemCount() {
        if (loading) {
            //如果正在加载更多 返回List.size()+1
            return getDataSize() + 1;
        }
        return getDataSize();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (footViewHolder == null) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view_load_more, parent, false);
            footViewHolder = new FootViewHolder(view);
        }
        return footViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //在这里处理RecyclerView的item的点击事件

        if (!(holder instanceof FootViewHolder) && onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onItemClickListener.onItemClick(view, position);
                }
            });
        }
    }

    /**
     * 网格布局Header、Footer、LoadMore 占一整行
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    RecyclerView.Adapter adapter = recyclerView.getAdapter();
                    if (isFullSpanType(adapter.getItemViewType(position))) {
                        Log.e("getSpanSize", "gridLayoutManager.getSpanCount()==" + gridLayoutManager.getSpanCount());
                        return gridLayoutManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    /**
     * 瀑布流布局Header、Footer、LoadMore 占一整行
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        int viewType = getItemViewType(position);
        if (isFullSpanType(viewType)) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                lp.setFullSpan(true);
            }
        }
    }

    /**
     * 获取最后一条展示索引
     *
     * @param layoutManager
     * @return
     */
    private int getLastVisiblePosition(RecyclerView.LayoutManager layoutManager) {
        int position;
        if (layoutManager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager mlayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = mlayoutManager.findLastVisibleItemPositions(new int[mlayoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = layoutManager.getItemCount() - 1;
        }
        return position;
    }


    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int maxPosition = Integer.MIN_VALUE;
        for (int position : positions) {
            maxPosition = Math.max(maxPosition, position);
        }
        return maxPosition;
    }


    /**
     * 布局类型是Header、Footer、LoadMore
     *
     * @param type
     * @return
     */
    private boolean isFullSpanType(int type) {
        return type == LOAD_MORE_ITEM;
    }

    /**
     * 底部加载的布局
     */
    protected class FootViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLLLoadNow;
        private TextView mTxtLoadMore;

        public FootViewHolder(View itemView) {
            super(itemView);
            mLLLoadNow = (LinearLayout) itemView.findViewById(R.id.footer_view_load_now);
            mTxtLoadMore = (TextView) itemView.findViewById(R.id.footer_view_load_all);
        }
    }
}
