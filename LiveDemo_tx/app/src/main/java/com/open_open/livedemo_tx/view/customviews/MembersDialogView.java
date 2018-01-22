package com.open_open.livedemo_tx.view.customviews;

import com.open_open.livedemo_tx.presenter.viewinterface.MvpView;
import com.tencent.imcore.MemberInfo;

import java.util.ArrayList;


/**
 * 成员列表回调
 */
public interface MembersDialogView extends MvpView {

    void showMembersList(ArrayList<MemberInfo> data);

}
