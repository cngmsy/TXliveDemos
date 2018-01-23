package com.open_open.livedemos.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.open_open.livedemos.R;


/**
 * 视频和照片输入页面
 */
public class FragmentProfile extends Fragment  {
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


}
