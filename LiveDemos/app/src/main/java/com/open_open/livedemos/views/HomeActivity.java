package com.open_open.livedemos.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.open_open.livedemos.R;
import com.open_open.livedemos.base.BaseFragmentActivity;
import com.open_open.livedemos.views.fragments.FragmentList;
import com.open_open.livedemos.views.fragments.FragmentProfile;
import com.orhanobut.logger.Logger;

/******************************************
 * 类名称：HomeActivity
 * 类描述：
 *
 * @version: 1.0
 * @author: chj
 * @time: 2018/1/23
 * @email: chj294671171@126.com
 * @github: https://github.com/cngmsy
 ******************************************/
public class HomeActivity extends BaseFragmentActivity  {

    private FragmentTabHost mTabHost;
    private LayoutInflater layoutInflater;
    private final Class fragmentArray[] = {FragmentList.class, FragmentPublish.class, FragmentProfile.class};
    private int mImageViewArray[] = {R.drawable.tab_live, R.drawable.icon_publish, R.drawable.tab_profile};
    private String mTextviewArray[] = {"live", "publish", "profile"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        Logger.d("进入homeActivity");

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
                //点击直播的icon  进入视频发布界面
                startActivity(new Intent(HomeActivity.this, PublishLiveActivity.class));

            }
        });


    }

    //获取指示器的view
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_content, null);
        ImageView icon = (ImageView) view.findViewById(R.id.tab_icon);
        icon.setImageResource(mImageViewArray[index]);
        return view;
    }

}
