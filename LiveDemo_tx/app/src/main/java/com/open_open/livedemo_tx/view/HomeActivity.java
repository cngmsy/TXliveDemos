package com.open_open.livedemo_tx.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.open_open.livedemo_tx.R;
import com.open_open.livedemo_tx.base.BaseFragmentActivity;
import com.open_open.livedemo_tx.model.MySelfInfo;
import com.open_open.livedemo_tx.presenter.LoginHelper;
import com.open_open.livedemo_tx.presenter.ProfileInfoHelper;
import com.open_open.livedemo_tx.presenter.viewinterface.ProfileView;
import com.open_open.livedemo_tx.utils.SxbLog;
import com.open_open.livedemo_tx.view.customviews.FragmentList;
import com.open_open.livedemo_tx.view.customviews.FragmentProfile;
import com.open_open.livedemo_tx.view.customviews.FragmentPublish;
import com.tencent.TIMUserProfile;
import com.tencent.ilivesdk.ILiveSDK;

import java.util.List;



/******************************************
 * 类名称：HomeActivity
 * 类描述：
 *
 * @version: 1.0
 * @author: chj
 * @time: 2018/1/22
 * @email: chj294671171@126.com
 * @github: https://github.com/cngmsy
 ******************************************/
public class HomeActivity extends BaseFragmentActivity implements ProfileView {
    private FragmentTabHost mTabHost;
    private LayoutInflater layoutInflater;
    private ProfileInfoHelper infoHelper;
    private LoginHelper mLoginHelper;
    private final Class fragmentArray[] = {FragmentList.class, FragmentPublish.class, FragmentProfile.class};
    private int mImageViewArray[] = {R.drawable.tab_live, R.drawable.icon_publish, R.drawable.tab_profile};
    private String mTextviewArray[] = {"live", "publish", "profile"};
    private static final String TAG = HomeActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        SxbLog.i(TAG, "HomeActivity onStart");
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        boolean living = pref.getBoolean("living", false);


        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        layoutInflater = LayoutInflater.from(this);
        //关联tabhost和Fragment
        mTabHost.setup(this, getSupportFragmentManager(), R.id.contentPanel);

        int fragmentCount = fragmentArray.length;


        for (int i = 0; i < fragmentCount; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.getTabWidget().setDividerDrawable(null);

        }
        mTabHost.getTabWidget().getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                DialogFragment newFragment = InputDialog.newInstance();
//                newFragment.show(ft, "dialog");

                //点击直播的icon  进入视频发布界面
              startActivity(new Intent(HomeActivity.this, PublishLiveActivity.class));

            }
        });

        // 检测是否需要获取头像
        if (TextUtils.isEmpty(MySelfInfo.getInstance().getAvatar())) {
            infoHelper = new ProfileInfoHelper(this);
            infoHelper.getMyProfile();
        }

    }

    @Override//在生命周期中设置判断是否登录
    protected void onStart() {
        SxbLog.i(TAG, "HomeActivity onStart");
        super.onStart();
        //初始化直播sdk
        if (ILiveSDK.getInstance().getAVContext() == null) {//retry
           // InitBusinessHelper.initApp(getApplicationContext());
            SxbLog.i(TAG, "HomeActivity retry login");
            mLoginHelper = new LoginHelper(this);
            mLoginHelper.iLiveLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
        }
    }

    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_content, null);
        ImageView icon = (ImageView) view.findViewById(R.id.tab_icon);
        icon.setImageResource(mImageViewArray[index]);
        return view;
    }

    @Override
    protected void onDestroy() {
        if (mLoginHelper != null)
            mLoginHelper.onDestory();
        SxbLog.i(TAG, "HomeActivity onDestroy");
        super.onDestroy();
    }

    @Override //更新用户资料
    public void updateProfileInfo(TIMUserProfile profile) {
        SxbLog.i(TAG, "updateProfileInfo");
        if (null != profile) {
            MySelfInfo.getInstance().setAvatar(profile.getFaceUrl());
            MySelfInfo.getInstance().setSign(profile.getSelfSignature());
            if (!TextUtils.isEmpty(profile.getNickName())) {
                MySelfInfo.getInstance().setNickName(profile.getNickName());
            } else {
                MySelfInfo.getInstance().setNickName(profile.getIdentifier());
            }
        }
    }

    @Override
    public void updateUserInfo(int reqid, List<TIMUserProfile> profiles) {
    }

    @Override
    protected void onRequireLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        super.onRequireLogin();
    }
}
