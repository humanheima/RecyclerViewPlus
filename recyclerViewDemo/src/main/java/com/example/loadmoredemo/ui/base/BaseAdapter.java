package com.example.loadmoredemo.ui.base;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.loadmoredemo.listener.OnItemClickListener;
import com.example.loadmoredemo.listener.OnItemLongClickListener;
import com.example.loadmoredemo.listener.OnLoadMoreListener;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Administrator on 2016/8/10.
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String tag = "BaseAdapter";
    private static final int LOAD_MORE_ITEM = -100;//加载更多的item类型
    private static final int HEAD_ITEM = -200;//头布局item类型
    private static final int CONTENT_ITEM = -300;//我们的content布局
    private static final int FOOT_ITEM = -400;//foot布局item类型
    protected RecyclerView mRecyclerView;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading = false;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    //是否全部加载完成
    private boolean isLoadAll = false;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private View headerView;
    private View loadingView;
    private View footView;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        initOnLoadMoreListener();
    }

    private void initOnLoadMoreListener() {
        if (onLoadMoreListener != null) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = getLastVisiblePosition(layoutManager);
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && dy > 0 && !isLoadAll) {
                        loading = true;
                        if (loadingView != null) {
                            Log.e(tag, "onScrolled add loadingView");
                            notifyItemInserted(getItemCount());
                        }
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                    }
                }
            });
        }
    }

    //抽象出一个方法用来返回和adapter绑定的List的size的大小
    protected abstract int getDataSize();

    protected abstract RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType);

    protected abstract void onBindView(RecyclerView.ViewHolder holder, int position);


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
     * 添加LoadMore
     *
     * @param view
     */
    public void addLoadingView(View view) {
        Log.e(tag, "addLoadingView");
        if (view == null) {
            throw new NullPointerException("LoadingView is null!");
        }
        if (loadingView != null) {
            return;
        }
        loadingView = view;
        loadingView.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
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
        if (headerView != null && position == 0) {
            return HEAD_ITEM;
        }
        int lastPos = headerView == null ? getDataSize() : getDataSize() + 1;
        if (loadingView != null && position >= lastPos && loading) {
            return LOAD_MORE_ITEM;
        }
        if (footView != null && position >= lastPos) {
            return FOOT_ITEM;
        }
        return CONTENT_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEAD_ITEM) {
            return new VH(headerView);
        } else if (viewType == LOAD_MORE_ITEM) {
            return new VH(loadingView);
        } else if (viewType == FOOT_ITEM) {
            return new VH(footView);
        } else {
            return onCreateView(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        boolean isVH = holder instanceof VH;
        if (headerView != null) {
            position = position - 1;
        }
        if (!isVH) {
            onBindView(holder, position);
        }
        if (!isVH && onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    if (headerView != null) {
                        pos = pos - 1;
                    }
                    onItemClickListener.onItemClick(view, pos);
                }
            });
        }
        if (!isVH && onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if (headerView != null) {
                        pos = pos - 1;
                    }
                    onItemLongClickListener.onItemLongClick(v, pos);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = getDataSize();
        if (count == 0) {
            return 0;
        }
        if (headerView != null) {
            count++;
        }
        if (loading && loadingView != null) {
            count++;
        }
        if (footView != null) {
            count++;
        }

        return count;
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

    public final void reset() {
        if (mRecyclerView != null) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (loading) {
                        removeLoadingView();
                    } else {
                        notifyDataSetChanged();
                    }
                    loading = false;
                }
            });
        }

    }

    private void removeLoadingView() {
        loading = false;
        notifyDataSetChanged();
    }

    /**
     * 添加Footer
     *
     * @param view
     */
    public void addFooterView(View view) {
        if (view == null) {
            throw new NullPointerException("FooterView is null!");
        }
        if (footView != null) {
            return;
        }
        footView = view;
        footView.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        notifyItemInserted(getItemCount() - 1);
    }


    /**
     * 判读数据是否加载完毕，显示不同的布局
     *
     * @param loadAll
     */
    public final void setLoadAll(boolean loadAll) {
        if (loading) {
            reset();
        } else if (!loadAll) {
            footView = null;
        }
        isLoadAll = loadAll;
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
        return type == LOAD_MORE_ITEM || type == HEAD_ITEM || type == FOOT_ITEM;
    }

    /**
     * 头布局
     */
    protected class VH extends RecyclerView.ViewHolder {

        public VH(View itemView) {
            super(itemView);
        }
    }

}
