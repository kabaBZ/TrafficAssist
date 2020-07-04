package com.zjy.trafficassist;

import android.content.SharedPreferences;

import com.zjy.trafficassist.model.User;

/**
 * Created 2016/5/1.
 * @author 郑家烨.
 * @function
 */
public class UserStatus {

    public static boolean LOGIN_STATUS = false;

    public static User USER = new User();

    public static boolean first_show = true;

    public static SharedPreferences SP;

    public static SharedPreferences.Editor EDITOR;
}
