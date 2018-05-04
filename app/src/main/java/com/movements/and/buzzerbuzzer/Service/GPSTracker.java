package com.movements.and.buzzerbuzzer.Service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by samkim on 2017. 9. 7..
 */
public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    private boolean isGPSEnabled = false;

    // flag for network status
    private boolean isNetworkEnabled = false;

    // flag for GPS status
    private boolean canGetLocation = false;

    private Location location; // location
    public static double latitude; // latitude
    public static double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 30; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    private Handler mHandler;
    public static String realtimefixedlocation;

    private String id, mPassword, password;
    private SharedPreferences setting;
    public static String GPSstate = "error";
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    // public static boolean CHECK_SELECT_REAL_GPS = false;
    public boolean CHECK_UPDATE_GPS = true;

    private JsonConverter jc;

    public GPSTracker(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        jc = new JsonConverter();
        getLocation();
        // Log.d("핸들러트래커", "GPSTracker");
    }

    public GPSTracker(Context context) {
        this.mContext = context;
        jc = new JsonConverter();
        getLocation();
        // Log.d("핸들러트래커", "GPSTracker");
    }

    public void Update() {
        getLocation();
        //Log.d("핸들러업뎃", "update");
    }

    public Location getLocation() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);


            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                GPSstate = "normal";
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        sendString(location);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            //update_gps_cycle(id, latitude, longitude, realtimefixedlocation, GPSstate,"first");
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            sendString(location);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                //   update_gps_cycle(id, latitude, longitude, realtimefixedlocation, GPSstate,"first2");
                            }
                        }
                    }
                }
            }
            sendaddress(getAddress(latitude, longitude));
//            sendString(location);
            realtimefixedlocation = getAddress(latitude, longitude);
//            if (CHECK_UPDATE_GPS) {
//                update_gps_cycle(id, latitude, longitude, realtimefixedlocation, GPSstate, "first3");
//            }
//            CHECK_UPDATE_GPS = false;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS 가 꺼져있습니다");

        // Setting Dialog Message
        //alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setMessage("원활한 이용을 위해\n'Google 위치 서비스'를 사용함으로 체크해주세요");

        // On pressing Settings button
        alertDialog.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                mContext.startActivity(intent);
                ((Activity) mContext).startActivityForResult(intent, 1);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    private void sendString(Location location) {
        // 메시지 얻어오기
        Message msg = mHandler.obtainMessage();
        // 메시지 ID 설정
        msg.what = FriendList.SEND_GPS;
        // 메시지 내용 설정 (Object)
        msg.obj = new String(String.valueOf(location.getLatitude()));
        // 메시지 전
        mHandler.sendMessage(msg);
    }

    private void sendString1(Location location) {
        // 메시지 얻어오기
        Message msg = mHandler.obtainMessage();
        // 메시지 ID 설정
        msg.what = FriendList.SEND_GPS;
        // 메시지 내용 설정 (Object)
        msg.obj = new String(String.valueOf(location.getLatitude()) + "체인지");
        // 메시지 전
        mHandler.sendMessage(msg);
    }

    private void sendaddress(String address) {
        // 메시지 얻어오기
        Message msg = mHandler.obtainMessage();
        // 메시지 ID 설정
        msg.what = FriendList.SEND_ADDR;
        // 메시지 내용 설정 (Object)
        msg.obj = new String(address);
        // 메시지 전
        mHandler.sendMessage(msg);
    }

//    private void sendString(String str) {
//        // 메시지 얻어오기
//        Message msg = mHandler.obtainMessage();
//        // 메시지 ID 설정
//        msg.what = FriendList.SEND_PRINT;
//        // 메시지 내용 설정 (Object)
//        msg.obj = new String(str);
//        // 메시지 전
//        mHandler.sendMessage(msg);
//    }


    private void sendDisable(String error) {
        // 메시지 얻어오기
        Message msg = mHandler.obtainMessage();
        // 메시지 ID 설정
        msg.what = FriendList.SEND_PRINT;
        // 메시지 내용 설정 (Object)
        msg.obj = new String(error);
        // 메시지 전
        mHandler.sendMessage(msg);
    }


    @Override
    public void onLocationChanged(Location location) {

        jc = new JsonConverter();
        if (location != null) {
            //CHECK_UPDATE_GPS = false;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            //Toast.makeText(mContext, "onLocationChanged is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();
            //sendString("onLocationChanged is - \nLat: " + latitude + "\nLong: " + longitude + " provider:"+location.getProvider()+" mock:"+location.isFromMockProvider());
            realtimefixedlocation = getAddress(latitude, longitude);
            //update_gps_cycle(id, latitude, longitude, realtimefixedlocation, GPSstate, "change");//3분
            sendString1(location);

            try{
                sendaddress(getAddress(latitude, longitude));
            }catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(mContext, "onProviderDisabled " + provider, Toast.LENGTH_SHORT).show();
        mHandler.sendEmptyMessage(FriendList.RENEW_GPS);
        GPSstate = "error";
        update_gps_cycle(id, latitude, longitude, realtimefixedlocation, GPSstate, "dis");//3분
        //sendString( "onProviderDisabled " + provider);
        sendDisable("error");
    }


    @Override
    public void onProviderEnabled(String provider) {
        //Toast.makeText(mContext, "onProviderEnabled " + provider, Toast.LENGTH_SHORT).show();
        mHandler.sendEmptyMessage(FriendList.RENEW_GPS);
        GPSstate = "normal";
        //update_gps_cycle(id, latitude, longitude, realtimefixedlocation, GPSstate);//3분
        sendDisable("normal");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {//프로바이더 체인지
        //Toast.makeText(mContext, "onStatusChanged " + provider + " : " + status, Toast.LENGTH_SHORT).show();
        mHandler.sendEmptyMessage(FriendList.RENEW_GPS);
        //sendString("onStatusChanged " + provider + " : " + status + ":" + printBundle(extras));
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;

        return null;
    }


//

    public String getAddress(double lat, double lng) {
        String address = null;
        String addres = null;

        //위치정보를 활용하기 위한 구글 API 객체

        //주소 목록을 담기 위한 HashMap
        List<Address> list = null;
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            list = geocoder.getFromLocation(lat, lng, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (list == null) {
            Log.e("getAddress", "주소 데이터 얻기 실패");
            return null;
        }

        if (list.size() > 0) {
            Address addr = list.get(0);
            addres = addr.getAdminArea() + " " + addr.getLocality() + " " + addr.getSubLocality() + " " + addr.getThoroughfare() + " " + addr.getPostalCode();
            address = addres.replace("null", "");
            //address = " adminarea: "+addr.getAdminArea() + " getPostalCode: "+addr.getPostalCode()+ " getLocality: "+addr.getLocality() + " getThoroughfare: " + addr.getThoroughfare()+" getFeatureName: "+addr.getFeatureName();
            //adminarea: null getPostalCode: 135-081 getLocality: 서울특별시 getThoroughfare: null getFeatureName: 25
        } else {
            address = "GPS 활성화 해주십시오";
        }
        Log.e("GPSTracker 실제주소", address);
        update_gps_cycle(id, latitude, longitude, realtimefixedlocation, GPSstate, "first3");
        return address;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        locationManager.removeUpdates();
        stopUsingGPS();

    }

    @SuppressLint("LongLogTag")
    private void update_gps_cycle(String id1, double latitude1, double longitude1, String realtimefixedlocation1, String normal1, final String change) {

        setting = mContext.getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();


        id = setting.getString("user_id","");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        Log.e("GPSTracker update_gps_cycle", id + ", " + FriendList.id);

        //final String id = id1;
        final String latitude = String.valueOf(latitude1);
        final String longitude = String.valueOf(longitude1);
        final String realtimefixedlocation = realtimefixedlocation1;
        final String normal = normal1;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("GPSTracker update_gps_cycle", String.valueOf(response));
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {
                                //Toast.makeText(mContext, "활성화" + change, Toast.LENGTH_SHORT).show();

                            } else {
                                //Toast.makeText(mContext, "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
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
                        Log.e("update_gps_cycle", " update_gps_cycle Error!");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(setting.getString("user_id",""), mPassword, "android", String.valueOf(Build.VERSION.RELEASE).toString().trim(), Build.MODEL,"proc_00_update_real_gps", new String[]{setting.getString("user_id",""), latitude, longitude, realtimefixedlocation+"_", normal}));//no55
                Log.e("proc_00_update_real_gps : ", String.valueOf(params));
                //Log.e("한글깨짐 확인", Build.VERSION.RELEASE);
                //Log.e("id 확인", setting.getString("user_id",""));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }



    public String getTimeStr() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        //SimpleDateFormat sdfNow = new SimpleDateFormat("MM/dd HH:mm:ss");
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm:ss");
        return sdfNow.format(date);
    }
}
