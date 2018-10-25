package com.niles.huaweiloginmodule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.niles.huawei_login.AuthResultListener;
import com.niles.huawei_login.LoginConfig;
import com.niles.huawei_login.LoginManager;

public class MainActivity extends AppCompatActivity implements AuthResultListener {

    private EditText mUsernameEditView;
    private EditText mPasswordEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsernameEditView = findViewById(R.id.et_username);
        mPasswordEditView = findViewById(R.id.et_password);

        mUsernameEditView.setText(BuildConfig.USERNAME);
        mPasswordEditView.setText(BuildConfig.PASSWORD);
    }

    public void onButtonClicked(View view) {
        LoginManager.doAuthentication(this, new LoginConfig.Builder()
                .setAuthInBackground(false)
                .setGateway(BuildConfig.GATEWAY)
//                .setUsername(mUsernameEditView.getText().toString())
//                .setPassword(mPasswordEditView.getText().toString())
                .setAuthResultListener(this)
                .build());
    }

    @Override
    public void onResult(final int resultCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, String.valueOf(resultCode), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
