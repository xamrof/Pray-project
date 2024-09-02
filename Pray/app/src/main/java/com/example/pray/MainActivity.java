package com.example.pray;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.app.admin.FactoryResetProtectionPolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pray.Services.BlockPhoneService;
import com.example.pray.Services.WebSocketService;
import com.example.pray.Workers.TurnOnServicesWorker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private ActivityResultLauncher<Intent> overlayPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(this, MyAdmin.class);

       if(!devicePolicyManager.isAdminActive(adminComponent)){
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You need to activate Device admin");
            startActivity(intent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            devicePolicyManager.addUserRestrictionGlobally(UserManager.DISALLOW_FACTORY_RESET);
            devicePolicyManager.addUserRestriction(adminComponent, UserManager.DISALLOW_SAFE_BOOT);
        }

        Intent socketService = new Intent(this, WebSocketService.class);

        overlayPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (Settings.canDrawOverlays(this)) {
                            startForegroundService(socketService);
                        }else{
                            // Handle permission denied
                            Log.d("MainActivity", "Permission denied");
                        }
                    }
                }
        );

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(!Settings.canDrawOverlays(this)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
                overlayPermissionLauncher.launch(intent);
            }else{
                startForegroundService(socketService);
            }
        }else{
            startService(socketService);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if(!pm.isIgnoringBatteryOptimizations(getPackageName())){
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:"+getPackageName()));
                startActivity(intent);
            }
        }

        ComponentName receiver = new ComponentName(this, BroadcastBoot.class);
        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }


    private void scheduleAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(context, BlockPhoneService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            PendingIntent pendingIntent = PendingIntent.getForegroundService(context, 0, serviceIntent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       if (socket != null) {
            Log.d("SocketIO", "Disconnecting");
            stopLockTask();
            socket.disconnect();
  }
    }
}


