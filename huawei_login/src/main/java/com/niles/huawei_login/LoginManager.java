package com.niles.huawei_login;

import android.content.Context;

import com.huawei.anyoffice.sdk.log.Log;
import com.huawei.esdk.byod.anyoffice.AnyOfficeSDK;
import com.huawei.esdk.byod.anyoffice.AnyOfficeUserInfo;
import com.niles.huawei_login.scene.SDKAuthenticator;
import com.niles.huawei_login.scene.SDKAuthenticatorFactory;
import com.niles.huawei_login.scene.SDKConfiguration;
import com.niles.huawei_login.scene.SDKInitializer;

/**
 * Created by Niles
 * Date 2018/10/9 21:25
 * Email niulinguo@163.com
 */
public class LoginManager {

    private static final String TAG = "LoginManager";
    private static int authStatus = 0;
    private static int appVpnStatus = IAppVpnStatusObserver.VPN_STATUS_CONNECTED;
    private static int sdkAuthResult = 0;

    private static void authenticationComplete(LoginConfig loginConfig) {
        int tmpResult;
        if (sdkAuthResult == 0 && appVpnStatus == IAppVpnStatusObserver.VPN_STATUS_CONNECTED) {
            tmpResult = 0;
        } else if (sdkAuthResult != 0) {
            Log.e(TAG, "sdk authentication error: " + sdkAuthResult);
            tmpResult = sdkAuthResult;
        } else {
            Log.e(TAG, "app vpn error: " + appVpnStatus);
            tmpResult = appVpnStatus - 1;
        }

        final int result = tmpResult;
        if (result == 0) {
            if (SDKConfiguration.getInstance().sdkMode == SDKConfiguration.SDK_MODE_L4VPN) {
                String tunnelIp = AnyOfficeSDK.getTunnelIPAddress();
                AnyOfficeUserInfo userInfo = AnyOfficeSDK.getUserInfo();
                Log.e(TAG, userInfo.toString());
            }
        }
        loginConfig.getAuthResultListener().onResult(result);
    }

    public static void doAuthentication(Context context, LoginConfig loginConfig) {

        SDKInitializer initializer = SDKInitializer.getInstance();
        initializer.init(context);

        AnyOfficeUserInfo userInfo = AnyOfficeSDK.getUserInfo();
        if (userInfo != null && loginConfig.getUsername().equals(userInfo.getUsername())) {
            loginConfig.getAuthResultListener().onResult(0);
            return;
        }

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
