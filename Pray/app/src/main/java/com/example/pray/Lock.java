package com.example.pray;

import android.app.ActivityManager;
import android.app.admin.FactoryResetProtectionPolicy;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.pray.Workers.TurnOffScreenWorker;

import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;

public class Lock extends AppCompatActivity implements SensorEventListener {
    private Socket socket;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponent;
    private Context context;
    private Boolean isBlockActive = false;//This variable is used to check if the device is blocked and use in DB
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private SensorManager sensorManager;
    private Sensor mProximitySensor;
    private boolean isScreenOn = true;
    private WindowManager windowManager;
    private View overlayView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, MyAdmin.class);
        context = this;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.password_native_activity);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            isBlockActive = true;
            startKioskMode();
        }


        final EditText passwordEditText = findViewById(R.id.EditText_Lock_Password);
        final Button unlockButton = findViewById(R.id.Button_Lock_Unlock);
        final ImageView imageLock = findViewById(R.id.imageView_Lock_AccessDenied);
        final TextView text = findViewById(R.id.TextView_Lock_AccessDenied);
        final View contentView = findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int mPreviousHeight;
            @Override
            public void onGlobalLayout() {
                int newHeight = contentView.getHeight();
                Log.d("show", "previousHeight: "+mPreviousHeight);
                if(mPreviousHeight != 0){
                    if(mPreviousHeight > newHeight){
                        Log.d("Lock", "Software Keyboard was shown");
                        imageLock.setVisibility(View.GONE);
                    }
                    else if (mPreviousHeight < newHeight){
                        Log.d("Lock", "Software keyboard was not shown");
                        imageLock.setVisibility(View.VISIBLE);
                    }
                }
                mPreviousHeight = newHeight;
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                text.setText(R.string.lock_access_denied);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String unlock = "unlock";
                    String key = passwordEditText.getText().toString().trim();

                    if(unlock.equals(key)){
                        Toast.makeText(Lock.this, "Correct Password", Toast.LENGTH_SHORT).show();
                        isBlockActive = false;
                        stopLockTask();
                        finish();
                        //uninstallApk();
                        //SHOWING AN ACTIVITY WILL SAY THAT THE USER IS UNLOCKED
                    }else{
                        Toast.makeText(Lock.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.d("Lock", "Error: "+e.getMessage());
                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("Lock", "backPressed");
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    //I NEED ADD WHAT HAPPEN WHEN THE USER IS UNLOCKED, MAYBE MAIN ACTIVITY
    //ADD REST OF CODE
    @Override
    protected void onResume(){
        super.onResume();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener( this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        if(isBlockActive){
            scheduleWorkRequest();
        }

    }

    private void scheduleWorkRequest(){
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TurnOffScreenWorker.class)
                .setInitialDelay(5, TimeUnit.SECONDS)
                .addTag("TurnOffScreenWorker")
                .build();
        WorkManager.getInstance(context).enqueue(workRequest);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
        WorkManager.getInstance(context).cancelAllWorkByTag("TurnOffScreenWorker");
        startKioskMode();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && isBlockActive){
            if(!isInLockTaskMode()){
                startKioskMode();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO){
            Toast.makeText(this, "Keyboard visible", Toast.LENGTH_SHORT).show();
        }else if(newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES){
            Toast.makeText(this, "Keyboard hidden", Toast.LENGTH_SHORT).show();
        }
    }

    private void startKioskMode(){
        startLockTask();
       if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            devicePolicyManager.setLockTaskPackages(adminComponent, new String[]{getPackageName()});
            startLockTask();
        }
    }

    private boolean isInLockTaskMode(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            return activityManager.getLockTaskModeState() != ActivityManager.LOCK_TASK_MODE_NONE;
        }else{
            return activityManager.isInLockTaskMode();
        }
    }

    private void uninstallApk(){

        if(devicePolicyManager.isAdminActive(adminComponent)){
            devicePolicyManager.clearDeviceOwnerApp(getPackageName());
            devicePolicyManager.removeActiveAdmin(adminComponent);
        }

        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:"+getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            Log.d("SocketIO", "Disconnecting");
            windowManager.removeView(overlayView);
            socket.disconnect();
            WorkManager.getInstance(context).cancelAllWorkByTag("TurnOffScreenWorker");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY){
            if(sensorEvent.values[0] < mProximitySensor.getMaximumRange()){
                Log.d("Lock", "paso por aqui");
                isScreenOn = false;
                WorkManager.getInstance(context).cancelAllWork();
            }else{
                isScreenOn = true;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
