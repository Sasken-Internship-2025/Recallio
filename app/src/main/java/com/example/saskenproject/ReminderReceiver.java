package com.example.saskenproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String noteTitle = intent.getStringExtra("noteTitle");
        String noteContent = intent.getStringExtra("noteContent");

        // Intent to open AddNoteActivity with note data
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("noteTitle", noteTitle);
        notificationIntent.putExtra("noteContent", noteContent);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ReminderChannel")
                .setSmallIcon(R.drawable.ic_notification_background)
                .setContentTitle(noteTitle)
                .setContentText(noteContent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(noteContent))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // 🔒 Check for POST_NOTIFICATIONS permission before showing notification
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {

            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.notify((int) System.currentTimeMillis(), builder.build());

        } else {
            Log.w("ReminderReceiver", "Notification permission not granted");
        }
    }
}
