package com.open_open.livedemos.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.open_open.livedemos.R;
import com.open_open.livedemos.base.BaseActivity;
import com.open_open.livedemos.contents.Constants;
import com.open_open.livedemos.entity.CurLiveInfo;
import com.open_open.livedemos.entity.MySelfInfo;
import com.open_open.livedemos.views.customviews.CustomSwitch;
import com.open_open.livedemos.views.customviews.LineControllerView;
import com.orhanobut.logger.Logger;

import static com.open_open.livedemos.R.id.live_title;

/******************************************
 * 类名称：PublishLiveActivity
 * 类描述：
 *
 * @version: 1.0
 * @author: chj
 * @time: 2018/1/23
 * @email: chj294671171@126.com
 * @github: https://github.com/cngmsy
 ******************************************/
public class PublishLiveActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBtnCancel;
    private RelativeLayout mToppanel1;
    private ImageView mCover;
    private TextView mTvPicTip;
    private EditText mLiveTitle;
    private ImageView mImgLbs;
    private TextView mAddress;
    private CustomSwitch mBtnLbs;
    private LineControllerView mSpeedTest;
    private LineControllerView mLcvRole;
    private TextView mPushStream;
    private CustomSwitch mRecordBtn;
    private TextView mBtnPublish;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_publish);
        initView();
    }

    private void initView() {
        mBtnCancel = (TextView) findViewById(R.id.btn_cancel);
        mToppanel1 = (RelativeLayout) findViewById(R.id.toppanel1);
        mCover = (ImageView) findViewById(R.id.cover);
        mTvPicTip = (TextView) findViewById(R.id.tv_pic_tip);
        mLiveTitle = (EditText) findViewById(live_title);
        mImgLbs = (ImageView) findViewById(R.id.img_lbs);
        mAddress = (TextView) findViewById(R.id.address);
        mBtnLbs = (CustomSwitch) findViewById(R.id.btn_lbs);
        mSpeedTest = (LineControllerView) findViewById(R.id.speed_test);
        mLcvRole = (LineControllerView) findViewById(R.id.lcv_role);
        mPushStream = (TextView) findViewById(R.id.push_stream);
        mRecordBtn = (CustomSwitch) findViewById(R.id.record_btn);
        mBtnPublish = (TextView) findViewById(R.id.btn_publish);

        mBtnLbs.setOnClickListener(this);
        mRecordBtn.setOnClickListener(this);
        mBtnPublish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_lbs:

                break;
            case R.id.btn_publish:
                submit();
                Intent intent = new Intent(this, LiveActivity.class);
                MySelfInfo.getInstance().setIdStatus(Constants.HOST);
                MySelfInfo.getInstance().setJoinRoomWay(true);
                CurLiveInfo.setTitle(title);
                CurLiveInfo.setHostID(MySelfInfo.getInstance().getId());
                CurLiveInfo.setRoomNum(MySelfInfo.getInstance().getMyRoomNum());
                startActivity(intent);
                Logger.d("直播标题=="+title+"直播id"+MySelfInfo.getInstance().getId()+"直播房间号"+MySelfInfo.getInstance().getMyRoomNum());
                this.finish();

                break;
        }
    }

    private void submit() {
        // validate

        title = mLiveTitle.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "title不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }
}
