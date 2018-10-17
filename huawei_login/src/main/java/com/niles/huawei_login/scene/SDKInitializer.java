package com.niles.huawei_login.scene;

import android.content.Context;
import android.os.Environment;

import com.huawei.anyoffice.sdk.SDKContext;
import com.huawei.anyoffice.sdk.SDKContextOption;
import com.niles.huawei_login.utils.AppContextUtil;

import java.io.File;

/**
 * SDK初始化封装类。可以视具体情况对相关参数进行修改。
 */

public class SDKInitializer {
    private SDKContextOption ctxOption = new SDKContextOption();

    private String logPath;

    private int logLevel;

    private SDKInitializer() {
    }

    public static SDKInitializer getInstance() {
        return SingletonWrapper.singleton;
    }

    public SDKInitializer setUserName(String userName) {
        ctxOption.setUserName(userName);
        return this;
    }

    public boolean init(Context context) {
        ctxOption.setContext(context);

        // 初始化工作目录与日志目录
        String processName = AppContextUtil.getProcessName(ctxOption.getContext());
        String workPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + processName;
        ctxOption.setWorkPath(workPath);
        logPath = workPath + "/log";
        logLevel = com.huawei.anyoffice.sdk.log.Log.LOG_TYPE_INFO;

        File workPathDir = new File(ctxOption.getWorkPath());
        if (!workPathDir.exists()) {
            workPathDir.mkdir();
        }
        SDKContext.getInstance().setLogParam(logPath, logLevel);

        //使能全局水印，默认不开启
        //ctxOption.setEnableWaterMark(true);

        //使能MDM检查，默认不开启
        //ctxOption.setEnableSDKMdmCheck(true);

        //使能KMC密钥管理中心，默认不开启
        //ctxOption.setUseKMC(true);

        return SDKContext.getInstance().init(ctxOption);
    }

    private static class SingletonWrapper {
        public static SDKInitializer singleton = new SDKInitializer();
    }
}
