package com.example.lab16dev;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChronometreService extends Service {

    public static final String ACTION_START = "START";
    public static final String ACTION_STOP = "STOP";

    private final IBinder binder = new LocalBinder();

    private int secondes = 0;
    private boolean isRunning = false;

    private ScheduledExecutorService executor;
    private NotificationManager notificationManager;

    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "chrono_channel";

    public class LocalBinder extends Binder {
        public ChronometreService getService() {
            return ChronometreService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        creerNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent != null ? intent.getAction() : null;

        if (ACTION_STOP.equals(action)) {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (!isRunning) {
            isRunning = true;

            startForeground(NOTIFICATION_ID, creerNotification());

            demarrerChronometre();
        }

        return START_STICKY;
    }

    private void demarrerChronometre() {
        executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                secondes++;
                updateNotification();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void creerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Chronomètre Service",
                    NotificationManager.IMPORTANCE_LOW
            );

            channel.setDescription("Notification du chronomètre en arrière-plan");

            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification creerNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Chronomètre en cours")
                .setContentText("Temps : " + getTempsFormate())
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void updateNotification() {
        notificationManager.notify(NOTIFICATION_ID, creerNotification());
    }

    public String getTempsFormate() {
        int minutes = secondes / 60;
        int secondesRestantes = secondes % 60;

        return String.format("%02d:%02d", minutes, secondesRestantes);
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        isRunning = false;

        if (executor != null) {
            executor.shutdown();
            executor = null;
        }

        stopForeground(true);

        super.onDestroy();
    }
}