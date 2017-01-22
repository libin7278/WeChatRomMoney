package com.qianghongbao.libin.notification;

import android.app.Notification;

/**
 * Created by libin on 17/1/22.
 */

public interface IStatusBarNotification {
    String getPackageName();
    Notification getNotification();
}
