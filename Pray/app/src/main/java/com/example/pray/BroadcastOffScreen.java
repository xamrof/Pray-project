package com.example.pray;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.pray.Services.TurnOffScreen;
import com.example.pray.Services.TurnOffService;
import com.example.pray.Workers.TurnOffScreenWorker;

import java.util.concurrent.TimeUnit;

public class BroadcastOffScreen extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())){
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TurnOffScreenWorker.class)
                    .setInitialDelay(5, TimeUnit.SECONDS)
                    .build();

            WorkManager.getInstance(context).enqueue(workRequest);
        }
    }
}