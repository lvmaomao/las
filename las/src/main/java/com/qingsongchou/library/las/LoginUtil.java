package com.qingsongchou.library.las;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.qingsongchou.library.las.login.LoginListener;
import com.qingsongchou.library.las.login.LoginPlatform;
import com.qingsongchou.library.las.login.LoginResult;
import com.qingsongchou.library.las.login.instance.LoginInstance;
import com.qingsongchou.library.las.login.instance.QQLoginInstance;
import com.qingsongchou.library.las.login.instance.WeiboLoginInstance;
import com.qingsongchou.library.las.login.instance.WxLoginInstance;
import com.qingsongchou.library.las.login.result.BaseToken;
import com.sina.weibo.sdk.utils.LogUtil;

import static com.qingsongchou.library.las.ShareLogger.INFO;

/**
 * Created by kuangwen on 2016/12/3.
 */

public class LoginUtil {
    private LoginUtil() {
    }

    private static volatile LoginUtil mInstance;

    public static LoginUtil getInstance() {
        if (mInstance == null) {
            synchronized (LoginUtil.class) {
                if (mInstance == null) {
                    mInstance = new LoginUtil();
                }
            }
        }
        return mInstance;
    }

    private LoginInstance mLoginInstance;

    private LoginListener mLoginListener;

    private static int mPlatform;

    private static boolean isFetchUserInfo;

    static final int TYPE = 799;

    public void login(Context context, @LoginPlatform.Platform int platform,
                      LoginListener listener) {
        login(context, platform, listener, true);
    }

    public void login(Context context, @LoginPlatform.Platform int platform,
                      LoginListener listener, boolean fetchUserInfo) {
        mPlatform = platform;
        mLoginListener = new LoginListenerProxy(listener);
        isFetchUserInfo = fetchUserInfo;
        context.startActivity(_ShareActivity.newInstance(context, TYPE));
    }

    void action(Activity activity) {
        switch (mPlatform) {
            case LoginPlatform.QQ:
                mLoginInstance = new QQLoginInstance(activity, mLoginListener, isFetchUserInfo);
                break;
            case LoginPlatform.WEIBO:
                mLoginInstance = new WeiboLoginInstance(activity, mLoginListener, isFetchUserInfo);
                break;
            case LoginPlatform.WX:
                mLoginInstance = new WxLoginInstance(activity, mLoginListener, isFetchUserInfo);
                break;
            default:
                LogUtil.d("LoginUtil", "error platform:" + mPlatform);
                return;
        }
        if (!mLoginInstance.isInstall(activity)) {
            mLoginListener.loginFailure(new Exception(INFO.NOT_INSTALL));
            activity.finish();
            return;
        }
        mLoginInstance.doLogin(activity, mLoginListener, isFetchUserInfo);
    }

    void handleResult(int requestCode, int resultCode, Intent data) {
        if (mLoginInstance != null) {
            mLoginInstance.handleResult(requestCode, resultCode, data);
        }
    }

    public void recycle() {
        if (mLoginInstance != null) {
            mLoginInstance.recycle();
        }
        mLoginInstance = null;
        mLoginListener = null;
        mPlatform = 0;
        isFetchUserInfo = false;
    }

    private class LoginListenerProxy extends LoginListener {

        private LoginListener mListener;

        LoginListenerProxy(LoginListener listener) {
            mListener = listener;
        }

        @Override
        public void loginSuccess(LoginResult result) {
            ShareLogger.i(INFO.LOGIN_SUCCESS);
            mListener.loginSuccess(result);
            recycle();
        }

        @Override
        public void loginFailure(Exception e) {
            ShareLogger.i(INFO.LOGIN_FAIl);
            mListener.loginFailure(e);
            recycle();
        }

        @Override
        public void loginCancel() {
            ShareLogger.i(INFO.LOGIN_CANCEL);
            mListener.loginCancel();
            recycle();
        }

        @Override
        public void beforeFetchUserInfo(BaseToken token) {
            ShareLogger.i(INFO.LOGIN_AUTH_SUCCESS);
            mListener.beforeFetchUserInfo(token);
        }
    }
}
