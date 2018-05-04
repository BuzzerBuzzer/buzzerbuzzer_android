package com.movements.and.buzzerbuzzer;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.movements.and.buzzerbuzzer.Adapter.SpinnerAdapter;
import com.movements.and.buzzerbuzzer.Adapter.TabPagerAdapter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.Friend;
import com.movements.and.buzzerbuzzer.Service.GPSTracker;
import com.movements.and.buzzerbuzzer.Service.LongTimeNoSee;
import com.movements.and.buzzerbuzzer.Service.LongtimeAlarm;
import com.movements.and.buzzerbuzzer.Service.TimeReceiver;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.BuzzerDialog;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.Utill.PrefUtils;
import com.movements.and.buzzerbuzzer.Utill.TutoDialog;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;
import com.squareup.picasso.Picasso;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.movements.and.buzzerbuzzer.FriendFragment.check_real;
import static com.movements.and.buzzerbuzzer.OpeningActivity.nowtab;

/**
 * Created by samkim on 2017. 7. 24..
 */
//프렌드 팔로잉 팔로우 프레그먼트의 메인엑티비티
public class FriendList extends BaseActivity {

    private static final String[] TAB_NAMES = new String[]{"프렌즈", "팔로잉", "팔로워"};

    private String followNum, gps_address, friendNum, nickname, pic_src, followingNum, gps_type;
    private int badgecount1;
    private RelativeLayout allLayout_loca;
    private LinearLayout allLayout;
    private ImageView radiusProgress, radius;
    private LayoutParams layoutParams;

    GroupChannel filteredQuery;
    List<String> userIds;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabPagerAdapter pagerAdapter;
    private ImageView frAdd_btn, frAdd_btn2, frList_btn, jido, frList_btn_1;
    private ImageView buzzer, btn_noti, btn_noti2;

    private int[] tabCnts;
    private ArrayList<String> friendsList, ServiceCenterList;
    private ArrayList<String> followList;
    private ArrayList<String> followingList;

    private int times = 1000 * 60 * 30;//30M
    private int times2 = 1000 * 60 * 15;//30M

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;

    private PasswordEn passwordEn;

    public static String id, mPassword, password;;

    private JsonConverter jc;

    private String realtimefixedlocation;

    public static int RENEW_GPS = 1;
    public static int SEND_PRINT = 2;
    public static int SEND_GPS = 3;
    //public static int SEND_DIS = 5;
    public static int SEND_ADDR = 4;
    public static int SEND_REAL = 6;
    public static int SEND_UPDATE = 7;
    public static int FIX_END = 8;
    private GPSTracker gps = null;
    private Handler mHandler;
    public static Context mContext;
    private String firebase_token, fixaddress, basic_location;
    static String basic_set="";
    //private static final String INTENT_ACTION = "buzer.notimenosee.alarmmanager";
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backpressedTime = 0;

    private LongtimeAlarm lt;
    private static final String TAG = "FriendList";

    private TextView id_tx, address_tx, state_mymsg, tx, tv, tv2, smalltv, t, textView3;
    private Spinner spinner;
    private int spinnerValue;
    private ImageView user_pic, user_pic3, firstuser_pic;
    private RelativeLayout buzzerswipe;
    private LinearLayout linearLayout;
    private boolean basic_select = false;
    private boolean checkposition = true;
    private boolean fix_end = false;

    Typeface typefaceBold, typefaceExtraBold;

    private static final int MAX_TIME = 43200;      //12시간 43200
    private CountDownTimer mCountDown = null;
    private PrefUtils prefUtils;
    private int timeToStart;
    private TimerState timerState;

    private Handler handler = null;

    private boolean whoLayout;       //true -> 위치설정레이아웃, false -> 친구리스트 레이아웃
    static boolean upCheck;

    static int current_tab = 0; //탭 어디 찍었는지 검사

    private int downX, downY, X, Y;

    private Window wm;
    private Display display;

    private int windowHeight;
    private int userPicX;
    private int userPicY;
    private int userPicHeight;
    private int userPicWidth;
    private String phoneArr[];

    BackgroundThread backgroundThread;
    private final MyHandler mHandler2 = new MyHandler(this);
    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_LIST";
    private int y_count=0;
    private BuzzerDialog dialog3;
    private TutoDialog dialog_tuto;
    private ConfirmDialog dialog;

    private LinearLayout buzzer_service_center;
    private ImageView buzzer_service_center_pic;
    private TextView buzzer_service_center_msg, buzzer_service_center_id;
    private ColorfulRingProgressView crpv2;



    //TODO spinner 설정해주기 까먹지말기
    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);
        ServiceCenterList = new ArrayList<String>();
        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        radiusProgress = (ImageView)findViewById(R.id.radiusProgress);//친구리스트의 썸네일 파장
        radius = (ImageView)findViewById(R.id.radius); //위치설정의 썸네일 파장

        passwordEn = new PasswordEn();

        jc = new JsonConverter();
        lt = new LongtimeAlarm();

        mContext = this;

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == RENEW_GPS) {
                    makeNewGpsService();//gpg 비활성
                }
                if (msg.what == SEND_ADDR) {
                    realtimefixedlocation = (String) msg.obj;//주소
                }
                if (msg.what == SEND_REAL) {
                    select_real_gps("id", GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);//실시간 gps업뎃
                    mHandler.sendEmptyMessageDelayed(SEND_REAL, times);
                }

                if (msg.what == SEND_UPDATE) {
                    update_gps_cycle("id", GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);//15분 gps업뎃
                    mHandler.sendEmptyMessageDelayed(SEND_UPDATE, times2);
                }

                if (msg.what == FIX_END) {
                    update_end_fixed_gps(id, GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);
                    base_gps("id", GPSTracker.GPSstate);
//                    mHandler.sendEmptyMessageDelayed(SEND_UPDATE, times2);
                }

            }
        };

        if (gps == null) {
            gps = new GPSTracker(FriendList.this, mHandler);
        } else {
            gps.Update();//GPS
        }

        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            gps.showSettingsAlert();
        }

        /*
        if (gps.canGetLocation()) {

        } else {
            gps.showSettingsAlert();//gps off시 팝업창
        }
        */

        buzzer_service_center = (LinearLayout) findViewById(R.id.buzzer_service_center);
        buzzer_service_center_id = (TextView) findViewById(R.id.buzzer_service_center_id);
        buzzer_service_center_msg = (TextView) findViewById(R.id.buzzer_service_center_msg);
        buzzer_service_center_pic = (ImageView) findViewById(R.id.buzzer_service_center_pic);

        buzzer_service_center_pic.setBackground(new ShapeDrawable(new OvalShape()));
        buzzer_service_center_pic.setClipToOutline(true);
        crpv2 = (ColorfulRingProgressView) findViewById(R.id.crpv2);
        crpv2.setStartAngle(0);
        crpv2.setStrokeWidthDp(3);
        crpv2.setPercent(0);
        crpv2.setFgColorEnd(Color.parseColor("#7d51fc"));
        crpv2.setFgColorStart(Color.parseColor("#7d51fc"));

        buzzer = (ImageView) findViewById(R.id.btn_buzzer);
        btn_noti = (ImageView) findViewById(R.id.btn_noti);
        btn_noti2 = (ImageView) findViewById(R.id.btn_noti2);
        frAdd_btn = (ImageView) findViewById(R.id.frAdd_btn);
        frAdd_btn2 = (ImageView) findViewById(R.id.frAdd_btn2);
        frList_btn = (ImageView) findViewById(R.id.frList_btn);
        frList_btn_1 = (ImageView)findViewById(R.id.frList_btn_1);
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        tx = (TextView)findViewById(R.id.tx);
        spinner = (Spinner) findViewById(R.id.spinner);
        user_pic = (ImageView)findViewById(R.id.user_pic2);
        firstuser_pic = (ImageView)findViewById(R.id.user_pic3);
        buzzerswipe = (RelativeLayout)findViewById(R.id.buzzer_swipe);
        user_pic3 = (ImageView)findViewById(R.id.user_pic3);
        smalltv = (TextView)findViewById(R.id.smalltv);
        address_tx = (TextView)findViewById(R.id.address_tx);
        tv = (TextView)findViewById(R.id.tv);
        tv2 = (TextView)findViewById(R.id.tv2);
        t = (TextView)findViewById(R.id.t);
        textView3 = (TextView)findViewById(R.id.textView3);

        jido = (ImageView)findViewById(R.id.jido); // 이 버튼은 메인화면 보이기 전 위치 설정하는 레이아웃으로 넘어가기 위한 버튼

        buzzer_service_center_id.setTypeface(typefaceBold);
        buzzer_service_center_msg.setTypeface(typefaceBold);

        tx.setTypeface(typefaceExtraBold);
        address_tx.setTypeface(typefaceBold);
        smalltv.setTypeface(typefaceBold);
        tv.setTypeface(typefaceExtraBold);
        tv2.setTypeface(typefaceExtraBold);
        textView3.setTypeface(typefaceBold);

        wm = getWindow();
        display = FriendList.this.getWindowManager().getDefaultDisplay();

        windowHeight = display.getHeight();

        //id_tx = (TextView) findViewById(R.id.id_tx);
        //state_mymsg = (TextView) findViewById(R.id.state_mymsg);
        //linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        editor = setting.edit();
        prefUtils = new PrefUtils(getApplicationContext());//위치고정시간 SharedPreferences
        realtimefixedlocation = GPSTracker.realtimefixedlocation;//주소


        //메인에서
        user_pic.setBackground(new ShapeDrawable(new OvalShape()));
        user_pic.setClipToOutline(true);
//        user_pic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {//내 프로파일
//                Intent profile = new Intent(getApplicationContext(), Myprofile.class);
//                startActivity(profile);
//            }
//        });

        //위치 셋팅 레이아웃에서
        firstuser_pic.setBackground(new ShapeDrawable(new OvalShape()));
        firstuser_pic.setClipToOutline(true);
        firstuser_pic.setAdjustViewBounds(true);
//        firstuser_pic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {//내 프로파일
//                Intent profile = new Intent(getApplicationContext(), Myprofile.class);
//                startActivity(profile);
//            }
//        });



        //한 액티비티 안에 두 개의 작은 레이아웃 중 하나가 전체 화면을 차지하도록 구현하기 위해
        allLayout = (LinearLayout)findViewById(R.id.allLayout);
        allLayout_loca = (RelativeLayout)findViewById(R.id.allLayout_loca);

        String[] plants = {"실시간", "기본위치", "현재위치고정"};

        SpinnerAdapter adapter = new SpinnerAdapter(getApplicationContext(), R.layout.textview_with_font_change,  plants);

        // Create an ArrayAdapter using the string array and a default spinner layout
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.planets_array, R.layout.textview_with_font_change);
        //ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getApplicationContext(), R.array.planets_array, R.layout.textview_with_font_change){

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // spinner.setEnabled(false);
        spinner.setSelection(0, false);
        spinnerValue = setting.getInt("userChoiceSpinner", -1);

        if (spinnerValue != -1) {//저장된 스피너 위치 세팅
            spinner.setSelection(spinnerValue, false);
            // Log.e(TAG + " TESTTESTTEST", "1111"+spinnerValue);
        }
        if (spinnerValue <= 0) {
            if (check_real) {
                mHandler.sendEmptyMessage(SEND_REAL);
                // Log.e(TAG + " TESTTESTTEST", "2222");
            }
            // Log.e(TAG + " TESTTESTTEST", "3333");
        }
        switch (spinnerValue){
            case 0:tx.setText("실시간");break;
            case 2:tx.setText("현재위치고정");break;
            case 1:tx.setText("기본위치");break;
        }



        ((TextView)spinner.getChildAt(0)).setTypeface(typefaceExtraBold);
        ((TextView)spinner.getChildAt(0)).setTextColor(getApplication().getResources().getColor(R.color.colorMain));



        FirebaseMessaging.getInstance().subscribeToTopic("news");//파이어베이스 토큰
        firebase_token = FirebaseInstanceId.getInstance().getToken();
        String token = setting.getString("token", "MAINSWITCHCHECk");

        Log.e(TAG+" firebase_token", firebase_token);
        pushtoken(firebase_token);//파이어베이스토큰 등록
       /*
        Log.e(TAG+" token", token);
        if (!token.equals(firebase_token)) {
            pushtoken(firebase_token);//파이어베이스토큰 등록
            Log.e(TAG+" 321", "여기?");
        }
        if (token != null) {
            Log.e(TAG+" 324", "여기?");
            editor.putString("token", token);
            editor.commit();
            Log.e(TAG+" 327", setting.getString("token","토큰다름"));

        }*/
        //TODO:앱 시작시 실시간으로 셋팅 부분.
        if(setting.getString("spinner_position_memory", "0").equals("0")){
            StartRealGPS();//update_gps_cycle
            address_tx.setText(realtimefixedlocation);
        }

        if (spinnerValue == 2) {
            fixaddress = setting.getString("fixedAddress", " ");
            address_tx.setText(fixaddress);
        }
        if (basic_select) {
            //address_tx.setText(basic_location);
        }

//        getData(id, 0);//전제리스트 불러오기()
        spinnerclick();//실시간 위치고정 기본위치 스피너

        //basic_location = setting.getString("basic_location", "");
        basic_select = setting.getBoolean("basic_select", false);
        //Log.e("베이직 셋팅 : ", basic_set);
        //Log.e("온크리에잇 베이직 로케이션 : ", basic_location);
        //Log.e("로딩메인 이후 베이직 셋팅", "왜안나와");

        // 고객센터 프로필 불러오기
        getServiceCenter();

        button();

        if(upCheck == true){

            transAnimationUpQuick();
        }else{
            whoLayout = true;
        }
        thumnail();

        if(badgecount1 > 0 && setting.getBoolean("news_push", false)){
            btn_noti.setImageResource(R.drawable.tab_message_news_noti_dim);
        }else if(badgecount1 > 0){
            btn_noti.setImageResource(R.drawable.tab_message_noti_dim);
        }else if(setting.getBoolean("news_push", false)){
            btn_noti.setImageResource(R.drawable.tab_message_news_dim);
        }else
            btn_noti.setImageResource(R.drawable.tab_message_dim);

        selectTab();
        getmsg(id);
        new_check();
        loadingMainList(id, nowtab);

    }

    public void new_check(){
        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                GroupChannel.getTotalUnreadMessageCount(new GroupChannel.GroupChannelTotalUnreadMessageCountHandler() {
                    @Override
                    public void onResult(int i, SendBirdException e) {
                        badgecount1 = i;
                    }
                });
            }
        });
    }

    public void addbtnOff(){
        frAdd_btn.setVisibility(View.GONE);
        frAdd_btn2.setVisibility(View.GONE);
    }

    public void addbtnOn(){
        frAdd_btn.setVisibility(View.VISIBLE);
        frAdd_btn2.setVisibility(View.VISIBLE);
    }

    private void pushtoken(String token1) {

        final String token = token1;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        //Toast.makeText(getApplicationContext(), "토큰 Error!", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_00_update_user_token", new String[]{id, token}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void makeNewGpsService() {
        if (gps == null) {
            gps = new GPSTracker(FriendList.this, mHandler);
        } else {
            gps.Update();
        }
    }

    private void spinnerclick() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id1) {

                ((TextView)spinner.getChildAt(0)).setTypeface(typefaceExtraBold);
                ((TextView)spinner.getChildAt(0)).setTextColor(getApplication().getResources().getColor(R.color.colorMain));

                if (position == 0 && checkposition) {
                    if (timerState == TimerState.RUNNING) {
                        timerState = TimerState.STOPPED;
                        mCountDown.cancel();
                        onTimerFinish();
                        select_real_gps(id, GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);
                        update_end_fixed_gps(id, GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);
                        StartRealGPS();
                    }
                    tx.setText("실시간");
                    address_tx.setText(realtimefixedlocation);

//                                    Toast.makeText(getApplicationContext(), "실시간위치 선택", Toast.LENGTH_LONG).show();
                    select_real_gps("id", GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);//실시간 gps업뎃
                    selected_base(false);

                    selecteditemposition(position);
                    editor.putString("spinner_position_memory", "0");
                    editor.commit();
                    StartRealGPS();

                } else if (position == 2) {
                    fixAddressDialog();

                } else if (position == 1) {
                    if(!fix_end){
                        if (timerState == TimerState.RUNNING) {
                            timerState = TimerState.STOPPED;
                            mCountDown.cancel();
                            onTimerFinish();
                            update_end_fixed_gps(id, GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);
                            //Log.d("좌표테스트활성", GPSContext.provider);

                        }
                        tx.setText("기본위치");
                        selected_base(true);
                        // Log.e("기본위치 : ", basic_location);
                        OpeningActivity.basic_location_temp = basic_location;
                        address_tx.setText(basic_location);
                        selected_basic_location(basic_location);
                        selecteditemposition(position);
                        base_gps("id", GPSTracker.GPSstate);

                        editor.putString("spinner_position_memory", "1");
                        editor.commit();

                        StopRealGPS();
                    }else{

                        fix_end = false;

                        if (timerState == TimerState.RUNNING) {
                            timerState = TimerState.STOPPED;
                            mCountDown.cancel();
                            onTimerFinish();
                            update_end_fixed_gps(id, GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);
                            //Log.d("좌표테스트활성", GPSContext.provider);

                        }
                        tx.setText("기본위치");
                        selected_base(true);
//                        Log.e("기본위치 : ", basic_location);
                        address_tx.setText(basic_location);
                        selected_basic_location(basic_location);
                        selecteditemposition(position);
                        base_gps("id", GPSTracker.GPSstate);

                        editor.putString("spinner_position_memory", "1");
                        editor.commit();

                        StopRealGPS();

                    }



                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void thumnail(){
        final RelativeLayout.LayoutParams params
                = (RelativeLayout.LayoutParams) radius.getLayoutParams();
        final RelativeLayout.LayoutParams paramsradiusProgress
                = (RelativeLayout.LayoutParams) radiusProgress.getLayoutParams();

        final float scale = getResources().getDisplayMetrics().density; //dp


        if(whoLayout==true) {
            new CountDownTimer(1000, 1) {
                @Override
                public void onTick(long m) {
                    params.height = (int) (scale * (80+(80-0.08*m)));
                    params.width = (int) (scale * (80+(80-0.08*m)));
                    radius.setAlpha((float) ( m/ 2500.0));
                    radius.setLayoutParams(params);
                }

                @Override
                public void onFinish() {
                    params.height = (int)(scale*160);
                    params.width = (int)(scale*160);
                    radius.setAlpha((float)0.0);
                    radius.setLayoutParams(params);
                }
            }.start();
        }
        else{
            new CountDownTimer(1000, 1) {


                @Override
                public void onTick(long m) {
                    paramsradiusProgress.height = (int) (scale * (40+(40-0.04*m)));
                    paramsradiusProgress.width = (int) (scale * (40+(40-0.04*m)));
                    radiusProgress.setAlpha((float) ( m/ 2500.0));
                    //paramsradiusProgress.topMargin = (int)(scale*(13-((40-0.04*m)/2-7)));
                    paramsradiusProgress.topMargin = (int)(scale*((0.04*m)/2-7));
                    radiusProgress.setLayoutParams(paramsradiusProgress);

                }

                @Override
                public void onFinish() {
                    paramsradiusProgress.height = (int)(scale*40);
                    paramsradiusProgress.width = (int)(scale*40);
                    paramsradiusProgress.topMargin = (int)(scale*13);
                    radiusProgress.setAlpha((float)0.0);
                    radiusProgress.setLayoutParams(paramsradiusProgress);
                }
            }.start();
        }
    }

    private void button() {

        buzzer_service_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentfr = new Intent(getApplicationContext(), BscProfile.class);//유저프로필
                intentfr.putExtra("id", "BuzzerServiceCenter");
                startActivity(intentfr);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });

        btn_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent noticeint = new Intent(getApplicationContext(), Notice.class);//메세지 및 뉴스
                startActivity(noticeint);
                overridePendingTransition(0, 0);
            }
        });

        btn_noti2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intb = new Intent(getApplicationContext(), Buzzer.class);//searching
                startActivity(intb);
                overridePendingTransition(0, 0);

            }
        });
        frAdd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendin = new Intent(getApplicationContext(), PopupAddBuzzerId.class);//친구찾기
                //friendin.addFlags(friendin.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(friendin);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);

            }
        });
        frList_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(FriendList.this, Allsetting.class);//상세설정
                startActivity(profile);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });

        frList_btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(FriendList.this, Allsetting.class);//상세설정
                startActivity(profile);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);

            }
        });

        //다시 위치 세팅 레이아웃으로
        jido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                allLayout.setVisibility(View.GONE);
//                allLayout_loca.setVisibility(View.VISIBLE);
                transAnimationDown();
                whoLayout = true;
                thumnail();
            }
        });


//        allLayout_loca.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                final int action = event.getAction();
//
//                if (action == MotionEvent.ACTION_MOVE){
//                    allLayout_loca.setVisibility(View.GONE);
//                    allLayout.setVisibility(View.VISIBLE);
//                    smalltv.setText(address_tx.getText().toString());
//                    whoLayout = false;
//                    thumnail();
//                }
//            return true;
//            }
//        });

        user_pic.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                userPicX = (int) event.getRawX();
                userPicY = (int) event.getRawY();
//                Log.e(" y좌표", String.valueOf(userPicY));
//                Log.e(" 그림 높이하고 폭", userPicHeight+", "+userPicWidth);

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    downY = (int) event.getRawY();
                    downX = (int) event.getRawX();
                    userPicHeight = user_pic.getHeight();
                    userPicWidth = user_pic.getWidth();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if ((25 > (userPicY - downY) && -25 < (userPicY - downY)) && (25 > (userPicX - downX) && -25 < (userPicX - downX))) {
                        transAnimationUp();
                        upCheck = true;
                        Intent profile = new Intent(getApplicationContext(), Myprofile.class);
                        startActivity(profile);
                        overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                    }
                    else if( 25 < -(downY - userPicY- 25)){
                        transAnimationDown();
                        upCheck = false;
                    }
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE) {
//                    Log.d("loca y좌표", String.valueOf(-(downY - userPicY - 25)));

                    if (25 >= (downY - Y)) {
                        allLayout_loca.setY(-windowHeight-(downY - userPicY - 25));
                    }
                }


                return true;
            }
        });

        allLayout_loca.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Y = (int) event.getRawY();
                X = (int) event.getRawX();
//                Log.e("up/down y좌표", Y+" ," + downY);

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    downY = (int) event.getRawY();
                    downX = (int) event.getRawX();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if (100 >= (downY - Y- 100)) {
                        transAnimationDown();
                    }
                    else if( 100 < (downY - Y- 100)){
                        transAnimationUp();
                        upCheck = true;
                    }
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if (100 <= (downY - Y))
                    {
                        allLayout_loca.setY(-(downY - Y- 50));
                    }
                }

                return true;
            }
        });

        firstuser_pic.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Y = (int) event.getRawY();
                X = (int) event.getRawX();
//                Log.e("up/down y좌표", Y+" ," + downY);

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    downY = (int) event.getRawY();
                    downX = (int) event.getRawX();
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if ((50 > (Y - downY) && -50 < (Y - downY)) && (50 > (X - downX) && -50 < (X - downX))) {
                        Intent profile = new Intent(getApplicationContext(), Myprofile.class);
                        startActivity(profile);
                        overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                    }
                    else if( 100 < (downY - Y- 100)){
                        transAnimationUp();
                        upCheck = true;
                    }
                }
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if (50 <= (downY - Y))
                    {
                        allLayout_loca.setY(-(downY - Y- 50));
                    }
                }

                return true;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // gps = new GPSTracker(FriendList.this, mHandler);
            Intent recall = new Intent(getApplicationContext(), FriendList.class);
            recall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            recall.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(recall);

            selectTab();

//            getData(id, 0);
            if(badgecount1 > 0 && setting.getBoolean("news_push", false)){
                btn_noti.setImageResource(R.drawable.tab_message_news_noti_dim);
            }else if(setting.getBoolean("news_push", false)){
                btn_noti.setImageResource(R.drawable.tab_message_news_dim);
            }else
                btn_noti.setImageResource(R.drawable.tab_message_dim);
        }
    }

    public void loadingMainList(final String id, final int tabId) {
        //final String followNum, gps_address, friendNum, nickname, pic_src, followingNum, gps_type;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG+" loadingMainList", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {

                                JSONArray data = json.getJSONArray("data");
                                //JSONObject row = null;

                                for(int i = 0; i<data.length(); i++){
                                    followNum = data.getJSONObject(i).has("followNum")?data.getJSONObject(i).getString("followNum"):"";
                                    gps_address = data.getJSONObject(i).has("gps_address")?data.getJSONObject(i).getString("gps_address"):"";
                                    friendNum = data.getJSONObject(i).has("friendNum")?data.getJSONObject(i).getString("friendNum"):"";
                                    nickname = data.getJSONObject(i).has("nickname")?data.getJSONObject(i).getString("nickname"):"";
                                    pic_src = data.getJSONObject(i).has("pic_src")?data.getJSONObject(i).getString("pic_src"):"";
                                    followingNum = data.getJSONObject(i).has("followingNum")?data.getJSONObject(i).getString("followingNum"):"";
                                    gps_type = data.getJSONObject(i).has("gps_type")?data.getJSONObject(i).getString("gps_type"):"";

                                }

                                int a = 0, b = 0, c = 0;
                                if (!"".equals(friendNum)){
                                    a = Integer.parseInt(friendNum);
                                }else
                                    a = 0;

                                if (!"".equals(followNum)){
                                    c = Integer.parseInt(followNum);
                                }else
                                    c = 0;

                                if (!"".equals(followingNum)){
                                    b = Integer.parseInt(followingNum);
                                }else
                                    b = 0;

                                tabCnts = new int[]{
                                        a,
                                        b,
                                        c
                                };

                                editor.putString("friend_num", String.valueOf(a));
                                editor.putString("following_num", String.valueOf(b));
                                editor.putString("follow_num", String.valueOf(c));
                                editor.commit();

                                setTabLayout(tabId);//뷰
                                if (pic_src.isEmpty()) {
                                    firstuser_pic.setImageResource(R.drawable.nopic__s_m);
                                    user_pic.setImageResource(R.mipmap.ic_launcher);
                                }else{
                                    Glide.with(getApplicationContext())
                                            .load(pic_src)
                                            .asBitmap()
                                            .centerCrop()
                                            .dontAnimate().into(user_pic);
                                    Glide.with(getApplicationContext())
                                            .load(pic_src)
                                            .asBitmap()
                                            .centerCrop()
                                            .dontAnimate().into(firstuser_pic);
                                }
                                //basic_set = gps_address;
                                address_tx.setText(gps_address);
                                basic_location = gps_address;
                                //basic_location = gps_address;
                                Log.e("지피에스 어드레스",gps_address);
                                Log.e("지피에스타입", gps_type);

                                /*
                                if(spinnerValue == 0 && gps_type.equals("base")){
                                    spinner.setSelection(1);
                                }
                                */
                                if(gps_type.equals("base")){
                                    spinner.setSelection(1);
                                }else if(gps_type.equals("fixed")){
                                    spinner.setSelection(2);
                                }else{
                                    spinner.setSelection(0);
                                }

                                if(setting.getInt("userChoiceSpinner",1) == 1){
                                    //smalltv.setText(OpeningActivity.basic_location_temp);
                                    smalltv.setText(gps_address);
                                }

//                                updateCurrentUserInfo(nickname, pic_src);

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
                        Toast.makeText(getApplicationContext(), "인터넷 연결 확인 후 다시 시도 해주세요", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "user_token Error: " + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_23_loading_main_list", new String[]{id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void getFriendList(final String id, final int tabId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG+" getFriendList", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {
                                friendsList = new ArrayList<String>();
//                                friendsList.clear();
                                JSONArray list = json.getJSONArray("data");
                                JSONObject row = null;
                                for (int i = 0; i < list.length(); i++) {
                                    row = list.getJSONObject(i);
                                    switch (row.getString("fr_class")) {
                                        case "friend":
                                            friendsList.add(row.toString());
                                            break;
                                        case "following":
                                            followingList.add(row.toString());
                                            break;
                                        case "follow":
                                            followList.add(row.toString());
                                            break;
                                    }
                                }
                                //Log.e("friendsList.size", String.valueOf(friendsList.size()));
                                setTabLayout(tabId);
                                current_tab = 0;

                            }else if(json.getString("result").equals("warn")){
                                friendsList = new ArrayList<String>();
                                friendsList.clear();
                                return;
                            }
                            else {
                                //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch(NullPointerException nullPointerException){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        Toast.makeText(getApplicationContext(), "인터넷 연결 확인 후 다시 시도 해주세요", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "user_token Error: " + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_24_get_friend_list", new String[]{id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void getServiceCenter() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String buzzer_service_center_condition_msg, buzzer_service_center_idd, buzzer_service_center_gauge_angle, buzzer_service_center_gauge_type, buzzer_service_center_nickname, buzzer_service_center_pic_src;

                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {
                                JSONArray data = json.getJSONArray("data");

                                buzzer_service_center_gauge_angle = data.getJSONObject(0).has("buzzer_service_center_gauge_angle") ? data.getJSONObject(0).getString("buzzer_service_center_gauge_angle") : "";
                                buzzer_service_center_idd = data.getJSONObject(0).has("buzzer_service_center_id") ? data.getJSONObject(0).getString("buzzer_service_center_id") : "";
                                buzzer_service_center_condition_msg = data.getJSONObject(0).has("buzzer_service_center_condition_msg") ? data.getJSONObject(0).getString("buzzer_service_center_condition_msg") : "";
                                buzzer_service_center_gauge_type = data.getJSONObject(0).has("buzzer_service_center_gauge_type") ? data.getJSONObject(0).getString("buzzer_service_center_gauge_type") : "";
                                buzzer_service_center_nickname = data.getJSONObject(0).has("buzzer_service_center_nickname") ? data.getJSONObject(0).getString("buzzer_service_center_nickname") : "";
                                buzzer_service_center_pic_src = data.getJSONObject(0).has("buzzer_service_center_pic_src") ? data.getJSONObject(0).getString("buzzer_service_center_pic_src") : "";

                                buzzer_service_center_msg.setText(buzzer_service_center_condition_msg);
                                buzzer_service_center_id.setText(buzzer_service_center_nickname);
                                Glide.with(getApplicationContext()).load(buzzer_service_center_pic_src).into(buzzer_service_center_pic);

                                crpv2.setPercent(((float) Integer.parseInt(buzzer_service_center_gauge_angle) / 360) * 100);

                                if(buzzer_service_center_gauge_type.equals("1")){

                                }else{
                                    crpv2.setFgColorEnd(Color.parseColor("#27a2ff"));
                                    crpv2.setFgColorStart(Color.parseColor("#27a2ff"));
                                }

                            }else if(json.getString("result").equals("warn")){

                                return;
                            }
                            else {
                                //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch(NullPointerException nullPointerException){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        Toast.makeText(getApplicationContext(), "인터넷 연결 확인 후 다시 시도 해주세요", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "user_token Error: " + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_68_get_service_center_element", new String[]{id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void getFollowingList(final String id, final int tabId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String fr_class, location_release_yn, gauge_type_user, nickname, condition_msg, pic_src, fr_id, fr_gauge_angle;
                        Log.e(TAG+" getFollowingList", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {
                                // Log.e("followingList", "여기됨? 1");
                                followingList = new ArrayList<String>();
//                                followingList.clear();
                                JSONArray list = json.getJSONArray("data");
                                JSONObject row = null;
                                for (int i = 0; i < list.length(); i++) {
                                    row = list.getJSONObject(i);
                                    switch (row.getString("fr_class")) {
                                        case "friend":
                                            friendsList.add(row.toString());
                                            break;
                                        case "following":
                                            followingList.add(row.toString());
                                            break;
                                        case "follow":
                                            followList.add(row.toString());
                                            break;
                                    }
                                }

                                // Log.e("followingList.size", String.valueOf(followingList.size()));
                                setTabLayout(tabId);
                                current_tab = 1;

                            }else if(json.getString("result").equals("warn")){
                                followingList = new ArrayList<String>();
                                followingList.clear();
                                return;

                            } else {
                                //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch(NullPointerException e){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        Toast.makeText(getApplicationContext(), "인터넷 연결 확인 후 다시 시도 해주세요", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "user_token Error: " + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_25_get_following_list", new String[]{id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void getFollowList(final String id, final int tabId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG+" getFollowList", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {
                                followList = new ArrayList<String>();
//                                followList.clear();
                                JSONArray list = json.getJSONArray("data");
                                JSONObject row = null;
                                for (int i = 0; i < list.length(); i++) {
                                    row = list.getJSONObject(i);
                                    switch (row.getString("fr_class")) {
                                        case "friend":
                                            friendsList.add(row.toString());
                                            break;
                                        case "following":
                                            followingList.add(row.toString());
                                            break;
                                        case "follow":
                                            followList.add(row.toString());
                                            break;
                                    }
                                }
                                // Log.e("followList.size", String.valueOf(followList.size()));
                                setTabLayout(tabId);
                                current_tab = 2;

                            }else if(json.getString("result").equals("warn")){
                                setTabLayout(tabId);
                                followList = new ArrayList<String>();
                                followList.clear();
                                return;

                            } else {
                                //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch(NullPointerException e){

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        Toast.makeText(getApplicationContext(), "인터넷 연결 확인 후 다시 시도 해주세요", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "user_token Error: " + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_26_get_follow_list", new String[]{id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void selectTab(){
        if(nowtab == 0){
            getFollowingList(id,0);
            getFollowList(id,0);
            getFriendList(id,0);
            getServiceCenter();
            loadingMainList(id, 0);
        }else if(nowtab == 1){
            getFollowingList(id,1);
            getFollowList(id,1);
            getFriendList(id,1);
            loadingMainList(id, 1);
        }else{
            getFollowingList(id,2);
            getFollowList(id,2);
            getFriendList(id,2);
            loadingMainList(id, 2);
        }
    }

    private void setTabLayout(int tabId) {

        if(tabId == 0){
            buzzer_service_center.setVisibility(View.VISIBLE);
        }else{
            buzzer_service_center.setVisibility(View.GONE);
        }

        // Initializing TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.removeAllTabs();

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int i = 0; i < TAB_NAMES.length; i++) {
            String st = TAB_NAMES[i]+" " + tabCnts[i];
            String strTabName = TAB_NAMES[i];
            String strTabCnt = "<font color=\"#7d51fc\">" + tabCnts[i] + "</font>";

            SpannableStringBuilder ssb = new SpannableStringBuilder(st);
            if(nowtab == i) {
                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#7d51fc")), 4, st.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else{
                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#DCD0FF")), 4, st.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            //tabLayout.addTab(tabLayout.newTab().setText(TAB_NAMES[i]+" " + tabCnts[i]));
            tabLayout.addTab(tabLayout.newTab().setText(Html.fromHtml(strTabName + " "+ strTabCnt)));

            ViewGroup vgTab = (ViewGroup) vg.getChildAt(i);
            int tabChildsCount = vgTab.getChildCount();
            for (int k = 0; k < tabChildsCount; k++) {
                View tabViewChild = vgTab.getChildAt(k);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(typefaceExtraBold);
                    ((TextView) tabViewChild).setText(ssb);
                }
                //TextView tv = (TextView) tabLayout.getChildAt(k).findViewById(R.id.title)
            }
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Initializing ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);


        // Creating TabPagerAdapter adapter
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), friendsList, followList, followingList);//friendsList, followList, followingList추가함
        viewPager.setAdapter(pagerAdapter);//TabPagerAdapter 프래그먼트그려짐
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Set TabSelectedListener 탭클릭시 이동
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getPosition() == 0){
                    buzzer_service_center.setVisibility(View.VISIBLE);
                }else{
                    buzzer_service_center.setVisibility(View.GONE);
                }

                viewPager.setCurrentItem(tab.getPosition());
                Config.tabposition = tab.getPosition();//현재 뷰페이저 위치
                editor.putString("friend_List_Tab_Position", String.valueOf(tab.getPosition()));
                nowtab = tab.getPosition();

                ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
                int tabsCount = vg.getChildCount();

                String st = tab.getText().toString().trim();

                SpannableStringBuilder ssb = new SpannableStringBuilder(st);
                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#7d51fc")), 4, st.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                ViewGroup vgTab = (ViewGroup) vg.getChildAt(nowtab);
                int tabChildsCount = vgTab.getChildCount();
                for (int k = 0; k < tabChildsCount; k++) {
                    View tabViewChild = vgTab.getChildAt(k);
                    if (tabViewChild instanceof TextView) {
                        ((TextView) tabViewChild).setTypeface(typefaceExtraBold);
                        ((TextView) tabViewChild).setText(ssb);
                    }
                }
                addbtnOn();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
                int tabsCount = vg.getChildCount();

                String st = tab.getText().toString().trim();

                SpannableStringBuilder ssb = new SpannableStringBuilder(st);
                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#DCD0FF")), 4, st.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                ViewGroup vgTab = (ViewGroup) vg.getChildAt(nowtab);
                int tabChildsCount = vgTab.getChildCount();
                for (int k = 0; k < tabChildsCount; k++) {
                    View tabViewChild = vgTab.getChildAt(k);
                    if (tabViewChild instanceof TextView) {
                        ((TextView) tabViewChild).setTypeface(typefaceExtraBold);
                        ((TextView) tabViewChild).setText(ssb);
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Log.d("탭리슨너", "onTabReselected");
            }
        });
        tabLayout.getTabAt(tabId).select();//다른곳에서 리플레쉬 tabLayout포지션이 0이기때문에 이걸 해줘야 현재위치화면으로 유지
    }

    private void select_real_gps(String id1, double latitude1, double longitude1, String gpsaddress1, String normal1) {
        final String real_gps_x = String.valueOf(latitude1);
        final String real_gps_y = String.valueOf(longitude1);
        final String real_gps_address = gpsaddress1;
        final String received_gps_state = normal1;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG+" select_real_gps", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                // getData(id, 1);
                                check_real = false;
                                selectTab();
                                //Call(id, Config.tabposition);//화면 리플레쉬
                                // Log.e(TAG +" 810 : ", id);
                                editor.putString("realgpscheck","real");
                                editor.commit();

                            } else {//json.getString("result").equals("fail")
                                Toast.makeText(getApplicationContext(), "GPS 확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                /*
                                dialog = new ConfirmDialog(FriendList.this,
                                        "실시간 위치를 설정할 수 없습니다.\nGPS 상태를 확인 후 다시 시도해주세요.", "확인");
                                dialog.setCancelable(true);
                                dialog.show();
                                */
                                spinner.setSelection(1);
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
                        //Toast.makeText(getApplicationContext(), " select_real_gps Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "select_real_gps Error: " + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_27_select_real_gps", new String[]{id, real_gps_x, real_gps_y, real_gps_address, received_gps_state}));
                return params;
            }
        };

        RequestQueue requestQueu = Volley.newRequestQueue(getApplicationContext());
        requestQueu.add(stringRequest);

    }

    private void update_gps_cycle(String id1, double latitude1, double longitude1, String realtimefixedlocation1, String normal1) {

        final String latitude = String.valueOf(latitude1);
        final String longitude = String.valueOf(longitude1);
        final String realtimefixedlocation = realtimefixedlocation1;
        final String normal = normal1;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG+" update_gps_cycle", String.valueOf(response));
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {
                            } else {
//                                Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(getApplicationContext(), " update_gps_cycle Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "update_gps_cycle Error: " + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_00_update_real_gps", new String[]{id, latitude, longitude, realtimefixedlocation, normal, ""}));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void update_end_fixed_gps(String id1, double latitude1, double longitude1, String realtimefixedlocation, String normal1) {
        final String real_gps_x = String.valueOf(latitude1);
        final String real_gps_y = String.valueOf(longitude1);
        final String real_gps_address = realtimefixedlocation;
        final String received_gps_state = normal1;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        Log.e("update_end_fixed_gps", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                //Toast.makeText(getActivity(), " update_end_fixed_gps 실행", Toast.LENGTH_SHORT).show();
                            } else {//json.getString("result").equals("fail")시
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
                        //Toast.makeText(getActivity(), " update_end_fixed_gps Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, " update_end_fixed_gps Error :" + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_30_end_fixed_gps", new String[]{id, real_gps_x, real_gps_y, real_gps_address, received_gps_state}));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void base_gps(String id1, String normal) {
        final String in_id = id;
        final String in_received_gps_state = normal;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        Log.e(TAG +" base_gps", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {
                                //((FriendList) FriendList.mContext).getData(id, TAB_ID);
                                //getData(id, Config.tabposition);
                                //Call(id, Config.tabposition);//화면 리플레쉬
                                selectTab();

                                editor.putString("realgpscheck","notreal");
                                editor.commit();
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
                        //Toast.makeText(getActivity(), "base Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, " select_base_gps Error" + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_29_select_base_gps", new String[]{in_id, in_received_gps_state}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void select_fixed_gps(String idtx, double latitude, double longitude, String gpsaddress, String normal) {
        final String in_id = id;
        final String new_fixed_gps_x = String.valueOf(latitude);
        final String new_fixed_gps_y = String.valueOf(longitude);
        final String new_fixed_gps_address = gpsaddress;
        final String in_received_gps_state = normal;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG +" select_fixed_gps", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                //((FriendList) FriendList.mContext).getData(id, TAB_ID);
//                                getData(id, Config.tabposition);
                                //Call(id, Config.tabposition);
                                selectTab();

                                editor.putString("realgpscheck","notreal");
                                editor.commit();

                                //   Call(id, Config.tabposition);//화면 리플레쉬
                                selectTab();
                            } else {//json.getString("result").equals("fail")시
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
                        //Toast.makeText(getActivity(), " getMyinfo Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, " select_fixed_gps Error :" + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_28_select_fixed_gps", new String[]{in_id, new_fixed_gps_x, new_fixed_gps_y, new_fixed_gps_address, in_received_gps_state}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void fixaddress(String realtimefixedlocation) {
        editor.putString("fixedAddress", realtimefixedlocation);
        //Log.d("위치고정", realtimefixedlocation);
        editor.commit();
    }

    private void selecteditemposition(int position) {
        editor.putInt("userChoiceSpinner", position);
        editor.commit();
    }

    private void selected_basic_location(String basic_location) {
        editor.putString("basic_location", basic_location);
        //Log.d("기본주소쉐어", basic_location);
        editor.commit();
    }

    private void selected_base(boolean check) {
        editor.putBoolean("basic_select", check);
        basic_select = check;
        editor.commit();
    }

    private long getNow() {
        Calendar rightNow = Calendar.getInstance();
        return rightNow.getTimeInMillis() / 1000;
    }

    private void initTimer() {
        long startTime = prefUtils.getStartedTime();
        //Log.d("생명", "initTimer  :" + spinnerValue);
        if (startTime > 0) {
            timeToStart = (int) (MAX_TIME - (getNow() - startTime));
            if (timeToStart <= 0) {
                // TIMER EXPIRED
                timeToStart = MAX_TIME;
                timerState = TimerState.STOPPED;
                update_end_fixed_gps(id, GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);
                onTimerFinish();
                checkposition = false;

                //원래 소스코드(실시간으로 변경)
//                selecteditemposition(0);
//                spinner.setSelection(0);
//                StartRealGPS();

                // 끝난후 기본위치설정으로 변경 StopRealGPS까지...
                fix_end = true;
                selecteditemposition(1);
                spinner.setSelection(1);
                FixEndBaseStart();

                if (spinnerValue == 1) {
                    update_end_fixed_gps(id, GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);
                }
            } else {
                startTimer();
                timerState = TimerState.RUNNING;
                //Log.d("체킹3", String.valueOf(timerState) + timeToStart);
            }
        } else {
            timeToStart = MAX_TIME;
            timerState = TimerState.STOPPED;
        }
    }


    private void onTimerFinish() {
        //Toast.makeText(getApplicationContext(), "위치고정 종료", Toast.LENGTH_SHORT).show();
        prefUtils.setStartedTime(0);
        timeToStart = MAX_TIME;

    }

    private void startTimer() {
        mCountDown = new CountDownTimer(timeToStart * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeToStart -= 1;

                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                String.valueOf(String.format("%d:%d:%d", hours, minutes, seconds));
                // Log.d("Countdown2", String.valueOf(String.format("%d:%d:%d", hours, minutes, seconds)));
            }

            @Override
            public void onFinish() {
                timerState = TimerState.STOPPED;
                update_end_fixed_gps(id, GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);
                onTimerFinish();
                removeAlarmManager();
                checkposition = false;

                //원래 소스코드(실시간으로 변경)
//                selecteditemposition(0);
//                spinner.setSelection(0);
//                StartRealGPS();

                // 끝난후 기본위치설정으로 변경 StopRealGPS까지...
                fix_end = true;
                selecteditemposition(1);
                spinner.setSelection(1);
                FixEndBaseStart();
            }
        }.start();
    }


    public void setAlarmManager() {
        int wakeUpTime = (prefUtils.getStartedTime() + MAX_TIME) * 1000;
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), TimeReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(wakeUpTime, sender), sender);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, wakeUpTime, sender);
        }
    }

    public void removeAlarmManager() {
        Intent intent = new Intent(getApplicationContext(), TimeReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }

    private enum TimerState {
        STOPPED,
        RUNNING
    }

    public void StartRealGPS() {
        mHandler.sendEmptyMessage(SEND_REAL);
    }

    public void StopRealGPS() {
        mHandler.removeMessages(SEND_REAL);
    }

    public void FixEndBaseStart() {
        mHandler.removeMessages(FIX_END);
    }


    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backpressedTime;
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            setRealtimeOn();//종료 후 30분 주기 실행
            super.onBackPressed();
        } else {
            backpressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 뒤로가기를 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setRealtimeOn() {

        Intent intent = new Intent(
                getApplicationContext(),//현재제어권자
                LongTimeNoSee.class); // 이동할 컴포넌트
        startService(intent); // 서비스 시작

        lt.setRealGpsAlarm(FriendList.this, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mHandler.removeMessages(SEND_REAL);
        mHandler.removeMessages(SEND_UPDATE);
        check_real = true;
        try {
            mCountDown.cancel();
        } catch (Exception e) {
        }
        mCountDown = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Boolean getBoolean = getIntent().getBooleanExtra("event_noti_click", false);
        if(OpeningActivity.eventCheck == true && getBoolean){
            bzcoinEvent();
        }

        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            gps.showSettingsAlert();
        }

        loadingMainList(id, nowtab);
        getServiceCenter();

        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        getmsg(id);

        initTimer();
        selectTab();
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


    @Override
    protected void onStart() {
        super.onStart();

        backgroundThread = new BackgroundThread();
        backgroundThread.setRunning(true);
        backgroundThread.start();

        GroupChannel.getTotalUnreadMessageCount(new GroupChannel.GroupChannelTotalUnreadMessageCountHandler() {
            @Override
            public void onResult(int i, SendBirdException e) {

                badgecount1 = i;
                // Log.e("그룹채널 토탈 언리드 메세지 카운트", String.valueOf(badgecount1));
                if(badgecount1 > 0 && setting.getInt("read_noti_count",0) > 0){
                    btn_noti.setImageResource(R.drawable.tab_message_news_noti_dim);
                }else if(badgecount1 > 0){
                    btn_noti.setImageResource(R.drawable.tab_message_noti_dim);
                }else if(setting.getInt("read_noti_count",0) > 0){
                    btn_noti.setImageResource(R.drawable.tab_message_news_dim);
                }else
                    btn_noti.setImageResource(R.drawable.tab_message_dim);
            }
        });
    }

    @Override
    protected void onPause() {
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
        super.onPause();
        if (timerState == TimerState.RUNNING) {
            mCountDown.cancel();
            setAlarmManager();
        }
    }

    private void transAnimationUp(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            allLayout_loca.animate().translationY(-2700).withLayer();
            allLayout_loca.animate().setDuration(500);
        }

        /*
        if(allLayout_loca.getY() == -windowHeight) {
            Log.e("y좌표 비교1", allLayout_loca.getY() + ", " + windowHeight);
        }else
            Log.e("y좌표 비교2", allLayout_loca.getY() +", "+windowHeight);
        */

        smalltv.setText(address_tx.getText().toString());
        whoLayout = false;

        if(OpeningActivity.isTuto == true){ //튜토 작업완료되면 false로 바꾸기
            OpeningActivity.isTuto = false;
            dialog_tuto = new TutoDialog(FriendList.this);
            dialog_tuto.setCancelable(true);
            dialog_tuto.show();
        }
    }

    private void transAnimationDown(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            allLayout_loca.animate().translationY(0).withLayer();
            allLayout_loca.animate().setDuration(500);
        }
        whoLayout = true;

    }

    private void transAnimationUpQuick(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            allLayout_loca.animate().translationY(-2700).withLayer();
            allLayout_loca.animate().setDuration(0);
        }
        /*
        if(allLayout_loca.getY() == -windowHeight) {
            Log.e("y좌표 비교1", allLayout_loca.getY() + ", " + windowHeight);
        }else
            Log.e("y좌표 비교2", allLayout_loca.getY() +", "+windowHeight);
        */
        smalltv.setText(address_tx.getText().toString());
        whoLayout = false;
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

                        //Log.e("전체 노티", String.valueOf(noti_count));
                        //Log.e("읽은 뉴스 노티", String.valueOf(y_count));
                        //Log.e("읽지 않은 뉴스 노티", String.valueOf(n_count));

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

    public void fixAddressDialog(){
        dialog3 = new BuzzerDialog(FriendList.this,
                "현재위치고정은 12시간 동안 유지 후 기본위치로 변경됩니다.\n변경하시겠습니까?", "변경하기", "취소", fixAddressOKListener, cancelListener);
        dialog3.setCancelable(true);
        dialog3.show();
    }

    //다이얼로그 클릭이벤트
    private View.OnClickListener fixAddressOKListener = new View.OnClickListener() {
        public void onClick(View v) {

            select_fixed_gps("id", GPSTracker.latitude, GPSTracker.longitude, GPSTracker.realtimefixedlocation, GPSTracker.GPSstate);

            address_tx.setText(realtimefixedlocation);
            if (timerState == TimerState.STOPPED) {
                prefUtils.setStartedTime((int) getNow());
                startTimer();
                timerState = TimerState.RUNNING;
            }
            tx.setText("현재위치고정");
            selected_base(false);
            selecteditemposition(2);
            address_tx.setText(GPSTracker.realtimefixedlocation);
            fixaddress(GPSTracker.realtimefixedlocation);//위치고정 주소 저장

            editor.putString("spinner_position_memory", "2");
            editor.commit();

            StopRealGPS();

            dialog3.dismiss();
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View v) {
            if(setting.getString("spinner_position_memory", "0").equals("0")) {
                spinner.setSelection(0);
            }else if(setting.getString("spinner_position_memory", "1").equals("1")) {
                spinner.setSelection(1);
            }
            dialog3.dismiss();
        }
    };

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

    private void handleMessage(Message msg) {
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
        private final WeakReference<FriendList> mActivity;
        public MyHandler(FriendList activity) {
            mActivity = new WeakReference<FriendList>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FriendList activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    private void bzcoinEventfore() {
        if (OpeningActivity.eventCheckFore == true) {
            OpeningActivity.eventCheckFore = false;
            dialog = new ConfirmDialog(FriendList.this,
                    OpeningActivity.EventMsgFore, "확인");
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    private void bzcoinEvent() {

        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String EventMsg;
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        Log.e("부저코인 이벤트 ","성공");
                        /*
                        JSONArray data = json.getJSONArray("data");
                        EventMsg = data.getJSONObject(0).has("msg") ? data.getJSONObject(0).getString("msg") : "";
                        Log.e("이벤트메세지",EventMsg);
                        */

                        EventMsg = json.getString("msg");
                        Log.e("이벤트메세지",EventMsg);

                        dialog = new ConfirmDialog(FriendList.this,
                                EventMsg, "확인");
                        dialog.setCancelable(true);
                        dialog.show();
                    } else {
                        Log.e("부저코인 이벤트 ","타임아웃");
                        /*
                        JSONArray data = json.getJSONArray("data");
                        EventMsg = data.getJSONObject(0).has("msg") ? data.getJSONObject(0).getString("msg") : "";
                        */
                        //Toast.makeText(getApplicationContext(), "다시시도해주세요", Toast.LENGTH_LONG).show();

                        EventMsg = json.getString("msg");
                        Log.e("이벤트메세지",EventMsg);

                        dialog = new ConfirmDialog(FriendList.this,
                                EventMsg, "확인");
                        dialog.setCancelable(true);
                        dialog.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("부저코인 이벤트 ","에러");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_9999_event_confirm", new String[]{id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
        OpeningActivity.eventCheck = false;
    }
}