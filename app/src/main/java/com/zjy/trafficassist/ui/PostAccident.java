package com.zjy.trafficassist.ui;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zjy.trafficassist.*;
import com.zjy.trafficassist.base.SlidingActivity;
import com.zjy.trafficassist.helper.PermissionHelper;
import com.zjy.trafficassist.model.AlarmHistory;
import com.zjy.trafficassist.utils.HttpUtil;
import com.zjy.trafficassist.utils.LogUtil;
import com.zjy.trafficassist.utils.TransForm;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.widget.MultiPickResultView;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zjy.trafficassist.UserStatus.USER;
import static com.zjy.trafficassist.helper.PermissionHelper.REQUEST_READ_STORAGE;
import static com.zjy.trafficassist.utils.HttpUtil.SUCCESS;

public class PostAccident extends AppCompatActivity {

    private Button btn_commit;
    private Uri imageUri;
    private Bitmap bitmap;
    private File imgFile;
    private ArrayList<File> imgFiles;

    //Snackbar的容器
    private CoordinatorLayout container;
    private RadioGroup tag_car_type;
    private RadioGroup tag_people_effect;
    private RadioGroup tag_car_crash;

    /**
     * 自定义组件
     */
    private MultiPickResultView recyclerView;

    private AlarmHistory mHistory;

    private String accidentTags = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 请求读取内部存储权限
        PermissionHelper.requestPermission(getApplicationContext(), PostAccident.this, REQUEST_READ_STORAGE);

        container = (CoordinatorLayout) findViewById(R.id.post_container);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        tag_car_type = (RadioGroup) findViewById(R.id.tag_car_type);
        tag_people_effect = (RadioGroup) findViewById(R.id.tag_people_effect);
        tag_car_crash = (RadioGroup) findViewById(R.id.tag_car_crash);

        recyclerView = (MultiPickResultView) findViewById(R.id.image_picker);
        recyclerView.setMaxCount(8);
        recyclerView.init(this, MultiPickResultView.ACTION_SELECT, null);

        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tag_car_type.getCheckedRadioButtonId() != -1
                        && tag_people_effect.getCheckedRadioButtonId() != -1
                        && tag_car_crash.getCheckedRadioButtonId() != -1){
                    final ProgressDialog mPDialog = new ProgressDialog(PostAccident.this);
                    mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mPDialog.setMessage(getResources().getString(R.string.now_upload_history));
                    mPDialog.setCancelable(true);
                    mPDialog.show();
                    RadioButton btn1 = (RadioButton) findViewById(tag_car_type.getCheckedRadioButtonId());
                    RadioButton btn2 = (RadioButton) findViewById(tag_people_effect.getCheckedRadioButtonId());
                    RadioButton btn3 = (RadioButton) findViewById(tag_car_crash.getCheckedRadioButtonId());
                    accidentTags = btn1.getText().toString() + "/" + btn2.getText().toString() + "/" + btn3.getText().toString();
                    final ArrayList<String> paths = recyclerView.getPhotos();

                    imgFiles = getImageFiles(paths);
                    String NamesString = "";
                    List<String> newPaths = new ArrayList<>();
                    if(imgFiles.size() > 0){
                        NamesString = imgFiles.get(0).getName();
                        for(int i = 1; i < imgFiles.size(); i++) {
                            NamesString += "/" + imgFiles.get(i).getName();
                        }
                        for(File imgFile : imgFiles){
                            newPaths.add(imgFile.getAbsolutePath());
                        }
                    }
                    List<MultipartBody.Part> parts = HttpUtil.files2Parts("image[]", newPaths);
                    HttpUtil.addTextPart(parts, "accidentTags", accidentTags, parts.size());
                    HttpUtil.addTextPart(parts, "realname", USER.getRealname(), parts.size());
                    HttpUtil.addTextPart(parts, "username", USER.getUsername(), parts.size());
                    HttpUtil.addTextPart(parts, "longitude", USER.getLocation().longitude+"", parts.size());
                    HttpUtil.addTextPart(parts, "latitude", USER.getLocation().latitude+"", parts.size());
                    HttpUtil.addTextPart(parts, "filenames", NamesString, parts.size());

                    HttpUtil.create().uploadHistory(parts).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            mPDialog.dismiss();
                            try {
                                String res = response.body().string();
                                LogUtil.e(res);
                                JSONObject json = new JSONObject(res);
                                if(HttpUtil.stateCode(res) == SUCCESS){
                                    Snackbar.make(container, "报警成功", Snackbar.LENGTH_LONG).show();
                                }else {
                                    Snackbar.make(container, "报警失败", Snackbar.LENGTH_LONG).show();
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                LogUtil.e("Exception:" + e.toString());
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            mPDialog.dismiss();
                            Snackbar.make(container, "连接失败:" + t.toString(), Snackbar.LENGTH_LONG).show();
                            LogUtil.e(t.toString());
                        }
                    });
                }else{
                    Toast.makeText(PostAccident.this, "请完整填写信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recyclerView.onActivityResult(requestCode,resultCode,data);

    }

    /**
     * 将要上传的文件保存到本地临时文件夹
     * @param paths 图片文件的原始路径
     * @return imgFiles 转存后的图片文件对象
     */
    public ArrayList<File> getImageFiles(ArrayList<String> paths) {
        String dir = "/storage/emulated/0/TrafficAssist/uploadTmp/";
        File Dir = new File(dir);
        if(!Dir.exists())
            Dir.mkdirs();
        ArrayList<File> imgFiles = new ArrayList<>();
        ArrayList<Bitmap> compedBitmaps = new ArrayList<>();

        for(int i = 0; i < paths.size(); i++) {
            imgFiles.add(new File(dir + TransForm.uuid() + ".jpg"));
            compedBitmaps.add(TransForm.compressImage(paths.get(i)));
        }
        for(int i = 0; i < paths.size(); i++) {
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgFiles.get(i)));
                compedBitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgFiles;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
