package com.movements.and.buzzerbuzzer;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by samkim on 2017. 7. 28..
 */
//하단 버튼 맨 왼쪽 클릭 후 화면//메세지 뉴스
public class Notice extends TabActivity {
    private TabHost tabHost;
    static TabWidget tabs;
    private Intent intent, intent2;
    private Typeface typefaceBold, typefaceExtraBold;
    static RelativeLayout buttonlayout;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    public static int badgecount_notice;
    TextView tv, tv2;
    private ImageView buzzer, btn_noti, btn_noti2, tab_new_chat, tab_new_news;
    private Handler mHandler;
    private JsonConverter jc;
    private String fr_class;
    public static Context mContext;
    private int yn_count=0;
    private String id, userid, buzzer_item_cnt, mPassword, password;
    private PasswordEn passwordEn;
    private ConfirmDialog dialog;

    BackgroundThread backgroundThread;
    private final MyHandler mHandler2 = new MyHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noti_list);

        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때
            getWindow().setStatusBarColor(Color.BLACK);
        }
        mContext = this;
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        id = setting.getString("user_id", "");
        passwordEn = new PasswordEn();
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);
        editor.putString("notice_tabid","message");
        editor.putBoolean("sendbird",false);
        editor.commit();
        jc = new JsonConverter();
        tabs = (TabWidget)findViewById(android.R.id.tabs);
        buzzer = (ImageView) findViewById(R.id.buzzer);
        buzzer.setImageResource(R.drawable.tab_buzzer_dim);
        btn_noti = (ImageView) findViewById(R.id.btn_noti);
        btn_noti2 = (ImageView) findViewById(R.id.btn_noti2);
        buttonlayout = (RelativeLayout) findViewById(R.id.buttonlayout);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        Resources res = getResources();//리소스 받기(res에서 사진가져 올려고)
        tabHost = getTabHost();//탭을 붙이기 위한 인스턴스 생성.

        TabHost.TabSpec spec1, spec2;//버튼 객체 /tabHost=getTabHost();먼저 선언해야함
        intent = new Intent().setClass(getApplicationContext(), Message.class);
        spec1 = tabHost.newTabSpec("Message").setIndicator("Message").setContent(intent);
        tabHost.addTab(spec1);

        intent = new Intent().setClass(getApplicationContext(), News.class);
        spec2 = tabHost.newTabSpec("news").setIndicator("news").setContent(intent);
        //newTabSpec("artist")는 생성할 객체에 대한 태그이름(아뒤라고 생각) //뒤에는 사진추가 setIndicator("Artist는 실제로 보이는것
        tabHost.addTab(spec2);

        tv = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title); //Unselected Tabs
        tv.setTextColor(getApplication().getResources().getColor(R.color.colorMain));
        tv2 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title); //Unselected Tabs
        tv2.setTextColor(getApplication().getResources().getColor(R.color.grey02));

        tv.setTypeface(typefaceExtraBold);
        tv2.setTypeface(typefaceExtraBold);

        button();
        getmsgg(id);
        Log.e("노우티스 와이카운트", String.valueOf(yn_count));
        setMenuVisible();
    }

    public void new_check_notice(){
        getmsgg(id);
        GroupChannel.getTotalUnreadMessageCount(new GroupChannel.GroupChannelTotalUnreadMessageCountHandler() {
            @Override
            public void onResult(int i, SendBirdException e) {
                //SystemClock.sleep(2000);
                badgecount_notice = i;
                //Log.e("그룹채널 토탈 언리드 메세지 카운트", String.valueOf(badgecount1));

                if(badgecount_notice > 0 && yn_count > 0){
                    btn_noti.setImageResource(R.drawable.tab_message_news_noti_focus);
                }else if(badgecount_notice > 0){
                    btn_noti.setImageResource(R.drawable.tab_message_noti_focus);
                }else if(yn_count > 0){
                    btn_noti.setImageResource(R.drawable.tab_message_news_focus);
                }else
                    btn_noti.setImageResource(R.drawable.tab_message_focus);
            }
        });

        if(setting.getString("notice_tabid","message").equals("message")){
            tv.setTextColor(getApplication().getResources().getColor(R.color.colorMain));
            tv2.setTextColor(getApplication().getResources().getColor(R.color.grey02));

            if(badgecount_notice > 0){
                tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background_new_msg));
            }else
                tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background));

            if(yn_count > 0){
                tabHost.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.custom_tab_background_new_sky2));
            }else
                tabHost.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.custom_tab_background_dim));
            editor.putString("notice_tabid","message");
            editor.commit();
        }
        else{
            tv.setTextColor(getApplication().getResources().getColor(R.color.grey02));
            tv2.setTextColor(getApplication().getResources().getColor(R.color.sky));
            //tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background_dim));

            if(badgecount_notice > 0){
                tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background_new_msg2));
            }else
                tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background_dim));

            if(yn_count > 0){
                tabHost.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.custom_tab_background_new_sky));
            }else
                tabHost.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.custom_tab_background_sky));

            editor.putString("notice_tabid","news");
            editor.commit();
        }
    }

    public static void setMenuGone(){
        tabs.setVisibility(View.GONE);
        buttonlayout.setVisibility(View.GONE);
    }

    public static void setMenuVisible(){
        tabs.setVisibility(View.VISIBLE);
        buttonlayout.setVisibility(View.VISIBLE);
    }


    private void button() {


        buzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Toast.makeText(FriendList.this, "buzzer", Toast.LENGTH_SHORT).show();
                editor.putString("notice_tabid","message");
                editor.commit();
                Intent noticeint = new Intent(getApplicationContext(), FriendList.class);
                noticeint.addFlags(noticeint.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(noticeint);
                finish();
                overridePendingTransition(0,R.anim.bottom_exit);

            }
        });

        //현재 페이지
        btn_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //주위유저 사진보는 페이지로
        btn_noti2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("notice_tabid","message");
                editor.commit();
                Intent intb = new Intent(getApplicationContext(), Buzzer.class);
                startActivity(intb);
                finish();
                overridePendingTransition(0,R.anim.bottom_exit);
            }
        });

        //TODO: 수정중이었음. 어떤걸? 탭 이미지를.
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("Message")){
                    tv.setTextColor(getApplication().getResources().getColor(R.color.colorMain));
                    tv2.setTextColor(getApplication().getResources().getColor(R.color.grey02));

                    if(badgecount_notice > 0){
                        tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background_new_msg));
                    }else
                        tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background));

                    if(yn_count > 0){
                        tabHost.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.custom_tab_background_new_sky2));
                    }else
                        tabHost.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.custom_tab_background_dim));
                    editor.putString("notice_tabid","message");
                    editor.commit();
                }
                else{
                    tv.setTextColor(getApplication().getResources().getColor(R.color.grey02));
                    tv2.setTextColor(getApplication().getResources().getColor(R.color.sky));
                    //tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background_dim));

                    if(badgecount_notice > 0){
                        tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background_new_msg2));
                    }else
                        tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background_dim));

                    if(yn_count > 0){
                        tabHost.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.custom_tab_background_new_sky));
                    }else
                        tabHost.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.custom_tab_background_sky));

                    editor.putString("notice_tabid","news");
                    editor.commit();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.bottom_exit);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
       // Log.d("테스트", String.valueOf(event));
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            //Log.d("test", String.valueOf(v.callOnClick())+"   "+ MotionEvent.ACTION_DOWN+"  "+v.isClickable() );
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Notice.class onResume","동작???????");
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                new_check_notice();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("액티비티 온스타트","진입");

        backgroundThread = new BackgroundThread();
        backgroundThread.setRunning(true);
        backgroundThread.start();

        new_check_notice();
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

    private void getmsgg(String id) {
        final String rid = id;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String confirm_ynn;
                        int length, no;
                        yn_count = 0;
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

                                confirm_ynn = data.getJSONObject(i).has("confirm_yn") ? data.getJSONObject(i).getString("confirm_yn") : "";

                                if(confirm_ynn.equals("0")){
                                    yn_count += 1;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        if(badgecount_notice > 0 && yn_count > 0){
            btn_noti.setImageResource(R.drawable.tab_message_news_noti_focus);
        }else if(badgecount_notice > 0){
            btn_noti.setImageResource(R.drawable.tab_message_noti_focus);
        }else if(yn_count > 0){
            btn_noti.setImageResource(R.drawable.tab_message_news_focus);
        }else
            btn_noti.setImageResource(R.drawable.tab_message_focus);


        if(setting.getString("notice_tabid","message").equals("message")){
            tv.setTextColor(getApplication().getResources().getColor(R.color.colorMain));
            tv2.setTextColor(getApplication().getResources().getColor(R.color.grey02));

            if(badgecount_notice > 0){
                tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background_new_msg));
            }else
                tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background));

            if(yn_count > 0){
                tabHost.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.custom_tab_background_new_sky2));
            }else
                tabHost.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.custom_tab_background_dim));
            editor.putString("notice_tabid","message");
            editor.commit();
        }
        else{
            tv.setTextColor(getApplication().getResources().getColor(R.color.grey02));
            tv2.setTextColor(getApplication().getResources().getColor(R.color.sky));
            //tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background_dim));

            if(badgecount_notice > 0){
                tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background_new_msg2));
            }else
                tabHost.getTabWidget().getChildAt(0).setBackground(getDrawable(R.drawable.custom_tab_background_dim));

            if(yn_count > 0){
                tabHost.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.custom_tab_background_new_sky));
            }else
                tabHost.getTabWidget().getChildAt(1).setBackground(getDrawable(R.drawable.custom_tab_background_sky));

            editor.putString("notice_tabid","news");
            editor.commit();
        }

        bzcoinEventfore();
    }

    // 핸들러 객체 만들기
    private static class MyHandler extends Handler {
        private final WeakReference<Notice> mActivity;
        public MyHandler(Notice activity) {
            mActivity = new WeakReference<Notice>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            Notice activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    private void bzcoinEventfore() {
        if (OpeningActivity.eventCheckFore == true) {
            OpeningActivity.eventCheckFore = false;
            dialog = new ConfirmDialog(Notice.this,
                    OpeningActivity.EventMsgFore, "확인");
            dialog.setCancelable(true);
            dialog.show();
        }
    }
}
