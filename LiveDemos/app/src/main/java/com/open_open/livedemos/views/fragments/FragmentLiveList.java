package com.open_open.livedemos.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.open_open.livedemos.R;


/**
 * 直播列表页面   最新直播的界面
 */
public class FragmentLiveList extends Fragment  {
    private static final String TAG = "FragmentLiveList";
    private ListView mLiveList;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int reqCode = 0;

    public FragmentLiveList() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //最新直播的界面
        View view = inflater.inflate(R.layout.liveframent_layout, container, false);

        return view;
    }


}
