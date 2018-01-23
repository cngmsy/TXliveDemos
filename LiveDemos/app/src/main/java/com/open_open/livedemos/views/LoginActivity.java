package com.open_open.livedemos.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.open_open.livedemos.R;
import com.open_open.livedemos.base.BaseActivity;
import com.open_open.livedemos.presenters.LoginHelper;
import com.open_open.livedemos.presenters.viewinterface.LoginView;

import static com.open_open.livedemos.R.id.username;


public class LoginActivity extends BaseActivity implements LoginView, View.OnClickListener {
    private LoginHelper mLoginHeloper;
    private EditText mUsername;
    private EditText mPassword;
    private Button mBtnLogin;
    private TextView mRegisterNewUser;
    private String usernameString;
    private String passwordString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginHeloper = new LoginHelper(this, this);
        setContentView(R.layout.activity_main);
        initView();


    }

    /**
     * 登录成功
     */
    @Override
    public void loginSucc() {
        jumpIntoHomeActivity();
    }

    /**
     * 登录失败
     *
     * @param module
     * @param errCode
     * @param errMsg
     */
    @Override
    public void loginFail(String module, int errCode, String errMsg) {

    }

    private void initView() {
        mUsername = (EditText) findViewById(username);
        mPassword = (EditText) findViewById(R.id.password);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mRegisterNewUser = (TextView) findViewById(R.id.registerNewUser);

        mBtnLogin.setOnClickListener(this);
        mRegisterNewUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerNewUser:
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_login:
                submit();
                //这里给个实际的值

               // mLoginHeloper.standardLogin(usernameString, passwordString);
                mLoginHeloper.standardLogin("123456p", "123456a");
                finish();
                break;
        }
    }


    /**
     *   登录成功跳转进入直接跳转主界面
     */
    private void jumpIntoHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void submit() {
        // validate
        usernameString = mUsername.getText().toString().trim();
        if (TextUtils.isEmpty(usernameString)) {
            Toast.makeText(this, "用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        passwordString = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(passwordString)) {
            Toast.makeText(this, "密码", Toast.LENGTH_SHORT).show();
            return;
        }




    }





}
