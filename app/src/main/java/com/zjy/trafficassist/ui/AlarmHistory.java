package com.zjy.trafficassist.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zjy.trafficassist.base.SlidingActivity;
import com.zjy.trafficassist.adapter.HistoryListAdapter;
import com.zjy.trafficassist.listener.RecyclerItemClickListener;
import com.zjy.trafficassist.utils.HttpUtil;
import com.zjy.trafficassist.utils.TransForm;
import com.zjy.trafficassist.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zjy.trafficassist.UserStatus.USER;
import static com.zjy.trafficassist.utils.HttpUtil.EMPTY;
import static com.zjy.trafficassist.utils.HttpUtil.SUCCESS;

public class AlarmHistory extends AppCompatActivity implements RecyclerItemClickListener {


    private static boolean FROM_INTERNET;

    private RecyclerView historyList;
    private ArrayList<com.zjy.trafficassist.model.AlarmHistory> local_history;
    private ArrayList<com.zjy.trafficassist.model.AlarmHistory> internet_history;
    private HistoryListAdapter historyListAdapter;
    private SwipeRefreshLayout RefreshLayout;
//    private DatabaseManager databaseManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        databaseManager = new DatabaseManager(this);
        RefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        if (RefreshLayout != null) {
            RefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                    android.R.color.holo_red_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);
        }
        historyList = (RecyclerView) findViewById(R.id.historyList);
        historyList.setLayoutManager(new LinearLayoutManager(this));    //设置LinearLayoutManager
        historyList.setItemAnimator(new DefaultItemAnimator());         //设置ItemAnimator
        historyList.setHasFixedSize(true);                              //设置固定大小
//        historyList.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL,
//                16, getResources().getColor(R.color.WHITE)));

        FROM_INTERNET = true;
        getListItem();

        RefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshLayout.setRefreshing(true);
                FROM_INTERNET = true;
                getListItem();
                RefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getListItem() {

        final ArrayList<com.zjy.trafficassist.model.AlarmHistory> history_item = new ArrayList<>();

        if (FROM_INTERNET) {
            HttpUtil.create().history(USER.getUsername()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String res = response.body().string();
                        List<com.zjy.trafficassist.model.AlarmHistory> list = TransForm.parseHistory(res);
                        if(HttpUtil.stateCode(res) == SUCCESS){
                            for (int i = 0; i < list.size(); i++) {
                                history_item.add(list.get(i));
                            }
                            historyListAdapter = new HistoryListAdapter(AlarmHistory.this, history_item);
                            historyListAdapter.setOnRecyclerItemClickListener(AlarmHistory.this);
                            historyList.setAdapter(historyListAdapter);
                            historyListAdapter.notifyDataSetChanged();
                        }else if(HttpUtil.stateCode(res) == EMPTY){
                            Toast.makeText(AlarmHistory.this, "没有历史记录", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(AlarmHistory.this, "连接失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "点击了 " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
