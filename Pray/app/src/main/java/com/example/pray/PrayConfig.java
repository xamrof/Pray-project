package com.example.pray;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrayConfig {
    public static final String UNLOCK_PASS = "unlock_pass";
    private Context ctx;

    private void saveString(String key, String value){
        try{
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(key, value);
            editor.apply();
        }catch (Exception e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    private String getString(String key, String defaultValue){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        return settings.getString(key, defaultValue);
    }

    public String getUnlockPass() { return getString(PrayConfig.UNLOCK_PASS, null);}

    public void setUnlockPass(String unlockPass){
        this.saveString(UNLOCK_PASS, unlockPass);
    }

}

