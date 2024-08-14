package com.example.pray.Services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.pray.SocketManager;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketWorker extends Worker {

    private static final String TAG = "SocketWorker";
    private static final String SERVER_URL = "http://10.0.2.2:3000";
    private static final String EVENT_CONNECT = Socket.EVENT_CONNECT;
    private static final String EVENT_CONNECT_ERROR = "connect_error";
    private static final String EVENT_MESSAGE_SERVER = "messageServer";
    private static final String MESSAGE_LOCK_SCREEN = "lock_screen";
    private static final String MESSAGE_HI = "Hi from android"; // add an ID



    //private SocketManager webSocketManager;

    public SocketWorker(@NonNull Context context, @NonNull WorkerParameters workerParams){
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork(){

        try{

           // webSocketManager = new SocketManager(this.getApplicationContext());
         /*   IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"};

            //For execute in an android device use ipConfig the ipv4 address
            socket = IO.socket("http://10.0.2.2:3000", options);


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
                    Intent intent = new Intent(this.getApplicationContext(), TurnOffScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startService(intent);
                }

            } );
            socket.connect();*/

        }catch(Error e){
            Log.e("SocketIO-URI", "Error: "+e.getMessage());
            return Result.failure();
        }

        return Result.success();
    }

}