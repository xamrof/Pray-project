package com.example.pray.Workers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.pray.Services.BlockPhone;
import com.example.pray.Services.BlockPhoneService;
import com.example.pray.Services.WebSocketService;
import com.google.common.util.concurrent.ListenableFuture;

public class TurnOnServicesWorker extends Worker {

    private static final String CHANNEL_ID = "TurnOnServicesWorker";
    private static final int NOTIFICATION_ID = 1;

    public TurnOnServicesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try{
            Context context = getApplicationContext();
            /*Intent intent = new Intent(getApplicationContext(), BlockPhone.class);
            context.startService(intent);*/
           startService(context, BlockPhoneService.class);
            startService(context, WebSocketService.class);
            return Result.success();
        }catch (Exception e){
            Log.e("TurnOnServicesWorker", "Error: "+e.getMessage());
            return Result.failure();
        }
    }

    @NonNull
    @Override
    public ForegroundInfo getForegroundInfo(){
        Context context = getApplicationContext();
        String title = "Turn on Services";
        String cancel = "Cancel";

        Notification notification = createNotification(title, cancel);

        return new ForegroundInfo(NOTIFICATION_ID, notification);
    }

    private void startService(Context context, Class<?> serviceClass){
        Intent intent = new Intent(context, serviceClass);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent);
        }else{
            context.startService(intent);
        }
    }

    private Notification createNotification(String title, String cancel){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                    "Turn on Services Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            if(manager != null){
                manager.createNotificationChannel(channel);
            }
        }

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
            .setContentTitle(title)
            .setTicker(title)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build();
    }
}
