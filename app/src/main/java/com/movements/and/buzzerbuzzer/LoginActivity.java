package com.movements.and.buzzerbuzzer;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Service.LongTimeNoSee;
import com.movements.and.buzzerbuzzer.Service.LongtimeAlarm;
import com.movements.and.buzzerbuzzer.Service.RealtimeGpsBroadReceiver;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.google.firebase.iid.FirebaseInstanceId;
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
 * Created by samkim on 2017. 7. 21..
 */
//로그인 화면
public class LoginActivity extends BaseActivity {

    InputMethodManager imm;

    private EditText idex, pwdex;
    private CheckBox check1;
    private Button loginbtn;
    //private ImageButton settingbtn;
    private TextView newmembertv, findmember;

    //폰트적용을 위해 "아직회원이아니신가요?" 의 id값
    private TextView textQ;
    //외부 폰트 적용
    private Typeface typefaceBold, typefaceExtraBold;

    //아이디, 패스워드 포커스 리스너
    private myOnFocusChangeListener myListener = new myOnFocusChangeListener();

    static String id_temp="";
    static String email_temp="";
    static String pw_temp="";
    static String phone_temp="";

    private SharedPreferences setting2;
    private SharedPreferences.Editor editor2;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private JsonConverter jc;

    private static final int ALL_CHECK_BUTTON = 0;
    private boolean checklongtime;

    private CheckBox[] mCheckBoxs;
    private Dialog mMainDialog;

    private LongtimeAlarm lt;
    //private PopupWindow mPopupWindow;
    private ContentLoadingProgressBar mProgressBar;
    private PasswordEn passwordEn;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backpressedTime = 0;
    private static final String TAG = "LoginActivity";
    private LinearLayout linearLayout;
    private ImageView imageView;
    private boolean Auto_Login;
    private String remember_check;
    private ConfirmDialog dialog;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        linearLayout = (LinearLayout) findViewById(R.id.ll);
        imageView = (ImageView) findViewById(R.id.imageView);

        idex = (EditText) findViewById(R.id.idex);
        pwdex = (EditText) findViewById(R.id.pwdex);
        check1 = (CheckBox) findViewById(R.id.check1);
        check1.setChecked(true);
        check1.setButtonDrawable(R.drawable.login_chk_press);
        loginbtn = (Button) findViewById(R.id.loginbtn);
        //settingbtn = (ImageButton) findViewById(R.id.settingbtn);
        newmembertv = (TextView) findViewById(R.id.newmembertv);
        findmember = (TextView) findViewById(R.id.findmember);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar_login);
        jc = new JsonConverter();//제이슨컨퍼터
        lt = new LongtimeAlarm();//longtimenosee 알람
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        setting2 = getSharedPreferences("setting2", MODE_PRIVATE);
        editor2 = setting2.edit();
        textQ = (TextView)findViewById(R.id.textQ);

        //startService(new Intent(LoginActivity.this, DeleteTokenService.class));


        editor.putBoolean("firtmembership", false);
        editor.commit();

        passwordEn = new PasswordEn();//패스워드암호화

        checklongtime = setting.getBoolean("logtimecheck", true);

        linearLayout.setOnClickListener(clickListener);
        imageView.setOnClickListener(clickListener);

        /*
        if (checklongtime && setting.getBoolean("Auto_Login_enabled", false)) {//longtimenosee 세팅
            Intent intent = new Intent(
                    getApplicationContext(),//현재제어권자
                    LongTimeNoSee.class); // 이동할 컴포넌트
            startService(intent); // 서비스 시작
            lt.setAlarm(getApplicationContext(), 1000);
            Log.e("호출된페이지", "로그인");
        }
        */

        Log.e(TAG, setting.getString("realgpscheck","notreal"));

        if(setting.getString("realgpscheck","notreal").trim().equals("real")){
            Intent intent = new Intent(
                    getApplicationContext(),//현재제어권자
                    RealtimeGpsBroadReceiver.class); // 이동할 컴포넌트
            startService(intent); // 서비스 시작
            lt.setRealGpsAlarm(getApplicationContext(), 1000);
            Log.e("로그인 지피에스 시작","되느냐?");
        }else
            releaseAlarm(getApplicationContext());//30분 gps업뎃 해체
        //TODO 음 해제하네..

        button();

        //by yugyeong
        //forgot? 에 밑줄 긋기
        findmember.setPaintFlags(findmember.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //아이디와 패스워드에 포커스 주기
        idex.setOnFocusChangeListener(myListener);
        pwdex.setOnFocusChangeListener(myListener);
        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");
        check1.setTypeface(typefaceBold);
        findmember.setTypeface(typefaceBold);
        textQ.setTypeface(typefaceBold);
        newmembertv.setTypeface(typefaceExtraBold);
        loginbtn.setTypeface(typefaceExtraBold);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("로그인액티비티","온리썸 진입");

        Boolean getBoolean = getIntent().getBooleanExtra("from_logout", false);
        if(getBoolean){
            Log.e("온리썸","로그아웃을 통해 들어옴");
            remember_check = setting2.getString("remember_check", "true");
            Log.e("온리썸 리멤버체크", remember_check);
            if(remember_check.equals("false")){
                check1.setChecked(false);
                check1.setButtonDrawable(R.drawable.login_chk_normal);
            }else{
                check1.setChecked(true);
                check1.setButtonDrawable(R.drawable.login_chk_press);
                if(!OpeningActivity.id_exist.equals("")){
                    idex.setText(OpeningActivity.id_exist);
                    pwdex.setText(OpeningActivity.pw_exist);
                }
            }
        }
    }

    //TODO:전화번호부 목록.
    public ArrayList<String> phoneBook() {
        Log.d("전화번호부","Start" );
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
                Log.d("전화번호부","size :"+phoneLists.size());
                Log.e("전화번호 : ", String.valueOf(phoneLists));
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

    private void button() {
        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//remember
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //체크박스 체크표시로 변환
                    check1.setButtonDrawable(R.drawable.login_chk_press);
                    editor2.putString("remember_check","true");
                    editor2.commit();
                } else {
                    //체크박스 체크표시 해제
                    check1.setButtonDrawable(R.drawable.login_chk_normal);
                    editor.putBoolean("Auto_Login_enabled", false);
                    editor2.putString("remember_check","false");
                    editor2.commit();
                }
            }
        });
        newmembertv.setOnClickListener(new View.OnClickListener() {//회원가입
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Membership_info.class);
                startActivity(intent);
                overridePendingTransition(R.anim.side_on, R.anim.side_off);
            }
        });
        findmember.setOnClickListener(new View.OnClickListener() {//아이디비번찾기
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(LoginActivity.this, FindMyinfo.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {//로그인
            @Override
            public void onClick(View v) {
                if (idex.getText().toString().equals("") || pwdex.getText().toString().equals("")) {
                    //Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 입력하세요!!!", Toast.LENGTH_LONG).show();
                    dialog = new ConfirmDialog(LoginActivity.this,
                            "아이디와 비밀번호를\n입력해 주세요.", "확인");
                    dialog.setCancelable(true);
                    dialog.show();
                    return;
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    gologin();
                }
            }
        });
    }

    private void gologin() {//로그인
        final String id = idex.getText().toString().trim();
        final String password = pwdex.getText().toString().trim();
        final String mPassword = passwordEn.PasswordEn(password);
        final String userNickName = setting.getString("user_nickname", id);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG+"263 테스트",userNickName);
                        Log.e(TAG+"263",response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                String msg = json.getString("msg");
                                Log.d(TAG,msg);
                                mProgressBar.setVisibility(View.VISIBLE);
                                OpeningActivity.isLoged = true;
                                editor.putString("isLogedCheck","true");
                                editor.putString("user_id", id);
                                Log.e(TAG,id);
                                editor.putString("user_pw", password);
                                Log.e(TAG,password);
                                editor.commit();
                                //getUser(id);
                                if(check1.isChecked() == true){
                                    editor.putBoolean("Auto_Login_enabled", true);
                                    editor.commit();
                                }
                                PreferenceUtils.setUserId(id);
                                PreferenceUtils.setNickname(setting.getString("user_nickname", id));
                                connectToSendBird(id, setting.getString("user_nickname", id), setting.getString("user_pic_URL", null));//샌드버드등록

                            } else if(json.getString("result").equals("network")){
                                mProgressBar.setVisibility(View.GONE);
                                dialog = new ConfirmDialog(LoginActivity.this,
                                        "일시정지된 회원입니다.\n고객센터에 문의하세요.", "확인");
                                dialog.setCancelable(true);
                                dialog.show();
                                return;
                            } else if(json.getString("result").equals("Check_user_fail")) {
                                Log.e(TAG+"263 테스트","체크유저페일");
                                mProgressBar.setVisibility(View.GONE);
                                dialog = new ConfirmDialog(LoginActivity.this,
                                        "아이디와 비밀번호를 다시 확인해 주세요.", "확인");
                                dialog.setCancelable(true);
                                dialog.show();
                                return;
                            }else if(json.getString("result").equals("fail")) {
                                Log.e(TAG+"263 테스트","fail");
                                mProgressBar.setVisibility(View.GONE);
                                dialog = new ConfirmDialog(LoginActivity.this,
                                        "아이디와 비밀번호를 다시 확인해 주세요.", "확인");
                                dialog.setCancelable(true);
                                dialog.show();
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
                        mProgressBar.setVisibility(View.GONE);
                        dialog = new ConfirmDialog(LoginActivity.this,
                                "로그인 실패!\n인터넷 연결을 확인해주세요.", "확인");
                        dialog.setCancelable(true);
                        dialog.show();
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

    //SendBird 연결
    private void connectToSendBird(final String userId, final String userNickname, final String profileUrl) {//닉네임은 널일떄 id로 들어감
        // Show the loading indicator
        showProgressBar(true);
        loginbtn.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(setting.getStringSet("phoneNums",null) == null){
                    // Log.e("전화번호","현재 널");
                    ArrayList<String> phoneLists = phoneBook();
                    Set<String> set = new HashSet<>();
                    set.addAll(phoneLists);
                    editor.putStringSet("phoneNums",set);
                    editor.commit();
                    OpeningActivity.isPhonenumLoading = true;
                }
            }
        }).start();

        ConnectionManager.login(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                // Callback received; hide the progress bar.
                showProgressBar(false);

                if (e != null) {
                    // Error!
                    Toast.makeText(
                            LoginActivity.this, getString(R.string.str_sendbird_login_err),
                            Toast.LENGTH_SHORT)
                            .show();

                    loginbtn.setEnabled(true);
                    showProgressBar(false);
                    PreferenceUtils.setConnected(false);
                    return;
                }

                PreferenceUtils.setNickname(user.getNickname());
                PreferenceUtils.setProfileUrl(user.getProfileUrl());
                PreferenceUtils.setConnected(true);

                // Update the user's nickname
                updateCurrentUserPushToken();

                mProgressBar.setVisibility(View.GONE);

//                Toast.makeText(getApplicationContext(), "로그인이 되었습니다", Toast.LENGTH_SHORT).show();
                Intent inLogin = new Intent(LoginActivity.this, FriendList.class);
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
                            LoginActivity.this, getString(R.string.str_sendbird_login_err),
                            Toast.LENGTH_SHORT)
                            .show();

                    loginbtn.setEnabled(true);
                    showProgressBar(false);
                    return;
                }

                //updateCurrentUserInfo(userNickname, profileUrl);
                updateCurrentUserPushToken();

                mProgressBar.setVisibility(View.GONE);

//                Toast.makeText(getApplicationContext(), "로그인이 되었습니다", Toast.LENGTH_SHORT).show();
                Intent inLogin = new Intent(LoginActivity.this, FriendList.class);
                inLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                inLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(inLogin);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                //finish();
            }
        });
        */
    }

    //SendBird 닉네임 업데이트
    @SuppressLint("LongLogTag")
    private void updateCurrentUserInfo(String userNickname, String profileUrl) {//사진 업뎃 null 에 주소 추가 프로필 세팅사진 업뎃 할떄도 이거 쓰라..
//        final String user_pic_url = setting.getString("user_pic_URL", null);
        Log.d(TAG+"updateCurrentUserInfo", userNickname + "  :  " + profileUrl);
        if (profileUrl == null) {
            SendBird.updateCurrentUserInfo(userNickname, profileUrl, new SendBird.UserInfoUpdateHandler() {
                @Override
                public void onUpdated(SendBirdException e) {
                    // Log.d("user_pic_URL",user_pic_url);
                    if (e != null) {
                        // Error!
                        Toast.makeText(
                                LoginActivity.this, getString(R.string.str_sendbird_update_nickname_err),
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
                        Log.d("넘버", "SendBirdException e" + "  : " + e);
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
    protected void onDestroy() {
        super.onDestroy();
        //mPopupWindow.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backpressedTime;
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backpressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 뒤로가기를 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBar.show();
        } else {
            mProgressBar.hide();
        }
    }

    // 알람 해제
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

    //made by yugyeong
    class myOnFocusChangeListener implements View.OnFocusChangeListener{
        @Override
        public void onFocusChange(final View view, final boolean hasFocus) {
            Drawable img;
            switch (view.getId()) {
                case R.id.idex:
                    //아이디
                    if (hasFocus) {
                        //포커스 받았을 때-밑줄
                        idex.setBackgroundResource(R.drawable.custom_login_image_focus);
                        idex.setTypeface(typefaceExtraBold);
                        idex.setTextColor(Color.parseColor("#282828"));
                        //포커스 받았을 때-이미지
                        img = getDrawable(R.drawable.login_id_focus);
                    } else {
                        //포커스 안받았을 때-밑줄
                        idex.setBackgroundResource(R.drawable.custom_login_image);
                        idex.setTextColor(Color.parseColor("#888888"));
                        idex.setTypeface(typefaceBold);
                        //포커스 안받았을 때-이미지
                        img = getDrawable(R.drawable.login_id_dim);
                    }
                    idex.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    break;
                //패스워드
                case R.id.pwdex:
                    if (hasFocus) {
                        //포커스 받았을 때
                        pwdex.setBackgroundResource(R.drawable.custom_login_image_focus);
                        pwdex.setTextColor(Color.parseColor("#282828"));
                        //포커스 받았을 때-이미지
                        img = getDrawable(R.drawable.login_password_focus);
                    } else {
                        pwdex.setBackgroundResource(R.drawable.custom_login_image);
                        pwdex.setTextColor(Color.parseColor("#888888"));
                        //포커스 받았을 때-이미지
                        img = getDrawable(R.drawable.login_password_dim);
                    }
                    pwdex.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    break;
            }
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            hideKeyboard();
            switch (v.getId())
            {
                case R.id.ll :
                    break;

                case R.id.imageView :
                    break;
            }
        }
    };

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(idex.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(pwdex.getWindowToken(), 0);
    }

}
