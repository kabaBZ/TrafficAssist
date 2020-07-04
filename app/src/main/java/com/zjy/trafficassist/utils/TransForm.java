package com.zjy.trafficassist.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.zjy.trafficassist.UserStatus;
import com.zjy.trafficassist.model.AlarmHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import static com.zjy.trafficassist.UserStatus.USER;

/**
 * Created 2016/5/5.
 *
 * @author 郑家烨.
 */
public class TransForm {

    // 解析从服务器传回的JSON数据
    public static ArrayList<AlarmHistory> parseHistory(String json)
            throws UnsupportedEncodingException {
        if (json == null)
            return null;
        ArrayList<AlarmHistory> alarmHistories = new ArrayList<>();;
        ArrayList<String> picUrl = null;
        JSONObject historyInfo;
        JSONObject info;
        try {
            historyInfo = new JSONObject(json);
            info = historyInfo.getJSONObject("info");
            JSONArray histories = info.getJSONArray("allDetails");
            for (int i = 0; i < histories.length(); i++) {
                JSONObject each_item = (JSONObject) histories.get(i);
                String str_acctag = (String) each_item.get("detail");
                JSONArray filenames = each_item.getJSONArray("fileNames");
                picUrl = new ArrayList<>();
                for (int j = 0; j < filenames.length(); j++) {
                    picUrl.add("http://120.27.130.203:8001/trafficassist/AccidentImage/" + filenames.get(j));
                }
                alarmHistories.add(new AlarmHistory(
                        str_acctag,
                        USER.getRealname(),
                        USER.getUsername(),
                        picUrl));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return alarmHistories;
    }

    public static void syncUserInfo(String res, Context context){
        if (res != null){
            try {
                JSONObject json = new JSONObject(res);
                JSONObject info = json.getJSONObject("info");
                String real_name = info.getString("realname");
                String sex = info.getString("sex");
                String telephone = info.getString("telephone");
                String driver_licence_number = info.getString("driver_licence_number");
                String car_type = info.getString("car_type");
                String car_number = info.getString("car_number");

                USER.setRealname(real_name);
                USER.setDriverNumber(driver_licence_number);
                USER.setDriverType(car_type);
                USER.setCarNumber(car_number);

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                sp.edit().putString("user_real_name", real_name).apply();
                sp.edit().putString("user_phone_number", USER.getUsername()).apply();
                sp.edit().putString("user_driver_number", driver_licence_number).apply();
                sp.edit().putString("user_driver_type", car_type).apply();
                sp.edit().putString("user_car_number", car_number).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 用当前系统日期
     */
    public static String getDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }

    /**
     * 用当前系统时间
     */
    public static String getTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
        return dateFormat.format(date);
    }

    public static String uuid(){
        return UUID.randomUUID().toString();
    }

    /**
     * 图片大小压缩算法
     *
     * @param filePath 输入图片文件的路径
     * @return Bitmap
     */
    public static Bitmap compressImage(String filePath) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        option.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, option);
        option.inJustDecodeBounds = false;
//        int w = option.outWidth;
//        int h = option.outHeight;
//        float hh = 1280f;//这里设置高度为1920f
//        float ww = 720f;//这里设置宽度为1080f
//        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//        int be = 1;//be=1表示不缩放
//        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
//            be = (int) (option.outWidth / ww);
//        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
//            be = (int) (option.outHeight / hh);
//        }
        int bWidth = option.outWidth;
        int bHeight = option.outHeight;
        int toWidth = 640;
        int toHeight = 360;
        int be = 1;  //be = 1代表不缩放
        if (bWidth / toWidth > bHeight / toHeight && bWidth > toWidth) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) bWidth / toWidth;
        } else if (bWidth / toWidth < bHeight / toHeight && bHeight > toHeight) {//如果高度高的话根据宽度固定大小缩放
            be = (int) bHeight / toHeight;
        }
        option.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, option);
//        return comp(bitmap);//压缩好比例大小后再进行质量压缩
        return comp(BitmapFactory.decodeFile(filePath, option));
    }

    /**
     * 图片质量压缩算法
     *
     * @param image 输入是经过大小压缩的Bitmap
     * @return Bitmap
     */
    private static Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        System.out.println("压缩前" + baos.toByteArray().length);
        //循环判断如果压缩后图片是否大于100kb,大于继续压缩
        if (baos.toByteArray().length / 1024 > 3000) {
            while (baos.toByteArray().length / 1024 > 800) {
                options -= 10;//每次都减少10
                baos.reset();//重置baos即清空baos
                //这里压缩options%，把压缩后的数据存放到baos中
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
        } else {
            while (baos.toByteArray().length / 1024 > 200) {
                options -= 10;//每次都减少10
                baos.reset();//重置baos即清空baos
                //这里压缩options%，把压缩后的数据存放到baos中
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
        }
        System.out.println("压缩后" + baos.toByteArray().length);
        //把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }
}
