package com.movements.and.buzzerbuzzer;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
import com.bumptech.glide.Glide;
import com.movements.and.buzzerbuzzer.Adapter.MySlidingImage_Adapter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.UserPic;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.BuzzerDialog;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.chat.PersonalChatActivity;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBirdException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by samkim on 2017. 4. 17..
 */
//고객센터 프로필
public class BscProfile extends BaseActivity {

    private ImageView backbtn, showpicbtn;

    private ImageView user_pic;
    private Button singo_btn, question_btn;
    private String nick;
    private TextView idtx, msgtx;

    private Typeface typefaceBold, typefaceExtraBold;

    private MySlidingImage_Adapter adapter;
    private String user_id, id, tel, mPassword, password;;
    private JsonConverter jc;
    private BuzzerDialog dialog;

    private SharedPreferences setting;
    private PasswordEn passwordEn;
    private final String TAG = "BscProfile";
    private String buzzer_email;
    private int downY, userPicY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bsc_profile);

        jc = new JsonConverter();

        backbtn = (ImageView) findViewById(R.id.backbtn);
        singo_btn = (Button) findViewById(R.id.chat_btn);
        question_btn = (Button) findViewById(R.id.call_btn);
        showpicbtn = (ImageView) findViewById(R.id.showpicbtn);

        msgtx = (TextView) findViewById(R.id.state_msg);
        user_pic = (ImageView) findViewById(R.id.user_pic2);

        idtx = (TextView)findViewById(R.id.id);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        idtx.setTypeface(typefaceExtraBold);
        msgtx.setTypeface(typefaceBold);
        singo_btn.setTypeface(typefaceExtraBold);
        question_btn.setTypeface(typefaceExtraBold);

        passwordEn = new PasswordEn();

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        id = setting.getString("user_id", "");

        Intent intent = getIntent();
        user_id = intent.getExtras().getString("id");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        button();
        getBSC(user_id);//유저 정보
    }



    @SuppressLint("ClickableViewAccessibility")
    private void button() {



        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        //신고하기 버튼
        singo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Declare.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });

        singo_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    singo_btn.setBackgroundDrawable(getApplication().getResources().getDrawable(R.drawable.largebutton_sub_dim_hover));
                    singo_btn.setTextColor(getApplication().getResources().getColor(R.color.white));
                    singo_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.report3, 0, 0, 0);
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    singo_btn.setBackgroundDrawable(getApplication().getResources().getDrawable(R.drawable.largebutton_sub_dim));
                    singo_btn.setTextColor(getApplication().getResources().getColor(R.color.colorMain));
                    singo_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.report2, 0, 0, 0);
                }
                return false;
            }
        });

        //문의하기 버튼
        question_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                try {
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{buzzer_email});

                    emailIntent.setType("text/html");
                    emailIntent.setPackage("com.google.android.gm");
                    if(emailIntent.resolveActivity(getPackageManager())!=null)
                        startActivity(emailIntent);

                    startActivity(emailIntent);
                } catch (Exception e) {
                    e.printStackTrace();

                    emailIntent.setType("text/html");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{buzzer_email});

                    startActivity(Intent.createChooser(emailIntent, "Send Email"));
                }
            }
        });

        question_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    question_btn.setBackgroundDrawable(getApplication().getResources().getDrawable(R.drawable.largebutton_sub_dim_hover));
                    question_btn.setTextColor(getApplication().getResources().getColor(R.color.white));
                    question_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mail3, 0, 0, 0);
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    question_btn.setBackgroundDrawable(getApplication().getResources().getDrawable(R.drawable.largebutton_sub_dim));
                    question_btn.setTextColor(getApplication().getResources().getColor(R.color.colorMain));
                    question_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mail2, 0, 0, 0);
                }
                return false;
            }
        });

        user_pic.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                userPicY = (int) event.getRawY();

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    downY = (int) event.getRawY();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if( 125 < -(downY - userPicY- 25)){
                        onBackPressed();
                    }
                }
                return true;
            }
        });
    }

    private void getBSC(String query) {
        final String fid = query;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String nickname, condition_msg, pic_src, id, email;
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                JSONArray data = json.getJSONArray("data");
                                nickname = data.getJSONObject(0).has("nickname") ? data.getJSONObject(0).getString("nickname") : "";
                                condition_msg = data.getJSONObject(0).has("condition_msg") ? data.getJSONObject(0).getString("condition_msg") : "";
                                pic_src = data.getJSONObject(0).has("pic_src") ? data.getJSONObject(0).getString("pic_src") : "";
                                id = data.getJSONObject(0).has("id") ? data.getJSONObject(0).getString("id") : "";
                                email = data.getJSONObject(0).has("email") ? data.getJSONObject(0).getString("email") : "";

                                Glide.with(getApplicationContext()).load(pic_src).into(user_pic);
                                idtx.setText(nickname);
                                msgtx.setText(condition_msg);
                                buzzer_email = email;

                            } else {
                                //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(getApplicationContext(), " getinfo Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG," user_profile Error");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_65_loading_buzzer_service_center", new String[]{}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_exit, R.anim.bottom_down);
    }

}
