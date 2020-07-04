package com.zjy.trafficassist.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.zjy.trafficassist.UserStatus;
import com.zjy.trafficassist.helper.ConnectIMServerHelper;
import com.zjy.trafficassist.helper.LoginHelper;
import com.zjy.trafficassist.listener.LoginStatusChangedListener;
import com.zjy.trafficassist.model.User;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.zjy.trafficassist.UserStatus.SP;
import static com.zjy.trafficassist.utils.HttpUtil.SUCCESS;

/**
 * com.zjy.trafficassist.utils
 * Created by 73958 on 2017/4/6.
 */

public class LoginCheck {

    private User user;
    private Context context;
    private ProgressDialog mPDialog;
    private LoginStatusChangedListener loginStatusChangedListener;

    public LoginCheck(Context context, LoginStatusChangedListener listener){
        this.context = context;
        this.loginStatusChangedListener = listener;
        mPDialog = null;
    }

    public LoginCheck(Context context, LoginStatusChangedListener listener , User user){
        this(context, listener);
        this.user = user;
        mPDialog = null;
    }

    public LoginCheck(Context context, User user, ProgressDialog mPDialog){
        this.context = context;
        this.user = user;
        this.mPDialog = mPDialog;
    }

    public void setOnLoginStatusChanged(LoginStatusChangedListener listener){
        this.loginStatusChangedListener = listener;
    }

    public void login(){
        HttpUtil.create().login(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(mPDialog != null)
                    mPDialog.dismiss();
                try {
                    String res = response.body().string();
                    if(HttpUtil.stateCode(res) == SUCCESS){
                        UserStatus.LOGIN_STATUS = true;
                        UserStatus.USER = user;
                        // 保存登录信息
                        LoginHelper.getInstance().saveUserInfo(context);
                        // 同步用户个人信息
                        TransForm.syncUserInfo(res, context);

                        // IM服务器登录
                        SharedPreferences preferences = context.getSharedPreferences("RongKitConfig", MODE_PRIVATE);
                        String token = preferences.getString("token", "");
                        ConnectIMServerHelper.getInstance().connectIMServer(context, token);
                        // 设置登录状态监听
                        loginStatusChangedListener.onLoginStatusChanged(UserStatus.LOGIN_STATUS);

                        // 如果是LoginActivity登录的话，则关闭activity
                        String contextString = context.toString();
                        String AtyName = contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
                        if(Objects.equals(AtyName, "LoginActivity")){
                            ((Activity)context).finish();
                        }
                    }else{
                        Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void logout(){
        UserStatus.LOGIN_STATUS = false;
        UserStatus.USER = null;
        context.getSharedPreferences("USER_INFO", MODE_PRIVATE).edit().clear().apply();
        context.getSharedPreferences("RongKitConfig", MODE_PRIVATE).edit().clear().apply();
        loginStatusChangedListener.onLoginStatusChanged(UserStatus.LOGIN_STATUS);
        LogUtil.i("unLogin success");
    }
}
