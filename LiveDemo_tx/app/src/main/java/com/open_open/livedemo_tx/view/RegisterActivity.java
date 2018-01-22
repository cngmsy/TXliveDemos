package com.open_open.livedemo_tx.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.open_open.livedemo_tx.App;
import com.open_open.livedemo_tx.R;
import com.open_open.livedemo_tx.base.BaseActivity;
import com.open_open.livedemo_tx.presenter.LoginHelper;
import com.open_open.livedemo_tx.presenter.viewinterface.LoginView;

import java.util.regex.Pattern;


/**
 * 注册账号类
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, LoginView {

    private EditText mUserName, mPassword, mRepassword;
    private TextView mBtnRegister;
    private ImageButton mBtnBack;
    App mMyApplication;
    LoginHelper mLoginHeloper;
    private static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_independent_register);
        mUserName = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mRepassword = (EditText) findViewById(R.id.repassword);
        mBtnRegister = (TextView) findViewById(R.id.btn_register);
        mBtnBack = (ImageButton) findViewById(R.id.back);
        mBtnBack.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mMyApplication = (App) getApplication();
        mLoginHeloper = new LoginHelper(this, this);
    }

    @Override
    protected void onDestroy() {
        mLoginHeloper.onDestory();
        super.onDestroy();
    }

    //3333注册按钮
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_register) {
            String userId = mUserName.getText().toString();
            String userPW = mPassword.getText().toString();
            String userPW2 = mRepassword.getText().toString();


            if (userId.length() < 4 || userId.length() > 24 || Pattern.compile("^[0-9]*$").matcher(userId).matches()
                    || !Pattern.compile("^[a-zA-Z0-9_]*$").matcher(userId).matches()) {
                Log.i(TAG, "onClick " + userId.length());
                Toast.makeText(RegisterActivity.this, R.string.str_hint_account, Toast.LENGTH_SHORT).show();
                return;
            }


            if (userId.length() == 0 || userPW.length() == 0 || userPW2.length() == 0) {
                Toast.makeText(RegisterActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!userPW.equals(userPW2)) {
                Toast.makeText(RegisterActivity.this, "两次密码输入密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userPW.length() < 8 || userPW.length() > 16) {
                Toast.makeText(RegisterActivity.this, R.string.str_hint_pwd, Toast.LENGTH_SHORT).show();
                return;
            }

            //注册一个账号
            mLoginHeloper.standardRegister(userId, mPassword.getText().toString());
        }
        //返回键逻辑
        if (view.getId() == R.id.back) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override//4444  跳转到主页面
    public void loginSucc() {
        jumpIntoHomeActivity();
    }

    @Override
    public void loginFail(String module, int errCode, String errMsg) {
        Toast.makeText(this, "code "+errCode+"     "+errMsg , Toast.LENGTH_SHORT).show();

    }

    /**
     *   登录成功跳转进入直接跳转主界面
     */
    private void jumpIntoHomeActivity() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
