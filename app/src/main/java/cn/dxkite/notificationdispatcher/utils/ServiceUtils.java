package cn.dxkite.notificationdispatcher.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ServiceUtils {

    public static boolean isServiceRunning(Context context, Class serviceName) {
        if (serviceName == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);
        int uid = android.os.Process.myUid();
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (runningServiceInfo.uid == uid && runningServiceInfo.service.getClassName().equals(serviceName.getCanonicalName())) {
                return true;
            }
        }
        return false;
    }
}
