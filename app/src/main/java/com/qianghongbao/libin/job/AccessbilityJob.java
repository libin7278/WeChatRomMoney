package com.qianghongbao.libin.job;

import android.view.accessibility.AccessibilityEvent;

import com.qianghongbao.libin.service.QiangHongBaoService;

/**
 * Created by libin on 17/1/22.
 */
public interface AccessbilityJob {
    String getTargetPackageName();
    void onCreateJob(QiangHongBaoService service);
    void onReceiveJob(AccessibilityEvent event);
    void onStopJob();
    boolean isEnable();
}
