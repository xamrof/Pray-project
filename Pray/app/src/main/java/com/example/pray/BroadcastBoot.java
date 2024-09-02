package com.example.pray;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;

import com.example.pray.Services.BlockPhoneService;
import com.example.pray.Services.WebSocketService;
import com.example.pray.Workers.BlockWorker;
import com.example.pray.Workers.TurnOffScreenWorker;
import com.example.pray.Workers.TurnOnServicesWorker;

public class BroadcastBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
          if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
              Log.d("BroadcastBoot", "Boot completed");
              OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TurnOnServicesWorker.class)
                      .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                      .build();

              WorkManager.getInstance(context).enqueue(workRequest);
        }
    }
}
