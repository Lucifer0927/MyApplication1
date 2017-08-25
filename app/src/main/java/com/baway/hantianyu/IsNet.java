package com.baway.hantianyu;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 类描述：
 * 创建人：韩天宇
 * 班级：移动通信1504D
 * 创建时间：2017/7/28 17:00
 */
public class IsNet {
     public static boolean isNetworkConnected(Context context) {
             if (context != null) {
                 ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                         .getSystemService(Context.CONNECTIVITY_SERVICE);
                 NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                 if (mNetworkInfo != null) {
                     return mNetworkInfo.isAvailable();
                 }
             }
             return false;
         }
}
