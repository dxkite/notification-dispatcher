package cn.dxkite.notificationdispatcher.service;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import cn.dxkite.notificationdispatcher.Message;
import cn.dxkite.notificationdispatcher.R;

public class NotificationListener extends NotificationListenerService {
    final static String TAG = "NotificationListener";
    final static int notificationId = 1000;

    @Override
    public void onCreate() {
        Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getString(R.string.payment_is_listening))
                .setContentTitle(getString(R.string.app_name))
                .build();
        notification.flags|=Notification.FLAG_NO_CLEAR;
        notification.flags|=Notification.FLAG_ONGOING_EVENT;
        startForeground(notificationId,notification);
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "onNotificationPosted");
        String packageName = sbn.getPackageName();
        Message.Type type;
        if ("com.eg.android.AlipayGphone".equals(packageName)) {
            type = Message.Type.Alipay;
        } else if ("com.tencent.mm".equals(packageName)) {
            type = Message.Type.Wechat;
        } else {
            return;
        }
        Notification notification = sbn.getNotification();
        Bundle bundle = notification.extras;
        String title = bundle.getString(Notification.EXTRA_TITLE, "");
        Message message = new Message();
        message.from = bundle.getString(Notification.EXTRA_TITLE);
        message.content = bundle.getString(Notification.EXTRA_TEXT);
        message.localTime = System.currentTimeMillis();
        message.unixTime = message.localTime / 1000L;
        message.time = notification.when;
        message.type = type;
        Message.send(this, message);
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, "onNotificationRemoved");
        super.onNotificationRemoved(sbn);
    }
}
