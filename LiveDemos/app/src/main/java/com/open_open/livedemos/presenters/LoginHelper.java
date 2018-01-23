package com.open_open.livedemos.presenters;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.open_open.livedemos.base.Presenter;
import com.open_open.livedemos.entity.MySelfInfo;
import com.open_open.livedemos.presenters.viewinterface.LoginView;
import com.open_open.livedemos.presenters.viewinterface.LogoutView;
import com.orhanobut.logger.Logger;
import com.tencent.TIMManager;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

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


    /**
     * 独立模式 登录
     */
    public void standardLogin(String id, String password) {
        loginTask = new StandardLoginTask();
        loginTask.execute(id, password);

    }


    /**
     * 1111独立模式 注册    在p层的注册逻辑,注册完成直接走了登录
     */
    public void standardRegister(final String id, final String psw) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final UserServerHelper.RequestBackInfo result = UserServerHelper.getInstance().registerId(id, psw);
                Logger.d("注册返回的信息"+result.getErrorCode()+result.getErrorInfo());
                if (null != mContext) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        public void run() {

                            if (result != null && result.getErrorCode() == 0) {
                                standardLogin(id, psw);
                            } else if (result != null) {
                                //
                                Toast.makeText(mContext, "  " + result.getErrorCode() + " : " + result.getErrorInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }


    //登录模式登录
    private StandardLoginTask loginTask;

    class StandardLoginTask extends AsyncTask<String, Integer, UserServerHelper.RequestBackInfo> {

        @Override
        protected UserServerHelper.RequestBackInfo doInBackground(String... strings) {

            //注册完成后通过id进行登录
            return UserServerHelper.getInstance().loginId(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(UserServerHelper.RequestBackInfo result) {

            if (result != null) {
                if (result.getErrorCode() == 0) {

                    MySelfInfo.getInstance().writeToCache(mContext);

                    Logger.d("注册222"+MySelfInfo.getInstance().toString());
                    //登录
                    iLiveLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
                } else {
                    mLoginView.loginFail("Module_TLSSDK", result.getErrorCode(), result.getErrorInfo());
                }
            }

        }


        /**
         * 把id和sig签名输入,返回登录的结果
         * @param id
         * @param sig
         */
        public void iLiveLogin(String id, String sig) {
            //登录
            ILiveLoginManager.getInstance().iLiveLogin(id, sig, new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    Log.d("ILVB_LINK", "iLiveLogin->env: "+ TIMManager.getInstance().getEnv());

                  Logger.d("登录获取的结果有==="+MySelfInfo.getInstance().toString());

                    if (mLoginView != null)
                        mLoginView.loginSucc();
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    if (mLoginView != null)
                        mLoginView.loginFail(module, errCode, errMsg);
                }
            });
        }
    }






    @Override
    public void onDestory() {

    }
}
