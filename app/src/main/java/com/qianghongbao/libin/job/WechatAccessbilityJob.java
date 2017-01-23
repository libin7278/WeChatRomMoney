package com.qianghongbao.libin.job;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.qianghongbao.libin.BuildConfig;
import com.qianghongbao.libin.config.Config;
import com.qianghongbao.libin.notification.IStatusBarNotification;
import com.qianghongbao.libin.service.QiangHongBaoService;
import com.qianghongbao.libin.utils.AccessibilityHelper;
import com.qianghongbao.libin.utils.NotifyHelper;

import java.util.List;

/**
 * Created by libin on 17/1/22.
 */
public class WechatAccessbilityJob extends BaseAccessbilityJob {

    private static final String TAG = "WechatAccessbilityJob";

    /**
     * 微信的包名
     */
    public static final String WECHAT_PACKAGENAME = "com.tencent.mm";

    /**
     * 红包消息的关键字
     */
    private static final String HONGBAO_TEXT_KEY = "[微信红包]";

    private static final String BUTTON_CLASS_NAME = "android.widget.Button";


    /**
     * 不能再使用文字匹配的最小版本号
     */
    private static final int USE_ID_MIN_VERSION = 700;// 6.3.8 对应code为680,6.3.9对应code为700

    private static final int WINDOW_NONE = 0;
    private static final int WINDOW_LUCKYMONEY_RECEIVEUI = 1;
    private static final int WINDOW_LUCKYMONEY_DETAIL = 2;
    private static final int WINDOW_LAUNCHER = 3;
    private static final int WINDOW_OTHER = -1;

    private int mCurrentWindow = WINDOW_NONE;

    private boolean isReceivingHongbao;
    private PackageInfo mWechatPackageInfo = null;
    private Handler mHandler = null;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新安装包信息
            updatePackageInfo();
        }
    };

    @Override
    public void onCreateJob(QiangHongBaoService service) {
        super.onCreateJob(service);

        updatePackageInfo();

        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");

        getContext().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onStopJob() {
        try {
            getContext().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)

    @Override
    public boolean isEnable() {
        return getConfig().isEnableWechat();
    }

    @Override
    public String getTargetPackageName() {
        return WECHAT_PACKAGENAME;
    }

    @Override
    public void onReceiveJob(AccessibilityEvent event) {
        Log.i(Config.TAG, "onReceiveJob");
        final int eventType = event.getEventType();
        //通知栏事件
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Log.i(Config.TAG, "通知栏事件 TYPE_NOTIFICATION_STATE_CHANGED");
            List<CharSequence> texts = event.getText();
            if (!texts.isEmpty()) {
                for (CharSequence text : texts) {
                    String content = text.toString();
                    Log.i(Config.TAG, "通知栏文本===>:" + content);
                    if (content.contains(HONGBAO_TEXT_KEY)) {
                        //模拟打开通知栏消息
                        if (event.getParcelableData() != null
                                &&
                                event.getParcelableData() instanceof Notification) {
                            Notification notification = (Notification) event.getParcelableData();
                            PendingIntent pendingIntent = notification.contentIntent;
                            try {
                                pendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.i(Config.TAG, "微信红包界面 TYPE_WINDOW_STATE_CHANGED");
            openHongBao(event);
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            Log.i(Config.TAG, "微信聊天界面 TYPE_WINDOW_CONTENT_CHANGED");
            if(mCurrentWindow != WINDOW_LAUNCHER) { //不在聊天界面或聊天列表，不处理
                Log.i(Config.TAG, "聊天界面 mCurrentWindow != WINDOW_LAUNCHER");
                return;
            }

            handleChatListHongBao();

        }
    }

    /**
     * 是否为群聊天
     */
    private boolean isMemberChatUi(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return false;
        }
        String id = "com.tencent.mm:id/ces";
        int wv = getWechatVersion();
        if (wv <= 680) {
            id = "com.tencent.mm:id/ew";
        } else if (wv <= 700) {
            id = "com.tencent.mm:id/cbo";
        }
        String title = null;
        AccessibilityNodeInfo target = AccessibilityHelper.findNodeInfosById(nodeInfo, id);
        if (target != null) {
            title = String.valueOf(target.getText());
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("返回");

        if (list != null && !list.isEmpty()) {
            AccessibilityNodeInfo parent = null;
            for (AccessibilityNodeInfo node : list) {
                if (!"android.widget.ImageView".equals(node.getClassName())) {
                    continue;
                }
                String desc = String.valueOf(node.getContentDescription());
                if (!"返回".equals(desc)) {
                    continue;
                }
                parent = node.getParent();
                break;
            }
            if (parent != null) {
                parent = parent.getParent();
            }
            if (parent != null) {
                if (parent.getChildCount() >= 2) {
                    AccessibilityNodeInfo node = parent.getChild(1);
                    if ("android.widget.TextView".equals(node.getClassName())) {
                        title = String.valueOf(node.getText());
                    }
                }
            }
        }

        if (title != null && title.endsWith(")")) {
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openHongBao(AccessibilityEvent event) {
        if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
            mCurrentWindow = WINDOW_LUCKYMONEY_RECEIVEUI;
            //点中了红包，下一步就是去拆红包
            Log.i(Config.TAG, "点中了红包，下一步就是去拆红包");
            handleLuckyMoneyReceive();
        } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {
            mCurrentWindow = WINDOW_LUCKYMONEY_DETAIL;
            //拆完红包后看详细的纪录界面
            Log.i(Config.TAG, "拆完红包后看详细的纪录界面");
            if (getConfig().getWechatAfterGetHongBaoEvent() == Config.WX_AFTER_GET_GOHOME) { //返回主界面，以便收到下一次的红包通知
                AccessibilityHelper.performHome(getService());
            }
        } else if ("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
            mCurrentWindow = WINDOW_LAUNCHER;
            //在聊天界面,去点中红包
            Log.i(Config.TAG, "在聊天界面,去点中红包");
            handleChatListHongBao();
        } else {
            mCurrentWindow = WINDOW_OTHER;
        }
    }

    /**
     * 点击聊天里的红包后，显示的界面
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void handleLuckyMoneyReceive() {
        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }

        AccessibilityNodeInfo targetNode = null;

        int event = getConfig().getWechatAfterOpenHongBaoEvent();

        Log.e(Config.TAG,"抢到红包后处理模式 "+event +" 规则: 0-拆 1-看手气 2-静静地看着");

        int wechatVersion = getWechatVersion();
        if (event == Config.WX_AFTER_OPEN_HONGBAO) { //拆红包
            Log.e(Config.TAG,"拆红包 WX_AFTER_OPEN_HONGBAO");

            if (wechatVersion < USE_ID_MIN_VERSION) {
                targetNode = AccessibilityHelper.findNodeInfosByText(nodeInfo, "拆红包");
            } else {
                String buttonId = "com.tencent.mm:id/b43";

                if (wechatVersion == 700) {
                    buttonId = "com.tencent.mm:id/b2c";
                }

                if (buttonId != null) {
                    targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, buttonId);
                }

                if (targetNode == null) {
                    //分别对应固定金额的红包 拼手气红包
                    AccessibilityNodeInfo textNode = AccessibilityHelper.findNodeInfosByTexts(nodeInfo, "发了一个红包", "给你发了一个红包", "发了一个红包，金额随机");

                    if (textNode != null) {
                        for (int i = 0; i < textNode.getChildCount(); i++) {
                            AccessibilityNodeInfo node = textNode.getChild(i);
                            if (BUTTON_CLASS_NAME.equals(node.getClassName())) {
                                targetNode = node;
                                break;
                            }
                        }
                    }
                }

                if (targetNode == null) { //通过组件查找
                    targetNode = AccessibilityHelper.findNodeInfosByClassName(nodeInfo, BUTTON_CLASS_NAME);
                }
            }
        } else if (event == Config.WX_AFTER_OPEN_SEE) { //看一看
            Log.e(Config.TAG,"看一看大家手气 WX_AFTER_OPEN_SEE");

            if (getWechatVersion() < USE_ID_MIN_VERSION) { //低版本才有 看大家手气的功能
                targetNode = AccessibilityHelper.findNodeInfosByText(nodeInfo, "看看大家的手气");
            }
        } else if (event == Config.WX_AFTER_OPEN_NONE) {
            Log.e(Config.TAG,"静静的看着 WX_AFTER_OPEN_NONE");
            return;
        }

        if (targetNode != null) {
            final AccessibilityNodeInfo n = targetNode;
            long sDelayTime = getConfig().getWechatOpenDelayTime();
            if (sDelayTime != 0) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AccessibilityHelper.performClick(n);
                    }
                }, sDelayTime);
            } else {
                AccessibilityHelper.performClick(n);
            }

        }
    }

    /**
     * 收到聊天里的红包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void handleChatListHongBao() {
        Log.e(Config.TAG, "收到聊天里的红包");

        int mode = getConfig().getWechatMode();

        Log.e(Config.TAG, "抢红包模式" + mode);
        if (mode == Config.WX_MODE_3) { //只读模式
            return;
        }

        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(Config.TAG, "rootWindow为空");
            return;
        }

        if (mode != Config.WX_MODE_0) {
            boolean isMember = isMemberChatUi(nodeInfo);
            if (mode == Config.WX_MODE_1 && isMember) {//过滤群聊
                return;
            } else if (mode == Config.WX_MODE_2 && !isMember) { //过滤单聊
                return;
            }
        }

        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");

        Log.e(Config.TAG, "找到领取红包node" + list);

        if (list != null && list.isEmpty()) {
            // 从消息列表查找红包
            AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText(nodeInfo, "[微信红包]");

            Log.e(Config.TAG, "从消息列表查找红包");

            if (node != null) {
                Log.i(Config.TAG, "-->微信红包:" + node);

                isReceivingHongbao = true;
                AccessibilityHelper.performClick(nodeInfo);
            }
        } else if (list != null) {
            //最新的红包领起
            Log.e(Config.TAG, "最新的红包领起");

            AccessibilityNodeInfo node = list.get(list.size() - 1);
            AccessibilityHelper.performClick(node);
            isReceivingHongbao = false;

        }
    }

    private Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    /**
     * 获取微信的版本
     */
    private int getWechatVersion() {
        if (mWechatPackageInfo == null) {
            return 0;
        }
        return mWechatPackageInfo.versionCode;
    }

    /**
     * 更新微信包信息
     */
    private void updatePackageInfo() {
        try {
            mWechatPackageInfo = getContext().getPackageManager().getPackageInfo(WECHAT_PACKAGENAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
