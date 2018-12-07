package cn.dxkite.notificationdispatcher.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.dxkite.notificationdispatcher.R;
import cn.dxkite.notificationdispatcher.service.NotificationListener;
import cn.dxkite.notificationdispatcher.utils.ServiceUtils;

public class MainActivity extends AppCompatActivity {

    EditText textUrl, textToken, textSecret;
    final static String TAG = "Notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = findViewById(R.id.params_setting);

        textUrl = findViewById(R.id.request_url);
        textToken = findViewById(R.id.request_token);
        textSecret = findViewById(R.id.request_secret);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("url", textUrl.getText().toString());
                editor.putString("token", textToken.getText().toString());
                editor.putString("secret", textSecret.getText().toString());
                editor.apply();
                Toast.makeText(getApplicationContext(), R.string.setting_save_success, Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences preference = getSharedPreferences("config", MODE_PRIVATE);
        textUrl.setText(preference.getString("url", "http://192.168.0.109/paymentCallback"));
        textToken.setText(preference.getString("token", "dxkite"));
        textSecret.setText(preference.getString("secret", "dxkite"));

        if (!isNotificationEnabled()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.grant_notice)
                    .setMessage(R.string.need_permission)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                            startActivity(intent);
                            finish();
                        }
                    })
                    .show();
        }
        startListener();
    }


    public boolean isNotificationEnabled() {
        String names = Settings.Secure.getString(getApplicationContext().getContentResolver(), "enabled_notification_listeners");
//        Log.e(TAG, "enabled_notification_listeners = " + names);
        return names.contains(NotificationListener.class.getCanonicalName());
    }

    public void startListener() {
        if (ServiceUtils.isServiceRunning(this, NotificationListener.class)) {
            Log.e(TAG, "service is running");
        } else {
            Intent start = new Intent(this, NotificationListener.class);
            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e(TAG, "start service");
            startService(start);
        }
    }

    //退出时的时间
    private long mExitTime;
    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出主程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
}
