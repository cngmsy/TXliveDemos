package com.open_open.livedemos.presenters.viewinterface;


import com.tencent.ilivesdk.data.ILivePushRes;

import java.util.List;

/**
 *  直播界面回调
 */
public interface LiveView extends MvpView {

    void enterRoomComplete(int id_status, boolean succ);


    void showInviteDialog();

    void refreshText(String text, String name);

    void refreshThumbUp();

    void changeCtrlView(boolean bVideoMember);

    boolean showInviteView(String id);

    void cancelInviteView(String id);

    void cancelMemberView(String id);

    void memberJoin(String id, String name);

    void hideInviteDialog();

    void pushStreamSucc(ILivePushRes streamRes);

    void stopStreamSucc();

    void startRecordCallback(boolean isSucc);

    void stopRecordCallback(boolean isSucc, List<String> files);

    void hostLeave(String id, String name);

    void hostBack(String id, String name);


    void linkRoomReq(String id, String name);

    void linkRoomAccept(String id, String strRoomId);

    void forceQuitRoom(String strMessage);
}
