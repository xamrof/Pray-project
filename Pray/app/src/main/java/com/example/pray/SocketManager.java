package com.example.pray;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.pray.Services.BlockPhone;
import com.example.pray.Services.BlockPhoneService;
import com.example.pray.Services.TurnOffScreen;
import com.example.pray.Workers.BlockWorker;
import com.example.pray.Workers.TurnOffScreenWorker;

import io.socket.client.Socket;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;


public class SocketManager {

    private static final String TAG = "SocketWorker";
    private static final String SERVER_URL = "http://192.168.100.91:3000";
    private static final String EVENT_CONNECT = Socket.EVENT_CONNECT;
    private static final String EVENT_CONNECT_ERROR = "connect_error";
    private static final String EVENT_MESSAGE_SERVER = "messageServer";
    private static final String MESSAGE_LOCK_SCREEN = "lock_screen";
    private static final String BLOCK_PHONE = "block_phone";
    private static final String MESSAGE_HI = "Hi from android";

    private Socket socket;
    private Context context;

    public SocketManager(Context context) {
        this.context = context;
    }

    public void run(){
        try{
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"};

            //For execute in an android device use ipConfig the ipv4 address
            socket = IO.socket(SERVER_URL, options);


            socket.on(EVENT_CONNECT, args -> {
                Log.d(TAG, "Connected");
                socket.emit("message", MESSAGE_HI);
            });

            socket.on(EVENT_CONNECT_ERROR, args -> {
                Object error = args[0];
                Log.e("SocketIO", "Connection error: "+error.toString());
            });
            socket.on(EVENT_MESSAGE_SERVER, args -> {
                String message = (String) args[0];
                Log.d("SocketIO", "Message received: "+args[0]);
                if(message.equals(MESSAGE_LOCK_SCREEN)){
                    startLookScreen(context);
                }else if(message.equals(BLOCK_PHONE)){
                    blockPhone(context);
                }

            } );
            socket.connect();
        }catch(URISyntaxException e){
            Log.e("SocketIO-URI", "Error: "+e.getMessage());
        }

    }

    private void startLookScreen(Context context){
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TurnOffScreenWorker.class)
                .build();

        WorkManager.getInstance(context).enqueue(workRequest);
    }

    private void blockPhone(Context context){

        Intent intent = new Intent(context, BlockPhone.class);

       context.startService(intent);

       /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent);
        }else{
            context.startService(intent);
        }*/




        /* Intent intent = new Intent();
        intent.setAction("com.example.pray.OPEN_ACTIVITY");
        context.sendBroadcast(intent); */


      /* Intent intent = new Intent(context, BlockPhoneService.class);
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       context.startService(intent); */


       /* if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent);
        }else{
            context.startService(intent);
        } */

      /*  OneTimeWorkRequest blockRequest = new OneTimeWorkRequest.Builder(BlockWorker.class).build();
        WorkManager.getInstance(context).enqueue(blockRequest); */
    }



   public void disconnect(){
        socket.disconnect();
    }
}
