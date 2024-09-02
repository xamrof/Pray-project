package com.example.pray.Services;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class TurnOffService extends Service {
    private final Handler handler = new Handler();
    private Runnable runnable;
    private DevicePolicyManager policeManager;

    public int onStartCommand(Intent intent, int flags, int startId) {
        runnable = new Runnable() {
            @Override
            public void run() {
                turnOffScreen();
                //handler.postDelayed(this, 5000); executed every 5 seconds
                stopSelf();
            }
        };
        handler.postDelayed(runnable, 5000);
        return START_STICKY;
    }

    public void onDestroy(){
        super.onDestroy();
        if(runnable != null){
            handler.removeCallbacks(runnable);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void turnOffScreen(){
        policeManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        policeManager.lockNow();
    }
}