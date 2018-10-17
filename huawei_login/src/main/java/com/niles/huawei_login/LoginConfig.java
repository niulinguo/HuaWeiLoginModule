package com.niles.huawei_login;

/**
 * Created by Niles
 * Date 2018/10/17 21:11
 * Email niulinguo@163.com
 */
public class LoginConfig {

    private final boolean mAuthInBackground;
    private final String mUsername;
    private final String mPassword;
    private final String mGateway;
    private final AuthResultListener mAuthResultListener;

    private LoginConfig(boolean authInBackground, String username, String password, String gateway, AuthResultListener authResultListener) {
        mAuthInBackground = authInBackground;
        mUsername = username;
        mPassword = password;
        mGateway = gateway;
        mAuthResultListener = authResultListener;
    }

    public AuthResultListener getAuthResultListener() {
        return mAuthResultListener;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getGateway() {
        return mGateway;
    }

    public boolean isAuthInBackground() {
        return mAuthInBackground;
    }

    public static final class Builder {

        private boolean mAuthInBackground;
        private String mUsername;
        private String mPassword;
        private String mGateway;
        private AuthResultListener mAuthResultListener;

        public boolean isAuthInBackground() {
            return mAuthInBackground;
        }

        public Builder setAuthInBackground(boolean authInBackground) {
            mAuthInBackground = authInBackground;
            return this;
        }

        public AuthResultListener getAuthResultListener() {
            return mAuthResultListener;
        }

        public Builder setAuthResultListener(AuthResultListener authResultListener) {
            mAuthResultListener = authResultListener;
            return this;
        }

        public String getUsername() {
            return mUsername;
        }

        public Builder setUsername(String username) {
            mUsername = username;
            return this;
        }

        public String getPassword() {
            return mPassword;
        }

        public Builder setPassword(String password) {
            mPassword = password;
            return this;
        }

        public String getGateway() {
            return mGateway;
        }

        public Builder setGateway(String gateway) {
            mGateway = gateway;
            return this;
        }

        public LoginConfig build() {
            return new LoginConfig(
                    mAuthInBackground,
                    mUsername,
                    mPassword,
                    mGateway,
                    mAuthResultListener
            );
        }
    }
}
