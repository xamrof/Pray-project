package com.example.pray.Workers;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.pray.MyAdmin;

public class TurnOffScreenWorker  extends Worker {
    private DevicePolicyManager policeManager;
    private ComponentName adminComponent;

    public TurnOffScreenWorker(Context context, WorkerParameters params) {
        super(context, params);
        policeManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(context, MyAdmin.class);
        Log.d("TurnOffWork", "HERE WORKER");
    }

    @NonNull
    @Override
    public Result doWork() {
        try{
            Log.d("TurnOffWork", "worker executed");
            turnOffScreen();
            return Result.success();
        }catch(Exception e){
            Log.e("TurnOffWork", "Error: "+e.getMessage());
            return Result.failure();
        }
    }

    private void turnOffScreen(){
        if(policeManager.isAdminActive(adminComponent)){
            policeManager.lockNow();
        }else{
            Log.d("TurnOffWork", "Admin not active");
        }
    }
}
