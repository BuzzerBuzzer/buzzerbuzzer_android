package com.movements.and.buzzerbuzzer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
//나의프로필
public class Myprofile extends BaseActivity {
    private ImageView backbtn;
    private TextView id, friedns_cnt, follow_cnt, following_cnt, buzzer_cnt,numb_text, total_text, state_msg, friend_tx, age_tx, follow_tx;
    private ImageView profilegallery;
    private Button setting_btn;
    private ImageView user_pic, showpicbtn;
    private ViewPager viewPager;
    private MySlidingImage_Adapter adapter;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private String set_id, mPassword, password;
    private JsonConverter jc;
    private ArrayList<UserPic> imagesArray;
    private PasswordEn passwordEn;
    private RelativeLayout all_my_p;
    private Typeface typefaceBold, typefaceExtraBold;

    private static final String TAG = "Myprofile";

    private int downY, userPicY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);
        imagesArray = new ArrayList<>();
        jc = new JsonConverter();

        setting_btn = (Button) findViewById(R.id.setting_btn);
        backbtn = (ImageView) findViewById(R.id.backbtn);
        showpicbtn = (ImageView) findViewById(R.id.showpicbtn);
        id = (TextView) findViewById(R.id.id);
        friedns_cnt = (TextView) findViewById(R.id.friedns_cnt);
        following_cnt = (TextView) findViewById(R.id.following_cnt);
        follow_cnt = (TextView) findViewById(R.id.follow_cnt);
        buzzer_cnt = (TextView) findViewById(R.id.buzzer_cnt);
        numb_text = (TextView) findViewById(R.id.numb_text);
        total_text = (TextView) findViewById(R.id.total_text);
        state_msg = (TextView) findViewById(R.id.state_msg);
        user_pic = (ImageView) findViewById(R.id.user_pic);
        //viewPager = (ViewPager) findViewById(R.id.view_pager3);
        friend_tx = (TextView)findViewById(R.id.friend_tx);
        age_tx = (TextView) findViewById(R.id.age_tx);
        follow_tx = (TextView) findViewById(R.id.follow_tx);
        profilegallery = (ImageView)findViewById(R.id.profilegallery);


        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        id.setTypeface(typefaceExtraBold);
        setting_btn.setTypeface(typefaceExtraBold);
        buzzer_cnt.setTypeface(typefaceExtraBold);
        friedns_cnt.setTypeface(typefaceExtraBold);
        following_cnt.setTypeface(typefaceExtraBold);
        follow_cnt.setTypeface(typefaceExtraBold);
        state_msg.setTypeface(typefaceExtraBold);

        friend_tx.setTypeface(typefaceBold);
        age_tx.setTypeface(typefaceBold);
        follow_tx.setTypeface(typefaceBold);
        total_text.setTypeface(typefaceBold);

//        user_pic.setBackground(new ShapeDrawable(new OvalShape()));
//        user_pic.setClipToOutline(true);

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();
        set_id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);
        id.setText(set_id);

        //viewPager.setHorizontalFadingEdgeEnabled(true);
//        adapter = new MySlidingImage_Adapter(this, imagesArray);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                // onPageSelected(0);
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
      //  });

        //viewPager.setAdapter(adapter);

//        total_text.setText(String.valueOf(imagesArray.size()));//프로필사진 총 갯수
//        if(total_text.getText().toString().trim().equals("0")) {  //프로필사진이 없으면
//            total_text.setVisibility(View.INVISIBLE);
            showpicbtn.setImageResource(R.drawable.btn_thumb);
//        }

        Button();
        getUser(set_id);//나의정보가져오기
    }


    private void Button() {

        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intset = new Intent(getApplicationContext(), ProfileSetting.class);
                intset.putExtra("gps_base_check", true);
                startActivity(intset);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                finish();

            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onBackPressed();
                Intent intset = new Intent(getApplicationContext(), FriendList.class);
                intset.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intset.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intset);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);

            }
        });

        showpicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intset = new Intent(Myprofile.this, Picprofile.class);
                intset.putExtra("user_id", set_id);
                startActivityForResult(intset, 1);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });
//        user_pic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intset = new Intent(Myprofile.this, Picprofile.class);
//                intset.putExtra("user_id", set_id);
//                startActivityForResult(intset, 1);
//            }
//        });

        user_pic.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                userPicY = (int) event.getRawY();

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    downY = (int) event.getRawY();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if( 125 < -(downY - userPicY- 25)){
                        Intent intset = new Intent(getApplicationContext(), FriendList.class);
                        startActivity(intset);
                        overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                    }
                }
                return true;
            }
        });
    }


    private void getUser(String query) {
        final String set_id = query;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String pic_src = null, nickname = null, condition_msg = null, buzzer_open_profile_cnt = null, follow_num = null, friend_num, following_num, totalNum = null,gender = null;
                        Log.d(TAG+" 183 :", response);
                        ArrayList<UserPic> list2 = new ArrayList<>();
                        String no, user_pic_src, date;
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                JSONArray data = json.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    no = data.getJSONObject(i).has("no") ? data.getJSONObject(i).getString("no") : "";
                                    user_pic_src = data.getJSONObject(i).has("user_pic_src") ? data.getJSONObject(i).getString("user_pic_src") : "";
                                    UserPic userPic = new UserPic(no, user_pic_src);
                                    list2.add(userPic);
                                }
                                gender = data.getJSONObject(0).has("gender") ? data.getJSONObject(0).getString("gender") : "0";
                                totalNum = data.getJSONObject(0).has("totalNum") ? data.getJSONObject(0).getString("totalNum") : "0";
                                friend_num = data.getJSONObject(0).has("friendNum") ? data.getJSONObject(0).getString("friendNum") : "0";
                                follow_num = data.getJSONObject(0).has("followNum") ? data.getJSONObject(0).getString("followNum") : "0";
                                following_num = data.getJSONObject(0).has("followingNum") ? data.getJSONObject(0).getString("followingNum") : "0";
                                pic_src = data.getJSONObject(0).has("pic_src") ? data.getJSONObject(0).getString("pic_src") : "";
                                nickname = data.getJSONObject(0).has("nickname") ? data.getJSONObject(0).getString("nickname") : "";
                                condition_msg = data.getJSONObject(0).has("condition_msg") ? data.getJSONObject(0).getString("condition_msg") : "";
                                buzzer_open_profile_cnt = data.getJSONObject(0).has("buzzer_open_profile_cnt") ? data.getJSONObject(0).getString("buzzer_open_profile_cnt") : "";
                                if (pic_src.isEmpty()) {
                                } else {
                                    Glide.with(getApplicationContext()).load(pic_src).into(user_pic);
                                    Glide.with(getApplicationContext()).load(pic_src).into(profilegallery);
                                    //Glide.with(getApplicationContext()).load(pic_src).into(user_pic);
                                }
                                state_msg.setText(condition_msg);
                                id.setText(nickname);
                                buzzer_cnt.setText(buzzer_open_profile_cnt);

                                Log.e("프렌즈 카운트", friend_num);
                                if(friend_num.equals("")){
                                    friedns_cnt.setText("0");
                                }else{
                                    friedns_cnt.setText(friend_num);
                                }
                                if(following_num.equals("")){
                                    following_cnt.setText("0");
                                }else{
                                    following_cnt.setText(following_num);
                                }
                                if(follow_num.equals("")){
                                    follow_cnt.setText("0");
                                }else{
                                    follow_cnt.setText(follow_num);
                                }



                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        imagesArray.clear();
                        imagesArray.addAll(list2);
                        //adapter.notifyDataSetChanged();
                        total_text.setText(totalNum);//프로필사진 총 갯수
                        total_text.bringToFront();
                        
                        editor.putString("my_total_pic_num", totalNum);
                        editor.putString("gender", gender);
                        editor.commit();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        //Toast.makeText(getApplicationContext(), " getMyinfo Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG," my_profile Error");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(set_id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_22_loading_my_profile", new String[]{set_id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            getUser(set_id);
//            ((FriendList) FriendList.mContext).getData(set_id, 0);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getUser(set_id);
        //viewPager.setCurrentItem(0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_exit, R.anim.bottom_down);
    }
}
