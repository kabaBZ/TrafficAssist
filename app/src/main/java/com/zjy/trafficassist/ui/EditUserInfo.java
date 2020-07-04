package com.zjy.trafficassist.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.zjy.trafficassist.R;
import com.zjy.trafficassist.UserStatus;
import com.zjy.trafficassist.base.AppCompatPreferenceActivity;
import com.zjy.trafficassist.utils.HttpUtil;
import com.zjy.trafficassist.utils.LogUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserInfo extends AppCompatPreferenceActivity {

    private static final String REAL_NAME = "1";

    private static final String PHONE_NUM = "2";

    private static final String DRIVER_LICENSE_NUM = "3";

    private static final String DRIVER_LICENSE_TYPE = "4";

    private static final String CAR_NUM = "5";

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {

            if (preference instanceof EditTextPreference) {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                String infoType = "";
                switch (preference.getKey()) {
                    case "user_real_name":
                        infoType = REAL_NAME;
                        break;
                    case "user_phone_number":
                        infoType = PHONE_NUM;
                        break;
                    case "user_driver_number":
                        infoType = DRIVER_LICENSE_NUM;
                        break;
                    case "user_driver_type":
                        infoType = DRIVER_LICENSE_TYPE;
                        break;
                    case "user_car_number":
                        infoType = CAR_NUM;
                        break;
                }
                final String stringValue = o.toString();
                preference.setSummary(stringValue);

                Map<String, String> userInfo = new HashMap<>();
                userInfo.put("username", UserStatus.USER.getUsername());
                userInfo.put("infoType", infoType);
                userInfo.put("info", stringValue);
                HttpUtil.create().modifyUserInfo(userInfo).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String res = response.body().string();
                            if (HttpUtil.stateCode(res) == HttpUtil.SUCCESS) {
//                                Toast.makeText(EditUserInfo.this., "修改成功", Toast.LENGTH_SHORT).show();
                                LogUtil.i(stringValue + " 修改成功");
                            } else {
//                                Toast.makeText(EditUserInfo.this, "修改失败", Toast.LENGTH_SHORT).show();
                                LogUtil.e("修改失败");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        Toast.makeText(EditUserInfo.this, "连接失败", Toast.LENGTH_SHORT).show();
                        LogUtil.e("连接失败");
                    }
                });
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        UserInfoPreference preUserInfo = new UserInfoPreference();
        fragmentTransaction.add(R.id.modify_user_info_fragment, preUserInfo);
        fragmentTransaction.commit();
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class UserInfoPreference extends PreferenceFragment{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_edit_user_info);
            initPreferences();
        }

        public void initPreferences() {
            bindPreferenceSummaryToValue(findPreference("user_real_name"));
            bindPreferenceSummaryToValue(findPreference("user_phone_number"));
            bindPreferenceSummaryToValue(findPreference("user_driver_number"));
            bindPreferenceSummaryToValue(findPreference("user_driver_type"));
            bindPreferenceSummaryToValue(findPreference("user_car_number"));
        }
    }
}
