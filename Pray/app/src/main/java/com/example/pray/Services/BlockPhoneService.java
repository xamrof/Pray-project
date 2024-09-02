package com.example.pray.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import com.example.pray.Lock;
import com.example.pray.R;

public class BlockPhoneService extends Service {

    private WindowManager windowManager;
    private View overlayView;
    BroadcastReceiver screenUnlockReceiver;

   @Override
    public void onCreate(){
       super.onCreate();
       createNotificationChannel();
       startForeground(1, getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("BlockPhoneService", "onStartCommand");
        checkScreenStatus();
        return START_STICKY;
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    "BlockPhoneServiceChannel",
                    "BlockPhoneService Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification getNotification(){
        return new NotificationCompat.Builder(this, "BlockPhoneServiceChannel")
                .setContentTitle("BlockPhoneService")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
    }

    private void checkScreenStatus(){
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isScreenOn = powerManager.isInteractive();
        final IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);

        if(isScreenOn){
            startActivity();
        }else{
            screenUnlockReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    startActivity();

                    unregisterReceiver(screenUnlockReceiver);

                }
            };
            registerReceiver(screenUnlockReceiver, filter);
        }

    }

    private void startActivity(){
        Intent activityIntent = new Intent(this, Lock.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(activityIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("BlockPhoneService", "onDestroy");
        if(overlayView != null && screenUnlockReceiver != null){
           windowManager.removeView(overlayView);
           //unregisterReceiver(screenUnlockReceiver);
        }

    }

}