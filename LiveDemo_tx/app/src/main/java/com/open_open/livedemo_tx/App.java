package com.open_open.livedemo_tx;

import android.app.Application;

import com.open_open.livedemo_tx.model.MessageObservable;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveConfig;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.qalsdk.sdk.MsfSdkUtils;

/******************************************
 * 类名称：App
 * 类描述：
 *
 * @version: 1.0
 * @author: chj
 * @time: 2018/1/22
 * @email: chj294671171@126.com
 * @github: https://github.com/cngmsy
 ******************************************/
public class App extends Application {

    public static final int SDK_APPID = 1400027849;
    public static final int ACCOUNT_TYPE = 11656;

    @Override
    public void onCreate() {
        super.onCreate();
        if (MsfSdkUtils.isMainProcess(this)) {
            //ILiveSDK初始化
            ILiveSDK.getInstance().initSdk(this, SDK_APPID, ACCOUNT_TYPE);



            //初始化直播场景
            /*
            ILVLiveConfig liveConfig = new ILVLiveConfig();
            ILVLiveManager.getInstance().init(liveConfig);
             */
            ILVLiveManager.getInstance().init(new ILVLiveConfig()
                    .setLiveMsgListener(MessageObservable.getInstance()));

        }
    }


}
