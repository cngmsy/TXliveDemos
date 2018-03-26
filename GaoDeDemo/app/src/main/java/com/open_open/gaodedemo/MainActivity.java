package com.open_open.gaodedemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * 81:77:2C:60:0F:16:8D:6F:9C:B3:9B:C3:2C:83:74:18:F3:7E:35:13
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private MapView mapView;
    private AMap aMap;
    private Button btns;
    private LatLng latLng;
    private Intent intent;

    //声明定位回调监听器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
         /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
      /*  //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
        // MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        if (aMap == null) {
            aMap = mapView.getMap();
        }


        checkPromi();
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
//设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);


        AMapLocationClientOption option = new AMapLocationClientOption();


//设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        *//**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         *//*
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            // mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }*/
    }
/*
    private void checkPromi() {
        int permission0 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permission1 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permission2 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE);
        int permission3 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission0 != PackageManager.PERMISSION_GRANTED | permission3 != PackageManager.PERMISSION_GRANTED | permission1 != PackageManager.PERMISSION_GRANTED | permission2 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );

            //startNative_Gaode(MainActivity.this);
        }
    }


    //可以通过类implement方式实现AMapLocationListener接口，也可以通过创造接口类对象的方法实现
//以下为后者的举例：
    AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {

                    int type = amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    Double jingdu = amapLocation.getLatitude();//获取纬度
                    Double weidu = amapLocation.getLongitude();//获取经度
                    float jindu = amapLocation.getAccuracy();//获取精度信息
                    String address = amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    String country = amapLocation.getCountry();//国家信息
                    String prov = amapLocation.getProvince();//省信息
                    String cityMes = amapLocation.getCity();//城市信息
                    String cityQue = amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码
                    amapLocation.getAoiName();//获取当前定位点的AOI信息
                    amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                    amapLocation.getFloor();//获取当前室内定位的楼层
                    amapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
//获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    String ss = df.format(date);

                    String resutl = jindu + "" + weidu + "" + address + country + prov + cityMes + cityQue + "定位时间";
                    Toast.makeText(MainActivity.this, resutl, Toast.LENGTH_SHORT).show();
//可在其中解析amapLocation获取相应内容。

                    latLng = new LatLng(jingdu, weidu);


                    changeCamera(
                            CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                    latLng, 18, 30, 30)));
                    aMap.clear();
                    aMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    System.out.println("========AmapError" + "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };*/


    /**
     * 开发 > URI API > Android
     * 跳转到百度地图
     *
     * @param context
     */
 /*   public static void startNative_Baidu(Context context) {
        if (loc1 == null || loc2 == null) {
            return;
        }
       if (loc1.getAddress() == null || "".equals(loc1.getAddress())) {
            loc1.setAddress("我的位置");
        }
        if (loc2.getAddress() == null || "".equals(loc2.getAddress())) {
            loc2.setAddress("目的地");
        }
        try {

//            Intent intent = Intent.getIntent("intent://map/direction?origin=latlng:" + loc1.getStringLatLng() + "|name:" + loc1.getAddress() + "&destination=latlng:" + loc2.getStringLatLng() + "|name:" + loc2.getAddress() + "&mode=transit&src=某某公司#Intent;" + "scheme=bdapp;package=com.baidu.BaiduMap;end");

            //Intent intent = Intent.getIntent("intent://map/direction?origin=|name:" + loc1.getAddress() + "&destination=|name:" + loc2.getAddress() + "&mode=transit&src=某某公司#Intent;" + "scheme=bdapp;package=com.baidu.BaiduMap;end");
            Intent intent = Intent.getIntent("intent://map/direction?origin=|name:" + "&destination=|name:" + "北京天安门" + "&mode=transit&src=某某公司#Intent;" + "scheme=bdapp;package=com.baidu.BaiduMap;end");

            //起点  此处不传值默认选择当前位置
            // Intent intent = Intent.getIntent("intent://map/direction?destination=|name:" + "&mode=transit&src=某某公司#Intent;" + "scheme=bdapp;package=com.baidu.BaiduMap;end");

            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "地址解析错误", Toast.LENGTH_SHORT).show();
        }


    }
*/

    /**
     * 开发 > URI API > Android
     * 调起高德地图
     *
     * @param context
     */
   /* public void startNative_Gaode(Context context) {

        if (isAvilible(MainActivity.this, "com.autonavi.minimap")) {
            try {
                //"origin=latlng:"+"34.264642646862,108.95108518068&" +   //起点  此处不传值默认选择当前位置
                //  intent = Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&destination=大雁塔&mode=driving®ion=西安&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                intent = Intent.getIntent("androidamap://viewGeo?");

                intent.setPackage("com.autonavi.minimap");// pkg=com.autonavi.minimap
                intent.addCategory("android.intent.category.DEFAULT");

                // intent = Intent.getIntent("androidamap://navi?sourceApplication=慧医&poiname=我的目的地&lat="+34.264642646862+"&lon="+108.95108518068+"&dev=0");
                startActivity(intent);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MainActivity.this, "您尚未安装高德地图", Toast.LENGTH_LONG).show();
            Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
            intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }*/


    /*  * 检查手机上是否安装了指定的软件
      * @param context
      * @param packageName：应用包名
      * @return
              */
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update) {

        aMap.moveCamera(update);

    }

    private void initView() {
        btns = (Button) findViewById(R.id.btns);

        btns.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btns:
               // startNative_Gaode(MainActivity.this);
                // startNative_Baidu(MainActivity.this);
                startActivity(new Intent(this,Main2Activity.class));
                break;
        }
    }
}
