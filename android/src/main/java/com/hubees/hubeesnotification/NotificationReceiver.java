package com.hubees.hubeesnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.content.SharedPreferences;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("CLOSE_NOTIFICATION".equals(intent.getAction())) {
            // Parar o serviço
            context.stopService(new Intent(context, BeePassNotificationService.class));

            // Cancelar a notificação
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(1);  // O '1' é o ID da notificação
            }

            // Salva o estado de fechamento da notificação no SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notificationClosed", true); // Define que a notificação foi fechada
            editor.apply();
        }
    }
}

