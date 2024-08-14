package com.example.pray.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import com.example.pray.R;
import com.example.pray.SocketManager;

public class WebSocketService extends Service {
    private static final String CHANNEL_ID = "WebSocketServiceChannel";
    private SocketManager socketManager;


    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannel();
        startForeground(1, getNotification());

        socketManager = new SocketManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //IF DETECT THAT THE DEVICE IS BLOCKED
        return START_STICKY;
    }

    public void onDestroy(){
        super.onDestroy();
        if(socketManager != null){
            socketManager.disconnect();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "WebSocket Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification getNotification(){
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("WebSocket Service")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

    }

}