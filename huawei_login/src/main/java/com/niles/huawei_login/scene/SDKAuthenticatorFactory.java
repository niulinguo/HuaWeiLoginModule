package com.niles.huawei_login.scene;

import com.niles.huawei_login.scene.appvpn.AppVPNAuthenticator;
import com.niles.huawei_login.scene.l4vpn.L4VPNAuthenticator;
import com.niles.huawei_login.scene.sandbox.SandboxAuthenticator;

/**
 * Created by y90004712 on 7/10/2017.
 */

public class SDKAuthenticatorFactory {
    public static SDKAuthenticator getInstance() {
        switch (SDKConfiguration.getInstance().sdkMode) {
            case SDKConfiguration.SDK_MODE_L4VPN:
                return L4VPNAuthenticator.getInstance();
            case SDKConfiguration.SDK_MODE_APPVPN:
                return AppVPNAuthenticator.getInstance();
            case SDKConfiguration.SDK_MODE_SANDBOX:
                return SandboxAuthenticator.getInstance();
            default: {
                return null;
            }
        }
    }
}
