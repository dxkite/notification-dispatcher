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
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import cn.dxkite.notificationdispatcher.R;
import cn.dxkite.notificationdispatcher.service.NotificationListener;
import cn.dxkite.notificationdispatcher.utils.ExtraConfig;
import cn.dxkite.notificationdispatcher.utils.ServiceUtils;

public class MainActivity extends AppCompatActivity {

    EditText textUrl, textToken, textSecret;
    Switch timing,booting;
    Button apply;
    ToggleButton start;
    ExtraConfig config;

    final static String TAG = "Notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        config = new ExtraConfig(getSharedPreferences("config", MODE_PRIVATE), getIntent().getData());

        start = findViewById(R.id.start);
        apply = findViewById(R.id.apply_setting);
        textUrl = findViewById(R.id.request_url);
        textToken = findViewById(R.id.request_token);
        textSecret = findViewById(R.id.request_secret);
        timing = findViewById(R.id.weekup_time);
        booting = findViewById(R.id.weekup_boot);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start.isChecked()) {
                    Toast.makeText(getApplicationContext(), R.string.starting_service, Toast.LENGTH_SHORT).show();
                    startListener();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.stopping_service, Toast.LENGTH_SHORT).show();
                    stopListener();
                }
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopListener();
                start.setChecked(false);
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("url", textUrl.getText().toString());
                editor.putString("token", textToken.getText().toString());
                editor.putString("secret", textSecret.getText().toString());
                editor.putBoolean("booting", booting.isChecked());
                editor.putBoolean("timing",timing.isChecked());
                editor.apply();
                Toast.makeText(MainActivity.this, "参数设置成功", Toast.LENGTH_SHORT).show();
            }
        });

        textUrl.setText(config.getString("url", "http://192.168.31.50/paymentCallback"));
        textToken.setText(config.getString("token", "dxkite"));
        textSecret.setText(config.getString("secret", "dxkite"));
        booting.setChecked(config.getBoolean("booting",true));
        timing.setChecked(config.getBoolean("timing",true));

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
        start.setChecked(ServiceUtils.isServiceRunning(this, NotificationListener.class));
    }

    public boolean isNotificationEnabled() {
        String names = Settings.Secure.getString(getApplicationContext().getContentResolver(), "enabled_notification_listeners");
//        Log.e(TAG, "enabled_notification_listeners = " + names);
        return names.contains(NotificationListener.class.getCanonicalName());
    }

    public void startListener() {
        if (ServiceUtils.isServiceRunning(this, NotificationListener.class)) {
            Log.d(TAG, "service is already running");
        } else {
            Intent intent = new Intent(this, NotificationListener.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d(TAG, "start service");
            startService(intent);
        }
    }
    public void stopListener() {
        if (ServiceUtils.isServiceRunning(this, NotificationListener.class)) {
            Intent intent = new Intent(this, NotificationListener.class);
            Log.d(TAG, "stop service");
            stopService(intent);
        } else {
            Log.d(TAG, "service is already stopped");
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
