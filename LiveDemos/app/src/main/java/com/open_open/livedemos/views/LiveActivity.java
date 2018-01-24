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

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.open_open.livedemos.R;
import com.open_open.livedemos.base.BaseActivity;
import com.open_open.livedemos.contents.Constants;
import com.open_open.livedemos.entity.CurLiveInfo;
import com.open_open.livedemos.entity.MySelfInfo;
import com.open_open.livedemos.presenters.LiveHelper;
import com.open_open.livedemos.presenters.UserServerHelper;
import com.open_open.livedemos.presenters.viewinterface.GetLinkSigView;
import com.open_open.livedemos.presenters.viewinterface.LiveListView;
import com.open_open.livedemos.presenters.viewinterface.LiveView;
import com.open_open.livedemos.presenters.viewinterface.ProfileView;
import com.orhanobut.logger.Logger;
import com.tencent.TIMUserProfile;
import com.tencent.av.TIMAvManager;
import com.tencent.av.opengl.ui.GLView;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLog;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILivePushOption;
import com.tencent.ilivesdk.core.ILiveRecordOption;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.data.ILivePushRes;
import com.tencent.ilivesdk.data.ILivePushUrl;
import com.tencent.ilivesdk.tools.quality.ILiveQualityData;
import com.tencent.ilivesdk.tools.quality.LiveInfo;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;
import com.tencent.livesdk.ILVLiveManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Live直播类
 */
public class LiveActivity extends BaseActivity implements LiveView, View.OnClickListener, ProfileView, LiveListView, GetLinkSigView {
    private static final String TAG = LiveActivity.class.getSimpleName();
    private static final int GETPROFILE_JOIN = 0x200;

    private LiveHelper mLiveHelper;



    private static final int MINFRESHINTERVAL = 500;
    private static final int UPDAT_WALL_TIME_TIMER_TASK = 1;
    private static final int TIMEOUT_INVITE = 2;
    private boolean mBoolRefreshLock = false;
    private boolean mBoolNeedRefresh = false;
    private final Timer mTimer = new Timer();

    private TimerTask mTimerTask = null;
    private static final int REFRESH_LISTVIEW = 5;
    private Dialog mMemberDg, inviteDg;

    private HeartBeatTask mHeartBeatTask;//心跳
    private ImageView mHeadIcon;
    private TextView mHostNameTv;
    private LinearLayout mHostLayout, mHostLeaveLayout;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private long mSecond = 0;
    private String formatTime;
    private Timer mHearBeatTimer, mVideoTimer;
    private VideoTimerTask mVideoTimerTask;//计时器
    private TextView mVideoTime;
    private ObjectAnimator mObjAnim;
    private ImageView mRecordBall;
    private ImageView mQualityCircle;
    private TextView mQualityText;
    private TextView roomId;
    private int thumbUp = 0;
    private long admireTime = 0;
    private int watchCount = 0;
    private boolean bCleanMode = false;
    private boolean bInAvRoom = false, bSlideUp = false, bDelayQuit = false;
    private boolean bReadyToChange = false;
    private boolean bHLSPush = false;
    private boolean bVideoMember = false;       // 是否上麦观众

    private String backGroundId;

    private TextView tvMembers;
    private TextView tvAdmires;
    private AVRootView mRootView;

    private Dialog mDetailDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   // 不锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_live);
        checkPermission();

        mLiveHelper = new LiveHelper(this, this);


        initView();
        backGroundId = CurLiveInfo.getHostID();
        //进入房间流程
        mLiveHelper.startEnterRoom();

    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPDAT_WALL_TIME_TIMER_TASK:
                    updateWallTime();
                    break;
                case REFRESH_LISTVIEW:

                    break;
                case TIMEOUT_INVITE:
                    String id = "" + msg.obj;
                    cancelInviteView(id);
                    mLiveHelper.sendGroupCmd(Constants.AVIMCMD_MULTI_HOST_CANCELINVITE, id);
                    break;
            }
            return false;
        }
    });

    /**
     * 时间格式化
     */
    private void updateWallTime() {
        String hs, ms, ss;

        long h, m, s;
        h = mSecond / 3600;
        m = (mSecond % 3600) / 60;
        s = (mSecond % 3600) % 60;
        if (h < 10) {
            hs = "0" + h;
        } else {
            hs = "" + h;
        }

        if (m < 10) {
            ms = "0" + m;
        } else {
            ms = "" + m;
        }

        if (s < 10) {
            ss = "0" + s;
        } else {
            ss = "" + s;
        }
        if (hs.equals("00")) {
            formatTime = ms + ":" + ss;
        } else {
            formatTime = hs + ":" + ms + ":" + ss;
        }

        if (Constants.HOST == MySelfInfo.getInstance().getIdStatus() && null != mVideoTime) {
            Logger.i(TAG, " refresh time ");
            mVideoTime.setText(formatTime);
        }
    }

    /**
     * 初始化UI
     */
    private TextView BtnBack, BtnMic, BtnNormal, mVideoChat, mBeautyConfirm;
    private TextView inviteView1, inviteView2, inviteView3;
    private TextView btnChageVoice, btnFlash, btnChangeRole, btnFilter, btnMagic;
    private ListView mListViewMsgItems;
    private LinearLayout mHostCtrView, mCtrViewMore, mNomalMemberCtrView, mVideoMemberCtrlView, mBeautySettings;
    private FrameLayout mFullControllerUi;

    private TextView pushBtn, recordBtn;

    private void showHeadIcon(ImageView view, String avatar) {
        if (TextUtils.isEmpty(avatar)) {

        } else {
            Logger.d(TAG, "load icon: " + avatar);
            RequestManager req = Glide.with(this);

        }
    }

    /**
     * 美颜方法
     */
    private void initILiveBeauty(){

    }

    /**
     * 初始化界面
     */
    private void initView() {
        mHostCtrView = (LinearLayout) findViewById(R.id.host_bottom_layout);
        mCtrViewMore = (LinearLayout) findViewById(R.id.host_bottom_layout_more);
        mNomalMemberCtrView = (LinearLayout) findViewById(R.id.member_bottom_layout);
        mVideoMemberCtrlView = (LinearLayout) findViewById(R.id.video_member_bottom_layout);
        mHostLeaveLayout = (LinearLayout) findViewById(R.id.ll_host_leave);

        mVideoChat = (TextView) findViewById(R.id.video_interact);

        mVideoTime = (TextView) findViewById(R.id.broadcasting_time);
        mHeadIcon = (ImageView) findViewById(R.id.head_icon);
        mHostNameTv = (TextView) findViewById(R.id.host_name);
        tvMembers = (TextView) findViewById(R.id.member_counts);
        tvAdmires = (TextView) findViewById(R.id.heart_counts);
        mQualityText = (TextView) findViewById(R.id.quality_text);
        mQualityCircle = (ImageView) findViewById(R.id.quality_circle);



        // 通用按钮初始化
        findViewById(R.id.speed_test_btn).setOnClickListener(this);
        findViewById(R.id.log_report).setOnClickListener(this);
        findViewById(R.id.back_primary).setOnClickListener(this);

        btnChageVoice = (TextView)findViewById(R.id.change_voice);
        btnChangeRole = (TextView)findViewById(R.id.change_role);
        btnFlash = (TextView)findViewById(R.id.flash_btn);
        btnFilter = (TextView)findViewById(R.id.tv_filter);
        btnMagic = (TextView)findViewById(R.id.tv_magic);

        btnChageVoice.setOnClickListener(this);
        btnChangeRole.setOnClickListener(this);
        btnFlash.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnMagic.setOnClickListener(this);

        roomId = (TextView) findViewById(R.id.room_id);

        //for 测试用
        TextView paramVideo = (TextView) findViewById(R.id.param_video);
        paramVideo.setOnClickListener(this);
        tvTipsMsg = (TextView) findViewById(R.id.qav_tips_msg);
        tvTipsMsg.setTextColor(Color.BLACK);
        paramTimer.schedule(task, 1000, 1000);

        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
            // 初始化主播控件
            mHostCtrView.setVisibility(View.VISIBLE);
            mNomalMemberCtrView.setVisibility(View.GONE);
            mVideoMemberCtrlView.setVisibility(View.GONE);
            mRecordBall = (ImageView) findViewById(R.id.record_ball);
            BtnMic = (TextView) findViewById(R.id.host_mic_btn);
            // 推流
            pushBtn = (TextView) findViewById(R.id.push_btn);
            pushBtn.setVisibility(View.VISIBLE);
            pushBtn.setOnClickListener(this);
            // 录制
            recordBtn = (TextView) findViewById(R.id.record_btn);
            recordBtn.setVisibility(View.VISIBLE);
            recordBtn.setOnClickListener(this);

            mVideoChat.setVisibility(View.VISIBLE);

            findViewById(R.id.host_message_input).setOnClickListener(this);
            findViewById(R.id.host_fullscreen_btn).setOnClickListener(this);
            findViewById(R.id.host_switch_cam).setOnClickListener(this);
            findViewById(R.id.host_beauty_btn).setOnClickListener(this);
            findViewById(R.id.host_menu_more).setOnClickListener(this);
            mVideoChat.setOnClickListener(this);
            inviteView1 = (TextView) findViewById(R.id.invite_view1);
            inviteView2 = (TextView) findViewById(R.id.invite_view2);
            inviteView3 = (TextView) findViewById(R.id.invite_view3);
            inviteView1.setOnClickListener(this);
            inviteView2.setOnClickListener(this);
            inviteView3.setOnClickListener(this);

            tvAdmires.setVisibility(View.VISIBLE);

            View view = findViewById(R.id.link_btn);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(this);
            view = findViewById(R.id.unlink_btn);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(this);



            // mMemberDg = new MembersDialog(this, R.style.floag_dialog, this);
            startRecordAnimation();
            showHeadIcon(mHeadIcon, MySelfInfo.getInstance().getAvatar());
        } else {
            // 初始化观众控件
            mHostCtrView.setVisibility(View.GONE);
            changeCtrlView(bVideoMember);

            BtnMic = (TextView) findViewById(R.id.vmember_mic_btn);

            findViewById(R.id.record_tip).setVisibility(View.GONE);
            mHostNameTv.setVisibility(View.VISIBLE);

            findViewById(R.id.vmember_close_member_video).setOnClickListener(this);
            findViewById(R.id.vmember_fullscreen_btn).setOnClickListener(this);
            findViewById(R.id.member_fullscreen_btn).setOnClickListener(this);
            findViewById(R.id.vmember_switch_cam).setOnClickListener(this);
            findViewById(R.id.member_message_input).setOnClickListener(this);
            findViewById(R.id.member_send_good).setOnClickListener(this);
            findViewById(R.id.member_menu_more).setOnClickListener(this);

            findViewById(R.id.vmember_beauty_btn).setOnClickListener(this);
            findViewById(R.id.vmember_menu_more).setOnClickListener(this);
            findViewById(R.id.vmember_message_input).setOnClickListener(this);
            findViewById(R.id.vmember_send_good).setOnClickListener(this);
            mVideoChat.setVisibility(View.GONE);

            List<String> ids = new ArrayList<>();
            ids.add(CurLiveInfo.getHostID());
            showHeadIcon(mHeadIcon, CurLiveInfo.getHostAvator());

            mHostLayout = (LinearLayout) findViewById(R.id.head_up_layout);
            mHostLayout.setOnClickListener(this);
        }
        BtnMic.setOnClickListener(this);

        BtnNormal = (TextView) findViewById(R.id.normal_btn);
        BtnNormal.setOnClickListener(this);
        mFullControllerUi = (FrameLayout) findViewById(R.id.controll_ui);

        initPushDialog();
        initRecordDialog();

        BtnBack = (TextView) findViewById(R.id.btn_back);
        BtnBack.setOnClickListener(this);

        mListViewMsgItems = (ListView) findViewById(R.id.im_msg_listview);


        tvMembers.setText("" + CurLiveInfo.getMembers());
        tvAdmires.setText("" + CurLiveInfo.getAdmires());

        //TODO 获取渲染层
        mRootView = (AVRootView) findViewById(R.id.av_root_view);
        //TODO 设置渲染层
        ILVLiveManager.getInstance().setAvVideoView(mRootView);


        mRootView.setBackground(R.mipmap.ic_launcher);
        mRootView.setGravity(AVRootView.LAYOUT_GRAVITY_RIGHT);
        mRootView.setSubMarginY(getResources().getDimensionPixelSize(R.dimen.small_area_margin_top));
        mRootView.setSubMarginX(getResources().getDimensionPixelSize(R.dimen.small_area_marginright));
        mRootView.setSubPadding(getResources().getDimensionPixelSize(R.dimen.small_area_marginbetween));
        mRootView.setSubWidth(getResources().getDimensionPixelSize(R.dimen.small_area_width));
        mRootView.setSubHeight(getResources().getDimensionPixelSize(R.dimen.small_area_height));
        mRootView.setSubCreatedListener(new AVRootView.onSubViewCreatedListener() {
            @Override
            public void onSubViewCreated() {
                for (int i = 1; i < ILiveConstants.MAX_AV_VIDEO_NUM; i++) {
                    final int index = i;
                    AVVideoView avVideoView = mRootView.getViewByIndex(index);
                    avVideoView.setRotate(false);
                    avVideoView.setGestureListener(new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            mRootView.swapVideoView(0, index);
                            backGroundId = mRootView.getViewByIndex(0).getIdentifier();
                            return super.onSingleTapConfirmed(e);
                        }
                    });
                }

                mRootView.getViewByIndex(0).setRotate(false);
                mRootView.getViewByIndex(0).setBackground(R.mipmap.ic_launcher);
                mRootView.getViewByIndex(0).setGestureListener(new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        if (MySelfInfo.getInstance().getIdStatus() != Constants.HOST) {
                            if (e1.getY() - e2.getY() > 20 && Math.abs(velocityY) > 10) {
                                bSlideUp = true;
                            } else if (e2.getY() - e1.getY() > 20 && Math.abs(velocityY) > 10) {
                                bSlideUp = false;
                            }

                        }
                        return false;
                    }
                });
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        ILiveRoomManager.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ILiveRoomManager.getInstance().onPause();
    }


    /**
     * 直播心跳
     */
    private class HeartBeatTask extends TimerTask {
        @Override
        public void run() {
            String host = CurLiveInfo.getHostID();
            Logger.i(TAG, "HeartBeatTask " + host);
            if (!TextUtils.isEmpty(MySelfInfo.getInstance().getId()) && MySelfInfo.getInstance().getId().equals(CurLiveInfo.getHostID()))
                UserServerHelper.getInstance().heartBeater(1);
            else
                UserServerHelper.getInstance().heartBeater(MySelfInfo.getInstance().getIdStatus());

        }
    }

    /**
     * 记时器
     */
    private class VideoTimerTask extends TimerTask {
        public void run() {
            Logger.i(TAG, "timeTask ");
            ++mSecond;
            if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST)
                mHandler.sendEmptyMessage(UPDAT_WALL_TIME_TIMER_TASK);
        }
    }

    @Override
    protected void onDestroy() {
        watchCount = 0;
        super.onDestroy();
        if (null != mHearBeatTimer) {
            mHearBeatTimer.cancel();
            mHearBeatTimer = null;
        }
        if (null != mVideoTimer) {
            mVideoTimer.cancel();
            mVideoTimer = null;
        }
        if (null != paramTimer) {
            paramTimer.cancel();
            paramTimer = null;
        }


        inviteViewCount = 0;
        thumbUp = 0;
        CurLiveInfo.setMembers(0);
        CurLiveInfo.setAdmires(0);
        CurLiveInfo.setCurrentRequestCount(0);
        mLiveHelper.onDestory();

        // ShareSDK.stopSDK(this);
    }


    /**
     * 点击Back键
     */
    @Override
    public void onBackPressed() {
        if (bInAvRoom) {
            bDelayQuit = false;
            quiteLiveByPurpose();
        } else {
            clearOldData();
            finish();
        }
    }

    @Override
    public void forceQuitRoom(String strMessage) {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        ILiveRoomManager.getInstance().onPause();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.str_tips_title)
                .setMessage(strMessage)
                .setPositiveButton(R.string.btn_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                callExitRoom();
            }
        });
        alertDialog.show();
    }

    /**
     * 主动退出直播
     */
    private void quiteLiveByPurpose() {
        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
            if (backDialog.isShowing() == false)
                backDialog.show();
        } else {
            callExitRoom();
        }
    }

    private void callExitRoom(){
        mLiveHelper.startExitRoom();
    }


    private Dialog backDialog;





    private int curFilter = 0;




    private int curMagic = 0;




    /**
     * 完成进出房间流程
     */
    @Override
    public void enterRoomComplete(int id_status, boolean isSucc) {


        mRootView.getViewByIndex(0).setVisibility(GLView.VISIBLE);

        //mRootView.getViewByIndex(0).setRotate(true);
//        mRootView.getViewByIndex(0).setDiffDirectionRenderMode(AVVideoView.ILiveRenderMode.BLACK_TO_FILL);
        bInAvRoom = true;
        bDelayQuit = true;
        bReadyToChange = true;
        roomId.setText("" + CurLiveInfo.getRoomNum());
        if (isSucc == true) {
            //主播心跳
            mHearBeatTimer = new Timer(true);
            mHeartBeatTask = new HeartBeatTask();
            mHearBeatTimer.schedule(mHeartBeatTask, 100, 5 * 1000); //5秒重复上报心跳 拉取房间列表

            //直播时间
            mVideoTimer = new Timer(true);
            mVideoTimerTask = new VideoTimerTask();
            mVideoTimer.schedule(mVideoTimerTask, 1000, 1000);

            //IM初始化
            if (id_status == Constants.HOST) {//主播方式加入房间成功
                mHostNameTv.setText(MySelfInfo.getInstance().getId());

                //注册一个音频回调为变声用
                ILiveSDK.getInstance().getAvAudioCtrl().registAudioDataCallbackWithByteBuffer(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_VOICEDISPOSE, new AVAudioCtrl.RegistAudioDataCompleteCallbackWithByteBuffer() {
                    @Override
                    public int onComplete(AVAudioCtrl.AudioFrameWithByteBuffer audioFrameWithByteBuffer, int i) {
                        return 0;
                    }
                });

                //开启摄像头渲染画面
                Logger.i(TAG, "createlive enterRoomComplete isSucc" + isSucc);
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putBoolean("living", true);
                editor.apply();
            } else {
                // 更新控制栏
                changeCtrlView(false);
                //发消息通知上线
                mLiveHelper.sendGroupCmd(Constants.AVIMCMD_ENTERLIVE, "");
            }
        }
    }




    private TextView mDetailTime, mDetailAdmires, mDetailWatchCount;











    @Override
    public void onGetSignRsp(String id, String roomid, String sign) {
        Logger.d(TAG, "onGetSignRsp->id:"+id+", room:"+roomid+", sign:"+sign);

    }

    /**
     * 红点动画
     */
    private void startRecordAnimation() {
        mObjAnim = ObjectAnimator.ofFloat(mRecordBall, "alpha", 1f, 0f, 1f);
        mObjAnim.setDuration(1000);
        mObjAnim.setRepeatCount(-1);
        mObjAnim.start();
    }

    private float getBeautyProgress(int progress) {
        Logger.d("shixu", "progress: " + progress);
        return (9.0f * progress / 100.0f);
    }


    @Override
    public void showInviteDialog() {
        if ((inviteDg != null) && (getBaseContext() != null) && (inviteDg.isShowing() != true)) {
            inviteDg.show();
        }
    }

    @Override
    public void hideInviteDialog() {
        if ((inviteDg != null) && (inviteDg.isShowing() == true)) {
            inviteDg.dismiss();
        }
    }


    @Override
    public void refreshText(String text, String name) {
        if (text != null) {

        }
    }

    @Override
    public void refreshThumbUp() {
        CurLiveInfo.setAdmires(CurLiveInfo.getAdmires() + 1);

        tvAdmires.setText("" + CurLiveInfo.getAdmires());
    }

    private int inviteViewCount = 0;

    @Override
    public boolean showInviteView(String id) {
        Logger.d(TAG,   "id " + id);
        int index = mRootView.findValidViewIndex();
        if (index == -1) {
            Toast.makeText(LiveActivity.this, "the invitation's upper limit is 3", Toast.LENGTH_SHORT).show();
            return false;
        }
        int requetCount = index + inviteViewCount;
        if (requetCount > 3) {
            Toast.makeText(LiveActivity.this, "the invitation's upper limit is 3", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (hasInvited(id)) {
            Toast.makeText(LiveActivity.this, "it has already invited", Toast.LENGTH_SHORT).show();
            return false;
        }
        switch (requetCount) {
            case 1:
                inviteView1.setText(id);
                inviteView1.setVisibility(View.VISIBLE);
                inviteView1.setTag(id);

                break;
            case 2:
                inviteView2.setText(id);
                inviteView2.setVisibility(View.VISIBLE);
                inviteView2.setTag(id);
                break;
            case 3:
                inviteView3.setText(id);
                inviteView3.setVisibility(View.VISIBLE);
                inviteView3.setTag(id);
                break;
        }
        mLiveHelper.sendC2CCmd(Constants.AVIMCMD_MUlTI_HOST_INVITE, "", id);
        inviteViewCount++;
        //30s超时取消
        Message msg = new Message();
        msg.what = TIMEOUT_INVITE;
        msg.obj = id;
        mHandler.sendMessageDelayed(msg, 30 * 1000);
        return true;
    }


    /**
     * 判断是否邀请过同一个人
     */
    private boolean hasInvited(String id) {
        if (null != inviteView1 && id.equals(inviteView1.getTag())) {
            return true;
        }
        if (null != inviteView2 && id.equals(inviteView2.getTag())) {
            return true;
        }
        if (null != inviteView3 && id.equals(inviteView3.getTag())) {
            return true;
        }
        return false;
    }

    @Override
    public void cancelInviteView(String id) {
        if ((inviteView1 != null) && (inviteView1.getTag() != null)) {
            if (inviteView1.getTag().equals(id)) {
            }
            if (inviteView1.getVisibility() == View.VISIBLE) {
                inviteView1.setVisibility(View.INVISIBLE);
                inviteView1.setTag("");
                inviteViewCount--;
            }
        }

        if (inviteView2 != null && inviteView2.getTag() != null) {
            if (inviteView2.getTag().equals(id)) {
                if (inviteView2.getVisibility() == View.VISIBLE) {
                    inviteView2.setVisibility(View.INVISIBLE);
                    inviteView2.setTag("");
                    inviteViewCount--;
                }
            } else {
                Log.i(TAG, "cancelInviteView inviteView2 is null");
            }
        } else {
            Log.i(TAG, "cancelInviteView inviteView2 is null");
        }

        if (inviteView3 != null && inviteView3.getTag() != null) {
            if (inviteView3.getTag().equals(id)) {
                if (inviteView3.getVisibility() == View.VISIBLE) {
                    inviteView3.setVisibility(View.INVISIBLE);
                    inviteView3.setTag("");
                    inviteViewCount--;
                }
            } else {
                Log.i(TAG, "cancelInviteView inviteView3 is null");
            }
        } else {
            Log.i(TAG, "cancelInviteView inviteView3 is null");
        }


    }

    @Override
    public void cancelMemberView(String id) {
        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
            mLiveHelper.sendGroupCmd(Constants.AVIMCMD_MULTI_CANCEL_INTERACT, id);
            mRootView.closeUserView(id, AVView.VIDEO_SRC_TYPE_CAMERA, true);
        } else {


            changeCtrlView(false);
        }
    }

    @Override
    public void memberJoin(String id, String name) {

    }


    private void showLogDialog(){
        final Dialog dialog = new Dialog(this, R.style.common_dlg);
        dialog.setContentView(R.layout.dialog_log_upload);

        dialog.setTitle(R.string.str_title_logupload);

        final EditText etDate = (EditText)dialog.findViewById(R.id.et_date);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int date = 0;
                try{
                    date = Integer.valueOf(etDate.getText().toString());
                }catch (NumberFormatException e){
                }
                ILiveSDK.getInstance().uploadLog("report log", date,  new ILiveCallBack(){
                    @Override
                    public void onSuccess(Object data) {
                        Toast.makeText(LiveActivity.this, "Log report succ!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        Toast.makeText(LiveActivity.this, "failed:"+module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showHostDetail() {
        Dialog hostDlg = new Dialog(this, R.style.host_info_dlg);
        hostDlg.setContentView(R.layout.host_info_layout);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = hostDlg.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.TOP);
        lp.width = (int) (display.getWidth()); //设置宽度

        hostDlg.getWindow().setAttributes(lp);
        hostDlg.show();

        TextView tvHost = (TextView) hostDlg.findViewById(R.id.tv_host_name);
        tvHost.setText(CurLiveInfo.getHostName());
        ImageView ivHostIcon = (ImageView) hostDlg.findViewById(R.id.iv_host_icon);
        showHeadIcon(ivHostIcon, CurLiveInfo.getHostAvator());
        TextView tvLbs = (TextView) hostDlg.findViewById(R.id.tv_host_lbs);

        ImageView ivReport = (ImageView) hostDlg.findViewById(R.id.iv_report);
        ivReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.host_message_input:
            case R.id.member_message_input:
            case R.id.vmember_message_input:

                break;
            case R.id.member_send_good:
            case R.id.vmember_send_good:

                break;
            case R.id.host_switch_cam:
            case R.id.vmember_switch_cam:
                ILiveRoomManager.getInstance().switchCamera(1- ILiveRoomManager.getInstance().getCurCameraId());
                break;
            case R.id.host_mic_btn:
            case R.id.vmember_mic_btn:

                break;
            case R.id.vmember_close_member_video:
                cancelMemberView(backGroundId);
                break;
            case R.id.host_beauty_btn:
            case R.id.vmember_beauty_btn:

                break;
            case R.id.host_menu_more:
            case R.id.member_menu_more:
            case R.id.vmember_menu_more:
                mHostCtrView.setVisibility(View.INVISIBLE);
                mVideoMemberCtrlView.setVisibility(View.INVISIBLE);
                mNomalMemberCtrView.setVisibility(View.INVISIBLE);
                mCtrViewMore.setVisibility(View.VISIBLE);
                break;
            case R.id.host_fullscreen_btn:
            case R.id.member_fullscreen_btn:
            case R.id.vmember_fullscreen_btn:
                bCleanMode = true;
                mFullControllerUi.setVisibility(View.INVISIBLE);
                BtnNormal.setVisibility(View.VISIBLE);
                break;
            case R.id.normal_btn:
                bCleanMode = false;
                mFullControllerUi.setVisibility(View.VISIBLE);
                BtnNormal.setVisibility(View.GONE);
                break;
            case R.id.video_interact:
                mMemberDg.setCanceledOnTouchOutside(true);
                mMemberDg.show();
                break;
            case R.id.head_up_layout:
                showHostDetail();
                break;

            case R.id.back_primary:
                mCtrViewMore.setVisibility(View.INVISIBLE);
                if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) { // 主播
                    mHostCtrView.setVisibility(View.VISIBLE);
                } else {
                    if (bVideoMember) { // 上麦观众
                        mVideoMemberCtrlView.setVisibility(View.VISIBLE);
                    }else{    // 普通观众
                        mNomalMemberCtrView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.flash_btn:        // 闪光
                if (!mLiveHelper.toggleFlashLight()){
                    Toast.makeText(LiveActivity.this, "toggle flash light failed!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_back:
                quiteLiveByPurpose();
                break;
            case R.id.param_video:
                showTips = !showTips;
                break;
            case R.id.push_btn:
                pushStream();
                break;
            case R.id.link_btn:

                break;
            case R.id.unlink_btn:

                break;
            case R.id.change_voice:       // 变声

                break;
            case R.id.change_role:

                break;
            case R.id.tv_filter:        // 滤镜

                break;
            case R.id.tv_magic:         // 挂件

                break;
            case R.id.log_report:
                showLogDialog();
                break;
            case R.id.record_btn:
                if (!mRecord) {
                    if (recordDialog != null)
                        recordDialog.show();
                } else {
                    mLiveHelper.stopRecord();
                }
                break;
            case R.id.speed_test_btn:
                //new SpeedTestDialog(this).start();
                break;
            case R.id.invite_view1:
                inviteView1.setVisibility(View.INVISIBLE);
                mLiveHelper.sendGroupCmd(Constants.AVIMCMD_MULTI_CANCEL_INTERACT, "" + inviteView1.getTag());
                break;
            case R.id.invite_view2:
                inviteView2.setVisibility(View.INVISIBLE);
                mLiveHelper.sendGroupCmd(Constants.AVIMCMD_MULTI_CANCEL_INTERACT, "" + inviteView2.getTag());
                break;
            case R.id.invite_view3:
                inviteView3.setVisibility(View.INVISIBLE);
                mLiveHelper.sendGroupCmd(Constants.AVIMCMD_MULTI_CANCEL_INTERACT, "" + inviteView3.getTag());
                break;
        }
    }

    private String getParams(String src, String title, String key){
        int pos = src.indexOf(key);
        if (-1 != pos){
            pos += key.length()+2;
            int endPos = src.indexOf(",", pos);
            return title+": "+src.substring(pos, endPos)+"\n";
        }

        return "";
    }

    // 补充房间信息
    private String expandTips(String tips){

        // 获取是否开启硬件编解码
        if (null != ILiveSDK.getInstance().getAVContext().getRoom()){
            String videoTips = ILiveSDK.getInstance().getAVContext().getRoom().getQualityParam();
            tips += getParams(videoTips, "大画面硬编解", "qos_big_hw");
        }

        if (null != ILiveSDK.getInstance().getAvVideoCtrl()) {
            // 输出采集支持分辨率
            Camera camera = (Camera) ILiveSDK.getInstance().getAvVideoCtrl().getCamera();
            if (null != camera) {
                tips += "摄像头支持分辨率: \n";
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                for (Camera.Size size : supportedPreviewSizes) {
                    tips += "\t"+size.width + "*" + size.height+"\n";
                }
            }
        }
        return tips;
    }

    //for 测试获取测试参数
    private boolean showTips = false;
    private TextView tvTipsMsg;
    Timer paramTimer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (showTips) {
                        mQualityCircle.setVisibility(View.VISIBLE);
                        mQualityText.setVisibility(View.VISIBLE);
                        if (tvTipsMsg != null && ILiveSDK.getInstance().getAVContext() != null &&
                                ILiveSDK.getInstance().getAVContext().getRoom() != null) {
                            //String tips =getQualityTips();
                            String tips = "";
                            ILiveQualityData qData = ILiveRoomManager.getInstance().getQualityData();
                            if (null != qData) {
                                tips += "FPS:\t" + qData.getUpFPS() + "\n";
                                tips += "Send:\t" + qData.getSendKbps() + "Kbps\t";
                                tips += "Recv:\t" + qData.getRecvKbps() + "Kbps\n";
                                tips += "SendLossRate:\t" + qData.getSendLossRate() + "%\t";
                                tips += "RecvLossRate:\t" + qData.getRecvLossRate() + "%\n";
                                tips += "AppCPURate:\t" + qData.getAppCPURate() + "%\t";
                                tips += "SysCPURate:\t" + qData.getSysCPURate() + "%\n";
                                Map<String, LiveInfo> userMaps = qData.getLives();
                                for (Map.Entry<String, LiveInfo> entry : userMaps.entrySet()) {
                                    tips += "\t" + entry.getKey() + "-" + entry.getValue().getWidth() + "*" + entry.getValue().getHeight() + "\n";
                                }
                            }

                            //tips = expandTips(tips);
                            tips += '\n';
                            tips += getQualityTips(ILiveSDK.getInstance().getAVContext().getRoom().getQualityTips());
                            tvTipsMsg.getBackground().setAlpha(125);
                            tvTipsMsg.setText(tips);
                            tvTipsMsg.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvTipsMsg.setText("");
                        tvTipsMsg.setVisibility(View.INVISIBLE);
                        mQualityCircle.setVisibility(View.GONE);
                        mQualityText.setVisibility(View.GONE);
                    }
                }
            });
        }
    };


    @Override
    public void changeCtrlView(boolean videoMember) {
        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST){
            // 主播不存在切换
            return;
        }
        bVideoMember = videoMember;
        mCtrViewMore.setVisibility(View.INVISIBLE);
        if (bVideoMember){
            mVideoMemberCtrlView.setVisibility(View.VISIBLE);
            mNomalMemberCtrView.setVisibility(View.GONE);

            btnChageVoice.setVisibility(View.VISIBLE);
            btnChangeRole.setVisibility(View.VISIBLE);
            btnFlash.setVisibility(View.VISIBLE);
            btnFilter.setVisibility(View.VISIBLE);
            btnMagic.setVisibility(View.VISIBLE);
        }else{
            mVideoMemberCtrlView.setVisibility(View.GONE);
            mNomalMemberCtrView.setVisibility(View.VISIBLE);

            btnChageVoice.setVisibility(View.INVISIBLE);
            btnChangeRole.setVisibility(View.INVISIBLE);
            btnFlash.setVisibility(View.INVISIBLE);
            btnFilter.setVisibility(View.INVISIBLE);
            btnMagic.setVisibility(View.INVISIBLE);
        }
    }











    @Override
    public void updateProfileInfo(TIMUserProfile profile) {

    }

    @Override
    public void updateUserInfo(int requestCode, List<TIMUserProfile> profiles) {
        if (null != profiles) {
            switch (requestCode) {
                case GETPROFILE_JOIN:
                    for (TIMUserProfile user : profiles) {
                        tvMembers.setText("" + CurLiveInfo.getMembers());
                        Logger.w(TAG, "get nick name:" + user.getNickName());
                        Logger.w(TAG, "get remark name:" + user.getRemark());
                        Logger.w(TAG, "get avatar:" + user.getFaceUrl());

                    }
                    break;
            }

        }
    }

    //旁路直播
    private static boolean isPushed = false;

    /**
     * 旁路直播 退出房间时必须退出推流。否则会占用后台channel。
     */
    public void pushStream() {
        if (!isPushed) {
            bHLSPush = false;
            if (mPushDialog != null)
                mPushDialog.show();
        } else {
            mLiveHelper.stopPush();
        }
    }

    private Dialog mPushDialog;

    //开启推流
    private void initPushDialog() {
        mPushDialog = new Dialog(this, R.style.dialog);
        mPushDialog.setContentView(R.layout.push_dialog_layout);
        final EditText pushfileNameInput = (EditText) mPushDialog.findViewById(R.id.push_filename);
        final RadioGroup radgroup = (RadioGroup) mPushDialog.findViewById(R.id.push_type);


        Button recordOk = (Button) mPushDialog.findViewById(R.id.btn_record_ok);
        recordOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ILivePushOption option = new ILivePushOption();
                if (pushfileNameInput.getText().toString().equals("")) { // 推流名字为空
                    Toast.makeText(LiveActivity.this, "name can't be empty", Toast.LENGTH_SHORT);
                    return;
                } else {
                    option.channelName(pushfileNameInput.getText().toString());
                }

                if (radgroup.getCheckedRadioButtonId() == R.id.hls) {//默认格式
                    option.encode(ILivePushOption.Encode.HLS);
                    bHLSPush = true;
                } else {
                    option.encode(ILivePushOption.Encode.RTMP);
                }
                mLiveHelper.startPush(option);//开启推流
                mPushDialog.dismiss();
            }
        });


        Button recordCancel = (Button) mPushDialog.findViewById(R.id.btn_record_cancel);
        recordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPushDialog.dismiss();
            }
        });

        Window dialogWindow = mPushDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        mPushDialog.setCanceledOnTouchOutside(false);
    }

    private void showPushUrl(final String url) {
        ILiveLog.d("ILVBX", "showPushUrl->entered:" + url);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.str_push_title)
                .setMessage(url)
                .setPositiveButton(getString(R.string.str_push_copy), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager cmb = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("text", url);
                        cmb.setPrimaryClip(clipData);
                        Toast.makeText(getApplicationContext(), "Copy Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancel), null);
        if (bHLSPush) {
           
        }
        builder.show();
    }

   


    /**
     * 推流成功
     */
    @Override
    public void pushStreamSucc(ILivePushRes streamRes) {
        List<ILivePushUrl> liveUrls = streamRes.getUrls();
        isPushed = true;
        pushBtn.setText(R.string.live_btn_stop_push);
        int length = liveUrls.size();
        String url = null;
        String url2 = null;
        if (length == 1) {
            ILivePushUrl avUrl = liveUrls.get(0);
            url = avUrl.getUrl();
        } else if (length == 2) {
            ILivePushUrl avUrl = liveUrls.get(0);
            url = avUrl.getUrl();
            ILivePushUrl avUrl2 = liveUrls.get(1);
            url2 = avUrl2.getUrl();
        }

        showPushUrl(url);
    }

    private Dialog recordDialog;
    private String filename = "";
    private boolean mRecord = false;
    private EditText filenameEditText;

    private void initRecordDialog() {
        recordDialog = new Dialog(this, R.style.dialog);
        recordDialog.setContentView(R.layout.record_layout);

        filenameEditText = (EditText) recordDialog.findViewById(R.id.record_filename);

        if (filename.length() > 0) {
            filenameEditText.setText(filename);
        }
        filenameEditText.setText("" + CurLiveInfo.getRoomNum());

        Button videoRecord = (Button) recordDialog.findViewById(R.id.btn_record_video);
       
        Button audioRecord = (Button) recordDialog.findViewById(R.id.btn_record_audio);
        audioRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ILiveRecordOption option = new ILiveRecordOption();
                filename = filenameEditText.getText().toString();
                option.fileName("sxb_" + ILiveLoginManager.getInstance().getMyUserId() + "_" + filename);

                option.classId(123);
                option.recordType(TIMAvManager.RecordType.AUDIO);
                mLiveHelper.startRecord(option);
                recordDialog.dismiss();
                recordDialog.dismiss();
            }
        });
        Window dialogWindow = recordDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        recordDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 停止推流成功
     */
    @Override
    public void stopStreamSucc() {
        isPushed = false;
        pushBtn.setText(R.string.live_btn_push);
    }

    @Override
    public void startRecordCallback(boolean isSucc) {
        mRecord = true;
        recordBtn.setText(R.string.live_btn_stop_record);
    }

    @Override
    public void stopRecordCallback(boolean isSucc, List<String> files) {
        if (isSucc == true) {
            mRecord = false;
            recordBtn.setText(R.string.live_btn_record);
        }
    }

    @Override
    public void hostLeave(String id, String name) {

    }

    @Override
    public void hostBack(String id, String name) {

    }

    @Override
    public void linkRoomReq(String id, String name) {

    }

    @Override
    public void linkRoomAccept(String id, String strRoomId) {

    }

    void checkPermission() {
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if ((checkSelfPermission(Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WAKE_LOCK);
            if ((checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
            if (permissionsList.size() != 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        }
    }

    // 清除老房间数据
    private void clearOldData() {

        mBoolNeedRefresh = true;
        if (mBoolRefreshLock) {
            return;
        } else {

        }
        mRootView.clearUserView();
    }


   

    

    private static String getValue(String src, String param, String sep) {
        int idx = src.indexOf(param);
        if (-1 != idx) {
            idx += param.length() + 1;
            if (-1 != sep.indexOf(src.charAt(idx))) {
                idx++;
            }
            for (int i = idx; i < src.length(); i++) {
                if (-1 != sep.indexOf(src.charAt(i))) {
                    return src.substring(idx, i).trim();
                }
            }
        }

        return "";
    }

    public String getQualityTips(String qualityTips) {
        String strTips = "";
        String sep = "[](),\n";

        strTips += "AVSDK版本号: " + getValue(qualityTips, "sdk_version", sep) + "\n";
        strTips += "房间号: " + getValue(qualityTips, "RoomID", sep) + "\n";
        strTips += "角色: " + getValue(qualityTips, "ControlRole", sep) + "\n";
        strTips += "权限: " + getValue(qualityTips, "Authority", sep) + "\n";
        String tmpStr = getValue(qualityTips, "视频采集", "\n");
        if (!TextUtils.isEmpty(tmpStr))
            strTips += "采集信息: " + getValue(qualityTips, "视频采集", "\n") + "\n";
        strTips += "麦克风: " + getValue(qualityTips, "Mic", sep) + "\n";
        strTips += "扬声器: " + getValue(qualityTips, "Spk", sep) + "\n";

        return strTips;
    }

    
}
