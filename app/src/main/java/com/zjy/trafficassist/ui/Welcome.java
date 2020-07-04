package com.zjy.trafficassist.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zjy.trafficassist.R;
import com.zjy.trafficassist.helper.PermissionHelper;

public class Welcome extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private Button reg;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private boolean fristload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        login = (Button) findViewById(R.id.wel_login);
        reg = (Button) findViewById(R.id.wel_reg);
        login.setOnClickListener(this);
        reg.setOnClickListener(this);

        // 请求定位权限
        PermissionHelper.requestPermission(getApplicationContext(), Welcome.this, PermissionHelper.REQUEST_LOCATION);

        sp = this.getSharedPreferences("first_check", MODE_PRIVATE);
        fristload = sp.getBoolean("fristload", true);
        if (!fristload) {
            startActivity(new Intent(Welcome.this, MapActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        editor = sp.edit();
        editor.putBoolean("fristload", false);
        editor.apply();
        switch (v.getId()){
            case R.id.wel_login:
                startActivity(new Intent(Welcome.this, MapActivity.class));
                startActivity(new Intent(Welcome.this, LoginActivity.class));
                break;
            case R.id.wel_reg:
                startActivity(new Intent(Welcome.this, MapActivity.class));
                startActivity(new Intent(Welcome.this, SignupActivity.class));
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
