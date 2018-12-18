package www.sunsh.com.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sunsh.flatadapter.CommonAdapter;
import sunsh.flatadapter.MultiItemTypeAdapter;
import sunsh.flatadapter.base.ViewHolder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView viewById = findViewById(R.id.rv);
        viewById.setVerticalScrollBarEnabled(true);
        viewById.setLayoutManager(new LinearLayoutManager(this));
        List<String> list = new ArrayList<>();
        MultiItemTypeAdapter multiItemTypeAdapter = new CommonAdapter<String>(this, R.layout.item, list) {
            @Override
            protected void convert(ViewHolder holder, String o, int position) {
                TextView view = holder.getView(R.id.tv);
                view.setText("s" + position);
            }
        };
        Button button = new Button(this);
        button.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        multiItemTypeAdapter.addHeaderView(button);
        multiItemTypeAdapter.addHeaderView(new Button(this));
        multiItemTypeAdapter.addHeaderView(new Button(this));
        multiItemTypeAdapter.setEmptyView("wgsdgs", R.mipmap.ic_launcher, 0, 0);
        viewById.setAdapter(multiItemTypeAdapter);
        multiItemTypeAdapter.setOnLoadingListener(loadView -> {
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            time++;
            multiItemTypeAdapter.notifyItemInserted(loadView);
            multiItemTypeAdapter.setLoadingComplete(time > 10);
        });
    }

    int time = 1;


}
