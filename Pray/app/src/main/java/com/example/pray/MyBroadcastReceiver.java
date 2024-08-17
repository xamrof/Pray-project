package com.example.pray;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.pray.Services.BlockPhoneService;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Log.d("BroadcastReceiver", "onReceive");
        if(intent.getAction().equals("com.example.pray.OPEN_ACTIVITY")){
            Log.d("BroadcastReceiver", "onReceive");
        }

        Intent i = new Intent(context, BlockPhoneService.class);
        context.startService(i);
    }
}
