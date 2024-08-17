package com.example.pray.Services;

import android.app.IntentService;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;

import com.example.pray.MyAdmin;
import com.example.pray.SocketManager;

import java.net.Socket;


public class TurnOffScreen extends IntentService {

    private DevicePolicyManager policeManager;
    private ComponentName adminComponent;
    SocketManager socket;

    public TurnOffScreen() {
        super("TurnOffScreen");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        policeManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        policeManager.lockNow();
    }

}