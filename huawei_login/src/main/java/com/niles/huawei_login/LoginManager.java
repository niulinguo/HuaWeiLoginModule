package com.niles.huawei_login;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.huawei.anyoffice.sdk.SDKContext;
import com.huawei.anyoffice.sdk.SDKContextOption;
import com.huawei.anyoffice.sdk.network.NetChangeCallback;
import com.huawei.anyoffice.sdk.network.NetStatusManager;
import com.huawei.esdk.byod.anyoffice.AnyOfficeSDK;
import com.niles.huawei_login.scene.SDKAuthenticator;
import com.niles.huawei_login.scene.SDKAuthenticatorFactory;
import com.niles.huawei_login.scene.SDKConfiguration;
import com.niles.huawei_login.scene.SDKInitializer;

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
    private static int authStatus = 0;
    private static int appVpnStatus = IAppVpnStatusObserver.VPN_STATUS_CONNECTED;
    private static int sdkAuthResult = 0;

    private static void authenticationComplete(LoginConfig loginConfig) {
        int tmpResult;
        if (sdkAuthResult == 0 && appVpnStatus == IAppVpnStatusObserver.VPN_STATUS_CONNECTED) {
            tmpResult = 0;
        } else if (sdkAuthResult != 0) {
            com.huawei.anyoffice.sdk.log.Log.e(TAG, "sdk authentication error: " + sdkAuthResult);
            tmpResult = sdkAuthResult;
        } else {
            com.huawei.anyoffice.sdk.log.Log.e(TAG, "app vpn error: " + appVpnStatus);
            tmpResult = appVpnStatus - 1;
        }

        final int result = tmpResult;
        if (result == 0) {
            if (SDKConfiguration.getInstance().sdkMode == SDKConfiguration.SDK_MODE_L4VPN) {
                String tunnelIp = AnyOfficeSDK.getTunnelIPAddress();
            }
        }
        loginConfig.getAuthResultListener().onResult(result);
    }

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

    public static void doAuthentication(Context context, LoginConfig loginConfig) {
        SDKInitializer initializer = SDKInitializer.getInstance();
        initializer.init(context);

        authStatus = 0x2;
        if (SDKConfiguration.getInstance().sdkMode == SDKConfiguration.SDK_MODE_APPVPN) {
            // app vpn模式下，先等待app vpn上线在进行认证，保证能获取到app vpn使用的网关ip。
            authStatus = 0x0;
            throw new RuntimeException("暂不支持");
        } else {
            doSDKAuthentication(context, loginConfig);
        }
    }

    private static void doSDKAuthentication(Context context, final LoginConfig loginConfig) {
        SDKAuthenticator authenticator = SDKAuthenticatorFactory.getInstance();
        assert authenticator != null;
        authenticator.setAuthInBackground(loginConfig.isAuthInBackground());
        authenticator.setAccount(loginConfig.getUsername(), loginConfig.getPassword());
        authenticator.setGatewayAddress(loginConfig.getGateway());
        authenticator.setListener(new SDKAuthenticator.IAuthenticationListener() {
            @Override
            public int onAuthenticationResult(int result) {
                authStatus |= 0x1;
                sdkAuthResult = result;
                if ((authStatus & 0x3) == 0x3) {
                    authenticationComplete(loginConfig);
                }
                return 0;
            }
        });
        authenticator.authenticate(context);
    }
}
