package com.movements.and.buzzerbuzzer.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 11. 8..
 */

public class RealtimeGpsBroadReceiver extends BroadcastReceiver {
    private JsonConverter jc;
    private int type=-1;
    private Context mContext;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password;

    private GPSTracker gps = null;
    private Handler mHandler;
    @Override
    public void onReceive(Context context, Intent intent) {
        jc=new JsonConverter();
        mContext = context;
        setting = context.getSharedPreferences("setting", context.MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        mHandler=new Handler();
        gps=new GPSTracker(context,mHandler);
        //gps.Update();//지피
        Bundle bundle = intent.getExtras();
        type = bundle.getInt("real_time");

        Log.e("알람 실행중","알람 실행중");

        if (type == 0) {
            if(GPSTracker.latitude!=0.0) {
                update_gps_cycle(id, GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);
            }else {
                base_gps("id", GPSTracker.GPSstate);
            }
        }

        //TODO:실시간 gps 전송 부분인데 30분 적용 / 기본, 현위치는 여기다 적용하면되나? 흠.. 다른데 적용되고있는건가..
//        if(type==0&&setting.getString("user_id", "")!=null){
//            if(GPSTracker.latitude!=0.0) {
//                update_gps_cycle(id, GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);
//            }else {
//                base_gps("id", GPSTracker.GPSstate);
//            }
//
//        }
    }

    private void update_gps_cycle(String id2, double latitude1, double longitude1, String gpsaddress1, String normal1) {
        final String id = id2;
        final String real_gps_x = String.valueOf(latitude1);
        final String real_gps_y = String.valueOf(longitude1);
        final String real_gps_address = gpsaddress1;
        final String received_gps_state = normal1;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("실시간 gps 확인 ", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {
                            } else {
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
                        //Toast.makeText(mContext, " update_gps_cycle!", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_00_update_real_gps", new String[]{id, real_gps_x, real_gps_y, real_gps_address+"_", received_gps_state, ""}));
                Log.e("proc_00_update_real_gps", String.valueOf(params));
                return params;
            }
        };
        RequestQueue requestQueu = Volley.newRequestQueue(mContext);
        requestQueu.add(stringRequest);
    }

    private void base_gps(String id1, String normal) {
        final String in_id = id;
        final String in_received_gps_state = normal;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {
                            } else {
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
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_29_select_base_gps", new String[]{in_id, in_received_gps_state}));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }
}
