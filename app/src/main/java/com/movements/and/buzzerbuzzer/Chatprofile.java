package com.movements.and.buzzerbuzzer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Adapter.MySlidingImage_Adapter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.UserPic;
import com.movements.and.buzzerbuzzer.R;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.BuzzerDialog;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.chat.GroupChannelActivity;
import com.movements.and.buzzerbuzzer.chat.PersonalChatActivity;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBirdException;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018-04-02.
 */

public class Chatprofile extends BaseActivity {
    private LinearLayout following_ll;
    private ImageView showpicbtn, backbtn, profilegallery;
    private Button chat_btn, call_btn, big_friend_btn;
    private ViewPager viewPager;
    private ImageView user_pic;
    private TextView idex, total_text, age_tx, state_msg, like_cnt, following_cnt, following_btn,state;

    private MySlidingImage_Adapter adapter;
    private JsonConverter jc;
    private static final int TAB_ID = 1;

    private String user_id, id, tel, mPassword, password;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private ArrayList<UserPic> imagesArray;
    private  final String TAG = "Followingprofile";

    private Typeface typefaceBold, typefaceExtraBold;
    private String nick;

    private int downY, userPicY;
    private String fr_class2="";
    private BuzzerDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_profile);

        imagesArray = new ArrayList<>();
        jc = new JsonConverter();

        following_ll = (LinearLayout) findViewById(R.id.following_ll);
        backbtn = (ImageView) findViewById(R.id.backbtn);
        profilegallery = (ImageView) findViewById(R.id.profilegallery);
        showpicbtn = (ImageView) findViewById(R.id.showpicbtn);
        chat_btn = (Button) findViewById(R.id.chat_btn);
        call_btn = (Button)findViewById(R.id.call_btn);
        big_friend_btn = (Button)findViewById(R.id.big_friend_btn);
        following_btn = (TextView) findViewById(R.id.friend_btn);
        //viewPager = (ViewPager) findViewById(R.id.view_pager2);
        //user_pic = (ImageView) findViewById(R.id.user_pic2);
        total_text = (TextView) findViewById(R.id.total_text);
        state_msg = (TextView) findViewById(R.id.state_msg);
        state = (TextView) findViewById(R.id.state);
        like_cnt = (TextView) findViewById(R.id.buzzer_cnt);
        following_cnt = (TextView) findViewById(R.id.following_cnt);
        age_tx = (TextView) findViewById(R.id.age_tx);

        //user_pic.setBackground(new ShapeDrawable(new OvalShape()));
        //user_pic.setClipToOutline(true);
        idex = (TextView) findViewById(R.id.id);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");


        idex.setTypeface(typefaceExtraBold);
        state_msg.setTypeface(typefaceBold);
        state.setTypeface(typefaceBold);
        following_cnt.setTypeface(typefaceExtraBold);
        age_tx.setTypeface(typefaceBold);
        like_cnt.setTypeface(typefaceExtraBold);
        following_btn.setTypeface(typefaceExtraBold);
        call_btn.setTypeface(typefaceExtraBold);
        chat_btn.setTypeface(typefaceExtraBold);

//        viewPager.setHorizontalFadingEdgeEnabled(true);
//        adapter = new MySlidingImage_Adapter(this, imagesArray);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//            @Override
//            public void onPageSelected(int position) {
//                numb_text.setText(String.valueOf(position + 1));
//            }
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//        viewPager.setAdapter(adapter);
        Intent intent = getIntent();
        user_id = intent.getExtras().getString("id");
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        getUser(user_id);//사용자정보
        fr_call();
    }

    private void fr_call(){
        Log.e("온크리에잇에서 FR클래스2 : ", fr_class2);
    }

    private void getUser(String query) {
        final String set_id = query;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String fr_class = "0";
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                JSONArray data = json.getJSONArray("data");

                                fr_class = data.getJSONObject(0).has("fr_class") ? data.getJSONObject(0).getString("fr_class") : "0";
                                Log.e("에프알클래스 :",fr_class);
                                if(fr_class.equals("unknown")){
                                    Intent intentfr = new Intent(Chatprofile.this, Buzzerprofile.class);//유저프로필
                                    intentfr.putExtra("id", user_id);
                                    intentfr.putExtra("coincheck", 1);
                                    startActivity(intentfr);
                                    finish();
                                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                                }
                                if(fr_class.equals("friend")){
                                    Intent intentfr = new Intent(Chatprofile.this, FriendProfile.class);//유저프로필
                                    intentfr.putExtra("id", user_id);
                                    intentfr.putExtra("coincheck", 1);
                                    startActivity(intentfr);
                                    finish();
                                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                                }
                                if(fr_class.equals("following")){
                                    Intent intentfr = new Intent(Chatprofile.this, Followingprofile.class);//유저프로필
                                    intentfr.putExtra("id", user_id);
                                    intentfr.putExtra("coincheck", 1);
                                    startActivity(intentfr);
                                    finish();
                                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                                }
                                if(fr_class.equals("follow")){
                                    Intent intentfr = new Intent(Chatprofile.this, Followprofile.class);//유저프로필
                                    intentfr.putExtra("id", user_id);
                                    intentfr.putExtra("coincheck", 1);
                                    startActivity(intentfr);
                                    finish();
                                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        //Toast.makeText(getApplicationContext(), " FollowingList Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG," user_profile Error"+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_51_loading_user_profile", new String[]{id, set_id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
