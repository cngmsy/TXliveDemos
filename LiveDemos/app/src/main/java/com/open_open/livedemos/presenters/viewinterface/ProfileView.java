package com.open_open.livedemos.presenters.viewinterface;

import com.tencent.TIMUserProfile;

import java.util.List;

/**
 * 个人资料页
 */
public interface ProfileView {
    void updateProfileInfo(TIMUserProfile profile);

    void updateUserInfo(int requestCode, List<TIMUserProfile> profiles);
}
