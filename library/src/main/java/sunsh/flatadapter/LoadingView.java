package sunsh.flatadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoadingView {
    private View view;

    public LoadingView(Context mContext) {
        this.view = LayoutInflater.from(mContext).inflate(R.layout.rv_loading, null);
        this.view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setLoadingType(LoadingType loadingType) {
        if (loadingType.equals(LoadingType.NO_MORE)){
            view.findViewById(R.id.loading_nomore).setVisibility(View.VISIBLE);
            view.findViewById(R.id.loading).setVisibility(View.GONE);
        }else if (loadingType.equals(LoadingType.LOADING)){
            view.findViewById(R.id.loading_nomore).setVisibility(View.GONE);
            view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
        }else if (loadingType.equals(LoadingType.HIDE)){

        }
    }
}
