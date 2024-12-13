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
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BeeMovingNotificationService extends android.app.Service {
    private static final String CHANNEL_ID = "custom_notification_channel";
    private static final int NOTIFICATION_ID = 1;

    private int value;
    private int permanence;
    private int valueWithDiscount;

    private Retrofit retrofit;
    private StayService stayService;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        // Configura o OkHttpClient com o AuthInterceptor
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .build();

        // Configura o Retrofit utilizando o OkHttpClient personalizado
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api-dev.hubees.com.br/")
                .client(client) // Define o cliente HTTP com interceptador
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        stayService = retrofit.create(StayService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "ACTION_RECREATE_NOTIFICATION".equals(intent.getAction())) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.notify(NOTIFICATION_ID, buildNotification());
            }
            return START_STICKY;
        }

        if (intent != null) {
            value = intent.getIntExtra("value", 0);
            permanence = intent.getIntExtra("permanence", 0);
            valueWithDiscount =  intent.getIntExtra("valueWithDiscount", 0);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.notify(NOTIFICATION_ID, buildNotification());
            }

            permanence++;
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
        fetchAndUpdateValue();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, BeeMovingNotificationService.class);
        intent.putExtra("value", value);
        intent.putExtra("permanence", permanence);
        intent.putExtra("valueWithDiscount", valueWithDiscount);

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

    private void fetchAndUpdateValue() {
        stayService.getActiveStay().enqueue(new Callback<StayResponse>() {
            @Override
            public void onResponse(Call<StayResponse> call, Response<StayResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    value = response.body().getValue();
                    Log.e("BeeService", "Resposta do servidor: " + response.body());
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.notify(NOTIFICATION_ID, buildNotification());
                    }
                } else {
                    Log.e("BeeService", "Erro na resposta do servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StayResponse> call, Throwable t) {
                Log.e("BeeService", "Erro ao chamar o endpoint", t);
            }
        });
    }

    private void cancelNotificationUpdate() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, BeeMovingNotificationService.class);
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

        RemoteViews notificationLayoutSmall = new RemoteViews(getPackageName(), R.layout.beemoving_notification_layout_small);
        RemoteViews notificationLayoutLarge = new RemoteViews(getPackageName(), R.layout.beemoving_notification_layout_large);
        notificationLayoutSmall.setTextViewText(R.id.permanence, formatTime(permanence));
        notificationLayoutSmall.setTextViewText(R.id.valueWithDiscount, String.valueOf(valueWithDiscount));
        notificationLayoutSmall.setTextViewText(R.id.value, String.valueOf(value));
        notificationLayoutLarge.setTextViewText(R.id.permanence, formatTime(permanence));
        notificationLayoutLarge.setTextViewText(R.id.valueWithDiscount, String.valueOf(valueWithDiscount));
        notificationLayoutLarge.setTextViewText(R.id.value, String.valueOf(value));

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.hubees)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayoutSmall)
                .setCustomBigContentView(notificationLayoutLarge)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setShowWhen(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build();
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

    private String formatTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02dh%02d", hours, mins);
    }
}
