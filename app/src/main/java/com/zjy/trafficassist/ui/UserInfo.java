package com.zjy.trafficassist.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zjy.trafficassist.R;

public class UserInfo extends AppCompatActivity {

    private TextView realName;
    private TextView phoneNumber;
    private TextView driverNumber;
    private TextView driverType;
    private TextView carNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_user_info);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //给页面设置工具栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //设置工具栏标题
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle("用户信息");
            collapsingToolbar.setExpandedTitleColor(Color.WHITE);//设置收缩前Toolbar上字体的颜色
            collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);//设置收缩后Toolbar上字体的颜色
        }
        initUserInfoView();

    }

    public void initUserInfoView(){
        realName = (TextView) findViewById(R.id.user_real_name);
        phoneNumber = (TextView) findViewById(R.id.user_phone_number);
        driverNumber = (TextView) findViewById(R.id.user_driver_number);
        driverType = (TextView) findViewById(R.id.user_driver_type);
        carNumber = (TextView) findViewById(R.id.user_car_number);
    }

    public String getPreference(String key){
        return PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .getString(key, "");
    }

    public void onEditUserInfo(View view){
        startActivity(new Intent(UserInfo.this, EditUserInfo.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        realName.setText(getPreference("user_real_name"));
        phoneNumber.setText(getPreference("user_phone_number"));
        driverNumber.setText(getPreference("user_driver_number"));
        driverType.setText(getPreference("user_driver_type"));
        carNumber.setText(getPreference("user_car_number"));
    }
}
