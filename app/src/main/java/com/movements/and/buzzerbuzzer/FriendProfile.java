package com.movements.and.buzzerbuzzer;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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
 * Created by samkim on 2017. 4. 17..
 */
//친구프로필
public class FriendProfile extends BaseActivity {
    String NEWS_NOTI_READ;

    private static final int TAB_ID = 0;
    private ImageView backbtn, showpicbtn;
    private TextView unfollow, sign;
    private ViewPager viewPager;
    private ImageView user_pic, profilegallery;
    private Button chat_btn, call_btn;
    private String nick;
    private TextView idtx, msgtx, total_text, follow_cnt, like_cnt, numb_text, follow_tx;

    private Typeface typefaceBold, typefaceExtraBold;

    private MySlidingImage_Adapter adapter;
    private String user_id, id, tel, mPassword, password;;
    private JsonConverter jc;
    private BuzzerDialog dialog;
    private ArrayList<UserPic> imagesArray;

    private SharedPreferences setting;
    private PasswordEn passwordEn;
    private final String TAG = "FriendProfile";

    String[] phoneArr;

    private int downY, userPicY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_profile);
        imagesArray = new ArrayList<>();
        jc = new JsonConverter();

        backbtn = (ImageView) findViewById(R.id.backbtn);
        profilegallery = (ImageView) findViewById(R.id.profilegallery);
        chat_btn = (Button) findViewById(R.id.chat_btn);
        call_btn = (Button) findViewById(R.id.call_btn);
        showpicbtn = (ImageView) findViewById(R.id.showpicbtn);
        total_text = (TextView) findViewById(R.id.total_text);
        follow_cnt = (TextView) findViewById(R.id.follow_cnt);
        like_cnt = (TextView) findViewById(R.id.buzzer_cnt);
        numb_text = (TextView) findViewById(R.id.numb_text);
        msgtx = (TextView) findViewById(R.id.state_msg);
        user_pic = (ImageView) findViewById(R.id.user_pic2);
        unfollow = (TextView) findViewById(R.id.unfollow);
        viewPager = (ViewPager) findViewById(R.id.view_pager2);
        sign = (TextView)findViewById(R.id.sign);
        follow_tx = (TextView)findViewById(R.id.follow_tx);
        idtx = (TextView)findViewById(R.id.id);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        like_cnt.setTypeface(typefaceExtraBold);
        idtx.setTypeface(typefaceExtraBold);
        msgtx.setTypeface(typefaceBold);
        call_btn.setTypeface(typefaceExtraBold);
        chat_btn.setTypeface(typefaceExtraBold);
        follow_cnt.setTypeface(typefaceExtraBold);
        follow_tx.setTypeface(typefaceBold);
        sign.setTypeface(typefaceExtraBold);
        total_text.setTypeface(typefaceBold);
        unfollow.setTypeface(typefaceExtraBold);


//        user_pic.setBackground(new ShapeDrawable(new OvalShape()));
//        user_pic.setClipToOutline(true);
        idtx = (TextView) findViewById(R.id.id);

//        viewPager.setClipToPadding(false);
//        viewPager.setHorizontalFadingEdgeEnabled(true);
//        adapter = new MySlidingImage_Adapter(this, imagesArray);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                numb_text.setText(String.valueOf(position + 1));
//                // currentpostion = position;
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
//        viewPager.setAdapter(adapter);

        passwordEn = new PasswordEn();

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        id = setting.getString("user_id", "");

        Intent intent = getIntent();
        user_id = intent.getExtras().getString("id");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

//        ArrayList<String> phoneLists = phoneBook();
//        phoneArr = new String[phoneLists.size()];
//        phoneArr = phoneLists.toArray(phoneArr);

        button();
        getUser(user_id);//유저 정보



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
                    String replephone = null;//me
                    if (hasPhone > 0) {
                        Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        if (cp != null && cp.moveToFirst()) {
                            replephone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phone = replephone.replace("-", "");
                            cp.close();
                            Log.d("전화번호부","phone :"+phone);
                            phoneLists.add(phone);

                        }
                    }

                } while (cursor.moveToNext());
                // clean up cursor
                cursor.close();
                Log.d("전화번호부","size :"+phoneLists.size());
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
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                AlertDialog.Builder alert = new AlertDialog.Builder(FriendProfile.this, R.style.BuzzerAlertStyle);
                alert.setMessage(nick + "님의\n팔로우를 취소 하시겠습니까?")
                        //.setCancelable(true)
                        .setPositiveButton("팔로우 취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Delete(user_id);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alert.show();
*/
                if("언팔로우하기".equals(unfollow.getText().toString().trim())) {
                    unfollowDialog();
                    /*
                    new android.support.v7.app.AlertDialog.Builder(FriendProfile.this, R.style.BuzzerAlertStyle)
                            .setMessage(nick + "님의\n팔로우를 취소 하시겠습니까?")
                            .setPositiveButton("팔로우취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Delete(user_id);
                                }

                            })
                            .setNegativeButton("취소", null)
                            .show();  */
//                followingstate();//팔로잉 결과 서버
                }else if("맞팔하기".equals(unfollow.getText().toString().trim())){
                    followDialog();
                    /*
                    new android.support.v7.app.AlertDialog.Builder(FriendProfile.this, R.style.BuzzerAlertStyle)
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
                /*
                new android.support.v7.app.AlertDialog.Builder(FriendProfile.this, R.style.BuzzerAlertStyle)
                        .setMessage(nick + "님의\n팔로우를 취소 하시겠습니까?")
                        .setPositiveButton("팔로우취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Delete(user_id);
                            }

                        })
                        .setNegativeButton("취소", null)
                        .show();
                */
            }
        });

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //해당 유저와 채널을 만듬
                //복수의 사용자와 그룹 채널을 만들수도 있으나, 1:1 채팅이므로 선택한 사용자와의 채널만을 오픈함
                //1:1채팅에서 다른 사용자를 초대하여 복수의 그룹채팅을 만들수 있음
                List<String> userlist = new ArrayList<>();
                userlist.add(user_id);
                createGroupChannel(userlist, true);
            }
        });
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });

        showpicbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagesArray.size() > 0) {
                    Intent intset = new Intent(FriendProfile.this, Picprofile2.class);
                    intset.putExtra("user_id", user_id);
//                    intset.putExtra("no", String.valueOf(imagesArray.get(currentpostion).getNo()));
                    startActivity(intset);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
            }
        });

//        user_pic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (imagesArray.size() > 0) {
//                    Intent intset = new Intent(FriendProfile.this, Picprofile2.class);
//                    intset.putExtra("user_id", user_id);
//                    startActivity(intset);
//                }
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
                        onBackPressed();
                    }
                }
                return true;
            }
        });
    }


    private void followstate() {
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시

                        //Toast.makeText(getApplicationContext(), "프렌즈가 되었습니다", Toast.LENGTH_LONG).show();
//                        following_btn.getBackground().setColorFilter(getResources().getColor(R.color.cancle_color), PorterDuff.Mode.SRC_ATOP);
                        unfollow.setText("언팔로우하기");
                        unfollow.setTextColor(getApplication().getResources().getColor(R.color.grey04));
                        sign.setVisibility(View.VISIBLE);

                        ((FriendList) FriendList.mContext).getFriendList(id, 0);
                        ((FriendList) FriendList.mContext).getFollowingList(id, 1);
                        ((FriendList) FriendList.mContext).getFollowList(id, 2);
                        ((FriendList) FriendList.mContext).loadingMainList(id, 2);

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
                //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                Log.e(TAG,"add_user_request_follow_each_other Error");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_54_follow_follow_each_other", new String[]{id, user_id}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void getUser(String query) {
        final String fid = query;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "프렌즈프로필 response : "+response);

                        String pic_src = null, nickname = null, condition_msg = null, buzzer_open_profile_cnt = null, follow_num = "0", fr_class = "0", totalNum = null,gender;
                        ArrayList<UserPic> list2 = new ArrayList<>();
                        String no, user_pic_src, date;
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
                                totalNum = data.getJSONObject(0).has("totalNum") ? data.getJSONObject(0).getString("totalNum") : "";
                                pic_src = data.getJSONObject(0).has("pic_src") ? data.getJSONObject(0).getString("pic_src") : "";
                                nickname = data.getJSONObject(0).has("nickname") ? data.getJSONObject(0).getString("nickname") : "";
                                condition_msg = data.getJSONObject(0).has("condition_msg") ? data.getJSONObject(0).getString("condition_msg") : "";
                                buzzer_open_profile_cnt = data.getJSONObject(0).has("buzzer_open_profile_cnt") ? data.getJSONObject(0).getString("buzzer_open_profile_cnt") : "";
                                follow_num = data.getJSONObject(0).has("followNum") ? data.getJSONObject(0).getString("followNum") : "0";
                                fr_class = data.getJSONObject(0).has("fr_class") ? data.getJSONObject(0).getString("fr_class") : "0";
                                tel = data.getJSONObject(0).has("phone") ? data.getJSONObject(0).getString("phone") : "";
                                gender = data.getJSONObject(0).has("gender") ? data.getJSONObject(0).getString("gender") : "";

                                if (pic_src.isEmpty()) {
                                    user_pic.setImageResource(R.drawable.nopic__s_m);
                                } else {
                                    Glide.with(getApplicationContext()).load(pic_src).into(user_pic);
                                    Glide.with(getApplicationContext()).load(pic_src).into(profilegallery);
                                }
                                msgtx.setText(condition_msg);
                                idtx.setText(nickname);
                                nick = nickname;
                                like_cnt.setText(buzzer_open_profile_cnt);
                                follow_cnt.setText(String.valueOf(follow_num));

                                if(gender.equals("w")){
                                    idtx.setTextColor(getApplicationContext().getResources().getColor(R.color.colorMain));
                                }

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
                            } else {
                                //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                return;
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
                        //Toast.makeText(getApplicationContext(), " getinfo Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG," user_profile Error");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_51_loading_user_profile", new String[]{id, fid}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void Delete(String fr_id) {
        final String fr_id1 = fr_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시

                                //Toast.makeText(getApplicationContext(), "언팔로우 되었습니다.", Toast.LENGTH_SHORT).show();
                                //TODO 뷰에 맞게 수정
//                                unfollow.getBackground().setColorFilter(getResources().getColor(R.color.cancle_color), PorterDuff.Mode.SRC_ATOP);
                                unfollow.setText("맞팔하기");
                                unfollow.setTextColor(getApplication().getResources().getColor(R.color.buzzerfollowbtn));
                                sign.setVisibility(View.GONE);

                                ((FriendList) FriendList.mContext).getFollowingList(id, 1);
                                ((FriendList) FriendList.mContext).getFollowList(id, 2);
                                ((FriendList) FriendList.mContext).loadingMainList(id, 0);
                                ((FriendList) FriendList.mContext).getFriendList(id, 0);

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
//                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG," unfollow_friend delete Error");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_52_unfollow_friend_user", new String[]{id, user_id}));//서버 서비코드
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
        dialog = new BuzzerDialog(FriendProfile.this,
                nick + " 님과\n프렌즈 하시겠습니까?", "프렌즈 하기", "취소", followOKListener, cancelListener);

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
        dialog = new BuzzerDialog(FriendProfile.this,
                nick + " 님의\n팔로우를 취소하시겠습니까?", "팔로우 취소", "취소", unfollowOKListener, cancelListener);

        //요청 이 다이어로그를 종료할 수 있게 지정함
        dialog.setCancelable(true);
        dialog.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener unfollowOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            Delete(user_id);
            dialog.dismiss();
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

}
