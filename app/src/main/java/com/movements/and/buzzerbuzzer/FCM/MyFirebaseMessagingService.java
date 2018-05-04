package com.movements.and.buzzerbuzzer.FCM;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.movements.and.buzzerbuzzer.BuzzerPurchase;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.FriendList;
import com.movements.and.buzzerbuzzer.OpeningActivity;
import com.movements.and.buzzerbuzzer.R;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.Foreground;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.chat.GroupChannelActivity;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBirdException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.movements.and.buzzerbuzzer.BuzzerPurchase;
import com.movements.and.buzzerbuzzer.OpeningActivity;
import com.movements.and.buzzerbuzzer.R;
import com.movements.and.buzzerbuzzer.chat.GroupChannelActivity;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBirdException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.movements.and.buzzerbuzzer.FriendList.id;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    //http://kitesoft.tistory.com/49
    // [START receive_message]
    public static Context mContext;
    private JsonConverter jc;
    private static SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private static boolean previewChat;
    private static final String TAG = "MyFirebaseMsgService";
    private Handler handler;
    private int badgeCount=0;
    private Boolean backCheck = false;
    private Boolean isNewsPush = false;
    private ConfirmDialog dialog;
    private String id, mPassword, password;
    private PasswordEn passwordEn;
    public static int badgeCount2 = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {//앱 실행시...
        Log.e("푸시 왔음","온메세지 리시브");
        backCheck = isAppIsInBackground(this);
        Log.e("백그라운드체크", String.valueOf(backCheck));
        jc = new JsonConverter();
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        id = setting.getString("user_id", "");
        editor = setting.edit();
        passwordEn = new PasswordEn();

        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        unReadCount();

        if(setting.getString("isLogedCheck","true").equals("true")) {
            Log.e("로그인 체크 안으로 들어옴","isLoged 트루");

            //TODO:푸시메세지 에러코드 NullPointException임.
            /**
             E/AndroidRuntime: FATAL EXCEPTION: pool-9-thread-1
             Process: com.movements.and.buzzerbuzzer, PID: 19699
             java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String com.google.firebase.messaging.RemoteMessage$Notification.getTitle()' on a null object reference
             at com.movements.and.buzzerbuzzer.FCM.MyFirebaseMessagingService.onMessageReceived(MyFirebaseMessagingService.java:34)
             at com.google.firebase.messaging.FirebaseMessagingService.handleIntent(Unknown Source)
             at com.google.firebase.iid.zzc.run(Unknown Source)
             at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1133)
             at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:607)
             at java.lang.Thread.run(Thread.java:761)
             D/LIFECYCLE: GroupChannelListFragment onDetach()
             **/
            try {
//            sendPushNotification( remoteMessage.getNotification().getBody());
            } catch (NullPointerException e) {
                Log.e(TAG, String.valueOf(e));
            }

            setting = getSharedPreferences("setting", MODE_PRIVATE);
            editor = setting.edit();

            // TODO(developer): Handle FCM messages here.
            // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
            Log.d(TAG, "From: " + remoteMessage.getFrom());

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                Log.d(TAG, "Message data payload: " + remoteMessage.getMessageType());
                Log.d(TAG, "Message data payload: " + remoteMessage.getMessageId());
                Log.d(TAG, "Message data payload: " + remoteMessage.getData().toString().contains("sendbird"));
                String channelUrl = null;
                if (remoteMessage.getData().get("sendbird") == null) {
                    return;
                }
                try {
                    JSONObject sendBird = new JSONObject(remoteMessage.getData().get("sendbird"));
                    JSONObject channel = (JSONObject) sendBird.get("channel");
                    channelUrl = (String) channel.get("channel_url");
                    badgeCount = (int) sendBird.get("unread_message_count");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Also if you intend on generating your own notifications as a result of a received FCM
                // message, here is where that should be initiated. See sendNotification method below.

                sendNotification(this, remoteMessage.getData().get("message"), channelUrl, badgeCount);
            }

            if(backCheck == true){
                //TODO: 여기서 푸시가 뭔지 확인 가능할듯한데..
                // Check if message contains a notification payload.

                if (remoteMessage.getNotification() != null) {
                    Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                    if (remoteMessage.getNotification().getBody().contains("팔로우")
                            || remoteMessage.getNotification().getBody().contains("프로필 사진")) {
                        editor.putBoolean("news_push", true);
                        editor.commit();
                        Log.e("넘기기 직전 배지카운트", String.valueOf(badgeCount2));
                        sendPushNotification(this, remoteMessage.getNotification().getBody(), badgeCount2);
                        isNewsPush = true;
                    }else if(remoteMessage.getNotification().getBody().contains("Bz코인")){
                        OpeningActivity.eventCheck = true;
                        sendPushNotification(this, remoteMessage.getNotification().getBody(), badgeCount2);
                    }else{
                        sendPushNotification(this, remoteMessage.getNotification().getBody(), badgeCount2);
                    }
                }
            }else{
                if (remoteMessage.getNotification() != null) {
                    if (remoteMessage.getNotification().getBody().contains("Bz코인")) {
                        bzcoinEvent();
                    }
                }
            }



            //setBadge(getApplicationContext(), badgeCount);

            /*
            if(isNewsPush != true){
                unReadCount();
            }*/

            mContext = this;
        }
    }

    public int getBadgeCount(){
        return badgeCount;
    }



    private void sendPushNotification(Context context, String body, int badgeCount) {

        Log.e(TAG, "센드푸시노티 안에 언리드: " + setting.getInt("unread_badge_count",0));
        Intent intent = new Intent(this, FriendList.class);
        intent.putExtra("event_noti_click", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        SystemClock.sleep(1000);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(this.getResources().getString(R.string.app_name))
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                //.setDefaults(Notification.DEFAULT_ALL)
                .setNumber(setting.getInt("unread_badge_count",0))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     * @param badgeCount
     */
    public static void sendNotification(Context context, String messageBody, String channelUrl, int badgeCount) {
        Intent intent = new Intent(context, GroupChannelActivity.class);
        intent.putExtra("groupChannelUrl", channelUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.d("샌드버드", messageBody);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                //.setDefaults(Notification.DEFAULT_ALL)
//                .setFullScreenIntent(pendingIntent, true);
                .setNumber(badgeCount)
                .setContentIntent(pendingIntent);

        previewChat = setting.getBoolean("sendbird_preview", true);//채팅 미리보기
        Log.e(TAG, String.valueOf(setting.getBoolean("sendbird_preview", true)));//채팅 미리보기

        if (previewChat == true) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }else
            return;
    }


/*
    public void setBadge(final Context context, final int count) {
        final String launcherClassName = getLauncherClassName(context);

        if (launcherClassName == null) {
            return;
        }
        isNewsPush = false;
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");

        Log.e("인텐트보낼때 배지카운트 : ", String.valueOf(count));
        intent.putExtra("badge_count_package_name", getApplicationContext().getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        intent.putExtra("badge_count", count);
        sendBroadcast(intent);
    }

    private static String getLauncherClassName(Context context) {
        final PackageManager pm = context.getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        for (final ResolveInfo resolveInfo : resolveInfos) {
            final String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }

        return null;
    }
*/
    private void unReadCount(){
        GroupChannel.getTotalUnreadMessageCount(new GroupChannel.GroupChannelTotalUnreadMessageCountHandler() {
            @Override
            public void onResult(int i, SendBirdException e) {
                badgeCount2 = i;
                editor.putInt("unread_badge_count", i);
                editor.commit();
            }
        });
    }

    @Override
    public void handleIntent(Intent intent){
        try
        {
            if (intent.getExtras() != null)
            {
                RemoteMessage.Builder builder = new RemoteMessage.Builder("MyFirebaseMessagingService");
                for (String key : intent.getExtras().keySet())
                {
                    builder.addData(key, intent.getExtras().get(key).toString());
                }
                onMessageReceived(builder.build());
            }
            else
            {
                super.handleIntent(intent);
            }
        }
        catch (Exception e)
        {
            super.handleIntent(intent);
        }
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    private void bzcoinEvent() {
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String msg;
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        Log.e("부저코인 이벤트 ","성공");
                        OpeningActivity.EventMsgFore = json.getString("msg");
                        OpeningActivity.eventCheckFore = true;
                    } else {
                        Log.e("부저코인 이벤트 ","타임아웃");
                        OpeningActivity.EventMsgFore = json.getString("msg");
                        OpeningActivity.eventCheckFore = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("부저코인 이벤트 ","에러");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_9999_event_confirm", new String[]{id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
        OpeningActivity.eventCheck = false;
    }

}