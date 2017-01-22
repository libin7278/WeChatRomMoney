package com.qianghongbao.libin.job;

import android.content.Context;

import com.qianghongbao.libin.config.Config;
import com.qianghongbao.libin.service.QiangHongBaoService;

/**
 * Created by libin on 17/1/22.
 */
public abstract class BaseAccessbilityJob implements AccessbilityJob {

    private QiangHongBaoService service;

    @Override
    public void onCreateJob(QiangHongBaoService service) {
        this.service = service;
    }

    public Context getContext() {
        return service.getApplicationContext();
    }

    public Config getConfig() {
        return service.getConfig();
    }

    public QiangHongBaoService getService() {
        return service;
    }
}
