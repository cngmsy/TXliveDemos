package com.open_open.livedemos.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.open_open.livedemos.contents.Constants;
import com.open_open.livedemos.entity.MySelfInfo;
import com.open_open.livedemos.utils.ConnectionChangeReceiver;


/**
 * Created by admin on 2016/5/20.
 */
public class BaseActivity extends Activity{
    private String TAG = "BaseActivity";
    private BroadcastReceiver recv;
    private ConnectionChangeReceiver netWorkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        recv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BD_EXIT_APP)){
                  //  Logger.d("BaseActivity", LoggerConstants.ACTION_HOST_KICK + LoggerConstants.DIV + MySelfInfo.getInstance().getId() + LoggerConstants.DIV + "on force off line");
                    onRequireLoggerin();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BD_EXIT_APP);
        registerReceiver(recv, filter);

        //监听网络变化
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new ConnectionChangeReceiver();
        }
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netWorkStateReceiver, filter2);
    }



    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(recv);
            unregisterReceiver(netWorkStateReceiver);
        }catch (Exception e){
        }
        super.onDestroy();
    }

    private void processOffline(String message){
        if (isDestroyed() || isFinishing()) {
            return;
        }
        /*AlertDiaLogger alertDiaLogger = new AlertDiaLogger.Builder(this)
                .setTitle(R.string.str_tips_title)
                .setMessage(message)
                .setPositiveButton(R.string.btn_sure, new DiaLoggerInterface.OnClickListener() {
                    @Override
                    public void onClick(DiaLoggerInterface diaLoggerInterface, int i) {
                        diaLoggerInterface.cancel();
                    }
                })
                .create();
        alertDiaLogger.setOnCancelListener(new DiaLoggerInterface.OnCancelListener() {
            @Override
            public void onCancel(DiaLoggerInterface diaLoggerInterface) {
                requiredLoggerin();
            }
        });
        alertDiaLogger.show();*/
    }

    protected void requiredLoggerin(){
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putBoolean("living", false);
        editor.apply();
        MySelfInfo.getInstance().clearCache(getBaseContext());
        getBaseContext().sendBroadcast(new Intent(Constants.BD_EXIT_APP));
    }

    protected void onRequireLoggerin(){
        finish();
    }
}
