package com.movements.and.buzzerbuzzer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Service.LongTimeNoSee;
import com.movements.and.buzzerbuzzer.Service.LongtimeAlarm;
import com.movements.and.buzzerbuzzer.Service.RealtimeGpsBroadReceiver;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog2;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.chatutils.PreferenceUtils;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by samkim on 2017. 5. 5..
 */
//오픈닝
public class OpeningActivity extends BaseActivity {

    //TODO : 권한 관련 인터페이스 추가 하자.
    private boolean checklongtime;
    private LongtimeAlarm lt;
    private RelativeLayout linearLayout;
    private static final String TAG = "OpeningActivity";
    public static int nowtab=0;
    private SharedPreferences setting;
    private SharedPreferences setting_first;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editor_first;
    private JsonConverter jc;
    private ContentLoadingProgressBar mProgressBar;
    private PasswordEn passwordEn;
    public static Boolean chatProfileCheck = false;
    public static String chatProfileId = "";
    static public Boolean isLoged = false;
    static public Boolean isTuto = false;
    static public Boolean eventCheck = false;
    static public Boolean eventCheckFore = false;
    static public String EventMsgFore;
    static public String id_exist = "";
    static public String pw_exist = "";
    static public String basic_location_temp="";
    static public Boolean isPhonenumLoading = false;

    private ConfirmDialog dialog;
    private ConfirmDialog2 dialog2;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        linearLayout = (RelativeLayout) findViewById(R.id.splash);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar_login);
        jc = new JsonConverter();//제이슨컨퍼터
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        setting_first = getSharedPreferences("setting_first", MODE_PRIVATE);
        editor = setting.edit();
        editor_first = setting_first.edit();
        passwordEn = new PasswordEn();//패스워드암호화
        checklongtime = setting.getBoolean("logtimecheck", true);
        lt = new LongtimeAlarm();//longtimenosee 알람

        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

//        Set<String> slist = setting.getStringSet("phoneNums", null);
//        List<String> plist = new ArrayList<String>(slist);
//
//        for(int i = 0; i < plist.size(); i++){
//            Log.d(TAG +"Phone List", plist.get(i));
//        }

        boolean Auto_Login = setting.getBoolean("Auto_Login_enabled", false);
        Log.e(TAG+"Auto_Login_enabled", String.valueOf(Auto_Login));

        if (Auto_Login) {//자동로그인
            gologin();//로그인
        }else
        {
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(setting_first.getString("is_first","true").equals("true")){
                        editor_first.putString("is_first","false");
                        editor_first.commit();
                        Intent intent = new Intent(OpeningActivity.this, Agree1.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                        finish();
                    }else{
                        Intent intent = new Intent(OpeningActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                        finish();
                    }
                }
            };

            handler.sendEmptyMessageDelayed(0, 3000);
        }

    }

    //TODO:전화번호부 목록.
    public ArrayList<String> phoneBook() {
        Log.e("전화번호부","Start" );
        ArrayList<String> phoneLists = new ArrayList<String>();
        try {

            ContentResolver cr = getContentResolver();
            Cursor cursor = getURI();
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    // get the contact's information
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    // get the user's phone number
                    String phone = null;
                    String phone2 = null;
                    String replephone = null;//me
                    if (hasPhone > 0) {
                        Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        if (cp != null && cp.moveToFirst()) {
                            replephone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phone = replephone.replace("-", "");
                            cp.close();
                            phoneLists.add(phone);
                            phone2 = phone.substring(1);
                            phone2 = "82" + phone2;
                            phoneLists.add(phone2);
                        }
                    }

                } while (cursor.moveToNext());
                // clean up cursor
                cursor.close();
                //Log.d("전화번호부","size :"+phoneLists.size());
                //Log.e("전화번호 : ", String.valueOf(phoneLists));
            }

        } catch (Exception ex) {

        }
        return phoneLists;
    }

    private Cursor getURI() {
        // 주소록 URI
        Uri people = ContactsContract.Contacts.CONTENT_URI;

        // 검색할 컬럼 정하기
        String[] projection = new String[]{ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.HAS_PHONE_NUMBER};

        // 쿼리 날려서 커서 얻기
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC";

        //return getContentResolver().query(people, projection, null, selectionArgs, sortOrder);
        return getContentResolver().query(people, null, null, selectionArgs, sortOrder);
    }





    private void gologin() {//로그인

        final String id = setting.getString("user_id", "");
        final String password = setting.getString("user_pw", "");
        final String mPassword = passwordEn.PasswordEn(password);
        final String userNickName = setting.getString("user_nickname", id);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG+"263 유저 닉네임",userNickName);
                Log.e(TAG+"263",response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
//                                // if(json.getString("data").equals("login_result"))
//                                JSONArray data = json.getJSONArray("data");
//                                JSONObject row = null;
//                                row = data.getJSONObject(0);
//                                int result = row.getInt("login_result");
//
                        String msg = json.getString("msg");
                        //Log.d(TAG,msg);
                        isLoged = true;
                        editor.putString("isLogedCheck","true");
                        editor.putString("user_id", id);
                        //Log.e(TAG,id);
                        editor.putString("user_pw", password);
                        //Log.e(TAG,password);
                        editor.commit();
                        //getUser(id);
                        PreferenceUtils.setUserId(id);
                        PreferenceUtils.setNickname(setting.getString("user_nickname", id));
                        connectToSendBird(id, setting.getString("user_nickname", id), setting.getString("user_pic_URL", null));//샌드버드등록

                    } else if(json.getString("result").equals("network")){//json.getString("result").equals("fail")시
                        //Toast.makeText(getApplicationContext(), "기존 계정과 일치하지 않습니다. 계정이 없으시면 계정을 만들 수 있습니다.", Toast.LENGTH_SHORT).show();
                        dialog = new ConfirmDialog(OpeningActivity.this,
                                "일시정지된 회원입니다.\n고객센터에 문의하세요.", "확인");
                        dialog.setCancelable(true);
                        dialog.show();
                                /*
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setMessage("아이디와 비밀번호를 다시 확인해 주세요.")
                                        .setPositiveButton("확인", null)
                                        .show();*/
                        return;
                    } else {//json.getString("result").equals("fail")시
                        Toast.makeText(getApplicationContext(), "기존 계정과 일치하지 않습니다. 계정이 없으시면 계정을 만들 수 있습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), "로그인 실패 인터넷 연결을 확인해주세요!", Toast.LENGTH_SHORT).show();
                        dialog2 = new ConfirmDialog2(OpeningActivity.this,
                                "인터넷 연결 확인 후\n다시 실행해 주세요.", "확인", okListener);
                        dialog2.setCancelable(true);
                        dialog2.show();
                        Log.e(TAG, "login Error" + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("param", jc.createJsonParam("login", new String[]{id, mPassword}));//login 서비스코드 id,mPassword(암호화된 비밀번호)
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_01_login", new String[]{id, mPassword}));//login 서비스코드 id,mPassword(암호화된 비밀번호)
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBar.show();
        } else {
            mProgressBar.hide();
        }
    }

    //SendBird 연결
    private void connectToSendBird(final String userId, final String userNickname, final String profileUrl) {//닉네임은 널일떄 id로 들어감
        // Show the loading indicator
        showProgressBar(true);
        //loginbtn.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> phoneLists = phoneBook();
                Set<String> set = new HashSet<>();
                set.addAll(phoneLists);
                editor.putStringSet("phoneNums",set);
                editor.commit();
                isPhonenumLoading = true;
            }
        }).start();

        /*
        if (checklongtime && setting.getBoolean("Auto_Login_enabled", false)) {//longtimenosee 세팅
            Intent intent = new Intent(
                    getApplicationContext(),//현재제어권자
                    LongTimeNoSee.class); // 이동할 컴포넌트
            startService(intent); // 서비스 시작
            lt.setAlarm(getApplicationContext(), 1000);
            //Log.e("호출된페이지", "로그인");
        }
        */

        Log.e(TAG, setting.getString("realgpscheck","notreal"));

        if(setting.getString("realgpscheck","notreal").trim().equals("real")){
            Intent intent = new Intent(
                    getApplicationContext(),//현재제어권자
                    RealtimeGpsBroadReceiver.class); // 이동할 컴포넌트
            startService(intent); // 서비스 시작
            lt.setRealGpsAlarm(getApplicationContext(), 1000);
            //Log.e("로그인 지피에스 시작","되느냐?");
        }else
            releaseAlarm(getApplicationContext());//30분 gps업뎃 해체


        ConnectionManager.login(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                // Callback received; hide the progress bar.
                showProgressBar(false);

                if (e != null) {
                    // Error!
                    Toast.makeText(
                            OpeningActivity.this, getString(R.string.str_sendbird_login_err),
                            Toast.LENGTH_SHORT)
                            .show();

                    showProgressBar(false);
                    PreferenceUtils.setConnected(false);
                    return;
                }

                PreferenceUtils.setNickname(user.getNickname());
                PreferenceUtils.setProfileUrl(user.getProfileUrl());
                PreferenceUtils.setConnected(true);

                // Update the user's nickname
                updateCurrentUserPushToken();

//                Toast.makeText(getApplicationContext(), "로그인이 되었습니다", Toast.LENGTH_SHORT).show();
                Intent inLogin = new Intent(OpeningActivity.this, FriendList.class);
                inLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                inLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(inLogin);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });
        /*
        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                // Callback received; hide the progress bar.
                showProgressBar(false);

                if (e != null) {

                    Toast.makeText(
                            OpeningActivity.this, getString(R.string.str_sendbird_login_err),
                            Toast.LENGTH_SHORT)
                            .show();

                    //loginbtn.setEnabled(true);
                    showProgressBar(false);
                    return;
                }

                //updateCurrentUserInfo(userNickname, profileUrl);
                updateCurrentUserPushToken();

//                Toast.makeText(getApplicationContext(), "로그인이 되었습니다", Toast.LENGTH_SHORT).show();
                Intent inLogin = new Intent(OpeningActivity.this, FriendList.class);
                inLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                inLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(inLogin);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                //finish();
            }
        });
        */

//        checkAllAuth();
    }

    private void releaseAlarm(Context context) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;
        myIntent = new Intent(context, RealtimeGpsBroadReceiver.class);
        myIntent.putExtra("real_time", 0);
        pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        manager.cancel(pendingIntent);
        // 주석을 풀면 먼저 실행되는 알람이 있을 경우, 제거하고
        // 새로 알람을 실행하게 된다. 상황에 따라 유용하게 사용 할 수 있다.

    }

    //SendBird 닉네임 업데이트
    @SuppressLint("LongLogTag")
    private void updateCurrentUserInfo(String userNickname, String profileUrl) {//사진 업뎃 null 에 주소 추가 프로필 세팅사진 업뎃 할떄도 이거 쓰라..
//        final String user_pic_url = setting.getString("user_pic_URL", null);
        //Log.d(TAG+"updateCurrentUserInfo", userNickname + "  :  " + profileUrl);
        if (profileUrl == null) {
            SendBird.updateCurrentUserInfo(userNickname, profileUrl, new SendBird.UserInfoUpdateHandler() {
                @Override
                public void onUpdated(SendBirdException e) {
                    // Log.d("user_pic_URL",user_pic_url);
                    if (e != null) {
                        // Error!
                        Toast.makeText(
                                OpeningActivity.this, getString(R.string.str_sendbird_update_nickname_err),
                                Toast.LENGTH_SHORT)
                                .show();
                        return; //리턴 해줘야하나?
                    }
                }
            });
        }else {
            SendBird.updateCurrentUserInfo(userNickname, profileUrl, new SendBird.UserInfoUpdateHandler() {
                @Override
                public void onUpdated(SendBirdException e) {
                    if (e != null) {
                        //Log.d("넘버", "SendBirdException e" + "  : " + e);
                        // Error!
//                    Toast.makeText(
//                            Picprofile.this, getString(R.string.str_sendbird_update_nickname_err),
//                            Toast.LENGTH_SHORT)
//                            .show();

                        return;
                    }
                }
            });
        }
    }

    /**
     * Update the user's push token.
     */
    private void updateCurrentUserPushToken() {//센드버드 토큰
        // Register Firebase Token
        SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(),
                new SendBird.RegisterPushTokenWithStatusHandler() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                        if (e != null) {
                            // Error!
//                            Toast.makeText(LoginActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "updateCurrentUserPushToken :" + e.getCode() + ":" + e.getMessage());
                            return;
                        }
//                        Toast.makeText(LoginActivity.this, "Push token registered.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "updateCurrentUserPushToken :Push token registered.");
                        Log.e(TAG +" getToken", FirebaseInstanceId.getInstance().getToken());
                        String token1 = FirebaseInstanceId.getInstance().getToken();
                        Log.e(TAG +" Token", token1);
                        editor.putString("token",token1);
                        editor.commit();
                        setting.getString("token",null);
                        Log.e(TAG +" SP getToken", setting.getString("token",null));
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_exit, R.anim.bottom_down);
    }

    private static void finishAffinity(final Activity activity) {
        activity.setResult(Activity.RESULT_CANCELED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.finishAffinity();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.finishAffinity();
                }
            });
        }
    }

    private View.OnClickListener okListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog2.dismiss();
            finishAffinity(OpeningActivity.this);
        }
    };
}
