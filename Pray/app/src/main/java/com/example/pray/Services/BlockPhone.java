package com.example.pray.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BlockPhone extends Service {
    public BlockPhone() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}