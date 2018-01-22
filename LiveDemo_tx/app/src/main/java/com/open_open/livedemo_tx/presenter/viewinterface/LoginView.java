package com.open_open.livedemo_tx.presenter.viewinterface;


/**
 * 登录回调
 */
public interface LoginView extends MvpView{

    void loginSucc();

    void loginFail(String module, int errCode, String errMsg);
}
