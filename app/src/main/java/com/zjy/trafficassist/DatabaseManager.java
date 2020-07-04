package com.zjy.trafficassist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zjy.trafficassist.helper.DatabaseHelper;
import com.zjy.trafficassist.model.User;

/**
 * Created by ZJY on 2016/4/14.
 */
@Deprecated
public class DatabaseManager {

    private DatabaseHelper helper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        helper = new DatabaseHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        database = helper.getWritableDatabase();

    }

    public void Register(User user) {
        database.beginTransaction();  //开始事务
        try {
            database.execSQL("INSERT INTO user_info VALUES(null, ?, ?)", new Object[]{user.getUsername(), user.getPassword()});
            database.setTransactionSuccessful();  //设置事务成功完成
            System.out.println("写入成功");
        } finally {
            database.endTransaction();    //结束事务
        }
        database.close();
    }

    public boolean Login(User user) {
        String sql="select * from user_info where username=? and password=?";
        Cursor cursor=database.rawQuery(sql, new String[]{user.getUsername(),user.getPassword()});
        if(cursor.moveToFirst()){
            cursor.close();
            database.close();
            return true;
        }
        database.close();
        return false;
    }

//    public void SaveHistory(ArrayList<AlarmHistory> alarmHistories) {
//        database.beginTransaction();  //开始事务
//        try {
//            for(int i = 0; i < alarmHistories.size(); i++) {
//                database.execSQL("INSERT INTO user_history VALUES(null, ?, ?, ?, null)",
//                        new Object[]{alarmHistories.get(i).getRealname(),
//                                alarmHistories.get(i).getaccidentTags(),
//                                String.valueOf(alarmHistories.get(i).isSerious())});
//            }
//            database.setTransactionSuccessful();  //设置事务成功完成
//            System.out.println("写入成功");
//        } finally {
//            database.endTransaction();    //结束事务
//        }
//        database.close();
//    }

//    public ArrayList<AlarmHistory> LoadHistory() {
//        ArrayList<AlarmHistory> alarmHistories = new ArrayList<>();
//        String sql="select Nickname, Detail, isSerious from user_history";
//        Cursor cursor=database.rawQuery(sql, null);
//        while (cursor.moveToNext()){
//            String nickname = cursor.getString(cursor.getColumnIndex("Nickname"));
//            String detail = cursor.getString(cursor.getColumnIndex("Detail"));
//            Boolean isSerious = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("isSerious")));
//            alarmHistories.add(new AlarmHistory(isSerious, nickname, detail));
//        }
//        cursor.close();
//        database.close();
//        return alarmHistories;
//    }

    public long getUserCount(){
        //获取数据总数
        Cursor cursor =database.rawQuery("select count(*) from user_history", null);
        cursor.moveToFirst();
        return cursor.getLong(0);
    }
}
