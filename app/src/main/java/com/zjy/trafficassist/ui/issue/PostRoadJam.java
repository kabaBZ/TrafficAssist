package com.zjy.trafficassist.ui.issue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.zjy.trafficassist.R;
import com.zjy.trafficassist.utils.AMapUtil;
import com.zjy.trafficassist.utils.HttpUtil;
import com.zjy.trafficassist.utils.LogUtil;
import com.zjy.trafficassist.utils.TransForm;
import com.zjy.trafficassist.widget.tagFlow.FlowLayout;
import com.zjy.trafficassist.widget.tagFlow.TagAdapter;
import com.zjy.trafficassist.widget.tagFlow.TagFlowLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import me.iwf.photopicker.widget.MultiPickResultView;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.zjy.trafficassist.UserStatus.USER;
import static com.zjy.trafficassist.utils.HttpUtil.SUCCESS;

public class PostRoadJam extends AppCompatActivity implements GeocodeSearch.OnGeocodeSearchListener {

    private MultiPickResultView multiPickResultView;
    private TextView address;
    private EditText detail;
    private ViewGroup container;
    private TagFlowLayout tagFlow1, tagFlow2;
    private TagAdapter tagAdapter1, tagAdapter2;

    // 逆地理编码
    private GeocodeSearch geocodeSearch;
    private LatLonPoint latLonPoint;

    private String addressName;
    private String[] str1 = {"对向车道", "同向车道"};
    private String[] str2 = {"轻微拥堵", "严重拥堵", "几乎不动"};

    private ArrayList<File> imgFiles;
    private String jam_direction;
    private String jam_detail_tag;
    private String jam_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_road_jam);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        container = (ViewGroup) findViewById(R.id.jam_container);

        multiPickResultView = (MultiPickResultView) findViewById(R.id.jam_image_picker);
        multiPickResultView.setMaxCount(4);
        multiPickResultView.init(this, MultiPickResultView.ACTION_SELECT, null);

        address = (TextView) findViewById(R.id.jam_address);
        detail = (EditText) findViewById(R.id.road_jam_detail);

        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        if (USER.getLocation() != null) {
            latLonPoint = AMapUtil.convertToLatLonPoint(USER.getLocation());
            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
            geocodeSearch.getFromLocationAsyn(query);
        }
        setupTagFlowLayout();
    }

    private void setupTagFlowLayout() {
        tagFlow1 = (TagFlowLayout) findViewById(R.id.jam_tagFlow1);
        tagAdapter1 = new TagAdapter<String>(str1) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater
                        .from(PostRoadJam.this)
                        .inflate(R.layout.item_tag, tagFlow1, false);
                tv.setText(s);
                return tv;
            }
        };
        tagFlow1.setAdapter(tagAdapter1);
        tagFlow1.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, boolean isChecked, FlowLayout parent) {
                if (isChecked) {
                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.white));
                } else {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.light_black));
                }
                return false;
            }
        });
        tagFlow1.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet, View view) {
                jam_direction = getTags(selectPosSet, tagAdapter1);
            }
        });

        tagFlow2 = (TagFlowLayout) findViewById(R.id.jam_tagFlow2);
        tagAdapter2 = new TagAdapter<String>(str2) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater
                        .from(PostRoadJam.this)
                        .inflate(R.layout.item_tag, tagFlow2, false);
                tv.setText(s);
                return tv;
            }
        };
        tagFlow2.setAdapter(tagAdapter2);
        tagFlow2.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, boolean isChecked, FlowLayout parent) {
                if (isChecked) {
                    ((TextView) view).setTextColor(getResources().getColor(android.R.color.white));
                } else {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.light_black));
                }
                return false;
            }
        });
        tagFlow2.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet, View view) {
                jam_detail_tag = getTags(selectPosSet, tagAdapter2);
            }
        });
    }

    private String getTags(Set<Integer> selectPosSet, TagAdapter adapter) {
        String tags = "";
        Object[] id = selectPosSet.toArray();
        if (id.length > 0) {
            tags = (String) adapter.getItem(Integer.valueOf(id[0].toString()));
        }
        for (int i = 1; i < id.length; i++) {
            tags += "/" + adapter.getItem(Integer.valueOf(id[i].toString()));
        }
        LogUtil.d(tags);
        return tags;
    }

    public void onCommit(View view) {
        if (jam_direction != "" && jam_detail_tag != "") {
            if (detail.getText().toString() != "") {
                jam_detail =  detail.getText().toString();
            }
            final ArrayList<String> paths = multiPickResultView.getPhotos();

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
            HttpUtil.addTextPart(parts, "ISSUE_TYPE", "road_jam", parts.size());
            HttpUtil.addTextPart(parts, "DIRECTION", jam_direction, parts.size());
            HttpUtil.addTextPart(parts, "DETAIL_TAG", jam_detail_tag, parts.size());
            HttpUtil.addTextPart(parts, "DETAIL", jam_detail, parts.size());
            HttpUtil.addTextPart(parts, "PIC_PATH", NamesString, parts.size());
            HttpUtil.addTextPart(parts, "ADDRESS", addressName, parts.size());
            HttpUtil.addTextPart(parts, "JD", latLonPoint.getLongitude() + "", parts.size());
            HttpUtil.addTextPart(parts, "WD", latLonPoint.getLatitude() + "", parts.size());
            HttpUtil.create().uploadRoadIssue(parts).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String res = response.body().string();
                        LogUtil.e(res);
                        JSONObject json = new JSONObject(res);
                        if(HttpUtil.stateCode(res) == SUCCESS){
                            Toast.makeText(PostRoadJam.this, "上报成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(PostRoadJam.this, "上报失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        LogUtil.e("Exception:" + e.toString());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(PostRoadJam.this, "连接失败", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
        }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        multiPickResultView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (i == 1000) {
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                    && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = regeocodeResult.getRegeocodeAddress().getFormatAddress() + "附近";
                address.setText(addressName);
                LogUtil.i(addressName);
            } else {

            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

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