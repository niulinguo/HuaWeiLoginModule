package com.niles.huawei_login;

import android.app.Application;
import android.util.Log;

import com.huawei.anyoffice.sdk.SDKContext;
import com.huawei.anyoffice.sdk.SDKContextOption;
import com.huawei.anyoffice.sdk.network.NetChangeCallback;
import com.huawei.anyoffice.sdk.network.NetStatusManager;

import java.io.File;

/**
 * Created by Niles
 * Date 2018/10/9 21:25
 * Email niulinguo@163.com
 */
public class LoginManager {

    private static final String TAG = "LoginManager";

    private static final NetChangeCallback sNetChangeCallback = new NetChangeCallback() {
        @Override
        public void onNetChanged(int oldState, int newState, int errorCode) {
            Log.e(TAG, String.format("oldState:%d, newState:%d, errorCode:%d, ", oldState, newState, errorCode));
        }
    };

    public static void init(Application app) {
        //SDK初始化设置
        SDKContext ctx = SDKContext.getInstance();
        //设置网络回调，用于接收隧道状态变化通知
        NetStatusManager.getInstance().setNetChangeCallback(sNetChangeCallback);
        //设置SDK日志和输出目录
        String sdkLogPath = new File(app.getFilesDir(), "log").getAbsolutePath();
        SDKContext.getInstance().setLogParam(sdkLogPath, com.huawei.anyoffice.sdk.log.Log.LOG_TYPE_DEBUG);
        SDKContextOption option = new SDKContextOption();
        //设置SDK工作目录
        String workpath = new File(app.getFilesDir(), "anyoffice").getAbsolutePath();
        option.setWorkPath(workpath);
        option.setContext(app);
        //执行SDK初始化
        boolean ret = ctx.init(option);
        Log.e(TAG, "AnyOffice Init Result:" + ret);
    }
}
