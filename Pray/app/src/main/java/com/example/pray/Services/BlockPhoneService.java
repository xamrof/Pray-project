package com.example.pray.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.NotificationCompat;

import com.example.pray.Lock;
import com.example.pray.R;
import com.example.pray.block;

public class BlockPhoneService extends Service {

    private WindowManager windowManager;
    private View overlayView;


    @Override
    public void onCreate(){
       super.onCreate();
       createNotificationChannel();
       startForeground(1, getNotification());


      /* windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
       overlayView = LayoutInflater.from(this).inflate(R.layout.activity_lock, null);

       WindowManager.LayoutParams params = new WindowManager.LayoutParams(
               WindowManager.LayoutParams.WRAP_CONTENT,
               WindowManager.LayoutParams.WRAP_CONTENT,
               WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
               WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
               PixelFormat.TRANSLUCENT);

       params.gravity = Gravity.TOP | Gravity.LEFT;
       params.x = 0;
       params.y = 100;

       windowManager.addView(overlayView, params); */
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("BlockPhoneService", "onStartCommand");
        Intent activityIntent = new Intent(this, Lock.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(activityIntent);
        return START_STICKY;
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    "BlockPhoneServiceChannel",
                    "BlockPhoneService Channel",
                    NotificationManager.IMPORTANCE_HIGH
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

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("BlockPhoneService", "onDestroy");
        if(overlayView != null){
            windowManager.removeView(overlayView);
        }

    }

}