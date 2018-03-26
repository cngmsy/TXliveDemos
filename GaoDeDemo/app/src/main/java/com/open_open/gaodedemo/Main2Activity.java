package com.open_open.gaodedemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
    }

    private void initView() {
        Button button_bai = (Button) findViewById(R.id.button_bai);
        Button button_gao = (Button) findViewById(R.id.button_gao);
        Button button_gu = (Button) findViewById(R.id.button_gu);
        button_bai.setOnClickListener(this);
        button_gao.setOnClickListener(this);
        button_gu.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_bai:
                if(isAvilible(Main2Activity.this,"com.baidu.BaiduMap")){//传入指定应用包名

                    try {
                          intent = Intent.getIntent("intent://map/direction?#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                        //"origin=latlng:"+"34.264642646862,108.95108518068&" +   //起点  此处不传值默认选择当前位置
//终点
//导航路线方式
//
                     /*   intent = Intent.getIntent("intent://map/direction?" +
                                //"origin=latlng:"+"34.264642646862,108.95108518068&" +   //起点  此处不传值默认选择当前位置
                                "destination=latlng:"+34.264642646862+","+108.95108518068+"|name:我的目的地"+        //终点
                                "&mode=driving&" +          //导航路线方式
                                "region=北京" +           //
                                "&src=慧医#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");*/
                        startActivity(intent); //启动调用
                    } catch (URISyntaxException e) {
                        System.out.println(e.getMessage());
                    }
                }else{//未安装
                    //market为路径，id为包名
                    //显示手机上所有的market商店
                    Toast.makeText(Main2Activity.this, "您尚未安装百度地图", Toast.LENGTH_LONG).show();
                    Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
            case R.id.button_gao:
                if (isAvilible(Main2Activity.this, "com.autonavi.minimap")) {
                    try{
                        intent = Intent.getIntent("androidamap://viewGeo?");

                        intent.setPackage("com.autonavi.minimap");// pkg=com.autonavi.minimap
                        intent.addCategory("android.intent.category.DEFAULT");

                        // intent = Intent.getIntent("androidamap://navi?sourceApplication=慧医&poiname=我的目的地&lat="+34.264642646862+"&lon="+108.95108518068+"&dev=0");
                      //  startActivity(intent);
                       // intent = Intent.getIntent("androidamap://navi?sourceApplication=慧医&poiname=我的目的地&lat="+34.264642646862+"&lon="+108.95108518068+"&dev=0");
                        startActivity(intent);
                    } catch (URISyntaxException e)
                    {e.printStackTrace(); }
                }else{
                    Toast.makeText(Main2Activity.this, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                    Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
            case R.id.button_gu:
                if (isAvilible(Main2Activity.this,"com.google.android.apps.maps")) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+34.264642646862+","+108.95108518068 +", + Sydney +Australia");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }else {
                    Toast.makeText(Main2Activity.this, "您尚未安装谷歌地图", Toast.LENGTH_LONG).show();

                    Uri uri = Uri.parse("market://details?id=com.google.android.apps.maps");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);  }
                break;
        }
    }
    /*  * 检查手机上是否安装了指定的软件
      * @param context
      * @param packageName：应用包名
      * @return
              */
    public static boolean isAvilible(Context context, String packageName){
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }
}