package com.hubees.hubeesnotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import android.provider.Settings;

@CapacitorPlugin(name = "HubeesNotification")
public class HubeesNotificationPlugin extends Plugin {
    private static final String CHANNEL_ID = "custom_notification_channel";

    @Override
    public void load() {
        super.load();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Custom Notifications",
                NotificationManager.IMPORTANCE_LOW
            );

            channel.setSound(null, null); // Sem som
            channel.setVibrationPattern(new long[0]); // Sem vibração
            channel.enableVibration(false); // Desabilita vibração

            NotificationManager manager = getContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @PluginMethod
    public void isNotificationClosed(PluginCall call) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        boolean isClosed = sharedPreferences.getBoolean("notificationClosed", false);

        JSObject ret = new JSObject();
        ret.put("notificationClosed", isClosed);
        call.resolve(ret);
    }

    @PluginMethod
    public void sendNotificationBeePass(PluginCall call) {
        int remainingTime = call.getInt("remainingTime", 0);
        int permanence = call.getInt("permanence", 0);
        String arrivalTime = call.getString("arrivalTime");

        // Redefine o valor de notificationClosed para false
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notificationClosed", false);
        editor.apply();

        // Cria um Intent para iniciar o serviço
        Intent serviceIntent = new Intent(getContext(), BeePassNotificationService.class);
        serviceIntent.putExtra("remainingTime", remainingTime);
        serviceIntent.putExtra("permanence", permanence);
        serviceIntent.putExtra("arrivalTime", arrivalTime);

        getContext().startService(serviceIntent);

        JSObject response = new JSObject();
        response.put("success", true);
        call.resolve(response);
    }

    @PluginMethod
    public void closeNotification(PluginCall call) {
        // Cria um Intent para chamar o BroadcastReceiver
        Intent closeIntent = new Intent(getContext(), NotificationReceiver.class);
        closeIntent.setAction("CLOSE_NOTIFICATION");

        // Envia o Intent para o BroadcastReceiver
        getContext().sendBroadcast(closeIntent);

        // Retorna uma resposta de sucesso
        JSObject response = new JSObject();
        response.put("success", true);
        call.resolve(response);
    }

    @PluginMethod
    public void isExactAlarmPermissionGranted(PluginCall call) {
        JSObject ret = new JSObject();
        boolean isGranted = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            isGranted = alarmManager != null && alarmManager.canScheduleExactAlarms();
        } else {
            isGranted = true;
        }

        ret.put("granted", isGranted);
        call.resolve(ret);
    }

    @PluginMethod
    public void requestExactAlarmPermission(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intentPermission = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intentPermission.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intentPermission);
                call.resolve();
            } else {
                call.resolve();
            }
        } else {
            call.reject("Este recurso não é suportado em versões do Android abaixo do Android S.");
        }
    }

}
