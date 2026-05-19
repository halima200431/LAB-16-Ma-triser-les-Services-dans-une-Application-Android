package com.example.lab16dev;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private TextView tvTemps;
    private Button btnStart, btnStop;

    private ChronometreService chronometreService;
    private boolean isBound = false;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private static final int REQUEST_NOTIFICATION_PERMISSION = 100;

    private final Runnable updateUiRunnable = new Runnable() {
        @Override
        public void run() {
            if (isBound && chronometreService != null) {
                tvTemps.setText(chronometreService.getTempsFormate());
            }

            handler.postDelayed(this, 1000);
        }
    };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChronometreService.LocalBinder binder = (ChronometreService.LocalBinder) service;
            chronometreService = binder.getService();
            isBound = true;

            tvTemps.setText(chronometreService.getTempsFormate());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            chronometreService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTemps = findViewById(R.id.tvTemps);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        demanderPermissionNotification();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                demarrerServiceChronometre();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arreterServiceChronometre();
            }
        });

        handler.post(updateUiRunnable);
    }

    private void demanderPermissionNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION
                );
            }
        }
    }

    private void demarrerServiceChronometre() {
        Intent intent = new Intent(this, ChronometreService.class);
        intent.setAction(ChronometreService.ACTION_START);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void arreterServiceChronometre() {
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }

        Intent intent = new Intent(this, ChronometreService.class);
        intent.setAction(ChronometreService.ACTION_STOP);
        startService(intent);

        tvTemps.setText("00:00");
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(updateUiRunnable);

        if (isBound) {
            unbindService(connection);
            isBound = false;
        }

        super.onDestroy();
    }
}