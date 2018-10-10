package com.niles.huaweiloginmodule;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.anyoffice.sdk.login.LoginAgent;
import com.huawei.anyoffice.sdk.login.LoginParam;

import java.net.InetSocketAddress;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String GATEWAY = "218.4.33.66";
    //    private static final int PORT = 28080;
    private static final String PORT = "443";
    private static final String USERNAME = "eSDK_WEIXIN_516875";
    private static final String PASSWORD = "7FqhPy9f1I@";

    private EditText mGatewayEditView;
    private EditText mPortEditView;
    private EditText mUsernameEditView;
    private EditText mPasswordEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGatewayEditView = findViewById(R.id.et_gateway);
        mGatewayEditView.setText(GATEWAY);
        mPortEditView = findViewById(R.id.et_port);
        mPortEditView.setText(PORT);
        mUsernameEditView = findViewById(R.id.et_username);
        mUsernameEditView.setText(USERNAME);
        mPasswordEditView = findViewById(R.id.et_password);
        mPasswordEditView.setText(PASSWORD);
    }

    public void onButtonClicked(View view) {

        final String gateway = mGatewayEditView.getText().toString();

        if (TextUtils.isEmpty(gateway)) {
            Toast.makeText(this, mGatewayEditView.getHint(), Toast.LENGTH_SHORT).show();
            return;
        }

        final String port = mPortEditView.getText().toString();

        if (TextUtils.isEmpty(port)) {
            Toast.makeText(this, mPortEditView.getHint(), Toast.LENGTH_SHORT).show();
            return;
        }

        final String username = mUsernameEditView.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, mUsernameEditView.getHint(), Toast.LENGTH_SHORT).show();
            return;
        }

        final String password = mPasswordEditView.getText().toString();

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, mPasswordEditView.getHint(), Toast.LENGTH_SHORT).show();
            return;
        }

        @SuppressLint("StaticFieldLeak") AsyncTask<Object, Object, Integer> authTask = new AsyncTask<Object, Object, Integer>() {
            @Override
            protected Integer doInBackground(Object... params) {

                //网关认证
                LoginParam loginParam = new LoginParam();
                //初始化登录用户信息。
                LoginParam.UserInfo userInfo = loginParam.new UserInfo();
                userInfo.userName = username;
                userInfo.password = password;
                loginParam.setUserInfo(userInfo);
                loginParam.setInternetAddress(new InetSocketAddress(gateway, Integer.parseInt(port)));
                //设置关闭服务器证书校验
                loginParam.setAuthGateway(false);
                //设置为前台自动登录
                loginParam.setLoginBackground(true);
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
        authTask.execute();
    }
}
