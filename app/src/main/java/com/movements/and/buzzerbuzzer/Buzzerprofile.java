package com.movements.and.buzzerbuzzer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samkim on 2017. 3. 17..
 */
//서칭유저프로필
public class Buzzerprofile extends BaseActivity {
    private final String TAG = "Buzzerprofile";
    private ImageView backbtn, profilegallery, showpicbtn;
    private Button following_btn, chat_btn ,call_btn;
    private ViewPager viewPager;
    private ImageView user_pic;
    private TextView id_text, total_text, numb_text, state_msg, buzzer_cnt, following_cnt, like_cnt,following_tx, unfollow_btn, state;
    private LinearLayout u_ll;

    private MySlidingImage_Adapter adapter;
    private ArrayList<UserPic> imagesArray;
    private JsonConverter jc;

    private PasswordEn passwordEn;

    private String user_id, id, mPassword, password, gender;

    private Typeface typefaceBold, typefaceExtraBold;
    private int coincheck = 0;
    private int buzzer_item_cnt;
    private SharedPreferences setting;
    private static final int TAB_ID = 0;
    private String nick;
    private int downY, userPicY;

    private BuzzerDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        u_ll = (LinearLayout) findViewById(R.id.user_linearLayout);
        backbtn = (ImageView) findViewById(R.id.backbtn);
        profilegallery = (ImageView) findViewById(R.id.profilegallery);
        showpicbtn = (ImageView) findViewById(R.id.showpicbtn);
        following_btn = (Button) findViewById(R.id.friend_btn);
        chat_btn = (Button) findViewById(R.id.chat_btn);
        call_btn = (Button) findViewById(R.id.call_btn);
        like_cnt = (TextView) findViewById(R.id.buzzer_cnt);
        following_tx = (TextView)findViewById(R.id.following_tx);
        unfollow_btn = (TextView)findViewById(R.id.unfollow_btn);

        user_pic = (ImageView) findViewById(R.id.user_pic2);

        id_text = (TextView) findViewById(R.id.id);

        total_text = (TextView) findViewById(R.id.total_text);
        //numb_text = (TextView) findViewById(R.id.numb_text);
        state_msg = (TextView) findViewById(R.id.state_msg);
        buzzer_cnt = (TextView) findViewById(R.id.buzzer_cnt);
        following_cnt = (TextView) findViewById(R.id.following_cnt);
        state = (TextView) findViewById(R.id.state);

        id_text.setTypeface(typefaceExtraBold);
        state_msg.setTypeface(typefaceBold);
        state.setTypeface(typefaceBold);
        like_cnt.setTypeface(typefaceExtraBold);
        total_text.setTypeface(typefaceBold);
        following_tx.setTypeface(typefaceBold);
        following_cnt.setTypeface(typefaceExtraBold);
        following_btn.setTypeface(typefaceExtraBold);
        chat_btn.setTypeface(typefaceExtraBold);
        call_btn.setTypeface(typefaceExtraBold);
        unfollow_btn.setTypeface(typefaceExtraBold);

        imagesArray = new ArrayList<>();
        jc = new JsonConverter();

        Intent intent = getIntent();
        user_id = intent.getExtras().getString("id");
        coincheck = intent.getIntExtra("coincheck", 0);
        setting = getSharedPreferences("setting", MODE_PRIVATE);

        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);
        getUser(id, user_id);//회원정보불러오기

        button();
    }

    private void button() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent inback = new Intent(getApplicationContext(), Allsetting.class);
                inback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                inback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(inback);
                */
                finish();
                overridePendingTransition(R.anim.bottom_exit, R.anim.bottom_down);
            }
        });

        showpicbtn.setOnClickListener(new View.OnClickListener() {//우측상단 사진리스트버튼
            @Override
            public void onClick(View v) {
                if (imagesArray.size() > 0) {
                    Intent intset = new Intent(Buzzerprofile.this, Picprofile2.class);
                    intset.putExtra("user_id", user_id);
                    startActivity(intset);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
            }
        });

        following_btn.setOnClickListener(new View.OnClickListener() {//팔로우버튼
            @Override
            public void onClick(View v) {

                if (coincheck == 1){
                    Log.i("유저검색 친구추천","코인 미삭감");
                    nocoinfollowDialog();
                    /*
                    new AlertDialog.Builder(Buzzerprofile.this, R.style.BuzzerAlertStyle)
//                            .setTitle("Buzzer Buzzer")
                            .setMessage(nick + "님을\n팔로우 하시겠습니까?")
                            .setPositiveButton("팔로우하기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    followingstate2();
                                }

                            })
                            .setNegativeButton("취소", null)
                            .show();
                     */

                }else {

                    if (gender.equals("w")) {
                        if (buzzer_item_cnt > 2) {//이용권갯수

                            followDialog();
                            /*
                            new AlertDialog.Builder(Buzzerprofile.this, R.style.BuzzerAlertStyle)
//                            .setTitle("Buzzer Buzzer")
                                    .setMessage(nick + "님을\n팔로우 하시겠습니까?")
                                    .setPositiveButton("팔로우하기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            followingstate();
                                        }

                                    })
                                    .setNegativeButton("취소", null)
                                    .show();
                            */
//                    followingstate();//팔로잉 결과값을 서버로..
                        } else {
                            coinbuyDialog();
                            /*
                            new AlertDialog.Builder(Buzzerprofile.this)
                                    .setTitle("Buzzer Buzzer")
                                    .setMessage("이용권을 구매 해주십시오.\n구매페이지로 이동 하시겠습니까?")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent1 = new Intent(Buzzerprofile.this, BuzzerPurchase.class);
                                            startActivity(intent1);
                                            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                                        }

                                    })
                                    .setNegativeButton("취소", null)
                                    .show(); */
                        }

                    } else {
                        Log.e(TAG, "코인 1 차감");
                        if (buzzer_item_cnt > 0) {//이용권갯수
                            followDialog();
                            /*
                            new AlertDialog.Builder(Buzzerprofile.this, R.style.BuzzerAlertStyle)
//                            .setTitle("Buzzer Buzzer")
                                    .setMessage(nick + "님을\n팔로우 하시겠습니까?")
                                    .setPositiveButton("팔로우하기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            followingstate();
                                        }

                                    })
                                    .setNegativeButton("취소", null)
                                    .show();
                             */
//                    followingstate();//팔로잉 결과값을 서버로..
                        } else {
                            coinbuyDialog();
                            /*
                            new AlertDialog.Builder(Buzzerprofile.this)
                                    .setTitle("Buzzer Buzzer")
                                    .setMessage("이용권을 구매 해주십시오.\n구매페이지로 이동 하시겠습니까?")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent1 = new Intent(Buzzerprofile.this, BuzzerPurchase.class);
                                            startActivity(intent1);
                                            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                                        }

                                    })
                                    .setNegativeButton("취소", null)
                                    .show(); */
                        }
                    }

                }

            }
        });

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> userlist = new ArrayList<>();
                userlist.add(user_id);
                createGroupChannel(userlist, true);//채널을 만들고 만들어진 채널값으로 채팅방을 오픈
            }
        });

//        call_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
//                startActivity(intent);
//            }
//        });

        unfollow_btn.setOnClickListener(new View.OnClickListener() {//팔로잉버튼
            @Override
            public void onClick(View v) {

                if("언팔로우하기".equals(unfollow_btn.getText().toString().trim())) {
                    unfollowDialog();
                    /*
                    new AlertDialog.Builder(Buzzerprofile.this, R.style.BuzzerAlertStyle)
                            .setMessage(nick + "님의\n팔로우를 취소 하시겠습니까?")
                            .setPositiveButton("팔로우취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    unfollowingstate();
                                }

                            })
                            .setNegativeButton("취소", null)
                            .show();*/
//                followingstate();//팔로잉 결과 서버
                }
//                else if("팔로우하기".equals(unfollow_btn.getText().toString().trim())){
//                    new AlertDialog.Builder(Buzzerprofile.this)
//                            .setTitle("Buzzer Buzzer")
//                            .setMessage(user_id + "님을\n팔로우 하시겠습니까?")
//                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    followstate();
//                                }
//
//                            })
//                            .setNegativeButton("취소", null)
//                            .show();
//
//                }
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
                    if( 125 < -(downY - userPicY- 125)){
                        onBackPressed();
                    }
                }
                return true;
            }
        });
    }
//TODO: 아이템 카운트 getUser로 옮기기.
//    private void getbuzzer_item_cnt(final String id) {
//        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                String item_cnt = null;
//                try {
//                    JSONObject resJson = new JSONObject(response);
//
//                    if (resJson.getString("result").equals("success")) {//성공시
//                        JSONArray data = resJson.getJSONArray("data");
//                        item_cnt = data.getJSONObject(0).has("buzzer_item_cnt") ? data.getJSONObject(0).getString("buzzer_item_cnt") : "0";
//
//                    } else {
//
//                    }
//                    buzzer_item_cnt = Integer.parseInt(item_cnt);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "buzzerpurchase_cnt Error :" + error);
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("param", jc.createJsonParam("buzzerpurchase_cnt", new String[]{id, user_id, ""}));
//                return params;
//            }
//
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(multiPartRequest);
//    }

    private void unfollowingstate() {
        // final String id = set_id;
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        following_btn.getBackground().setColorFilter(getResources().getColor(R.color.colorMain), PorterDuff.Mode.SRC_ATOP);
                        following_btn.setText("팔로우");
                        following_btn.setTextColor(getApplication().getResources().getColor(R.color.grey04));

                        ((FriendList) FriendList.mContext).getFollowList(id, 2);
                        ((FriendList) FriendList.mContext).getFollowingList(id, 1);
                        ((FriendList) FriendList.mContext).getFriendList(id, 0);
                        ((FriendList) FriendList.mContext).loadingMainList(id, 0);

                        following_btn.setVisibility(View.VISIBLE);

                        state.setVisibility(View.GONE);
                        unfollow_btn.setVisibility(View.GONE);
                        u_ll.setVisibility(View.GONE);

                    } else {
                        Toast.makeText(getApplicationContext(), "다시시도해주세요", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();;
                Log.e(TAG," unfollow_following Error"+error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_53_unfollow_following_user", new String[]{id, user_id}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void followingstate() {
        final int coincheck2 = coincheck;
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        //Toast.makeText(getApplicationContext(), "다시시도해주세요", Toast.LENGTH_LONG).show();
//                        following_btn.getBackground().setColorFilter(getResources().getColor(R.color.buzzerfollowbtn), PorterDuff.Mode.SRC_ATOP);
//                        following_btn.setText("팔로잉");
//                        following_btn.setTextColor(Color.WHITE);
                        following_btn.setVisibility(View.GONE);

                        ((FriendList) FriendList.mContext).getFollowingList(id, 1);
                        ((FriendList) FriendList.mContext).getFollowList(id, 2);
                        ((FriendList) FriendList.mContext).loadingMainList(id, 0);
                        ((FriendList) FriendList.mContext).getFriendList(id, 0);

                        unfollow_btn.setVisibility(View.VISIBLE);
                        unfollow_btn.setTextColor(getResources().getColor(R.color.grey04));
                        unfollow_btn.bringToFront();
                        u_ll.setVisibility(View.VISIBLE);
                        state.setVisibility(View.VISIBLE);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "add_user_request_follow_searching Error :" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_64_follow_around_me_user", new String[]{id, user_id}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void followingstate2() {
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        //Toast.makeText(getApplicationContext(), "다시시도해주세요", Toast.LENGTH_LONG).show();
//                        following_btn.getBackground().setColorFilter(getResources().getColor(R.color.buzzerfollowbtn), PorterDuff.Mode.SRC_ATOP);
//                        following_btn.setText("팔로잉");
//                        following_btn.setTextColor(Color.WHITE);
                        following_btn.setVisibility(View.GONE);

                        ((FriendList) FriendList.mContext).getFollowingList(id, 1);
                        ((FriendList) FriendList.mContext).getFollowList(id, 2);
                        ((FriendList) FriendList.mContext).loadingMainList(id, 0);
                        ((FriendList) FriendList.mContext).getFriendList(id, 0);

                        unfollow_btn.setVisibility(View.VISIBLE);
                        unfollow_btn.setTextColor(getResources().getColor(R.color.grey04));
                        unfollow_btn.bringToFront();
                        u_ll.setVisibility(View.VISIBLE);
                        state.setVisibility(View.VISIBLE);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "add_user_request_follow_searching Error :" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_58_follow_user_one_side", new String[]{id, user_id}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }


    private void getUser(final String id, String query) {

        final String set_id = query;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String pic_src = null, nickname = null, condition_msg = null, fr_class, pic_src_no, buzzer_open_profile_cnt = null, follow_num = "0", totalNum = "0",item_cnt, phone;
                        ArrayList<UserPic> list2 = new ArrayList<>();
                        String no, user_pic_src;
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                JSONArray data = json.getJSONArray("data");

                                totalNum = data.getJSONObject(0).has("totalNum") ? data.getJSONObject(0).getString("totalNum") : "0";
                                pic_src = data.getJSONObject(0).has("pic_src") ? data.getJSONObject(0).getString("pic_src") : "";
                                nickname = data.getJSONObject(0).has("nickname") ? data.getJSONObject(0).getString("nickname") : "";
                                condition_msg = data.getJSONObject(0).has("condition_msg") ? data.getJSONObject(0).getString("condition_msg") : "";
                                buzzer_open_profile_cnt = data.getJSONObject(0).has("buzzer_open_profile_cnt") ? data.getJSONObject(0).getString("buzzer_open_profile_cnt") : "";
                                follow_num = data.getJSONObject(0).has("followNum") ? data.getJSONObject(0).getString("followNum") : "0";
                                item_cnt = data.getJSONObject(0).has("my_coin_cnt") ? data.getJSONObject(0).getString("my_coin_cnt") : "0";
                                phone = data.getJSONObject(0).has("phone") ? data.getJSONObject(0).getString("phone") : "0";
                                fr_class = data.getJSONObject(0).has("fr_class") ? data.getJSONObject(0).getString("fr_class") : "0";
                                pic_src_no = data.getJSONObject(0).has("pic_src_no") ? data.getJSONObject(0).getString("pic_src_no") : "0";
                                gender = data.getJSONObject(0).has("gender") ? data.getJSONObject(0).getString("gender") : "";
                                no = data.getJSONObject(0).has("pic_src_no") ? data.getJSONObject(0).getString("pic_src_no") : "";
                                user_pic_src = data.getJSONObject(0).has("pic_src") ? data.getJSONObject(0).getString("pic_src") : "";

                                UserPic userPic = new UserPic(no, user_pic_src);
                                list2.add(userPic);

                                if (pic_src.isEmpty()) {
                                    user_pic.setImageResource(R.drawable.nopic__s_m);
                                } else {
                                    Glide.with(getApplicationContext()).load(pic_src).into(user_pic);
                                    Glide.with(getApplicationContext()).load(pic_src).into(profilegallery);
                                }
                                nick = nickname;
                                state_msg.setText(condition_msg);
                                id_text.setText(nickname);
                                //like_cnt.setText(buzzer_open_profile_cnt);
//                                int following_num = 0;
//                                following_num = Integer.valueOf(follow_num) + Integer.valueOf(friend_num);
                                following_cnt.setText(follow_num);
                                buzzer_cnt.setText(buzzer_open_profile_cnt);
                                buzzer_item_cnt = Integer.parseInt(item_cnt);

                                if(gender.equals("w")){
                                    id_text.setTextColor(getApplicationContext().getResources().getColor(R.color.colorMain));
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        imagesArray.clear();
                        imagesArray.addAll(list2);
//                        adapter.notifyDataSetChanged();
                            total_text.setText(totalNum);//프로필사진 총 갯수

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        Log.e(TAG, "buzzer_profile Error :" + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_63_loading_around_me_user_profile", new String[]{id, set_id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
//            getbuzzer_item_cnt(id);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        getbuzzer_item_cnt(id);
    }

    private void createGroupChannel(List<String> userIds, boolean distinct) {
        GroupChannel.createChannelWithUserIds(userIds, distinct, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    Log.e("createGroupChannel :", e.toString());
                    //Toast.makeText(getApplicationContext(), "SendBirdException e\n" + e, Toast.LENGTH_SHORT).show();
                    return;
                }

                //개인 채팅창 열기
                //열린 channel값이 있다면 채팅방을 연다.
                if (groupChannel.getUrl() != null) {
                    Intent intent = new Intent(getApplicationContext(), PersonalChatActivity.class);
                    intent.putExtra("groupChannelUrl", groupChannel.getUrl());
                    startActivity(intent);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                } else {
//                    Toast.makeText(getApplicationContext(), getString(R.string.str_sendbird_chat_failed), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_exit, R.anim.bottom_down);
    }


    public void coinbuyDialog(){
        dialog = new BuzzerDialog(Buzzerprofile.this,
                "이용권을 구매 해주십시오.\n구매페이지로 이동 하시겠습니까?", "확인", "취소", coinbuyOKListener, cancelListener);

        //요청 이 다이어로그를 종료할 수 있게 지정함
        dialog.setCancelable(true);
        dialog.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener coinbuyOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent1 = new Intent(Buzzerprofile.this, BuzzerPurchase.class);
            startActivity(intent1);
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            dialog.dismiss();
        }
    };

    public void nocoinfollowDialog(){
        dialog = new BuzzerDialog(Buzzerprofile.this,
                nick + " 님을\n팔로우 하시겠습니까?", "팔로우", "취소", nocoinfollowOKListener, cancelListener);

        //요청 이 다이어로그를 종료할 수 있게 지정함
        dialog.setCancelable(true);
        dialog.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener nocoinfollowOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            followingstate2();
            dialog.dismiss();
        }
    };

    public void followDialog(){
        dialog = new BuzzerDialog(Buzzerprofile.this,
                nick + " 님을\n팔로우 하시겠습니까?", "팔로우", "취소", followOKListener, cancelListener);

        //요청 이 다이어로그를 종료할 수 있게 지정함
        dialog.setCancelable(true);
        dialog.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener followOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            followingstate();
            dialog.dismiss();
        }
    };

    public void unfollowDialog(){
        dialog = new BuzzerDialog(Buzzerprofile.this,
                nick + " 님의\n팔로우를 취소하시겠습니까?", "팔로우 취소", "취소", unfollowOKListener, cancelListener);

        //요청 이 다이어로그를 종료할 수 있게 지정함
        dialog.setCancelable(true);
        dialog.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener unfollowOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            unfollowingstate();
            dialog.dismiss();
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

}
