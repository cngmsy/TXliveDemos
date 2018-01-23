package com.open_open.livedemos.presenters.viewinterface;


/**
 * 登出回调
 */
public interface LogoutView extends MvpView{

    void logoutSucc();

    void logoutFail();
}
