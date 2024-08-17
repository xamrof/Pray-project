package com.example.pray;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pray.Services.WebSocketService;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    private Socket socket;
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
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
        ComponentName componentName = new ComponentName(this, MyAdmin.class);

       if(!devicePolicyManager.isAdminActive(componentName)){
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You need to activate Device admin");
            startActivity(intent);
        }

        Intent socketService = new Intent(this, WebSocketService.class);

        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(socketService);
        }else{
            startService(socketService);
        }*/

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


