package com.open_open.livedemos.views;

/******************************************
 * 类名称：RegisterActivity
 * 类描述：
 *
 * @version: 1.0
 * @author: chj
 * @time: 2018/1/23
 * @email: chj294671171@126.com
 * @github: https://github.com/cngmsy
 ******************************************/

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.open_open.livedemos.App;
import com.open_open.livedemos.R;
import com.open_open.livedemos.base.BaseActivity;
import com.open_open.livedemos.presenters.LoginHelper;
import com.open_open.livedemos.presenters.viewinterface.LoginView;

import static com.open_open.livedemos.R.id.repassword;
import static com.open_open.livedemos.R.id.username;

/**
 * 注册账号类
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener ,LoginView {

    private ImageButton mBack;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mRepassword;
    private Button mBtnRegister;
    private App mApp;
    private LoginHelper mLoginHeloper;
    private String usernameString;
    private String passwordString;
    private String repasswordString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_independent_register);
        initView();

        mApp = (App) getApplication();
        mLoginHeloper = new LoginHelper(this, this);
    }

    private void initView() {
        mBack = (ImageButton) findViewById(R.id.back);
        mUsername = (EditText) findViewById(username);
        mPassword = (EditText) findViewById(R.id.password);
        mRepassword = (EditText) findViewById(repassword);
        mBtnRegister = (Button) findViewById(R.id.btn_register);

        mBack.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_register:
                submit();
                //注册一个账号
                mLoginHeloper.standardRegister(usernameString, passwordString);
                break;
        }
    }

    private void submit() {
        // validate
        usernameString = mUsername.getText().toString().trim();
        if (TextUtils.isEmpty(usernameString)) {
            Toast.makeText(this, "usernameString不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        passwordString = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(passwordString)) {
            Toast.makeText(this, "passwordString不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        repasswordString = mRepassword.getText().toString().trim();
        if (TextUtils.isEmpty(repasswordString)) {
            Toast.makeText(this, "确认密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }

    @Override
    public void loginSucc() {
        jumpIntoHomeActivity();
    }

    @Override
    public void loginFail(String module, int errCode, String errMsg) {

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
