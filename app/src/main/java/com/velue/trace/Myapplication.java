package com.velue.trace;

import android.app.Application;

import com.lzy.okhttputils.OkHttpUtils;

/**
 * Created by win7 on 2017/2/23.
 * 描述:
 * 作者:小智 win7
 */

public class Myapplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpUtils.init(this);
    }
}
