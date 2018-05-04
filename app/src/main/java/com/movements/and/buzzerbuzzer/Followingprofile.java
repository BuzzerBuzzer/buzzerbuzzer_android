package com.movements.and.buzzerbuzzer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
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
import java.util.Set;

/**
 * Created by samkim on 2017. 3. 17..
 */
//팔로잉프로필
public class Followingprofile extends BaseActivity {

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

    private BuzzerDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.following_profile);

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
        user_pic = (ImageView) findViewById(R.id.user_pic2);
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

        button();
        getUser(user_id);//사용자정보

    }

    private void button() {

        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(call_btn.isEnabled()){
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
                    startActivity(intent);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
            }
        });

        following_btn.setOnClickListener(new View.OnClickListener() {//팔로잉버튼
            @Override
            public void onClick(View v) {

                if("언팔로우하기".equals(following_btn.getText().toString().trim())) {
                    unfollowDialog();
                    /*
                    new AlertDialog.Builder(Followingprofile.this, R.style.BuzzerAlertStyle)
                            .setMessage(nick + "님의\n팔로우를 취소 하시겠습니까?")
                            .setPositiveButton("팔로우취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    followingstate();
                                }

                            })
                            .setNegativeButton("취소", null)
                            .show();  */
//                followingstate();//팔로잉 결과 서버
                }else if("팔로우하기".equals(following_btn.getText().toString().trim())){
                    new AlertDialog.Builder(Followingprofile.this, R.style.BuzzerAlertStyle)
                            .setMessage(nick + "님을\n팔로우 하시겠습니까?")
                            .setPositiveButton("팔로우", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    followstate();
                                }

                            })
                            .setNegativeButton("취소", null)
                            .show();

                }
            }
        });

        big_friend_btn.setOnClickListener(new View.OnClickListener() {//팔로잉버튼
            @Override
            public void onClick(View v) {
                followDialog();
                /*
                new AlertDialog.Builder(Followingprofile.this, R.style.BuzzerAlertStyle)
                        .setMessage(nick + "님을\n팔로우 하시겠습니까?")
                        .setPositiveButton("팔로우", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                followstate();
                            }

                        })
                        .setNegativeButton("취소", null)
                        .show();*/
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//하단버튼 친구리스트
                /*
                Intent intfind1 = new Intent(getApplicationContext(), FriendList.class);
                intfind1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intfind1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intfind1);
                */
                finish();

            }
        });
        showpicbtn.setOnClickListener(new View.OnClickListener() {//우측상단 사진리스트버튼
            @Override
            public void onClick(View v) {
                if (imagesArray.size() > 0) {
                    Intent intset = new Intent(Followingprofile.this, Picprofile2.class);
                    intset.putExtra("user_id", user_id);
                    startActivity(intset);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
            }
        });

//        user_pic.setOnClickListener(new View.OnClickListener() {//프로필사진
//            @Override
//            public void onClick(View v) {
//                if (imagesArray.size() > 0) {
//                    Intent intset = new Intent(Followingprofile.this, Picprofile2.class);
//                    intset.putExtra("user_id", user_id);
//                    startActivity(intset);
//                }
//            }
//        });
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> userlist = new ArrayList<>();
                userlist.add(user_id);
                createGroupChannel(userlist, true);//채널을 만들고 만들어진 채널값으로 채팅방을 오픈
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


    private void followingstate() {
        // final String id = set_id;
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        //following_btn.getBackground().setColorFilter(getResources().getColor(R.color.err_btn), PorterDuff.Mode.SRC_ATOP);
//                        following_btn.setText("팔로우하기");
//                        following_btn.setTextColor(getApplication().getResources().getColor(R.color.colorMain));

                        ((FriendList) FriendList.mContext).getFollowList(id, 2);
                        ((FriendList) FriendList.mContext).getFriendList(id, 0);
                        ((FriendList) FriendList.mContext).loadingMainList(id, 0);
                        ((FriendList) FriendList.mContext).getFollowingList(id, 1);

                        big_friend_btn.setVisibility(View.VISIBLE);
                        following_ll.setVisibility(View.GONE);
                        following_btn.setVisibility(View.GONE);
                        state.setVisibility(View.GONE);

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

    private void followstate() {

        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        //Toast.makeText(getApplicationContext(), "다시시도해주세요", Toast.LENGTH_LONG).show();
                        following_btn.setText("언팔로우하기");
                        following_btn.setTextColor(getApplication().getResources().getColor(R.color.grey04));

                        following_ll.setVisibility(View.VISIBLE);
                        following_btn.setVisibility(View.VISIBLE);
                        big_friend_btn.setVisibility(View.GONE);

                        ((FriendList) FriendList.mContext).getFollowList(id, 2);
                        ((FriendList) FriendList.mContext).getFriendList(id, 0);
                        ((FriendList) FriendList.mContext).loadingMainList(id, 0);
                        ((FriendList) FriendList.mContext).getFollowingList(id, 1);

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


    private void getUser(String query) {
        final String set_id = query;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String pic_src = null, nickname = null, condition_msg = null, buzzer_open_profile_cnt = null, follow_num = "0", fr_class = "0", totalNum = null,gender;
//                        String pic_src = null, nickname = null, condition_msg = null, buzzer_open_profile_cnt = null, follow_num, friend_num;
                        //int follow_num = 0;
                        ArrayList<UserPic> list2 = new ArrayList<>();
                        String no, user_pic_src;
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                JSONArray data = json.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    no = data.getJSONObject(i).has("no") ? data.getJSONObject(i).getString("no") : "";
                                    user_pic_src = data.getJSONObject(i).has("pic_src") ? data.getJSONObject(i).getString("pic_src") : "";
                                    UserPic userPic = new UserPic(no, user_pic_src);
                                    list2.add(userPic);
                                }
                                gender = data.getJSONObject(0).has("gender") ? data.getJSONObject(0).getString("gender") : "";
                                totalNum = data.getJSONObject(0).has("totalNum") ? data.getJSONObject(0).getString("totalNum") : "";
                                pic_src = data.getJSONObject(0).has("pic_src") ? data.getJSONObject(0).getString("pic_src") : "";
                                nickname = data.getJSONObject(0).has("nickname") ? data.getJSONObject(0).getString("nickname") : "";
                                condition_msg = data.getJSONObject(0).has("condition_msg") ? data.getJSONObject(0).getString("condition_msg") : "";
                                buzzer_open_profile_cnt = data.getJSONObject(0).has("buzzer_open_profile_cnt") ? data.getJSONObject(0).getString("buzzer_open_profile_cnt") : "";
                                follow_num = data.getJSONObject(0).has("followNum") ? data.getJSONObject(0).getString("followNum") : "0";
                                fr_class = data.getJSONObject(0).has("fr_class") ? data.getJSONObject(0).getString("fr_class") : "0";
                                tel = data.getJSONObject(0).has("phone") ? data.getJSONObject(0).getString("phone") : "";


                                if (pic_src.isEmpty()) {
                                    user_pic.setImageResource(R.drawable.nopic__s_m);
                                } else {
                                    Glide.with(getApplicationContext()).load(pic_src).into(profilegallery);
                                    Glide.with(getApplicationContext()).load(pic_src).into(user_pic);
                                }
                                state_msg.setText(condition_msg);
                                nick = nickname;
                                idex.setText(nickname);

                                if(gender.equals("w")){
                                    idex.setTextColor(getApplicationContext().getResources().getColor(R.color.colorMain));
                                }
                                like_cnt.setText(buzzer_open_profile_cnt);
//                                int cnt = 0;
//                                cnt = Integer.valueOf(follow_num) + Integer.valueOf(friend_num);
                                following_cnt.setText(String.valueOf(follow_num));

                                Set<String> slist = setting.getStringSet("phoneNums", null);
                                List<String> plist = new ArrayList<String>(slist);
                                Log.d(TAG +"plist.size()", String.valueOf(plist.size()));

                                for(int i = 0; i < plist.size(); i++){
                                    if(tel.equals(plist.get(i))){
                                        call_btn.setEnabled(true);
                                        call_btn.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.btn_call), null , null,null);
                                        call_btn.setTextColor(getApplication().getResources().getColor(R.color.colorMain));
                                    }
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        imagesArray.clear();
                        imagesArray.addAll(list2);
                        //adapter.notifyDataSetChanged();
                        total_text.setText(totalNum);//프로필사진 총 갯수

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

    /*
채널을 만들고 만들어진 채널값으로 채팅방을 오픈
*/
    private void createGroupChannel(List<String> userIds, boolean distinct) {
        GroupChannel.createChannelWithUserIds(userIds, distinct, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    Log.e("createGroupChannel :", e.toString());
                    Toast.makeText(getApplicationContext(), "SendBirdException e\n" + e, Toast.LENGTH_SHORT).show();
                    return;
                }

                //개인 채팅창 열기
                //열린 channel값이 있다면 채팅방을 연다.
                if (groupChannel.getUrl() != null) {
                    Intent intent = new Intent(getApplicationContext(), PersonalChatActivity.class);
                    intent.putExtra("groupChannelUrl", groupChannel.getUrl());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.str_sendbird_chat_failed), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_exit, R.anim.bottom_down);
    }

    public void followDialog(){
        dialog = new BuzzerDialog(Followingprofile.this,
                nick + " 님을\n팔로우 하시겠습니까?", "팔로우", "취소", followOKListener, cancelListener);

        //요청 이 다이어로그를 종료할 수 있게 지정함
        dialog.setCancelable(true);
        dialog.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener followOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            followstate();
            dialog.dismiss();
        }
    };

    public void unfollowDialog(){
        dialog = new BuzzerDialog(Followingprofile.this,
                nick + " 님의\n팔로우를 취소하시겠습니까?", "팔로우 취소", "취소", unfollowOKListener, cancelListener);

        //요청 이 다이어로그를 종료할 수 있게 지정함
        dialog.setCancelable(true);
        dialog.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener unfollowOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            followingstate();
            dialog.dismiss();
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

}

