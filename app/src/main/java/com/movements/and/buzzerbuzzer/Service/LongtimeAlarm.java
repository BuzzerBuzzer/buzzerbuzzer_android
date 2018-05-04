package com.movements.and.buzzerbuzzer.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by samkim on 2017. 9. 27..
 */

public class LongtimeAlarm {
    //private static final String INTENT_ACTION = "buzer.notimenosee.alarmmanager";
    private static final long A_WEEK = 1000 * 60 * 60 * 24 * 7;
    private static final long EVERYDAYS = 1000 * 60 * 60 * 24;
    private static final long REALGPSINTERVAL = 1000 * 60 * 30;
    private static final long REALGPSINTERVAL_H = 1000 * 60 * 60;
    private Calendar calendar;
    private Calendar calendar1;
    private Calendar calendar2;
    private Calendar calendar3;

    // 알람 등록
    public void setAlarm(Context context, long second) {

        Log.i("알람", "setAlarm()");

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(System.currentTimeMillis());

        calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(System.currentTimeMillis());

        calendar3 = Calendar.getInstance();
        calendar3.setTimeInMillis(System.currentTimeMillis());

        //TODO: 30분 간격 셋팅
        PendingIntent pi3 = null;
        Intent intent3 = new Intent(context, LongTimeNoSee.class);
        intent3.putExtra("ALRAM_TYPE", 5);
//        calendar3.set(Calendar.HOUR_OF_DAY, 13);
        calendar3.set(Calendar.MINUTE, 29);

        if (calendar3.before(Calendar.getInstance())) {
            calendar3.add(Calendar.DATE, 1);
        }
        pi3 = PendingIntent.getBroadcast(context, 5, intent3, 0);
        alarmMgr.setRepeating(AlarmManager.RTC, calendar3.getTimeInMillis(), 1000*60*30, pi3);
        //alarmMgr.setRepeating(AlarmManager.RTC, 1000, 1000, pi3);

        /*
        //TODO: 주중 (일 ~ 목) : 11:30 , 17:30   / 주말 (금 ~ 토) : 11:30 , 14:00 , 20:00 셋팅.
        PendingIntent pi = null;
        Intent intent = new Intent(context, LongTimeNoSee.class);
        intent.putExtra("ALRAM_TYPE", 0);
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 00);

        before();
        pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), EVERYDAYS, pi);


        PendingIntent pi1 = null;
        Intent intent2 = new Intent(context, LongTimeNoSee.class);
        intent2.putExtra("ALRAM_TYPE", 1);
        calendar1.set(Calendar.HOUR_OF_DAY, 13);
        calendar1.set(Calendar.MINUTE, 30);

        if (calendar1.before(Calendar.getInstance())) {
            calendar1.add(Calendar.DATE, 1);
        }
        pi1 = PendingIntent.getBroadcast(context, 1, intent2, 0);
        alarmMgr.setRepeating(AlarmManager.RTC, calendar1.getTimeInMillis(), EVERYDAYS, pi1);

        PendingIntent pi3 = null;
        Intent intent4 = new Intent(context, LongTimeNoSee.class);
        intent4.putExtra("ALRAM_TYPE", 3);
        calendar2.set(Calendar.HOUR_OF_DAY, 19);
        calendar2.set(Calendar.MINUTE, 30);

        if (calendar2.before(Calendar.getInstance())) {
            calendar2.add(Calendar.DATE, 1);
        }
        pi3 = PendingIntent.getBroadcast(context, 3, intent4, 0);
        alarmMgr.setRepeating(AlarmManager.RTC, calendar2.getTimeInMillis(), EVERYDAYS, pi3);

        PendingIntent pi2 = null;
        Intent intent3 = new Intent(context, LongTimeNoSee.class);
        intent3.putExtra("ALRAM_TYPE", 2);
        calendar3.set(Calendar.HOUR_OF_DAY, 17);
        calendar3.set(Calendar.MINUTE, 30);

        if (calendar3.before(Calendar.getInstance())) {
            calendar3.add(Calendar.DATE, 1);
        }
        pi2 = PendingIntent.getBroadcast(context, 2, intent3, 0);
        alarmMgr.setRepeating(AlarmManager.RTC, calendar3.getTimeInMillis(), EVERYDAYS, pi2);
        */

    }

    private void before() {
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
    }

    // 알람 등록
    public void setRealGpsAlarm(Context context, long second) {//앱종료후 30분마다 gps업데이트

        Log.i("알람", "setRealGpsAlarm() ");

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        PendingIntent pendingIntent = null;
        Intent myIntent1 = new Intent(context,RealtimeGpsBroadReceiver.class);
        myIntent1.putExtra("real_time", 0);
        calendar.set(Calendar.MINUTE, 30);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        long triggerTime = 1000 * 60 * 15;
        pendingIntent = PendingIntent.getBroadcast(context,0, myIntent1,0);
        //manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), 1000*60*30,pendingIntent);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, triggerTime, pendingIntent);



//
//        PendingIntent pendingIntent2 = null;
//        Intent myIntent2 = new Intent(context,RealtimeGpsBroadReceiver.class);
//        myIntent2.putExtra("real_time", 1);
//        calendar1.set(Calendar.MINUTE, 30);
//
//        if (calendar1.before(Calendar.getInstance())) {
//            calendar1.add(Calendar.DATE, 1);
//        }
//        pendingIntent2 = PendingIntent.getBroadcast(context,1,myIntent2,0);
//        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), AlarmManager.INTERVAL_HOUR,pendingIntent2);



        //--------------------------------------------------------------------------------------------------

//        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        PendingIntent pendingIntent = null;
//        Intent myIntent = new Intent(context,RealtimeGpsBroadReceiver.class);
//        myIntent.putExtra("real_time", 0);
//        pendingIntent = PendingIntent.getBroadcast(context,0,myIntent,0);
//        manager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),REALGPSINTERVAL,pendingIntent);

    }

    // 알람 해제
    public void releaseAlarm(Context context) {
        Log.i("realgps알람", "releaseAlarm()");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent Intent = new Intent(context, LongTimeNoSee.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, Intent, 0);
        alarmManager.cancel(pIntent);

        // 주석을 풀면 먼저 실행되는 알람이 있을 경우, 제거하고
        // 새로 알람을 실행하게 된다. 상황에 따라 유용하게 사용 할 수 있다.
//		alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 3000, pIntent);
    }
}
