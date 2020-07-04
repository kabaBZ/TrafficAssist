package com.zjy.trafficassist;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.zjy.trafficassist.ui.MapActivity;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * com.zjy.trafficassist
 * Created by 73958 on 2017/3/14.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))){
            RongIM.init(this);
        }
        // 每隔20s上传用户信息
//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                handler.postDelayed(this, 20000);
//            }
//        };
    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
