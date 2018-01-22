package com.open_open.livedemo_tx.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.open_open.livedemo_tx.R;
import com.open_open.livedemo_tx.model.MySelfInfo;
import com.open_open.livedemo_tx.presenter.LoginHelper;
import com.open_open.livedemo_tx.presenter.viewinterface.LoginView;
import com.open_open.livedemo_tx.utils.SxbLog;

import java.util.regex.Pattern;

public class LoginActivity extends Activity implements View.OnClickListener, LoginView {


    TextView mBtnLogin, mBtnRegister;
    EditText mPassWord, mUserName;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginHelper mLoginHeloper;
    private final int REQUEST_PHONE_PERMISSIONS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mLoginHeloper = new LoginHelper(this, this);

        //获取个人数据本地缓存
        MySelfInfo.getInstance().getCache(getApplicationContext());

        if (needLogin() == true) {//本地没有账户需要登录
            initView();
        } else {
            //有账户登录直接IM登录
            SxbLog.i(TAG, "LoginActivity onCreate");
            mLoginHeloper.iLiveLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
        }
    }

    @Override
    protected void onDestroy() {
        mLoginHeloper.onDestory();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    private void initView() {
        if (null == mBtnLogin) {
            setContentView(R.layout.activity_main);
            mBtnLogin = (TextView) findViewById(R.id.btn_login);
            mUserName = (EditText) findViewById(R.id.username);
            mPassWord = (EditText) findViewById(R.id.password);
            mBtnRegister = (TextView) findViewById(R.id.registerNewUser);
            mBtnRegister.setOnClickListener(this);
            mBtnLogin.setOnClickListener(this);
        }else{  // 登录失败清空密码
            mPassWord.setText("");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.registerNewUser) {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        if (view.getId() == R.id.btn_login) {//登录账号系统TLS
            String strUser = mUserName.getText().toString();
            String strPwd = mPassWord.getText().toString();
            if (TextUtils.isEmpty(strUser)) {
                Toast.makeText(LoginActivity.this, "name can not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(strPwd)) {
                Toast.makeText(LoginActivity.this, "password can not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (strUser.length() < 4 || strUser.length() > 24 || Pattern.compile("^[0-9]*$").matcher(strUser).matches()
                    || !Pattern.compile("^[a-zA-Z0-9_]*$").matcher(strUser).matches()) {
                Toast.makeText(LoginActivity.this, R.string.str_hint_account, Toast.LENGTH_SHORT).show();
                return;
            }
            if (strPwd.length() < 8 || strPwd.length() > 16) {
                Toast.makeText(LoginActivity.this, R.string.str_hint_pwd, Toast.LENGTH_SHORT).show();
                return;
            }
            mLoginHeloper.standardLogin(mUserName.getText().toString(), mPassWord.getText().toString());
        }
    }

    @Override
    public void loginSucc() {
        Toast.makeText(LoginActivity.this, "" + MySelfInfo.getInstance().getId() + " login ", Toast.LENGTH_SHORT).show();
        jumpIntoHomeActivity();
    }


    /**
     * 直接跳转主界面
     */
    private void jumpIntoHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginFail(String module, int errCode, String errMsg) {
        Toast.makeText(LoginActivity.this, "login fail" + MySelfInfo.getInstance().getId() + " : "+errMsg, Toast.LENGTH_SHORT).show();
        initView();
    }



    /**
     * 判断是否需要登录
     *
     * @return true 代表需要重新登录
     */
    public boolean needLogin() {
        if (MySelfInfo.getInstance().getId() != null) {
            return false;//有账号不需要登录
        } else {
            return true;//需要登录
        }

    }
}
