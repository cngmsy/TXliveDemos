package com.open_open.livedemos.base;

/******************************************
 * 类名称：Presenter
 * 类描述：
 *
 * @version: 1.0
 * @author: chj
 * @time: 2018/1/23
 * @email: chj294671171@126.com
 * @github: https://github.com/cngmsy
 ******************************************/
/**
 * 页面展示逻辑基类
 */
public abstract class Presenter {

    //销去持有外部的mContext;
    public abstract void onDestory();
}
