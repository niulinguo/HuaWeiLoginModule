package com.niles.huaweiloginmodule;

import android.app.Application;

import com.niles.huawei_login.LoginManager;

/**
 * Created by Niles
 * Date 2018/10/9 21:25
 * Email niulinguo@163.com
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LoginManager.init(this);
    }
}
