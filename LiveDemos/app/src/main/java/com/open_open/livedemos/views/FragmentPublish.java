package com.open_open.livedemos.views;

/******************************************
 * 类名称：FragmentPublish
 * 类描述：
 *
 * @version: 1.0
 * @author: chj
 * @time: 2018/1/23
 * @email: chj294671171@126.com
 * @github: https://github.com/cngmsy
 ******************************************/


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.open_open.livedemos.R;

/**
 * 视频和照片输入页面
 */
public class FragmentPublish extends Fragment implements View.OnClickListener {
    private static final String TAG = "FragmentLiveList";
    private ImageButton mBtn_videoCreate, mBtn_JoinRoom;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.publishfragment_layout, container, false);

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

    @Override
    public void onClick(View view) {
    }


}
