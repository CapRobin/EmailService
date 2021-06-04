package com.mail.utils;

import android.app.Application;

/**
 * Copyright © CapRobin
 *
 * Name：MyApplication
 * Describe：程序入口
 * Date：2018-08-28 17:13:12
 * Author: CapRobin@yeah.net
 *
 */
public class MyApplication extends Application {
    public static Preferences preference;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化本地存储文件
        preference = Preferences.instance(getApplicationContext());
    }
}
