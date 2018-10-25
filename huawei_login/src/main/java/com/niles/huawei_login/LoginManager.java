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
//    private static String httpRequest;
//    private static String httpResponse;

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
//                String tunnelIp = AnyOfficeSDK.getTunnelIPAddress();
                AnyOfficeUserInfo userInfo = AnyOfficeSDK.getUserInfo();
                Log.e(TAG, userInfo.toString());
//                doLogin("http://36.10.48.98", "8888", userInfo.getUsername(), userInfo.getPassword());
//                doLoginThroughURLConnection();
            }
        }
        loginConfig.getAuthResultListener().onResult(result);
    }

    public static void doAuthentication(Context context, LoginConfig loginConfig) {

        SDKInitializer initializer = SDKInitializer.getInstance();
        initializer.init(context);

//        AnyOfficeUserInfo userInfo = AnyOfficeSDK.getUserInfo();
//        if (userInfo != null && loginConfig.getUsername().equals(userInfo.getUsername())) {
//            String tunnelIp = AnyOfficeSDK.getTunnelIPAddress();
//            Log.e(TAG, userInfo.toString());
//            doLogin(loginConfig.getGateway(), "443", userInfo.getUsername(), userInfo.getPassword());
//            loginConfig.getAuthResultListener().onResult(0);
//            return;
//        }

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

//    /**
//     * get response from connection
//     *
//     * @param conn HttpURLConnection
//     */
//    private static String getResponseFronUrlConnection(HttpURLConnection conn)
//            throws Exception {
//        StringBuffer builder = new StringBuffer();
//        InputStream stream = conn.getInputStream();
//        builder.append(FileUtil.read(stream));
//        return builder.toString();
//    }

//    private static void doLoginThroughURLConnection() {
//        android.util.Log.i(TAG, "in urlConnectionLogin.");
//        final String urlInfo = "http://36.10.48.98:8888/pandian/login/signIn";
//        String username = "uf01";
//        String password = "uf01";
//        final List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
//        params.add(new BasicNameValuePair("username", username));
//        params.add(new BasicNameValuePair("password", password));
//        @SuppressLint("StaticFieldLeak") AsyncTask<Object, Integer, Boolean> loginTask = new AsyncTask<Object, Integer, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Object... paramVarArgs) {
//                boolean result = true;
//                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                });
//
////                SvnWebViewProxy.getInstance().setExceptionAddressList(true, "172.22.*");
//                URLConnectionFactoryHelper.setURLStreamHandlerFactory();
//                try {
//                    //init the request data
//                    URL url = new URL(urlInfo);
//                    HttpURLConnection connection = (HttpURLConnection) url
//                            .openConnection();
//                    connection.setDoOutput(true);
//                    connection.setDoInput(true);
//                    connection.setRequestMethod("POST");
//                    connection
//                            .setFixedLengthStreamingMode(null != params ? URLEncodedUtils
//                                    .format(params, "UTF-8").length() : 0);
//                    connection.setUseCaches(false); // Post 请求不能使用缓存，因为要保证post数据安全
//                    connection.setConnectTimeout(5000);// （单位：毫秒）jdk
//                    connection.setReadTimeout(5000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
//                    android.util.Log.d(TAG, "url = " + url.toString());
//                    connection.connect();
//                    String param = URLEncodedUtils.format(params, "UTF-8");
//                    connection.getOutputStream().write(param.getBytes(), 0,
//                            param.getBytes().length);
//                    connection.getOutputStream().flush();
//                    connection.getOutputStream().close();
//
//                    //get request and response
//                    httpRequest = connection.getHeaderFields().toString();
//                    httpResponse = getResponseFronUrlConnection(connection);
//                    android.util.Log.i(TAG, "urlConnection login success.");
//                } catch (Exception e) {
//                    result = false;
//                    e.printStackTrace();
//                    android.util.Log.e(TAG,
//                            "urlConnection login false.resCode = "
//                                    + e.getMessage());
//                }
//                return result;
//            }
//
//            @Override
//            protected void onPostExecute(Boolean result) {
//                handleLoginResult(result, httpRequest, httpResponse);
//            }
//        };
//        loginTask.execute(new Object());
//    }

//    private static void handleLoginResult(Boolean status, String request,
//                                          String response) {
//        if (status) {
//            ToastUtils.showLong(response);
//        }
//    }
}
