package com.open_open.livedemos.views;

/******************************************
 * 类名称：LiveActivity
 * 类描述：
 *
 * @version: 1.0
 * @author: chj
 * @time: 2018/1/23
 * @email: chj294671171@126.com
 * @github: https://github.com/cngmsy
 ******************************************/

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.open_open.livedemos.R;
import com.open_open.livedemos.base.BaseActivity;

/**
 * Live直播类
 */
public class LiveActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   // 不锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_live);
    }
}
