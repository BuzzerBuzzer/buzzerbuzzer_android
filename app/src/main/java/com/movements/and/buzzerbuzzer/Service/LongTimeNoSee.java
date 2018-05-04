package com.movements.and.buzzerbuzzer.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.FriendList;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 9. 26..
 */

public class LongTimeNoSee extends BroadcastReceiver {
    private JsonConverter jc;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password;
    private LongtimeAlarm lt;
    private boolean CheckLongtime;
    private Context mContext;

    private Calendar calendar;

    private int type = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        setting = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        lt = new LongtimeAlarm();
        jc = new JsonConverter();
        mContext = context;

        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        CheckLongtime = setting.getBoolean("logtimecheck", true);//longtimenosee 세팅값

        if(!CheckLongtime){
            return;
        }

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        //주중 (일 ~ 목) : 11:30 , 17:30    - 주말 (금 ~ 토) : 11:30 , 14:00 , 20:00
        Bundle bundle = intent.getExtras();
        type = bundle.getInt("ALRAM_TYPE");

        if (type == 0) {
            getUser(id, mContext);
           // Log.d("브로드", "11:00");
        }

        if (type == 5 ) {
            getUser(id, mContext);
            // Log.d("브로드", "11:00");
        }

        if (day == 6 || day == 7) {
            if (type == 1 || type == 3)
                getUser(id, mContext);
         //   Log.d("브로드", "휴일");
        } else if (type == 2) {
            getUser(id, mContext);
           // Log.d("브로드", "휴일아님");
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
           // Toast.makeText(context, "BOOT_COMPLETED", Toast.LENGTH_SHORT).show();
            Log.e("호출된페이지", "BOOT_COMPLETED");
            //lt.setAlarm(context, 1000);
            if (CheckLongtime) {
                Log.e("호출된페이지", "CheckLongtime  :" + String.valueOf(CheckLongtime));
                Intent intent1 = new Intent(
                        context,//현재제어권자
                        LongTimeNoSee.class); // 이동할 컴포넌트
                context.startService(intent1); // 서비스 시작
                lt.setAlarm(context, 1000);
            }


        } else {

            Log.e("호출된페이지", "non ACTION_BOOT_COMPLETED");
        }
    }

    private void getUser(String user1, final Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("롱티 클래스", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                Log.e("searching_long_t suc", response);
                            } else {//json.getString("result").equals("fail")시
                                //Toast.makeText(context, "searching_long_t failed", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        //Toast.makeText(context, "searching_long_t Error!", Toast.LENGTH_LONG).show();
                        Log.e("searching_long_t Error", String.valueOf(error));
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //  Log.d("파라", id);
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_00_get_long_t_user", new String[]{id}));//서버 서비코드
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
