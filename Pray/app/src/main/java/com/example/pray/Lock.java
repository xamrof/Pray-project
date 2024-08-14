package com.example.pray;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pray.R;

import io.socket.client.Socket;

public class Lock extends AppCompatActivity {
    private Socket socket;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.password_native_activity);

       /* devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, MyAdmin.class);

        if(!devicePolicyManager.isAdminActive(componentName)){
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You need to activate Device admin");
            startActivity(intent);
        } */

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startLockTask();
        }


        final EditText passwordEditText = findViewById(R.id.EditText_Lock_Password);
        final Button unlockButton = findViewById(R.id.Button_Lock_Unlock);
        final ImageView imageLock = findViewById(R.id.imageView_Lock_AccessDenied);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isAcceptingText()){
            Log.d("Lock", "Software Keyboard was shown");
        }else{
            Log.d("Lock", "Software keyboard was not shown");
        }

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

                    if(unlock != null && unlock.equals(key)){
                        Toast.makeText(Lock.this, "Correct Password", Toast.LENGTH_SHORT).show();
                        stopLockTask();
                    }else{
                        Toast.makeText(Lock.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.d("Lock", "Error: "+e.getMessage());
                }
            }
        });


    }

    //ADD REST OF CODE
    @Override
    protected void onResume(){
        super.onResume();
        Log.d("Lock", "onResume");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            Log.d("SocketIO", "Disconnecting");
            socket.disconnect();
        }
    }

   /* private void lockScreen(){
        devicePolicyManager.lockNow();
    }*/
}
