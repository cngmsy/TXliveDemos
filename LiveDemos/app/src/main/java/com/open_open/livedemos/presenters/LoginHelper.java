package com.open_open.livedemos.presenters;

import android.content.Context;

import com.open_open.livedemos.base.Presenter;
import com.open_open.livedemos.presenters.viewinterface.LoginView;
import com.open_open.livedemos.presenters.viewinterface.LogoutView;

/******************************************
 * 类名称：LoginHelper
 * 类描述：
 *
 * @version: 1.0
 * @author: chj
 * @time: 2018/1/23
 * @email: chj294671171@126.com
 * @github: https://github.com/cngmsy
 ******************************************/
public class LoginHelper extends Presenter {

    private Context mContext;
    private static final String TAG = LoginHelper.class.getSimpleName();
    private LoginView mLoginView;
    private LogoutView mLogoutView;

    public LoginHelper(Context context, LoginView loginView) {
        mContext = context;
        mLoginView = loginView;
    }





    @Override
    public void onDestory() {

    }
}
