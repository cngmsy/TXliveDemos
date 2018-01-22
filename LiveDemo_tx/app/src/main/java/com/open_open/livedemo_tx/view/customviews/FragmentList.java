package com.open_open.livedemo_tx.view.customviews;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.open_open.livedemo_tx.R;


/**
 * Created by tencent on 2016/12/22.
 */
public class FragmentList extends Fragment implements TabHost.OnTabChangeListener {
    private FragmentTabHost tabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);


        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        tabHost.setCurrentTabByTag(tabId);

    }




}
