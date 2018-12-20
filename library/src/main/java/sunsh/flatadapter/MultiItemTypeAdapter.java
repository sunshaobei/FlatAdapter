package sunsh.flatadapter;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import sunsh.flatadapter.base.ItemViewDelegate;
import sunsh.flatadapter.base.ItemViewDelegateManager;
import sunsh.flatadapter.base.ViewHolder;


/**
 * Created by sunsh on 18/5/30.
 */
public class MultiItemTypeAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;
    private int autoIncrementing;
    private int decrementing;

    private SparseArrayCompat<View> mHeaderViews;
    private SparseArrayCompat<View> mFootViews;


    protected Context mContext;
    protected List<T> mDatas;

    protected ItemViewDelegateManager mItemViewDelegateManager;
    protected OnItemClickListener mOnItemClickListener;
    protected OnItemLongClickListener mOnItemLongClickListener;
    private View emptyView;
    private LoadingView loadingView;
    private OnLoadingListener onLoadingListener;
    private boolean loadingComplete = true;
    private boolean enableLoading = false;

    public MultiItemTypeAdapter(Context context, List<T> datas) {
        mContext = context;
        mDatas = datas;
        mItemViewDelegateManager = new ItemViewDelegateManager();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        }
        if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - getDataItemCount());
        }

        if (!useItemViewDelegateManager()) return super.getItemViewType(position);
        if (position < mDatas.size())
            return mItemViewDelegateManager.getItemViewType(mDatas.get(position), position);
        else
            return mItemViewDelegateManager.getItemViewType(null, position);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (getHeadersCount() > 0 || getFootersCount() > 0) {
            Object o = null;
            if (getHeadersCount() > 0)
                o = mHeaderViews.get(viewType);
            if (o == null && getFootersCount() > 0)
                o = mFootViews.get(viewType);
            if (o != null) {
                ViewHolder holder = ViewHolder.createViewHolder(parent.getContext(), (View) o);
                return holder;
            }
        }
        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        ViewHolder holder = ViewHolder.createViewHolder(mContext, parent, layoutId);
        onViewHolderCreated(holder, holder.getConvertView());
        setListener(parent, holder, viewType);
        return holder;
    }

    public void onViewHolderCreated(ViewHolder holder, View itemView) {

    }


    public void convert(ViewHolder holder, T t) {
        mItemViewDelegateManager.convert(holder, t, holder.getAdapterPosition());
    }


    protected boolean isEnabled(int viewType) {
        return true;
    }


    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                int position = viewHolder.getAdapterPosition();
                if (position < mDatas.size())
                    mOnItemClickListener.onItemClick(v, viewHolder, position,position-getHeadersCount());
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(v -> {
            if (mOnItemLongClickListener != null) {
                int position = viewHolder.getAdapterPosition();
                if (position < mDatas.size())
                    return mOnItemLongClickListener.onItemLongClick(v, viewHolder, position,position-getHeadersCount());
            }
            return false;
        });
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            if (isFooterViewPos(position)) {
                int key = mFootViews.keyAt(position - getHeadersCount() - getDataItemCount());
                Object o = mFootViews.get(key);
                if (o != null && o.equals(getLoadingView().getView()) && loadingComplete && getLoadingView().getLoadingType().equals(LoadingType.LOADING)) {
                    getLoadingView().getView().postDelayed(() -> {
                        loadingComplete = false;
                        onLoadingListener.onLoading(getHeadersCount() + getDataItemCount());
                    }, 20);
                }
            }
            return;
        }
        if (position - getHeadersCount() < mDatas.size()) {
            convert(holder, mDatas.get(position - getHeadersCount()));
        } else {
            convert(holder, null);
        }
    }


    @Override
    public int getItemCount() {
        int itemCount = mDatas.size();
        if (itemCount == 0) {
            if (emptyView != null) {
//                if (!enableLoading || getLoadingView().getLoadingType().equals(LoadingType.ERROR)) {
//                    addFootView(emptyView);
//                } else {
//                    removeFootView(emptyView);
//                }
                addFootView(emptyView);
            }
        } else {
            if (emptyView != null) removeFootView(emptyView);
            if (enableLoading && !getLoadingView().getLoadingType().equals(LoadingType.ERROR)) {
                addLoadingView();
            } else {
                removeLoadingView();
            }
        }


        return itemCount + getHeadersCount() + getFootersCount();
    }


    public List<T> getDatas() {
        return mDatas;
    }


    public void setLoadingComplete(boolean noMore) {
        this.loadingComplete = true;
        if (noMore) {
            getLoadingView().setLoadingType(LoadingType.NO_MORE);
        }
    }

    public void setLoadingComplete() {
        setLoadingComplete(false);
    }


    public void addHeaderView(View view) {
        if (mHeaderViews == null) mHeaderViews = new SparseArrayCompat<>();
        if (mHeaderViews.containsValue(view)) return;
        autoIncrementing++;
        mHeaderViews.put(autoIncrementing + BASE_ITEM_TYPE_HEADER, view);
    }


    public void removeHeaderView(View v) {
        if (mHeaderViews == null) return;
        int i = mHeaderViews.indexOfValue(v);
        if (i >= 0) {
            mHeaderViews.removeAt(i);
        }
    }

    public void addLoadingView() {
        addFootView(getLoadingView().getView());
    }

    public void removeLoadingView() {
        if (loadingView != null)
            removeFootView(loadingView.getView());
    }

    public void addFootView(View view) {
        if (mFootViews == null) mFootViews = new SparseArrayCompat<>();
        if (mFootViews.containsValue(view)) return;
        decrementing--;
        mFootViews.put(decrementing + BASE_ITEM_TYPE_FOOTER, view);
    }

    public void removeFootView(View view) {
        if (mFootViews == null) return;
        int i = mFootViews.indexOfValue(view);
        if (i >= 0)
            mFootViews.removeAt(i);
    }

    public LoadingView getLoadingView() {
        if (loadingView == null) loadingView = new LoadingView(mContext);
        return loadingView;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isHeaderViewPos(position) || isFooterViewPos(position)) {
                        return ((GridLayoutManager) layoutManager).getSpanCount();
                    }
                    if (spanSizeLookup != null)
                        return spanSizeLookup.getSpanSize(position);
                    return 1;
                }
            });
            gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
        }
    }


    private boolean isHeaderViewPos(int position) {
        return mHeaderViews != null && position < getHeadersCount();
    }

    private int getDataItemCount() {
        return mDatas.size();
    }


    private boolean isFooterViewPos(int position) {
        return mFootViews != null && position >= getHeadersCount() + getDataItemCount();
    }

    public int getHeadersCount() {
        return mHeaderViews == null ? 0 : mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFootViews == null ? 0 : mFootViews.size();
    }


    public MultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    public MultiItemTypeAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ViewHolder holder, int position,int dataPosition);
    }
    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position,int dataPosition);
    }


    public interface OnLoadingListener {
        void onLoading(int lastPosition);
    }

    public void setOnLoadingListener(OnLoadingListener o) {
        this.onLoadingListener = o;
        setEnableLoading(true);
        getLoadingView().setLoadingType(LoadingType.LOADING);
    }

    public void setEnableLoading(boolean b) {
        enableLoading = b;
//        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

//    public void setError() {
//        getLoadingView().setLoadingType(LoadingType.ERROR);
//        notifyDataSetChanged();
//    }
//
//    public void resetLoading() {
//        getLoadingView().setLoadingType(LoadingType.LOADING);
//        notifyDataSetChanged();
//    }

    public void setEmptyView(String text, int resId, int width, int height) {
        emptyView = LayoutInflater.from(mContext).inflate(R.layout.rv_empty, null);
        TextView tv_empty = emptyView.findViewById(R.id.tv_empty);
        ImageView iv_empty = emptyView.findViewById(R.id.iv_empty);
        if (!TextUtils.isEmpty(text))
            tv_empty.setText(text);
        if (resId > 0)
            iv_empty.setImageResource(resId);
        ViewGroup.LayoutParams layoutParams = emptyView.getLayoutParams();
        if (layoutParams == null)
            layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (width > 0) layoutParams.width = width;
        if (height > 0) layoutParams.height = height;
        emptyView.setLayoutParams(layoutParams);
    }

    public void setEmptyView(String text, int resid) {
        setEmptyView(text, resid, 0, 0);
    }
}
