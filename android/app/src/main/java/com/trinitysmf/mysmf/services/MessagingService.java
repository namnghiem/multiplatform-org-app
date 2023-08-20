package com.trinitysmf.mysmf.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.trinitysmf.mysmf.R;
import com.trinitysmf.mysmf.ui.activities.MainActivity;


/**
 * Created by namxn_000 on 17/11/2017.
 */

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;

        // create message.
        if (remoteMessage.getNotification() != null && remoteMessage.getData().size()>0) {
            createNotification(remoteMessage);
        }

    }

    private void createNotification (RemoteMessage notification) {
        //get URI
        String link = notification.getData().get("link");
        Intent intent;
        if(link!=null) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        }else{
            intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity( this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        androidx.core.app.NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder( this)
                .setSmallIcon(R.drawable.ic_stat_trinity_logo11)
                .setContentTitle(notification.getNotification().getTitle())
                .setContentText(notification.getNotification().getBody())
                .setAutoCancel( true )
                .setSound(notificationSoundURI)
                .setContentIntent(resultIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder.build());
    }
}
