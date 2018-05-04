package com.movements.and.buzzerbuzzer.Utill;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by samkim on 2017. 8. 4..
 */

public class PrefUtils {
    private static final String START_TIME = "countdown_timer";
    private SharedPreferences mPreferences;

    public PrefUtils(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getStartedTime() {
        return mPreferences.getInt(START_TIME, 0);
    }

    public void setStartedTime(int startedTime) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(START_TIME, startedTime);
        editor.apply();
    }
}
