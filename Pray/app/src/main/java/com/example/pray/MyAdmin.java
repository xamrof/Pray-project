package com.example.pray;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyAdmin extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent){
        super.onEnabled(context, intent);
        Log.d("Device Admin", "Device Admin Enabled");
    }
    @Override
    public void onDisabled(Context context, Intent intent){
        super.onDisabled(context, intent);
        Log.d("Device Admin", "Device Admin Disabled");
    }
}
