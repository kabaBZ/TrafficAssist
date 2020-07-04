package com.zjy.trafficassist.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ZJY on 2016/4/12.
 */
@Deprecated
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydata1.db";//数据库名称
    private static final int VERSION = 1;//数据库版本

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String user_info = "create table if not exists user_info"
                + "(_id integer primary key autoincrement not null, " +
                    "username varchar(11) not null, password varchar(20) not null)";
        String user_history = "create table if not exists user_info"
                + "(_id integer primary key autoincrement not null, " +
                "Nickname varchar(11) not null, Detail varchar(255) not null," +
                " isSerious varchar(5), time varchar(20))";
        db.execSQL(user_history);
        db.execSQL(user_info);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
