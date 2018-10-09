package com.niles.huaweiloginmodule;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.huawei.anyoffice.sdk.SDKContext;
import com.huawei.anyoffice.sdk.SDKContextOption;
import com.huawei.anyoffice.sdk.login.LoginAgent;
import com.huawei.anyoffice.sdk.login.LoginParam;
import com.huawei.anyoffice.sdk.network.NetChangeCallback;
import com.huawei.anyoffice.sdk.network.NetStatusManager;

import java.net.InetSocketAddress;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String GATEWAY = "58.211.206.173";
    private static final String USERNAME = "hello";
    private static final String PASSWORD = "hello@123";

    private NetChangeCallback mNetChangeCallback = new NetChangeCallback() {
        @Override
        public void onNetChanged(int oldState, int newState, int errorCode) {
            Log.e(TAG, String.format("oldState:%d, newState:%d, errorCode:%d, ", oldState, newState, errorCode));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //SDK初始化设置
        SDKContext ctx = SDKContext.getInstance();
        //设置网络回调，用于接收隧道状态变化通知
        NetStatusManager.getInstance().setNetChangeCallback(mNetChangeCallback);
        //设置SDK日志和输出目录
        String sdkLogPath = "/data/data/" + getPackageName();
        SDKContext.getInstance().setLogParam(sdkLogPath, com.huawei.anyoffice.sdk.log.Log.LOG_TYPE_DEBUG);
        SDKContextOption option = new SDKContextOption();
        //设置SDK工作目录
        String workpath = "/data/data/" + getPackageName() + "/anyoffice";
        option.setWorkPath(workpath);
        option.setContext(MainActivity.this);
        //执行SDK初始化
        boolean ret = ctx.init(option);
        Log.e(TAG, "AnyOffice Init Result:" + ret);

    }

    public void onButtonClicked(View view) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Object, Object, Integer> authTask = new AsyncTask<Object, Object, Integer>() {
            @Override
            protected Integer doInBackground(Object... params) {

                //网关认证
                LoginParam loginParam = new LoginParam();
                //初始化登录用户信息。
                LoginParam.UserInfo userInfo = loginParam.new UserInfo();
                userInfo.userName = USERNAME;
                userInfo.password = PASSWORD;
                loginParam.setUserInfo(userInfo);
                loginParam.setInternetAddress(new InetSocketAddress(GATEWAY, 443));
                //设置关闭服务器证书校验
                loginParam.setAuthGateway(false);
                //设置为前台自动登录
                loginParam.setLoginBackground(false);
                loginParam.setAutoLoginType(LoginParam.AutoLoginType.auto_login_enable);
                //执行网关认证
                int result = LoginAgent.getInstance().loginSync(MainActivity.this, loginParam);
                Log.e(TAG, "AnyOffice login Result:" + result);
                return result;
            }

            @Override
            protected void onPostExecute(Integer result) {
                Toast.makeText(MainActivity.this, "AnyOffice Auth Result: " + result, Toast.LENGTH_LONG).show();
            }
        };
        authTask.execute(new Object());
    }
}
