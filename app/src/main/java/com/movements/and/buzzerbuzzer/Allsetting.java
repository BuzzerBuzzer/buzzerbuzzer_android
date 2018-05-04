package com.movements.and.buzzerbuzzer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Service.LongTimeNoSee;
import com.movements.and.buzzerbuzzer.Service.LongtimeAlarm;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.BuzzerDialog;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.chatutils.PreferenceUtils;
import com.movements.and.buzzerbuzzer.policy.GpsPolicy;
import com.movements.and.buzzerbuzzer.policy.MembershipPolicy;
import com.movements.and.buzzerbuzzer.policy.PrivacyPolicy;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 7. 18..
 */
//앱설정 페이지 친구추천허용,서칭허용,전체알람받기,채팅미리보기등
public class Allsetting extends BaseActivity {

    private ImageView settingbtn;
    private TextView  tv, tv2, fr_searching2, tv3, tv4, tv5, tv6, tv7, tv8, tv20;
    private TextView frblock, followblock, store_purchase, chagepwd, changephone, expire, versiontx, policy, privateinfo, gpspolicy, logout, textView21;
    private Switch switch1, switch2, switch3, switch4, switch5, switch20;
    private RelativeLayout rel3, rel4, rel, rel10, rel11, rel12, rel13, rel14, rel15, rel16, rel17;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;
    private String id, mPassword, password;;
    private boolean checklongtime;
    private boolean checkChatpreview;
    private JsonConverter jc;
    private LongtimeAlarm lt;
    private final String TAG = "Allsetting";

    private Typeface typefaceBold, typefaceExtraBold;

    private BuzzerDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allsetting);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");



        textView21 = (TextView) findViewById(R.id.textView21);
        tv = (TextView)findViewById(R.id.tv);
        tv2 = (TextView)findViewById(R.id.tv2);
        fr_searching2 = (TextView)findViewById(R.id.fr_searching2);
        tv3 = (TextView)findViewById(R.id.tv3);
        tv4 = (TextView)findViewById(R.id.tv4);
        tv5 = (TextView)findViewById(R.id.tv5);
        tv6 = (TextView)findViewById(R.id.tv6);
        tv7 = (TextView)findViewById(R.id.tv7);
        tv8 = (TextView)findViewById(R.id.tv8);
        tv20 = (TextView)findViewById(R.id.tv20);


        settingbtn = (ImageView) findViewById(R.id.settingbtn);
        frblock = (TextView) findViewById(R.id.frblock);
        followblock = (TextView) findViewById(R.id.followblock);
        store_purchase = (TextView) findViewById(R.id.store_purchase);
        chagepwd = (TextView) findViewById(R.id.chagepwd);
        changephone = (TextView) findViewById(R.id.changephone);
        expire = (TextView) findViewById(R.id.expire);
        versiontx = (TextView) findViewById(R.id.versiontx);
        policy = (TextView) findViewById(R.id.policy);
        privateinfo = (TextView) findViewById(R.id.privateinfo);
        gpspolicy = (TextView) findViewById(R.id.gpspolicy);
        logout = (TextView) findViewById(R.id.logout);

        textView21.setTypeface(typefaceExtraBold);
        tv.setTypeface(typefaceExtraBold);
        tv2.setTypeface(typefaceExtraBold);
        fr_searching2.setTypeface(typefaceExtraBold);
        frblock.setTypeface(typefaceExtraBold);
        followblock.setTypeface(typefaceExtraBold);
        tv3.setTypeface(typefaceExtraBold);
        tv4.setTypeface(typefaceExtraBold);
        tv5.setTypeface(typefaceExtraBold);
        tv6.setTypeface(typefaceExtraBold);
        store_purchase.setTypeface(typefaceExtraBold);
        tv7.setTypeface(typefaceExtraBold);
        tv8.setTypeface(typefaceExtraBold);
        tv20.setTypeface(typefaceExtraBold);
        chagepwd.setTypeface(typefaceExtraBold);
        changephone.setTypeface(typefaceExtraBold);
        versiontx.setTypeface(typefaceExtraBold);
        policy.setTypeface(typefaceExtraBold);
        privateinfo.setTypeface(typefaceExtraBold);
        gpspolicy.setTypeface(typefaceExtraBold);
        logout.setTypeface(typefaceExtraBold);
        expire.setTypeface(typefaceExtraBold);

        expire.setPaintFlags(expire.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        switch1 = (Switch) findViewById(R.id.switch1);
        switch2 = (Switch) findViewById(R.id.switch2);
        switch3 = (Switch) findViewById(R.id.switch3);
        switch4 = (Switch) findViewById(R.id.switch4);
        switch5 = (Switch) findViewById(R.id.switch5);
        switch20 = (Switch) findViewById(R.id.switch20);
//        switch6 = (Switch) findViewById(R.id.switch6);

        rel3 = (RelativeLayout) findViewById(R.id.rel3);
        rel4 = (RelativeLayout) findViewById(R.id.rel4);
        rel = (RelativeLayout) findViewById(R.id.rel);
        rel10 = (RelativeLayout) findViewById(R.id.rel10);
        rel11 = (RelativeLayout) findViewById(R.id.rel11);
        rel12 = (RelativeLayout) findViewById(R.id.rel12);
        rel13 = (RelativeLayout) findViewById(R.id.rel13);
        rel14 = (RelativeLayout) findViewById(R.id.rel14);
        rel15 = (RelativeLayout) findViewById(R.id.rel15);
        rel16 = (RelativeLayout) findViewById(R.id.rel16);
        rel17 = (RelativeLayout) findViewById(R.id.rel17);

        passwordEn = new PasswordEn();

        jc = new JsonConverter();
        lt = new LongtimeAlarm();//longtimenosee push alarm 세팅
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        checklongtime = setting.getBoolean("logtimecheck", true);//longtimenosee push alarm세팅저장값 불러오기
        checkChatpreview = setting.getBoolean("sendbird_preview", true);
        switch4.setChecked(checkChatpreview);
        if (checklongtime) {
            Log.d("checklongtime", String.valueOf(checklongtime));
            switch5.setChecked(true);
        } else {
            switch5.setChecked(false);
        }

        touch();

        getswichstate(id);//친구 추천 허용 및 서치허용 세팅값 서버에서 불러오기
        able();//switch on off
    }

    private void getswichstate(String id1) {
        final String id = id1;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                // if(json.getString("data").equals("login_result"))
                                JSONArray data = json.getJSONArray("data");
                                JSONObject row = null;
                                row = data.getJSONObject(0);
                                int recommand = row.getInt("recommand_friend_yn");
                                int searching = row.getInt("searching_me_yn");
                                int news = row.getInt("push_news_yn");
                                int long_t = row.getInt("push_long_t_yn");
                                //editor.putStringSet("push_long_t", long_t);
                                int chat = row.getInt("push_chat_yn");
                                int event = row.getInt("push_event_yn");
                                if (recommand == 1) {
                                    switch1.setChecked(true);
                                } else {
                                    switch1.setChecked(false);
                                }

                                if (searching == 1) {
                                    switch2.setChecked(true);
                                } else {
                                    switch2.setChecked(false);
                                }
                                if (chat == 1) {
                                    switch4.setChecked(true);
                                } else {
                                    switch4.setChecked(false);
                                }

                                if (news == 1) {
                                    switch3.setChecked(true);
                                } else {
                                    switch3.setChecked(false);
                                }

                                if (long_t == 1) {
                                    switch5.setChecked(true);
                                } else {
                                    switch5.setChecked(false);
                                }

                                if (event == 1){
                                    switch20.setChecked(true);
                                } else {
                                    switch20.setChecked(false);
                                }
                            } else {//json.getString("result").equals("fail")시
                                //Toast.makeText(getApplicationContext(), "세팅 호출 실패", Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "get_profile_settings Error :" + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_33_loading_setting", new String[]{id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void able() {
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//친구추천허용
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    // Toast.makeText(getApplicationContext(), "on", Toast.LENGTH_SHORT).show();
                    friendadd("1");
                } else {
                    // Toast.makeText(getApplicationContext(), "off", Toast.LENGTH_SHORT).show();
                    friendadd("0");
                }
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//서칭허용
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    //  Toast.makeText(getApplicationContext(), "on", Toast.LENGTH_SHORT).show();
                    searchingable("1");
                } else {
                    // Toast.makeText(getApplicationContext(), "off", Toast.LENGTH_SHORT).show();
                    searchingable("0");
                }
            }
        });
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//전체알람받기-아직구현안됨
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    //Toast.makeText(getApplicationContext(), "on", Toast.LENGTH_SHORT).show();
                    noti("1");
                } else {
                    //Toast.makeText(getApplicationContext(), "off", Toast.LENGTH_SHORT).show();
                    noti("0");
                }
            }
        });

        switch20.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    event("1");
                } else {
                    event("0");
                }
            }
        });

        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//채팅미리보기-아직구현안됨
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
//                    Toast.makeText(getApplicationContext(), "on", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("sendbird_preview", true);
                    editor.commit();
                    pushChat("1");
                    Log.d("chatting1", String.valueOf(setting.getBoolean("sendbird_preview", true)));
                } else {
//                    Toast.makeText(getApplicationContext(), "off", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("sendbird_preview", false);
                    editor.commit();
                    pushChat("0");
                    Log.d("chatting2", String.valueOf(setting.getBoolean("sendbird_preview", false)));
                }
            }
        });

        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//longtimenosee 푸시알람
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.e("롱티 스위치 ", "체크됨?");
                    Log.e("롱티 스위치 checked", String.valueOf(isChecked));
                    Log.e("롱티 스위치 checked", String.valueOf(switch5.isChecked()));
                    Intent intent = new Intent(
                            getApplicationContext(),//현재제어권자
                            LongTimeNoSee.class); // 이동할 컴포넌트
                    startService(intent); // 서비스 시작
                    lt.setAlarm(getApplicationContext(), 1000);
                    longtimenoseecheck(true);//설정상태 저장
                    longt("1");
                } else {
                    Intent intent = new Intent(
                            getApplicationContext(),//현재제어권자
                            LongTimeNoSee.class); // 이동할 컴포넌트
                    stopService(intent); // 서비스 종료
                    lt.releaseAlarm(getApplicationContext());
                    longtimenoseecheck(false);
                    longt("0");
                }
            }
        });
//        switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            //notify.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM); 해당 부분을 notify.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); 로 바꿔 주면 기본 메세지 수신음이 나옵니다.. 반복도 안되고요..
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {//실행중 채팅 소리 알람-아직구현안됨
//                if (isChecked == true) {
//                    // Toast.makeText(getApplicationContext(), "on", Toast.LENGTH_SHORT).show();
//                    //chatsoundable();
//                } else {
//                    //  Toast.makeText(getApplicationContext(), "off", Toast.LENGTH_SHORT).show();
//                    //chatsoundable();
//                }
//            }
//        });

    }

    private void longt(String ablestate) {
        final String able = ablestate;
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("상세설정 ", response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        //Toast.makeText(getApplicationContext(), "변경되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {//json.getString("result").equals("fail")시
                        Toast.makeText(getApplicationContext(), "변경을 할 수가 없습니다. 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "push_long_t_yn Error :" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_37_push_long_t", new String[]{id, able}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void pushChat(String ablestate) {
        final String able = ablestate;
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        //Toast.makeText(getApplicationContext(), "변경되었습니다.", Toast.LENGTH_SHORT).show();

                    } else {//json.getString("result").equals("fail")시
                        Toast.makeText(getApplicationContext(), "변경을 할 수가 없습니다. 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "push_news_yn Error :" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_36_push_chat", new String[]{id, able}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void noti(String ablestate) {
        final String able = ablestate;
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        //Toast.makeText(getApplicationContext(), "변경되었습니다.", Toast.LENGTH_SHORT).show();

                    } else {//json.getString("result").equals("fail")시
                        Toast.makeText(getApplicationContext(), "변경을 할 수가 없습니다. 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "push_news_yn Error :" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_38_push_news", new String[]{id, able}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void event(String ablestate) {
        final String able = ablestate;
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        //Toast.makeText(getApplicationContext(), "변경되었습니다.", Toast.LENGTH_SHORT).show();

                    } else {//json.getString("result").equals("fail")시
                        Toast.makeText(getApplicationContext(), "변경을 할 수가 없습니다. 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "push_event_yn Error :" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_9999_push_event", new String[]{id, able}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void procLogout() {
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                OpeningActivity.isLoged = false;
                editor.putString("isLogedCheck","false");

                //                try {
//                    JSONObject json = new JSONObject(response);
//                    if (json.getString("result").equals("success")) {//성공시
//                        //Toast.makeText(getApplicationContext(), "변경되었습니다.", Toast.LENGTH_SHORT).show();
//
//                    } else {//json.getString("result").equals("fail")시
//                        Toast.makeText(getApplicationContext(), "변경을 할 수가 없습니다. 다시 확인해주세요", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "push_news_yn Error :" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_01_logout", new String[]{id}));
                //Log.d("로그아웃 서비스코드", String.valueOf(params));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void longtimenoseecheck(boolean switchcheck) {
        editor.putBoolean("logtimecheck", switchcheck);
        editor.commit();
    }


    private void searchingable(String ablestate) {
        final String able = ablestate;
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        //Toast.makeText(getApplicationContext(), "변경되었습니다.", Toast.LENGTH_SHORT).show();

                    } else {//json.getString("result").equals("fail")시
                        Toast.makeText(getApplicationContext(), "변경을 할 수가 없습니다. 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "searching_yn Error :" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_35_recommand_searching", new String[]{id, able}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void friendadd(String ablestate) {
        final String able = ablestate;
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        //Toast.makeText(getApplicationContext(), "변경되었습니다.", Toast.LENGTH_SHORT).show();

                    } else {//json.getString("result").equals("fail")시
                        Toast.makeText(getApplicationContext(), "변경을 할 수가 없습니다. 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "friend_recommand Error :" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_34_recommand_user", new String[]{id, able}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void touch() {
        settingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                finish();
            }
        });

        rel3.setOnClickListener(new View.OnClickListener() {//내위치 차단친구 관리
            @Override
            public void onClick(View v) {
                Intent intentlo = new Intent(getApplicationContext(), MygpsAble.class);
                startActivity(intentlo);
            }
        });

        rel4.setOnClickListener(new View.OnClickListener() {//내위치 차단팔로우 관리
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), MygpsFollowerAble.class);
                startActivity(intent1);

            }
        });
        rel.setOnClickListener(new View.OnClickListener() {//이용권조회
            @Override
            public void onClick(View v) {
                Intent intentpur = new Intent(getApplicationContext(), BuzzerPurchase.class);
                startActivity(intentpur);

            }
        });
        rel10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//비밀번호 변경
                Intent intentpwd = new Intent(getApplicationContext(), ChangePwd.class);
                startActivity(intentpwd);


            }
        });
        rel11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//휴대폰 변경
                Intent intentphone = new Intent(getApplicationContext(), ChangePhonenum.class);
                startActivity(intentphone);

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {//로그아웃
            @Override
            public void onClick(View v) {

                logoutDialog();
                /*
                AlertDialog.Builder alert = new AlertDialog.Builder(Allsetting.this);
                alert.setTitle("로그아웃")
                        .setMessage("로그아웃을 하시겠습니까")
                        .setCancelable(true)
                        .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intlogout = new Intent(getApplicationContext(), LoginActivity.class);
                                intlogout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intlogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                editor.clear();
                                editor.commit();
                                procLogout();
                                startActivity(intlogout);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //  dialog.cancel();
                                dialog.dismiss();
                            }
                        });
                alert.show(); */

            }
        });
        rel13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//탈퇴
                Intent intentex = new Intent(getApplicationContext(), Withdraw.class);
                startActivity(intentex);
            }
        });
        rel14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//버전
                Intent intentver = new Intent(getApplicationContext(), Version.class);
                startActivity(intentver);


            }
        });
        rel15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//회원약관
                Intent intentprivate = new Intent(getApplicationContext(), MembershipPolicy.class);
                startActivity(intentprivate);

            }
        });
        rel16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//개인정보취급
                Intent intentprivate = new Intent(getApplicationContext(), PrivacyPolicy.class);
                startActivity(intentprivate);

            }
        });
        rel17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//위치기반서비스 이용약관
                Intent intentpgs = new Intent(getApplicationContext(), GpsPolicy.class);
                startActivity(intentpgs);
            }
        });

    }

    public void logoutDialog(){
        dialog = new BuzzerDialog(Allsetting.this,
                "로그아웃 하시겠습니까?", "로그아웃", "취소", logoutListener, cancelListener);
        dialog.setCancelable(true);
        dialog.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener logoutListener = new View.OnClickListener() {
        public void onClick(View v) {

            SendBird.unregisterPushTokenAllForCurrentUser(new SendBird.UnregisterPushTokenHandler() {
                @Override
                public void onUnregistered(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        e.printStackTrace();

                        // Don't return because we still need to disconnect.
                    } else {
//                    Toast.makeText(MainActivity.this, "All push tokens unregistered.", Toast.LENGTH_SHORT).show();
                    }

                    ConnectionManager.logout(new SendBird.DisconnectHandler() {
                        @Override
                        public void onDisconnected() {
                            PreferenceUtils.setConnected(false);
                            OpeningActivity.isPhonenumLoading = false;
                            OpeningActivity.id_exist = id;
                            OpeningActivity.pw_exist = password;
                            Intent intlogout = new Intent(getApplicationContext(), LoginActivity.class);
                            intlogout.putExtra("from_logout", true);
                            intlogout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intlogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            editor.clear();
                            editor.commit();
                            procLogout();
                            startActivity(intlogout);
                            dialog.dismiss();
                        }
                    });
                }
            });

        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

}
