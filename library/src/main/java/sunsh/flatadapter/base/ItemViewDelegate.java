package sunsh.flatadapter.base;

/**
 * Created by sunsh on 18/5/30.
 */
public interface ItemViewDelegate<T>
{

    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(ViewHolder holder, T t, int position);

}
