package com.movements.and.buzzerbuzzer.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by samkim on 2017. 8. 4..
 */

public class TimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Intent i = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, 0);

//        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        b.setSound(notification)
//                .setContentTitle("Buzzer Buzzer")
//                .setAutoCancel(true)
//                .setContentText("위치 고정 시간이 끝났습니다")
//                .setSmallIcon(android.R.drawable.ic_notification_clear_all)
//                .setContentIntent(pIntent);
//
//        Notification n = b.build();
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(0, n);

    }
}
