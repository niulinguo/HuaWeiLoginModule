package com.niles.huawei_login.scene.l4vpn;

import android.content.Context;

import com.huawei.anyoffice.sdk.login.LoginAgent;
import com.huawei.anyoffice.sdk.network.NetChangeCallback;
import com.huawei.anyoffice.sdk.network.NetStatusManager;
import com.niles.huawei_login.scene.SDKAuthenticator;
import com.niles.huawei_login.utils.AppContextUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SDK认证封装子类。
 * 该类使用场景：企业应用集成SDK使用L4VPN，同时使用单点登录、沙箱、防拷贝黏贴等特性。
 * 这种场景中，需要通过SDK认证，同时需要在企业应用中创建VPN隧道。
 */

public class L4VPNAuthenticator extends SDKAuthenticator implements NetChangeCallback {
    private L4VPNAuthenticator() {
        super();
    }

    public static L4VPNAuthenticator getInstance() {
        return SingletonWrapper.singleton;
    }

    /**
     * @param context 界面上下文，当缺少账号信息或者网关地址信息时，需要在此上下文基础上弹出界面让用户输入
     * @return 认证返回码，0为认证成功，否则为错误码
     */
    @Override
    public int authenticate(final Context context) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String processName = AppContextUtil.getProcessName(context);
                loginParam.setServiceType(processName);
                loginParam.setAppName(processName);

                // TODO: 测试时关闭网关证书校验。正式环境需要打开，以避免中间人攻击。
                loginParam.setAuthGateway(false);

                NetStatusManager.getInstance().setNetChangeCallback(L4VPNAuthenticator.this);
                int result = LoginAgent.getInstance().loginSync(context, loginParam);
                if (0 != result && null != listener) {
                    listener.onAuthenticationResult(result);
                }
            }
        });

        return 0;
    }

    @Override
    public void onNetChanged(int oldStatus, int newStatus, int errCode) {
        if (null == listener)
            return;

        if (newStatus == NetStatusManager.NET_STATUS_ONLINE) {
            listener.onAuthenticationResult(0);
        } else if (newStatus == NetStatusManager.NET_STATUS_OFFLINE) {
            listener.onAuthenticationResult(errCode);
        }
    }

    private static class SingletonWrapper {
        public static L4VPNAuthenticator singleton = new L4VPNAuthenticator();
    }
}
