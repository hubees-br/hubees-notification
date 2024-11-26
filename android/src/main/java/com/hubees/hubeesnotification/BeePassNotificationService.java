package com.hubees.hubeesnotification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;

public class BeePassNotificationService extends android.app.Service {
    private static final String CHANNEL_ID = "custom_notification_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int STAY_TIME = 180;

    private Handler handler;
    private Runnable runnable;
    private int remainingTime;
    private int permanence;
    private String arrivalTime;
    private int progress;
    private boolean firstTime = true;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Dados iniciais
        remainingTime = intent.getIntExtra("remainingTime", 0);
        permanence = intent.getIntExtra("permanence", 0);
        arrivalTime = intent.getStringExtra("arrivalTime");
        progress = calculateProgress(remainingTime);

        startAutoUpdate();

        // Configura a execução após 1 minuto
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              startForeground(NOTIFICATION_ID, buildNotification());
            }
        }, 500);

        return START_STICKY;
    }

    // @Override
    // public void onTaskRemoved(Intent rootIntent) {
    //     // Pare o serviço
    //     stopSelf();

    //     // Cancele a notificação
    //     NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    //     if (manager != null) {
    //         manager.cancel(1);
    //     }

    //     super.onTaskRemoved(rootIntent);
    // }

    private void startAutoUpdate() {
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!firstTime) {
                  remainingTime--;
                  permanence++;
                  progress = calculateProgress(remainingTime);
                }

                firstTime = false;

                // Atualiza a notificação
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(NOTIFICATION_ID, buildNotification());

                // Reexecuta após 1 minuto
                handler.postDelayed(this, 60000);
            }
        };

        handler.post(runnable);
    }

    private Notification buildNotification() {
        Intent notificationIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (remainingTime <= 0) {
            RemoteViews notificationLayoutExcessSmall = new RemoteViews(getPackageName(), R.layout.beepass_excess_notification_layout_small);
            RemoteViews notificationLayoutExcessLarge = new RemoteViews(getPackageName(), R.layout.beepass_excess_notification_layout_large);

            notificationLayoutExcessSmall.setTextViewText(R.id.permanence, formatTime(permanence));
            notificationLayoutExcessSmall.setTextViewText(R.id.excess_time, formatTime(permanence - 180));
            notificationLayoutExcessSmall.setTextViewText(R.id.arrival_time, arrivalTime);

            notificationLayoutExcessLarge.setTextViewText(R.id.permanence, formatTime(permanence));
            notificationLayoutExcessLarge.setTextViewText(R.id.excess_time, formatTime(permanence - 180));
            notificationLayoutExcessLarge.setTextViewText(R.id.arrival_time, arrivalTime);

            // Intent para fechar a notificação
            Intent closeIntent = new Intent(this, NotificationReceiver.class);
            closeIntent.setAction("CLOSE_NOTIFICATION");
            PendingIntent closePendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                closeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            notificationLayoutExcessLarge.setOnClickPendingIntent(R.id.button_close, closePendingIntent);


            return new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.hubees)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayoutExcessSmall)
                    .setCustomBigContentView(notificationLayoutExcessLarge)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setShowWhen(false)
                    .setOngoing(true)
                    .setSound(null)
                    .setVibrate(new long[0])
                    .setContentIntent(pendingIntent)
                    .build();
        } else {
            RemoteViews notificationLayoutSmall = new RemoteViews(getPackageName(), R.layout.beepass_notification_layout_small);
            RemoteViews notificationLayoutLarge = new RemoteViews(getPackageName(), R.layout.beepass_notification_layout_large);

            notificationLayoutSmall.setProgressBar(R.id.progressBar, 100, progress, false);
            notificationLayoutLarge.setTextViewText(R.id.notification_remaining_time, formatTime(remainingTime));
            notificationLayoutLarge.setTextViewText(R.id.notification_details, "Permanência " + formatTime(permanence));
            notificationLayoutLarge.setTextViewText(R.id.arrival_time_label, "Chegada " + arrivalTime);
            notificationLayoutLarge.setProgressBar(R.id.progressBar, 100, progress, false);

            // Intent para fechar a notificação
            Intent closeIntent = new Intent(this, NotificationReceiver.class);
            closeIntent.setAction("CLOSE_NOTIFICATION");
            PendingIntent closePendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                closeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            notificationLayoutLarge.setOnClickPendingIntent(R.id.button_close, closePendingIntent);

            return new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.hubees)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayoutSmall)
                    .setCustomBigContentView(notificationLayoutLarge)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setShowWhen(false)
                    .setOngoing(true)
                    .setSound(null)
                    .setVibrate(new long[0])
                    .setContentIntent(pendingIntent)
                    .build();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "BeePass Notifications",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Canal para notificações do BeePass");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private int calculateProgress(int remainingTime) {
        return Math.max((remainingTime * 100) / STAY_TIME, 0);
    }

    private String formatTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02dh%02d", hours, mins);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
