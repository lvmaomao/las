package com.qingsongchou.library.las.share.instance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.qingsongchou.library.las.share.ShareImageObject;
import com.qingsongchou.library.las.share.ShareListener;

/**
 * Created by kuangwen on 2016/11/18.
 */

public interface ShareInstance {

    void shareText(int platform, String text, Activity activity, ShareListener listener);

    void shareMedia(int platform, String title, String targetUrl, String summary,
                    ShareImageObject shareImageObject, Activity activity, ShareListener listener);

    void shareImage(int platform, ShareImageObject shareImageObject, Activity activity,
            ShareListener listener);

    void handleResult(Intent data);

    boolean isInstall(Context context);

    void recycle();
}
