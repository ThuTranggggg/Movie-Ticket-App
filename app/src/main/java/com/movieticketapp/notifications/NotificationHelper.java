package com.movieticketapp.notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.movieticketapp.R;
import com.movieticketapp.activities.TicketDetailActivity;
import com.movieticketapp.utils.DateTimeUtils;
import com.movieticketapp.utils.IntentKeys;

public final class NotificationHelper {
    public static final String CHANNEL_ID = "movie_showtime_reminders";
    public static final String EXTRA_NOTIFICATION_TITLE = "extra_notification_title";
    public static final String EXTRA_NOTIFICATION_BODY = "extra_notification_body";

    private NotificationHelper() {
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(context.getString(R.string.notification_channel_description));
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void showNotification(Context context, String title, String body, String ticketId) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, TicketDetailActivity.class);
        intent.putExtra(IntentKeys.EXTRA_TICKET_ID, ticketId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                ticketId == null ? 100 : ticketId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat.from(context).notify(ticketId == null ? 2024 : ticketId.hashCode(), builder.build());
    }

    public static void scheduleReminder(Context context, String ticketId, String movieTitle, String startTime) {
        long triggerAtMillis = DateTimeUtils.getReminderTriggerAtMillis(startTime);
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(IntentKeys.EXTRA_TICKET_ID, ticketId);
        intent.putExtra(EXTRA_NOTIFICATION_TITLE, context.getString(R.string.notification_title_prefix) + ": " + movieTitle);
        intent.putExtra(EXTRA_NOTIFICATION_BODY, context.getString(R.string.notification_default_body));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                ticketId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }
}
