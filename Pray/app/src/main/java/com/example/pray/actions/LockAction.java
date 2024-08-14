package com.example.pray.actions;

import android.content.Context;
import android.os.Build;
import android.util.Log;

public class LockAction {

    public void lockAction(final Context ctx){
        Log.d("LockAction", "Initializing LockAction");

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

        }
    }


}
