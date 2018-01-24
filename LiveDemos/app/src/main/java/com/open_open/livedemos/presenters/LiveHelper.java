package com.open_open.livedemos.presenters;

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.open_open.livedemos.R;
import com.open_open.livedemos.base.Presenter;
import com.open_open.livedemos.contents.Constants;
import com.open_open.livedemos.entity.CurLiveInfo;
import com.open_open.livedemos.entity.LogConstants;
import com.open_open.livedemos.entity.MySelfInfo;
import com.open_open.livedemos.presenters.viewinterface.LiveView;
import com.orhanobut.logger.Logger;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILivePushOption;
import com.tencent.ilivesdk.core.ILiveRecordOption;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.data.ILivePushRes;
import com.tencent.ilivesdk.data.ILivePushUrl;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConstants;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * 直播控制类
 */
public class LiveHelper extends Presenter implements ILiveRoomOption.onRoomDisconnectListener {
    private final String TAG = "LiveHelper";
    private LiveView mLiveView;
    public Context mContext;
    private boolean bCameraOn = false;
    private boolean bMicOn = false;
    private boolean flashLgihtStatus = false;
    private long streamChannelID;
    private ApplyCreateRoom createRoomProcess;

    @Override
    public void onRoomDisconnect(int errCode, String errMsg) {

    }


    class ApplyCreateRoom extends AsyncTask<String, Integer, UserServerHelper.RequestBackInfo> {

        @Override
        protected UserServerHelper.RequestBackInfo doInBackground(String... strings) {

            return UserServerHelper.getInstance().applyCreateRoom(); //获取后台
        }

        @Override
        protected void onPostExecute(UserServerHelper.RequestBackInfo result) {
            if (result != null && result.getErrorCode() == 0) {
                createRoom();
            } else {
                Log.i(TAG, "ApplyCreateRoom onPostExecute: " + (null!=result?result.getErrorInfo():"empty"));
            }
        }
    }


    /**
     * 申请房间
     */
    private void startCreateRoom() {
        createRoomProcess = new ApplyCreateRoom(); //申请房间
        createRoomProcess.execute();

    }


    /**
     * 上报房间
     */
    private void NotifyServerLiveTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

    }


    public LiveHelper(Context context, LiveView liveview) {
        mContext = context;
        mLiveView = liveview;

    }

    @Override
    public void onDestory() {
        mLiveView = null;
        mContext = null;

        ILVLiveManager.getInstance().quitRoom(null);
    }

    /**
     * 进入房间
     */
    public void startEnterRoom() {
        if (MySelfInfo.getInstance().isCreateRoom() == true) {
            startCreateRoom();
        } else {
            joinRoom();
        }
    }


    private void showToast(String strMsg){
        if (null != mContext){
            Toast.makeText(mContext, strMsg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showUserToast(String account, int resId){
        if (null != mContext){
            Toast.makeText(mContext, account+ mContext.getString(resId), Toast.LENGTH_SHORT).show();
        }
    }

    private void quitLiveRoom() {
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Logger.d(TAG, "ILVB-SXB|quitRoom->success");
                CurLiveInfo.setCurrentRequestCount(0);
                //通知结束
                NotifyServerLiveTask();
                if (null != mLiveView) {

                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Logger.d(TAG, "ILVB-SXB|quitRoom->failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mLiveView) {

                }
            }
        });
    }

    public void startExitRoom() {
        ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(null);
        quitLiveRoom();
    }

    /**
     * 发送信令
     */

    public int sendGroupCmd(int cmd, String param) {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setCmd(cmd);
        customCmd.setParam(param);
        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
        return sendCmd(customCmd);
    }

    public int sendC2CCmd(final int cmd, String param, String destId) {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setDestId(destId);
        customCmd.setCmd(cmd);
        customCmd.setParam(param);
        customCmd.setType(ILVText.ILVTextType.eC2CMsg);
        return sendCmd(customCmd);
    }

    /**
     * 打开闪光灯
     */
    public boolean toggleFlashLight() {
        AVVideoCtrl videoCtrl = ILiveSDK.getInstance().getAvVideoCtrl();
        if (null == videoCtrl) {
            return false;
        }

        final Object cam = videoCtrl.getCamera();
        if ((cam == null) || (!(cam instanceof Camera))) {
            return false;
        }
        final Camera.Parameters camParam = ((Camera) cam).getParameters();
        if (null == camParam) {
            return false;
        }

        Object camHandler = videoCtrl.getCameraHandler();
        if ((camHandler == null) || (!(camHandler instanceof Handler))) {
            return false;
        }

        //对摄像头的操作放在摄像头线程
        if (flashLgihtStatus == false) {
            ((Handler) camHandler).post(new Runnable() {
                public void run() {
                    try {
                        camParam.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        ((Camera) cam).setParameters(camParam);
                        flashLgihtStatus = true;
                    } catch (RuntimeException e) {
                        Logger.d("setParameters", "RuntimeException");
                    }
                }
            });
        } else {
            ((Handler) camHandler).post(new Runnable() {
                public void run() {
                    try {
                        camParam.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        ((Camera) cam).setParameters(camParam);
                        flashLgihtStatus = false;
                    } catch (RuntimeException e) {
                        Logger.d("setParameters", "RuntimeException");
                    }

                }
            });
        }
        return true;
    }

    public void startRecord(ILiveRecordOption option) {
        ILiveRoomManager.getInstance().startRecordVideo(option, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Logger.i(TAG, "start record success ");
                if (null != mLiveView)
                    mLiveView.startRecordCallback(true);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Logger.e(TAG, "start record error " + errCode + "  " + errMsg);
                if (null != mLiveView)
                    mLiveView.startRecordCallback(false);
            }
        });
    }

    public void stopRecord() {
        ILiveRoomManager.getInstance().stopRecordVideo(new ILiveCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                Logger.d(TAG, "stopRecord->success");
                for (String url : data) {
                    Logger.d(TAG, "stopRecord->url:" + url);
                }
                if (null != mLiveView)
                    mLiveView.stopRecordCallback(true, data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Logger.e(TAG, "stopRecord->failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mLiveView)
                    mLiveView.stopRecordCallback(false, null);
            }
        });
    }

    public void startPush(ILivePushOption option) {
        ILiveRoomManager.getInstance().startPushStream(option, new ILiveCallBack<ILivePushRes>() {
            @Override
            public void onSuccess(ILivePushRes data) {
                List<ILivePushUrl> liveUrls = data.getUrls();
                streamChannelID = data.getChnlId();
                if (null != mLiveView)
                    mLiveView.pushStreamSucc(data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Logger.e(TAG, "url error " + errCode + " : " + errMsg);
                showToast("start stream error,try again " + errCode + " : " + errMsg);
            }
        });
    }

    public void stopPush() {
        ILiveRoomManager.getInstance().stopPushStream(streamChannelID, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Logger.e(TAG, "stopPush->success");
                if (null != mLiveView)
                    mLiveView.stopStreamSucc();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Logger.e(TAG, "stopPush->failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }







    /**
     * 上报房间信息
     */
    public void notifyNewRoomInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject liveInfo = null;
                try {
                    liveInfo = new JSONObject();
                    liveInfo.put("token", MySelfInfo.getInstance().getToken());

                    JSONObject room = new JSONObject();
                    if (TextUtils.isEmpty(CurLiveInfo.getTitle())) {
                        room.put("title", mContext.getString(R.string.text_live_default_title));
                    } else {
                        room.put("title", CurLiveInfo.getTitle());
                    }
                    room.put("roomnum", MySelfInfo.getInstance().getMyRoomNum());
                    room.put("type", "live");
                    room.put("groupid", "" + CurLiveInfo.getRoomNum());
                    room.put("cover", CurLiveInfo.getCoverurl());
                    room.put("appid", Constants.SDK_APPID);
                    room.put("device", 1);
                    room.put("videotype", 0);
                    liveInfo.put("room", room);

                    JSONObject lbs = new JSONObject();
                    lbs.put("longitude", CurLiveInfo.getLong1());
                    lbs.put("latitude", CurLiveInfo.getLat1());
                    lbs.put("address", CurLiveInfo.getAddress());
                    liveInfo.put("lbs", lbs);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (liveInfo != null) {
                    UserServerHelper.getInstance().reporNewtRoomInfo(liveInfo.toString());
                }

            }
        }).start();
    }
   

    private void createRoom() {
        ILVLiveRoomOption hostOption = new ILVLiveRoomOption(MySelfInfo.getInstance().getId())
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_BSUPPORT)
                .controlRole(CurLiveInfo.getCurRole())
                .autoFocus(true)
                .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);

        //创建房间成功后会回调view的接口
        int ret = ILVLiveManager.getInstance().createRoom(MySelfInfo.getInstance().getMyRoomNum(), hostOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Logger.d(TAG, "ILVB-SXB|startEnterRoom->create room sucess");
                bCameraOn = true;
                bMicOn = true;
                if (null != mLiveView)
                    //创建成功回调view的enterRoomComplete
                    mLiveView.enterRoomComplete(MySelfInfo.getInstance().getIdStatus(), true);
                notifyNewRoomInfo();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
              Logger.d(TAG, "ILVB-SXB|createRoom->create room failed:" + module + "|" + errCode + "|" + errMsg);
                showToast("sendCmd->failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mLiveView) {
                   
                }
            }
        });
        
    }

    //加入房间
    private void joinRoom() {
        ILVLiveRoomOption memberOption = new ILVLiveRoomOption(CurLiveInfo.getHostID())
                .autoCamera(false)
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_BSUPPORT)
                .controlRole(MySelfInfo.getInstance().getGuestRole())
                .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)
                .autoMic(false);
        int ret = ILVLiveManager.getInstance().joinRoom(CurLiveInfo.getRoomNum(), memberOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Logger.d(TAG, "ILVB-Suixinbo|startEnterRoom->join room sucess");
                if (null != mLiveView)
                    mLiveView.enterRoomComplete(MySelfInfo.getInstance().getIdStatus(), true);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Logger.d(TAG, "ILVB-Suixinbo|startEnterRoom->join room failed:" + module + "|" + errCode + "|" + errMsg);
                Logger.d(TAG, "ILVB-SXB|createRoom->create room failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mLiveView) {
                                 }
            }
        });

        Logger.i(TAG, "joinLiveRoom startEnterRoom ");
    }

    private int sendCmd(final ILVCustomCmd cmd) {
        return ILVLiveManager.getInstance().sendCustomCmd(cmd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Logger.i(TAG, "sendCmd->success:" + cmd.getCmd() + "|" + cmd.getParam());
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
//                Toast.makeText(mContext, "sendCmd->failed:" + module + "|" + errCode + "|" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCustomMsg(int action, String param, String identifier, String nickname){
        Logger.d(TAG, "handleCustomMsg->action: "+action);
        if (null == mLiveView){
            return;
        }
        switch (action) {
            case Constants.AVIMCMD_MUlTI_HOST_INVITE:
                Logger.d(TAG, LogConstants.ACTION_VIEWER_SHOW + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + "receive invite message" +
                        LogConstants.DIV + "id " + identifier);
                mLiveView.showInviteDialog();
                break;
            case Constants.AVIMCMD_MUlTI_JOIN:
                Logger.i(TAG, "handleCustomMsg " + identifier);
                mLiveView.cancelInviteView(identifier);
                break;
            case Constants.AVIMCMD_MUlTI_REFUSE:
                mLiveView.cancelInviteView(identifier);
                showToast(identifier + " refuse !");
                break;
            case Constants.AVIMCMD_PRAISE:
                mLiveView.refreshThumbUp();
                break;
            case Constants.AVIMCMD_ENTERLIVE:
                mLiveView.memberJoin(identifier, nickname);
                break;

            case Constants.AVIMCMD_MULTI_CANCEL_INTERACT://主播关闭摄像头命令
                //如果是自己关闭Camera和Mic
                if (param.equals(MySelfInfo.getInstance().getId())) {//是自己
                    //TODO 被动下麦 下麦 下麦

                }
                //其他人关闭小窗口
                ILiveRoomManager.getInstance().getRoomView().closeUserView(param, AVView.VIDEO_SRC_TYPE_CAMERA,true);
                mLiveView.hideInviteDialog();
                mLiveView.changeCtrlView(false);
                break;
            case Constants.AVIMCMD_MULTI_HOST_CANCELINVITE:
                mLiveView.hideInviteDialog();
                break;
            case Constants.AVIMCMD_EXITLIVE:
                //startExitRoom();
                mLiveView.forceQuitRoom(mContext.getString(R.string.str_room_discuss));
                break;
            case ILVLiveConstants.ILVLIVE_CMD_LINKROOM_REQ:     // 跨房邀请
                mLiveView.linkRoomReq(identifier, nickname);
                break;
            case ILVLiveConstants.ILVLIVE_CMD_LINKROOM_ACCEPT:  // 接听
                mLiveView.linkRoomAccept(identifier, param);
                break;
            case ILVLiveConstants.ILVLIVE_CMD_LINKROOM_REFUSE:  // 拒绝
                showUserToast(identifier, R.string.str_link_refuse_tips);
                break;
            case ILVLiveConstants.ILVLIVE_CMD_LINKROOM_LIMIT:   // 达到上限
                showUserToast(identifier, R.string.str_link_limit);
                break;
            case Constants.AVIMCMD_HOST_BACK:
                mLiveView.hostBack(identifier, nickname);

            default:
                break;
        }
    }



}
