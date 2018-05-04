package com.movements.and.buzzerbuzzer.FCM;


import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;


/**
 * Created by samkim on 2016-09-28.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    //private JsonConverter jc;

    //[START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("firebasetoken", "Refreshed token: " + token);

        // TODO: Implement this method to send any registration to your app's servers.
        //sendRegistrationToServerbuzzer(token);
        sendRegistrationToServer(token);//sendbird
    }

    private void sendRegistrationToServer(String refreshedToken) {
        // TODO: Implement this method to send token to your app server.
        SendBird.registerPushTokenForCurrentUser(refreshedToken, new SendBird.RegisterPushTokenWithStatusHandler() {
            @Override
            public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(MyFirebaseInstanceIDService.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pushTokenRegistrationStatus == SendBird.PushTokenRegistrationStatus.PENDING) {
                    //Toast.makeText(MyFirebaseInstanceIDService.this, "Connection required to register push token.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"Connection required to register push token.");
                }
            }
        });
    }

}
