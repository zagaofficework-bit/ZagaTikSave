package com.zagavideodown.app.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;

import com.zagavideodown.app.GlobalConstant;
import com.zagavideodown.app.R;

public class OreoNotification extends ContextWrapper {
    private final Context innerContext;
    private NotificationManager notificationManager;


    public OreoNotification(Context base) {
        super(base);
        innerContext = base;
        createChannel(base);
    }

    private void createChannel(Context base) {

        if (getManager().getNotificationChannel(base.getPackageName()) == null) {

            GlobalConstant.myNotificationChannel = innerContext.getResources().getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(base.getPackageName(),
                    innerContext.getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(innerContext.getResources().getString(R.string.aio_auto));
            channel.enableLights(false);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }

    public Notification.Builder getOreoNotification(String title, String body,
                                                    PendingIntent pendingIntent, Uri soundUri, int icon) {
        return new Notification.Builder(getApplicationContext(), innerContext.getPackageName())
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(icon)
                .setSound(soundUri)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
    }
}

