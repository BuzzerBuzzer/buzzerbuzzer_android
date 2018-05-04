package com.movements.and.buzzerbuzzer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.movements.and.buzzerbuzzer.Adapter.MySlidingImage_Adapter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.UserPic;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 3. 17..
 */
//친구추천 프로필사진
public class Popupprofile extends BaseActivity {

    private ImageView backbtn, showpicbtn, profilegallery;
    private Button following_btn;
    //private ViewPager viewPager;
    private ImageView user_pic;
    private TextView id, total_text, numb_text, state_msg, like_cnt, following_cnt, following_tx;

    private Typeface typefaceBold, typefaceExtraBold;

    private MySlidingImage_Adapter adapter;
    private JsonConverter jc;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String myid, user_id, mPassword, password;

    private ArrayList<UserPic> imagesArray;
    private static final String TAG = "Popupprofile";
    private String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");


        setting = getSharedPreferences("setting", MODE_PRIVATE);
        myid = setting.getString("user_id", "");

        backbtn = (ImageView) findViewById(R.id.backbtn);
        following_btn = (Button) findViewById(R.id.friend_btn);
        user_pic = (ImageView) findViewById(R.id.user_pic2);
        showpicbtn = (ImageView) findViewById(R.id.showpicbtn);
        profilegallery = (ImageView) findViewById(R.id.profilegallery);
//        user_pic.setBackground(new ShapeDrawable(new OvalShape()));
//        user_pic.setClipToOutline(true);
        id = (TextView) findViewById(R.id.id);
        total_text = (TextView) findViewById(R.id.total_text);
        //numb_text = (TextView) findViewById(R.id.numb_text);
        state_msg = (TextView) findViewById(R.id.state_msg);
        like_cnt = (TextView) findViewById(R.id.buzzer_cnt);
        following_cnt = (TextView) findViewById(R.id.following_cnt);
        following_tx = (TextView)findViewById(R.id.following_tx);
        //viewPager = (ViewPager) findViewById(R.id.view_pager2);

        id.setTypeface(typefaceExtraBold);
        state_msg.setTypeface(typefaceBold);
        like_cnt.setTypeface(typefaceExtraBold);
        total_text.setTypeface(typefaceBold);
        following_tx.setTypeface(typefaceBold);
        following_cnt.setTypeface(typefaceExtraBold);
        following_btn.setTypeface(typefaceExtraBold);

        imagesArray = new ArrayList<>();
        jc = new JsonConverter();

        editor = setting.edit();
        passwordEn = new PasswordEn();

        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);
        //viewPager.setHorizontalFadingEdgeEnabled(true);
        //adapter = new MySlidingImage_Adapter(this, imagesArray);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                numb_text.setText(String.valueOf(position + 1));
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//        viewPager.setAdapter(adapter);
        Intent intent = getIntent();
        user_id = intent.getExtras().getString("id");

        button();
        getUser(user_id);//유저정보 불러오기

    }

    private void button() {
        //팔로잉 코인생각해야함.TODO:해야함.
        following_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followingstate();//팔로우
            }
        });

        showpicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagesArray.size() > 0) {
                    Intent intset = new Intent(Popupprofile.this, Picprofile2.class);
                    intset.putExtra("user_id", user_id);
                    startActivity(intset);
                }
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    private void followingstate() {
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject json = new JSONObject(response);
                    Log.e(TAG, response);
                    if (json.getString("result").equals("success")) {//성공시
                        following_btn.getBackground().setColorFilter(getResources().getColor(R.color.cancle_color), PorterDuff.Mode.SRC_ATOP);
                        following_btn.setText("팔로우 신청이 되었습니다.");
                        following_btn.setTextColor(Color.WHITE);
                        following_btn.setClickable(false);
                        ((FriendList) FriendList.mContext).getFollowingList(myid, 1);

                    } else {
                        Toast.makeText(getApplicationContext(), "다시 시도해주세요", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG," add_user_request_follow"+ error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(myid, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_58_follow_user_one_side", new String[]{myid, user_id, ""}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }


    private void getUser(String query) {
        final String set_id = query;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String pic, nick, msg, buzzer_open_profile_cnt, follow_num;
                        ArrayList<UserPic> list2 = new ArrayList<>();
                        String no, user_pic_src, date;
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {
                                JSONArray list = json.getJSONArray("data");
                                for (int i = 0; i < list.length(); i++) {
                                    no = list.getJSONObject(i).has("no") ? list.getJSONObject(i).getString("no") : "";
                                    user_pic_src = list.getJSONObject(i).has("pic_src") ? list.getJSONObject(i).getString("pic_src") : "";
                                    //date = list.getJSONObject(i).has("date") ? list.getJSONObject(i).getString("date") : "";
                                    UserPic userPic = new UserPic(no, user_pic_src);
                                    list2.add(userPic);

                                }
                                gender = list.getJSONObject(0).has("gender") ? list.getJSONObject(0).getString("gender") : "";
                                pic = list.getJSONObject(0).has("pic_src") ? list.getJSONObject(0).getString("pic_src") : "";
                                nick = list.getJSONObject(0).has("nickname") ? list.getJSONObject(0).getString("nickname") : "";
                                msg = list.getJSONObject(0).has("condition_msg") ? list.getJSONObject(0).getString("condition_msg") : "";
                                buzzer_open_profile_cnt = list.getJSONObject(0).has("buzzer_open_profile_cnt") ? list.getJSONObject(0).getString("buzzer_open_profile_cnt") : "";
                                follow_num = list.getJSONObject(0).has("followNum") ? list.getJSONObject(0).getString("followNum") : "0";

                                user_pic.setImageResource(R.drawable.nopic__s_m);
                                profilegallery.setImageResource(R.drawable.nopic__s_m);
                                if (pic.isEmpty()) {

                                } else {
                                    Picasso.with(getApplicationContext()).load(pic).into(user_pic);
                                    Picasso.with(getApplicationContext()).load(pic).into(profilegallery);
                                }

                                id.setText(nick);
                                if(gender.equals("w")){
                                    id.setTextColor(Color.rgb(125,81,252));
                                }
                                state_msg.setText(msg);
                                like_cnt.setText(buzzer_open_profile_cnt);
                                following_cnt.setText(follow_num);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        imagesArray.clear();
                        imagesArray.addAll(list2);
                        //adapter.notifyDataSetChanged();
                        total_text.setText(String.valueOf(imagesArray.size()));//프로필사진 총 갯수

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        Log.e(TAG," user_profile"+ error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(myid, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_57_loading_search_user_profile", new String[]{myid, set_id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
