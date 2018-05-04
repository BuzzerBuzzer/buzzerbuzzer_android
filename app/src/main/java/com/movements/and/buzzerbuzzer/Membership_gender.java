package com.movements.and.buzzerbuzzer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.google.firebase.iid.FirebaseInstanceId;
import com.movements.and.buzzerbuzzer.chatutils.PreferenceUtils;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Membership_gender extends BaseActivity {

    private TextView tv_gender_main, tv_gender, man, woman, complete_join;
    private ImageView btn_back, nextbtn;

    private String gender1;
    private JsonConverter jc;
    private PasswordEn passwordEn;

    private Typeface typefaceBold, typefaceExtraBold;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;

    private final String TAG = "Membership";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_gender);

        tv_gender_main = (TextView)findViewById(R.id.tv_gender_main);
        tv_gender = (TextView)findViewById(R.id.tv_gender);
        man = (TextView)findViewById(R.id.man);
        woman = (TextView)findViewById(R.id.woman);
        complete_join = (TextView)findViewById(R.id.complete_join);
        btn_back = (ImageView)findViewById(R.id.btn_back);
        nextbtn = (ImageView)findViewById(R.id.nextbtn);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv_gender_main.setTypeface(typefaceExtraBold);
        tv_gender.setTypeface(typefaceBold);
        man.setTypeface(typefaceExtraBold);
        woman.setTypeface(typefaceExtraBold);
        complete_join.setTypeface(typefaceBold);

        gender1 = "m";  //남자로 초기화

        jc = new JsonConverter();
        passwordEn = new PasswordEn();//패스워드암호화

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        button();
    }

    public void button(){
        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender1 = "w";
                woman.setTextColor(Color.parseColor("#7d51fc"));
                man.setTextColor(Color.parseColor("#888888"));
            }
        });
        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender1 = "m";
                man.setTextColor(Color.parseColor("#7d51fc"));
                woman.setTextColor(Color.parseColor("#888888"));
            }
        });

        //이전 페이지
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Membership_email.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.side_on2, R.anim.side_off2);
            }
        });

        //다음 페이지
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();//회원등록
                //Intent intent = new Intent(getApplicationContext(), Membership_finish.class);
                //startActivity(intent);
                //finish();
            }
        });

    }


    private void registerUser() {
        Intent getIntent = getIntent();
        //Log.i("ID : ",getIntent.getStringExtra("ID"));
        final String phonenum = LoginActivity.phone_temp;
        final String id = LoginActivity.id_temp;
        final String password = LoginActivity.pw_temp;
        final String mPassword = passwordEn.PasswordEn(password);
        final String email = LoginActivity.email_temp;
        final String gender = gender1;

        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                Log.d(TAG+" 138 : ", response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시

                        editor.putBoolean("Auto_Login_enabled", true);
                        editor.putString("user_id", id);
                        editor.putString("user_pw", password);
                        editor.commit();
//                        gologin();
                        PreferenceUtils.setUserId(id);
                        PreferenceUtils.setNickname(setting.getString("user_nickname", id));
                        connectToSendBird(id, setting.getString("user_nickname", null));

                        /*
                        //Toast.makeText(getApplicationContext(), "가입 성공하였습니다.", Toast.LENGTH_LONG).show();
                        Intent inLogin = new Intent(Membership_gender.this, Membership_finish.class);
                        inLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        inLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        //inLogin.putExtra("gps_base_check", false);
                        startActivity(inLogin);
                        finish();
                        overridePendingTransition(R.anim.side_on, R.anim.side_off);
                        */

                    } else {//json.getString("result").equals("fail")시
                        Toast.makeText(getApplicationContext(), "회원가입 실패!!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "회원가입 실패하였습니다. 인터넷 확인해주세요!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "membership Error" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //TODO 파람즈 순서 변경 by 유경
                params.put("param", jc.createJsonParam(id,mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_10_join_buzzerbuzzer", new String[]{id, mPassword, phonenum, "1", email, gender, "1", "1"}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

//    private void gologin() {//로그인
//        final String id = setting.getString("user_id",null);
//        final String password = setting.getString("user_pw",null);
//        final String mPassword = passwordEn.PasswordEn(password);
//        final String userNickName = setting.getString("user_nickname", null);
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                Log.e(TAG+"263",response);
//                try {
//                    JSONObject json = new JSONObject(response);
//                    if (json.getString("result").equals("success")) {//성공시
////                                // if(json.getString("data").equals("login_result"))
////                                JSONArray data = json.getJSONArray("data");
////                                JSONObject row = null;
////                                row = data.getJSONObject(0);
////                                int result = row.getInt("login_result");
////
//                        String msg = json.getString("msg");
//                        Log.d(TAG,msg);
//
//                        if (json.getString("msg").equals("Login success")) {
//                            editor.putString("user_id", id);
//                            Log.e(TAG,id);
//                            editor.putString("user_pw", password);
//                            Log.e(TAG,password);
//                            editor.commit();
//                            //getUser(id);
//                            connectToSendBird(id, setting.getString("user_nickname", null));//샌드버드등록
//                        } else {
//                            Toast.makeText(getApplicationContext(), "기존 계정과 일치하지 않습니다. 계정이 없으시면 계정을 만들 수 있습니다.", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {//json.getString("result").equals("fail")시
//                        Toast.makeText(getApplicationContext(), "기존 계정과 일치하지 않습니다. 계정이 없으시면 계정을 만들 수 있습니다.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), "로그인 실패 인터넷 연결을 확인해주세요!", Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "login Error" + error);
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                //params.put("param", jc.createJsonParam("login", new String[]{id, mPassword}));//login 서비스코드 id,mPassword(암호화된 비밀번호)
//                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_01_login", new String[]{id, mPassword}));//login 서비스코드 id,mPassword(암호화된 비밀번호)
//                return params;
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }

    private void connectToSendBird(final String userId, final String userNickname) {//닉네임은 널일떄 id로 들어감

        ConnectionManager.login(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {

                if (e != null) {
                    Toast.makeText(
                            Membership_gender.this, getString(R.string.str_sendbird_login_err),
                            Toast.LENGTH_SHORT)
                            .show();
                    PreferenceUtils.setConnected(false);
                    return;
                }

                PreferenceUtils.setNickname(user.getNickname());
                PreferenceUtils.setProfileUrl(user.getProfileUrl());
                PreferenceUtils.setConnected(true);

                // Update the user's nickname
                updateCurrentUserInfo(userNickname);
                updateCurrentUserPushToken();

                //Toast.makeText(getApplicationContext(), "로그인이 되었습니다", Toast.LENGTH_SHORT).show();
                Intent inLogin = new Intent(Membership_gender.this, Agree2.class);
                inLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                inLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(inLogin);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                //finish();
            }
        });

        /*

        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                // Callback received; hide the progress bar.

                if (e != null) {

                    Toast.makeText(
                            Membership_gender.this, getString(R.string.str_sendbird_login_err),
                            Toast.LENGTH_SHORT)
                            .show();

                    return;
                }

                updateCurrentUserInfo(userNickname);
                updateCurrentUserPushToken();

                //Toast.makeText(getApplicationContext(), "로그인이 되었습니다", Toast.LENGTH_SHORT).show();
                Intent inLogin = new Intent(Membership_gender.this, Agree2.class);
                inLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                inLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(inLogin);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                //finish();
            }
        });
        */
    }

    private void updateCurrentUserInfo(String userNickname) {//사진 업뎃 null 에 주소 추가 프로필 세팅사진 업뎃 할떄도 이거 쓰라..
        final String user_pic_url = setting.getString("user_pic_URL", null);
//        Log.d("결제2", userNickname + "  :  " + user_pic_url);
        SendBird.updateCurrentUserInfo(userNickname, user_pic_url, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                // Log.d("user_pic_URL",user_pic_url);
                if (e != null) {
                    // Error!
                    Toast.makeText(
                            Membership_gender.this, getString(R.string.str_sendbird_update_nickname_err),
                            Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
        });
    }

    private void updateCurrentUserPushToken() {//센드버드 토큰
        // Register Firebase Token
        SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(),
                new SendBird.RegisterPushTokenWithStatusHandler() {
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
                        String token = FirebaseInstanceId.getInstance().getToken();
                        editor.putString("token", token);
                        editor.commit();

                    }
                });
    }

}
