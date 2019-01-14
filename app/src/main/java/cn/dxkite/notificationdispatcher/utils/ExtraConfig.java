package cn.dxkite.notificationdispatcher.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

/**
 * 获取Intent或者SharedPreferences的配置
 */
public class ExtraConfig {
    private SharedPreferences sharedPreferences;
    private Uri uri;


    public ExtraConfig(SharedPreferences sharedPreferences, Uri uri) {
        this.sharedPreferences = sharedPreferences;
        this.uri = uri;
    }


    public boolean getBoolean(String name,boolean defValue) {
        if (uri != null) {
            if ("true".equals(uri.getQueryParameter(name))) {
                return true;
            }
            if ("false".equals(uri.getQueryParameter(name))) {
                return false;
            }
        }
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(name,defValue);
        }
        return defValue;
    }

    public String getString(String name,String defValue) {
        if (uri != null && uri.getQueryParameter(name) != null) {
                 return uri.getQueryParameter(name);
        }
        if (sharedPreferences != null) {
            return sharedPreferences.getString(name,defValue);
        }
        return defValue;
    }
}
