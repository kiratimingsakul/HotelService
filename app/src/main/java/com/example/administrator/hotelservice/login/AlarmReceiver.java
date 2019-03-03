package com.example.administrator.hotelservice.login;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.example.administrator.hotelservice.MainActivity;
import com.example.administrator.hotelservice.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

/**
 import android.app.Notification;
 import android.app.NotificationChannel;
 import android.app.NotificationManager;
 import android.app.PendingIntent;
 import android.app.TaskStackBuilder;
 import android.content.BroadcastReceiver;
 import android.content.Context;
 import android.content.Intent;
 import android.os.Build;

 import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

 /**
 * Created by Kirati Mingsakul on 12/17/2017.
 */

class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "com.singhajit.notificationDemo.channelId";
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    String datetime;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, Fesinthai.class);
        Calendar cal = Calendar.getInstance();
        datetime = sdf.format(cal.getTime());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = builder.setContentTitle("งานภายในวันที่")
                .setContentText(datetime)
                .setTicker("Festival In Date")
                .setSound(soundUri)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationDemo",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notification);
    }
}
