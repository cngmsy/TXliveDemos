package com.open_open.livedemo_tx.view.customviews;


import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.open_open.livedemo_tx.R;
import com.tencent.TIMManager;
import com.tencent.av.sdk.AVContext;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveManager;



/**
 * 视频和照片输入页面
 */
public class FragmentProfile extends Fragment {
    private static final String TAG = "FragmentLiveList";
    private final String beautyTypes[] = new String[]{"内置美颜", "插件美颜"};
    private TextView mProfileName, mProfileId;
    private ImageView mAvatar, mEditProfile;



    public FragmentProfile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profileframent_layout, container, false);


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }




    private void showSDKVersion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("IM SDK: " + TIMManager.getInstance().getVersion() + "\r\n"
                + "AV SDK: " + AVContext.getVersion()+ "\r\n"
                + "Live SDK: " + ILVLiveManager.getInstance().getVersion() + "\r\n"
                + "ILiveSDK: " + ILiveSDK.getInstance().getVersion());
        builder.show();
    }

    private String getAppVersion() {
        PackageManager packageManager = getActivity().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String version = "";
        try {
            packInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ;
        return version;
    }


}
