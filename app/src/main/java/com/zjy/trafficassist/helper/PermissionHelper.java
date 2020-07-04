package com.zjy.trafficassist.helper;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.zjy.trafficassist.utils.LogUtil;

/**
 * com.zjy.trafficassist.helper
 * Created by 73958 on 2017/4/6.
 */

public abstract class PermissionHelper {

    public static final int REQUEST_LOCATION = 0;

    public static final int REQUEST_READ_STORAGE = 1;

    private static String permissionStr = "";

    public static void requestPermission(Context context, final Activity activity, final int requestCode) {
        if (ContextCompat.checkSelfPermission(context, getPermissionString(requestCode))
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    getPermissionString(requestCode))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("请求权限")
                        .setMessage("应用需要" + permissionStr + "权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{getPermissionString(requestCode)},
                                        PermissionHelper.REQUEST_LOCATION);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                LogUtil.d("rationally");
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{getPermissionString(requestCode)},
                        requestCode);
                LogUtil.d("directly");
            }
        } else {
            LogUtil.d("granted");
        }
    }

    public static String getPermissionString(int requestCode) {
        String permission = "";
        switch (requestCode) {
            case REQUEST_LOCATION:
                permission = Manifest.permission.ACCESS_FINE_LOCATION;
                permissionStr = "定位";
                break;
            case REQUEST_READ_STORAGE:
                permission = Manifest.permission.READ_EXTERNAL_STORAGE;
                permissionStr = "读取内部存储";
                break;
        }
        return permission;
    }

    /**
     * 校验权限请求结果
     *
     * @param grantResults
     * @return boolean
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
