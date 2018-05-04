package com.movements.and.buzzerbuzzer;


import android.app.Activity;
import android.app.Application;

import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.chatutils.PreferenceUtils;
import com.sendbird.android.SendBird;


public class GlobalApplication extends Application {
    private static volatile GlobalApplication obj = null;
    private static volatile Activity currentActivity = null;


    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        PreferenceUtils.init(getApplicationContext());
        SendBird.init(Config.SendBird_APPID, getApplicationContext());
    }

//    public static GlobalApplication getGlobalApplicationContext() {
//        return obj;
//    }
//
//    public static Activity getCurrentActivity() {
//        return currentActivity;
//    }
//
//    // Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
//    public static void setCurrentActivity(Activity currentActivity) {
//        GlobalApplication.currentActivity = currentActivity;
//    }

}

