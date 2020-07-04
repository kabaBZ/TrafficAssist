package com.zjy.trafficassist.ui;

import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zjy.trafficassist.fragment.DiscoverFragment;
import com.zjy.trafficassist.R;
import com.zjy.trafficassist.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class NearbyService extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_nearby_service);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //给页面设置工具栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.nbs_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // 设置Tablayout和Viewpager
        setupLayout();
        // 设置搜索栏的提交按钮
        SearchView searchView = (SearchView) findViewById(R.id.nbs_search);
        searchView.setSubmitButtonEnabled(true);

    }

    public void setupLayout(){
        tabLayout = (TabLayout) findViewById(R.id.nearby_service_tab);
        viewPager = (ViewPager) findViewById(R.id.nearby_service_vp);

        //添加页卡标题
        List<String> mTitleList = new ArrayList<>();
        mTitleList.add("附近");
        mTitleList.add("维修");
        mTitleList.add("问路");
        mTitleList.add("接送");
        mTitleList.add("接送");
        mTitleList.add("接送");
        mTitleList.add("接送");

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.addTab(tabLayout.newTab().setText(mTitleList.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(mTitleList.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(mTitleList.get(2)));
        tabLayout.addTab(tabLayout.newTab().setText(mTitleList.get(3)));
        tabLayout.addTab(tabLayout.newTab().setText(mTitleList.get(4)));
        tabLayout.addTab(tabLayout.newTab().setText(mTitleList.get(5)));
        tabLayout.addTab(tabLayout.newTab().setText(mTitleList.get(6)));

        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this, mTitleList);
        mAdapter.addFragment(new DiscoverFragment());
        mAdapter.addFragment(new DiscoverFragment());
        mAdapter.addFragment(new DiscoverFragment());
        mAdapter.addFragment(new DiscoverFragment());
        mAdapter.addFragment(new DiscoverFragment());
        mAdapter.addFragment(new DiscoverFragment());
        mAdapter.addFragment(new DiscoverFragment());

        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
