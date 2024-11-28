package com.hubees.hubeesnotification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.provider.Settings;
import android.net.Uri;


public class BeePassNotificationService extends android.app.Service {
    private static final String CHANNEL_ID = "custom_notification_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int STAY_TIME = 180;

    private int remainingTime;
    private int permanence;
    private String arrivalTime;
    private int progress;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Verifique a permissão de alarmes exatos no Android 12 ou superior
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        //       AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //       if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
        //           Intent intentPermission = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        //           intentPermission.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //           startActivity(intentPermission);
        //           return START_STICKY;
        //       }
        // }

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //     // Criar o Intent para solicitar que o usuário ignore as otimizações de bateria
        //     Intent batteryOptimizationIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        //     batteryOptimizationIntent.setData(Uri.parse("package:" + this.getPackageName()));

        //     // Adicionar a FLAG_ACTIVITY_NEW_TASK
        //     batteryOptimizationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //     // Iniciar a Activity
        //     startActivity(batteryOptimizationIntent);
        // }

        if (intent != null) {
            // Recupera os dados passados
            remainingTime = intent.getIntExtra("remainingTime", 0);
            permanence = intent.getIntExtra("permanence", 0);
            arrivalTime = intent.getStringExtra("arrivalTime");
            progress = calculateProgress(remainingTime);

            // Atualiza a notificação
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.notify(NOTIFICATION_ID, buildNotification());
            }

            // Atualiza os valores para o próximo ciclo
            remainingTime--;
            permanence++;

            // Agenda a próxima execução
            scheduleNotificationUpdate();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelNotificationUpdate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void scheduleNotificationUpdate() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, BeePassNotificationService.class);
        intent.putExtra("remainingTime", remainingTime);
        intent.putExtra("permanence", permanence);
        intent.putExtra("arrivalTime", arrivalTime);

        PendingIntent pendingIntent = PendingIntent.getService(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            long triggerTime = System.currentTimeMillis() + 60000; // Dispara após 1 minuto
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private void cancelNotificationUpdate() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, BeePassNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
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
                    // .setOngoing(true)
                    .setSound(null)
                    .setVibrate(new long[0])
                    .setContentIntent(pendingIntent)
                    .setDeleteIntent(closePendingIntent)
                    .build();
        } else {
            RemoteViews notificationLayoutSmall = new RemoteViews(getPackageName(), R.layout.beepass_notification_layout_small);
            RemoteViews notificationLayoutLarge = new RemoteViews(getPackageName(), R.layout.beepass_notification_layout_large);

            notificationLayoutSmall.setProgressBar(R.id.progressBar, 100, progress, false);
            notificationLayoutLarge.setTextViewText(R.id.notification_remaining_time, formatTime(remainingTime));
            notificationLayoutLarge.setTextViewText(R.id.notification_details, "Permanência " + formatTime(permanence));
            notificationLayoutLarge.setTextViewText(R.id.arrival_time_label, "Chegada " + arrivalTime);
            notificationLayoutLarge.setProgressBar(R.id.progressBar, 100, progress, false);

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
                    // .setOngoing(true)
                    .setSound(null)
                    .setVibrate(new long[0])
                    .setContentIntent(pendingIntent)
                    .setDeleteIntent(closePendingIntent)
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
}
