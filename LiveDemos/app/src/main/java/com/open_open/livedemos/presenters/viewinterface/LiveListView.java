package com.open_open.livedemos.presenters.viewinterface;

import com.open_open.livedemo_tx.presenter.UserServerHelper;
import com.open_open.livedemo_tx.utils.RoomInfoJson;

import java.util.ArrayList;


/**
 *  列表页面回调
 */
public interface LiveListView extends MvpView{


    void showRoomList(UserServerHelper.RequestBackInfo result, ArrayList<RoomInfoJson> roomlist);
}
