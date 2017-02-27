package com.velue.trace;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn3)
    Button mBtn3;
    private LocationClient locationClient = null;
    private static final int UPDATE_TIME = 5000;
    private static int LOCATION_COUTNS = 0;
    double lon, lat;
    String mLat, mLon;
    String startTime;
    String endTime, serviveTime;
    public SharedPreferences share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        share = getSharedPreferences("usersId", MODE_PRIVATE);

        locationClient = new LocationClient(this);
        // 设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 是否打开GPS
        option.setCoorType("bd09ll"); // 设置返回值的坐标类型。
        option.setPriority(LocationClientOption.NetWorkFirst); // 设置定位优先级
        option.setProdName("LocationDemo"); // 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(UPDATE_TIME); // 设置定时定位的时间间隔。单位毫秒
        locationClient.setLocOption(option);
        // 注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null) {
                    return;
                }
                StringBuffer sb = new StringBuffer(256);

                sb.append("\nLatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nLontitude : ");
                sb.append(location.getLongitude());
                LOCATION_COUTNS++;
                sb.append("\n检查位置更新次数：");
                sb.append(String.valueOf(LOCATION_COUTNS));
                lon = location.getLongitude();
                lat = location.getLatitude();
                mLat = Double.toString(lat);
                mLon = Double.toString(lon);
//                shuju.setText(sb.toString());
                Toast.makeText(MainActivity.this, "Latitude" + sb.toString(), Toast.LENGTH_SHORT).show();
                sendTrack(); // 该方法调用鹰眼轨迹上传的接口。
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        });


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationClient.start();
                locationClient.requestLocation();
                serviveTime = getTime();
                SharedPreferences mySharedPreferences = getSharedPreferences(
                        "usersId", MODE_PRIVATE);
                // 实例化SharedPreferences.Editor对象（第二步）
                SharedPreferences.Editor Ieditor = mySharedPreferences
                        .edit();
                // 用putString的方法保存数据
                Ieditor.putString("uname", serviveTime);
                // 提交当前数据
                Ieditor.commit();
                Toast.makeText(MainActivity.this, "开始", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationClient.stop();
                end();
                Toast.makeText(MainActivity.this, "结束", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendTrack() {
        startTime = getTime();
        OkHttpUtils.post("http://api.map.baidu.com/trace/v2/track/addpoint")
                .params("ak", "")
                .params("service_id", "132448")
                .params("latitude", mLat)
                .params("longitude", mLon)
                .params("coord_type", "3")
                .params("loc_time", startTime)
                .params("mcode", "")
                .params("entity_name", "hhahah")
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                        System.out.println("----------------------START-------------" + s);
                    }
                });
    }

    private void end() {
        endTime = getTime();
        OkHttpUtils.get("http://api.map.baidu.com/trace/v2/track/gethistory")
                .params("ak", "")
                .params("service_id", "132448")
                .params("end_time", endTime)
                .params("mcode", "")
                .params("start_time", serviveTime)
                .params("entity_name", "hhahah")
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                        System.out.println("----------------------END-------------" + s);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
            locationClient = null;
        }
    }

    //获取时间戳
    public String getTime() {
        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        String str = String.valueOf(time);
        return str;
    }

    @OnClick(R.id.btn3)
    public void onClick() {
        Toast.makeText(this, "ssssssssssss", Toast.LENGTH_SHORT).show();
    }
}
