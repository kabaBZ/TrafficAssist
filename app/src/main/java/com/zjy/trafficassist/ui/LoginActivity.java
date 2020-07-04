package com.zjy.trafficassist.ui;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zjy.trafficassist.R;
import com.zjy.trafficassist.base.SlidingActivity;
import com.zjy.trafficassist.listener.LoginStatusChangedListener;
import com.zjy.trafficassist.model.User;
import com.zjy.trafficassist.utils.LoginCheck;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private User user;
//    private DatabaseManager DBManager;

    // UI references.
    private EditText mUsernameView;
    private ProgressDialog mPDialog;
    private EditText mPasswordView;
    private TextView mSignup;
    private TextView mForgetpassword;
    private Button mUserSignInButton;

    private static LoginStatusChangedListener loginStatusChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        mSignup = (TextView) findViewById(R.id.user_sign_up);
        mSignup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LoginActivity.this, SignupActivity.class), 0);
            }
        });

        mForgetpassword = (TextView) findViewById(R.id.forget_password);

        mUserSignInButton = (Button) findViewById(R.id.user_sign_in);
        mUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        /**
         * 初始化User对象
         */
        user = new User(mUsernameView.getText().toString(), mPasswordView.getText().toString());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the USER entered one.
        if (!TextUtils.isEmpty(user.getPassword()) && !isPasswordValid(user.getPassword())) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(user.getUsername())) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isEmailValid(user.getUsername())) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the USER login attempt.
            mPDialog = new ProgressDialog(LoginActivity.this);
            mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mPDialog.setMessage(getResources().getString(R.string.now_user_login));
            mPDialog.setCancelable(false);
            mPDialog.show();

            LoginCheck loginCheck = new LoginCheck(this, user, mPDialog);
            loginCheck.setOnLoginStatusChanged(loginStatusChangedListener);
            loginCheck.login();
        }
    }

    public static void setOnLoginStatusChanged(LoginStatusChangedListener listener) {
        loginStatusChangedListener = listener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            if (requestCode == 0) {
                if (data != null) {
                    mUsernameView.setText(data.getStringExtra("username"));
                    mPasswordView.setText(data.getStringExtra("password"));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isEmailValid(String username) {
        return username.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
