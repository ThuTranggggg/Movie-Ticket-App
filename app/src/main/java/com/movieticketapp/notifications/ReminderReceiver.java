package com.movieticketapp.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.movieticketapp.utils.IntentKeys;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(NotificationHelper.EXTRA_NOTIFICATION_TITLE);
        String body = intent.getStringExtra(NotificationHelper.EXTRA_NOTIFICATION_BODY);
        String ticketId = intent.getStringExtra(IntentKeys.EXTRA_TICKET_ID);
        NotificationHelper.showNotification(context, title, body, ticketId);
    }
}
