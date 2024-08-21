package com.example.pray;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.pray.Services.BlockPhoneService;

public class BroadcastBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Log.d("MyBroadcastReceiver", "Boot completed");
            Intent launchIntent = new Intent(context, BlockPhoneService.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(launchIntent);
            }else{
                context.startService(launchIntent);
            }

        }
    }
}
