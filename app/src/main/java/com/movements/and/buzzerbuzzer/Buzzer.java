package com.movements.and.buzzerbuzzer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.os.*;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Adapter.BuzzerAdapter;
import com.movements.and.buzzerbuzzer.Adapter.SpinnerAdapter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.Friend;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samkim on 2017. 5. 26..
 */

//서칭화면
public class Buzzer extends BaseActivity {

    //private ImageButton backbtn;
    private ImageView buzzer, btn_noti;
    //private Button showprofile_btn;
    private ImageView user_pic;
    private int y_count=0;
    private TextView tv, textView4;
    private Spinner spinner;
    private int spinnerValue;

    private Typeface typefaceBold, typefaceExtraBold;

    private RecyclerView recycler;
    private ArrayList<Friend> mUserList;
    private ProgressBar mProgressbar;
    private String fr_class;
    private BuzzerAdapter mAdapter;

    private JsonConverter jc;
    private SharedPreferences setting;
    private PasswordEn passwordEn;

    private String id, userid, buzzer_item_cnt, mPassword, password;
    //private ArrayList<String> imagesArray;

    private CoordinatorLayout coordinator;
    private AppBarLayout appbar;
    private GridLayoutManager mGridLayoutManager;
    private SwipeRefreshLayout swipeContainer;
    private static final String TAG = "Buzzer";
    private Handler mHandler;
    private boolean check_real;
    private SharedPreferences.Editor editor;
    private int badgecount1;
    private ConfirmDialog dialog;

    BackgroundThread backgroundThread;
    private final MyHandler mHandler2 = new MyHandler(this);
    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buzzer_friend);

        recycler = (RecyclerView) findViewById(R.id.recyclerview_bz);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLo);
// Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUser(id);
                swipeContainer.setRefreshing(false);
            }
        });

        spinner = (Spinner) findViewById(R.id.genderSpinner);

        //showprofile_btn = (Button) findViewById(R.id.showprofile_btn);
        buzzer = (ImageView) findViewById(R.id.btn_buzzer);
        buzzer.setImageResource(R.drawable.tab_buzzer_dim);
        btn_noti = (ImageView) findViewById(R.id.btn_noti);

        tv = (TextView)findViewById(R.id.tv);
        textView4 = (TextView)findViewById(R.id.textView4);

        //user_pic = (ImageView) findViewById(R.id.user_pic2);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBarbuzzer);
        //backbtn = (ImageButton) findViewById(R.id.backbtn);

        coordinator = (CoordinatorLayout) findViewById(R.id.mCoordinator);
        appbar = (AppBarLayout) findViewById(R.id.appbar);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");
        tv.setTypeface(typefaceBold);
        textView4.setTypeface(typefaceExtraBold);

        jc = new JsonConverter();

        mUserList = new ArrayList<>();
        mGridLayoutManager = new GridLayoutManager(this, 3);
        recycler.setLayoutManager(mGridLayoutManager);
        mAdapter = new BuzzerAdapter(getApplication(), mUserList, new BuzzerAdapter.RecyclerItemClickListener() {
            @Override
            public void onClickListener(Friend user, int position) {
//                showprofile_btn.setVisibility(View.VISIBLE);//프로필 보기버튼
//                if (user.getPic_src().isEmpty()) {
//                    user_pic.setImageResource(R.drawable.user_prof_bg);
//                    user_pic.setScaleType(ImageView.ScaleType.FIT_XY);
//                } else {
//                    user_pic.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                    Picasso.with(getApplicationContext()).load(user.getPic_src()).into(user_pic);//상단사진
//                }
                userid = user.getFr_id();
                appbar.setExpanded(true);
                //((LinearLayoutManager) recycler.getLayoutManager()).scrollToPositionWithOffset(position, 0);

                if(fr_class.equals("unknown")){
                    Intent intentfr = new Intent(getApplication(), Buzzerprofile.class);//유저프로필
                    intentfr.putExtra("id", userid);
                    intentfr.putExtra("buzzer_item_cnt", buzzer_item_cnt);
                    startActivity(intentfr);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
                if(fr_class.equals("following")){
                    Intent intentfr = new Intent(getApplication(), Followingprofile.class);//유저프로필
                    intentfr.putExtra("id", userid);
                    startActivity(intentfr);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
                /*
                Intent intentfr = new Intent(getApplication(), Buzzerprofile.class);
                intentfr.putExtra("id", userid);
                intentfr.putExtra("buzzer_item_cnt", buzzer_item_cnt);
                startActivity(intentfr);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                */
                getmsg(id);
            }
        });

        recycler.setAdapter(mAdapter);

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        String[] plants = {"전체유저", "남자만", "여자만"};

        SpinnerAdapter adapter = new SpinnerAdapter(getApplicationContext(), R.layout.textview_with_font_change_buzzerbuzzer,  plants);

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // spinner.setEnabled(false);
        spinner.setSelection(0, false);


        ((TextView)spinner.getChildAt(0)).setTypeface(typefaceExtraBold);
        ((TextView)spinner.getChildAt(0)).setTextColor(getApplication().getResources().getColor(R.color.colorMain));



        button();
        getmsg(id);
        GroupChannel.getTotalUnreadMessageCount(new GroupChannel.GroupChannelTotalUnreadMessageCountHandler() {
            @Override
            public void onResult(int i, SendBirdException e) {
                //SystemClock.sleep(2000);
                badgecount1 = i;
                Log.e("그룹채널 토탈 언리드 메세지 카운트", String.valueOf(badgecount1));
                if(badgecount1 > 0 && y_count > 0){
                    btn_noti.setImageResource(R.drawable.tab_message_news_noti_dim);
                }else if(badgecount1 > 0){
                    btn_noti.setImageResource(R.drawable.tab_message_noti_dim);
                }else if(y_count > 0){
                    btn_noti.setImageResource(R.drawable.tab_message_news_dim);
                }else
                    btn_noti.setImageResource(R.drawable.tab_message_dim);
            }
        });

//        if(setting.getBoolean("sendbird_push", false) &&setting.getBoolean("news_push", false)){
//            btn_noti.setImageResource(R.drawable.tab_message_news_noti_dim);
//        }else if(setting.getBoolean("sendbird_push", false) &&!setting.getBoolean("news_push", false)){
//            btn_noti.setImageResource(R.drawable.tab_message_noti_dim);
//        }else if(!setting.getBoolean("sendbird_push", false) &&setting.getBoolean("news_push", false)){
//            btn_noti.setImageResource(R.drawable.tab_message_news_dim);
//        }else
//            btn_noti.setImageResource(R.drawable.tab_message_dim);

        spinnerclick();

        editor.putString("gender2", "a");
        editor.commit();
        ((TextView)spinner.getChildAt(0)).setText("전체 유저");
        getUser(id);//서칭리스트 불러오기
        new_check();
    }

    public void new_check(){
        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                GroupChannel.getTotalUnreadMessageCount(new GroupChannel.GroupChannelTotalUnreadMessageCountHandler() {
                    @Override
                    public void onResult(int i, SendBirdException e) {
                        //SystemClock.sleep(2000);
                        badgecount1 = i;
                    }
                });
            }
        });
    }

    private void spinnerclick() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id1) {

                ((TextView)spinner.getChildAt(0)).setTypeface(typefaceExtraBold);
                ((TextView)spinner.getChildAt(0)).setTextColor(getApplication().getResources().getColor(R.color.colorMain));

                if (position == 0) {

                    editor.putString("gender2", "a");
                    editor.commit();
                    ((TextView)spinner.getChildAt(0)).setText("전체 유저");
                    getUser(id);

                } else if (position == 1) {
                    editor.putString("gender2", "m");
                    editor.commit();
                    ((TextView)spinner.getChildAt(0)).setText("남성 유저만");
                    getUser(id);

                } else if (position == 2) {
                    editor.putString("gender2", "w");
                    editor.commit();
                    ((TextView)spinner.getChildAt(0)).setText("여성 유저만");
                    getUser(id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    private void button() {
//        //backbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        buzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//하단버튼 프렌즈리스트
                Intent noticeint = new Intent(getApplicationContext(), FriendList.class);
                noticeint.addFlags(noticeint.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(noticeint);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        btn_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//하단버튼 메세지 뉴스
                Intent noticeint = new Intent(getApplicationContext(), Notice.class);
                startActivity(noticeint);
                overridePendingTransition(0, 0);
                finish();

            }
        });

//        showprofile_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {//프로필보기 버튼
//                Intent intentfr = new Intent(getApplication(), Buzzerprofile.class);
//                intentfr.putExtra("id", userid);
//                intentfr.putExtra("buzzer_item_cnt", buzzer_item_cnt);
//                startActivity(intentfr);
//            }
//        });
    }


    public void getUser(String id2) {
        final String sex = setting.getString("gender2", "a");

        final String id = id2;
        mProgressbar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String mUser_pic, id, distance, fr_id, my_coin_cnt,gender;
                        ArrayList<Friend> list = new ArrayList<>();

                        Log.d(TAG + " 193 :", response);
                        try {
                            JSONObject resJson = new JSONObject(response);

                            if (resJson.getString("result").equals("success")) {//성공시
                                JSONArray data = resJson.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    distance = data.getJSONObject(i).has("distance") ? data.getJSONObject(i).getString("distance") : "";
                                    id = data.getJSONObject(i).has("my_id") ? data.getJSONObject(i).getString("my_id") : "";
                                    mUser_pic = data.getJSONObject(i).has("fr_pic") ? data.getJSONObject(i).getString("fr_pic") : "";
                                    fr_id = data.getJSONObject(i).has("fr_id") ? data.getJSONObject(i).getString("fr_id") : "";
                                    fr_class = data.getJSONObject(i).has("fr_class") ? data.getJSONObject(i).getString("fr_class") : "";
                                    buzzer_item_cnt = data.getJSONObject(i).has("my_coin_cnt") ? data.getJSONObject(i).getString("my_coin_cnt") : "";
                                    gender = data.getJSONObject(i).has("fr_gender") ? data.getJSONObject(i).getString("fr_gender") : "";
                                    Friend user = new Friend(id, mUser_pic, distance, fr_id);
                                    list.add(user);
                                    Log.e(TAG,gender + mUser_pic);
                                }
                                //buzzer_item_cnt = data.getJSONObject(0).has("buzzer_item_cnt") ? data.getJSONObject(0).getString("buzzer_item_cnt") : "0";

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mProgressbar.setVisibility(View.GONE);
                        mUserList.clear();
                        mUserList.addAll(list);
                        mAdapter.notifyDataSetChanged();
                        mAdapter.setSelectedPosition(0);
                        //  Picasso.with(getApplicationContext()).load(list.get(0).getPic_src()).into(user_pic2);

//                        if (list.get(0).getPic_src().isEmpty()) {
//                            user_pic.setImageResource(R.drawable.user_prof_bg);
//                            user_pic.setScaleType(ImageView.ScaleType.FIT_XY);
//                        } else {
//                            Picasso.with(getApplicationContext()).load(list.get(0).getPic_src()).into(user_pic);
//                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressbar.setVisibility(View.GONE);
                        //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "searching Error :" + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_62_loading_around_me", new String[]{id, sex}));//서버 서비코드
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onPause() {
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
        super.onPause();
    }

    public void getUser2(String id2) {
        final String sex = setting.getString("gender2", "a");

        final String id = id2;
        mProgressbar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String mUser_pic, id, distance, fr_id, my_coin_cnt,gender;
                        ArrayList<Friend> list = new ArrayList<>();

                        Log.d(TAG + " 193 :", response);
                        try {
                            JSONObject resJson = new JSONObject(response);

                            if (resJson.getString("result").equals("success")) {//성공시
                                JSONArray data = resJson.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    distance = data.getJSONObject(i).has("distance") ? data.getJSONObject(i).getString("distance") : "";
                                    id = data.getJSONObject(i).has("my_id") ? data.getJSONObject(i).getString("my_id") : "";
                                    mUser_pic = data.getJSONObject(i).has("fr_pic") ? data.getJSONObject(i).getString("fr_pic") : "";
                                    fr_id = data.getJSONObject(i).has("fr_id") ? data.getJSONObject(i).getString("fr_id") : "";
                                    fr_class = data.getJSONObject(i).has("fr_class") ? data.getJSONObject(i).getString("fr_class") : "";
                                    buzzer_item_cnt = data.getJSONObject(i).has("my_coin_cnt") ? data.getJSONObject(i).getString("my_coin_cnt") : "";
                                    gender = data.getJSONObject(i).has("fr_gender") ? data.getJSONObject(i).getString("fr_gender") : "";
                                    Friend user = new Friend(id, mUser_pic, distance, fr_id);
                                    list.add(user);
                                    Log.e(TAG,gender + mUser_pic);
                                }
                                //buzzer_item_cnt = data.getJSONObject(0).has("buzzer_item_cnt") ? data.getJSONObject(0).getString("buzzer_item_cnt") : "0";

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mProgressbar.setVisibility(View.GONE);
                        mUserList.clear();
                        mUserList.addAll(list);
                        mAdapter.notifyDataSetChanged();
                        mAdapter.setSelectedPosition(0);
                        //  Picasso.with(getApplicationContext()).load(list.get(0).getPic_src()).into(user_pic2);

//                        if (list.get(0).getPic_src().isEmpty()) {
//                            user_pic.setImageResource(R.drawable.user_prof_bg);
//                            user_pic.setScaleType(ImageView.ScaleType.FIT_XY);
//                        } else {
//                            Picasso.with(getApplicationContext()).load(list.get(0).getPic_src()).into(user_pic);
//                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressbar.setVisibility(View.GONE);
                        //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "searching Error :" + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_62_loading_around_me", new String[]{id, sex}));//서버 서비코드
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                GroupChannel.getTotalUnreadMessageCount(new GroupChannel.GroupChannelTotalUnreadMessageCountHandler() {
                    @Override
                    public void onResult(int i, SendBirdException e) {
                        //SystemClock.sleep(2000);
                        badgecount1 = i;
                        Log.e("그룹채널 토탈 언리드 메세지 카운트", String.valueOf(badgecount1));

                        if(badgecount1 > 0 && y_count > 0){
                            btn_noti.setImageResource(R.drawable.tab_message_news_noti_dim);
                        }else if(badgecount1 > 0){
                            btn_noti.setImageResource(R.drawable.tab_message_noti_dim);
                        }else if(y_count > 0){
                            btn_noti.setImageResource(R.drawable.tab_message_news_dim);
                        }else
                            btn_noti.setImageResource(R.drawable.tab_message_dim);
                    }
                });
            }
        });

        getUser(id);
        getmsg(id);
        unReadCount();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("액티비티 온스타트","진입");

        backgroundThread = new BackgroundThread();
        backgroundThread.setRunning(true);
        backgroundThread.start();


        GroupChannel.getTotalUnreadMessageCount(new GroupChannel.GroupChannelTotalUnreadMessageCountHandler() {
            @Override
            public void onResult(int i, SendBirdException e) {
                //SystemClock.sleep(2000);
                badgecount1 = i;
                Log.e("그룹채널 토탈 언리드 메세지 카운트", String.valueOf(badgecount1));
                if(badgecount1 > 0 && y_count > 0){
                    btn_noti.setImageResource(R.drawable.tab_message_news_noti_dim);
                }else if(badgecount1 > 0){
                    btn_noti.setImageResource(R.drawable.tab_message_noti_dim);
                }else if(y_count > 0){
                    btn_noti.setImageResource(R.drawable.tab_message_news_dim);
                }else
                    btn_noti.setImageResource(R.drawable.tab_message_dim);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        boolean retry = true;
        backgroundThread.setRunning(false);

        while (retry) {
            try {
                backgroundThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void getmsg(String id) {
        final String rid = id;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String user_pic, nickname, id, fr_id, type, message, confirm, noti_time, confirm_yn, fr_class;
                        int no, length, n_count = 0, noti_count = 0;
                        y_count = 0;
                        //     Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject resJson = new JSONObject(response);
                            Log.e(TAG +" getmsg",response);
                            JSONArray data = resJson.getJSONArray("data");
                            if(data.length() > 99){
                                length = 99;
                            }else
                                length = data.length();
                            for (int i = 0; i < length; i++) {
                                fr_class = data.getJSONObject(i).has("fr_class") ? data.getJSONObject(i).getString("fr_class") : "";
                                user_pic = data.getJSONObject(i).has("pic_src") ? data.getJSONObject(i).getString("pic_src") : "";
                                nickname = data.getJSONObject(i).has("nickname") ? data.getJSONObject(i).getString("nickname") : "";
                                no = data.getJSONObject(i).has("no") ? data.getJSONObject(i).getInt("no") : -1;
                                id = data.getJSONObject(i).has("id") ? data.getJSONObject(i).getString("id") : "";
                                fr_id = data.getJSONObject(i).has("fr_id") ? data.getJSONObject(i).getString("fr_id") : "";
                                type = data.getJSONObject(i).has("type") ? data.getJSONObject(i).getString("type") : "";
                                message = data.getJSONObject(i).has("message") ? data.getJSONObject(i).getString("message") : "";
                                noti_time = data.getJSONObject(i).has("noti_time") ? data.getJSONObject(i).getString("noti_time") : "";
                                confirm_yn = data.getJSONObject(i).has("confirm_yn") ? data.getJSONObject(i).getString("confirm_yn") : "";

                                if(confirm_yn.equals("0")){
                                    y_count += 1;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.e("전체 노티", String.valueOf(noti_count));
                        Log.e("읽은 뉴스 노티", String.valueOf(y_count));
                        Log.e("읽지 않은 뉴스 노티", String.valueOf(n_count));

                        editor.putInt("news_noti_size", noti_count);
                        editor.putInt("read_noti_count", y_count);
                        editor.putInt("unread_noti_count", n_count);


                        if(y_count == noti_count){
                            editor.putBoolean("news_noti_allread", true);
                            editor.putBoolean("news_push", false);
                        }else
                            editor.putBoolean("news_noti_allread", false);

                        editor.commit();


                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG," notifi_news Error"+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //  Log.d("파라", id);
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(rid, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_60_get_news_list_data", new String[]{rid}));//서버 서비코드
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void setBadge(final Context context, final int count) {
        final String launcherClassName = getLauncherClassName(context);

        if (launcherClassName == null) {
            return;
        }

        final Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");

        Log.e("인텐트보낼때 배지카운트 : ", String.valueOf(count));
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        intent.putExtra("badge_count", count > 0? count : null);

        sendBroadcast(intent);
    }

    private static String getLauncherClassName(Context context) {
        final PackageManager pm = context.getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        for (final ResolveInfo resolveInfo : resolveInfos) {
            final String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }

        return null;
    }


    private void unReadCount(){
        GroupChannel.getTotalUnreadMessageCount(new GroupChannel.GroupChannelTotalUnreadMessageCountHandler() {
            @Override
            public void onResult(int i, SendBirdException e) {
                int unReadCount = i;
                //Log.e(TAG, "언리드카운트 " + unReadCount + i);
                //setBadge(getApplicationContext(), unReadCount);
            }
        });

    }


    public class BackgroundThread extends Thread {

        boolean running = false;

        void setRunning(boolean b) {
            running = b;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler2.sendMessage(mHandler2.obtainMessage());
            }
        }
    }


    private void handleMessage(android.os.Message msg) {
        //Log.e("핸들러에서 1초마다","돌고있음");
        if(badgecount1 > 0 && setting.getInt("read_noti_count",0) > 0){
            btn_noti.setImageResource(R.drawable.tab_message_news_noti_dim);
        }else if(badgecount1 > 0){
            btn_noti.setImageResource(R.drawable.tab_message_noti_dim);
        }else if(setting.getInt("read_noti_count",0) > 0){
            btn_noti.setImageResource(R.drawable.tab_message_news_dim);
        }else
            btn_noti.setImageResource(R.drawable.tab_message_dim);

        bzcoinEventfore();
    }

    // 핸들러 객체 만들기
    private static class MyHandler extends Handler {
        private final WeakReference<Buzzer> mActivity;
        public MyHandler(Buzzer activity) {
            mActivity = new WeakReference<Buzzer>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            Buzzer activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    private void bzcoinEventfore() {
        if (OpeningActivity.eventCheckFore == true) {
            OpeningActivity.eventCheckFore = false;
            dialog = new ConfirmDialog(Buzzer.this,
                    OpeningActivity.EventMsgFore, "확인");
            dialog.setCancelable(true);
            dialog.show();
        }
    }
}
