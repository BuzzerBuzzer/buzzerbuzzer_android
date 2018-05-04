package com.movements.and.buzzerbuzzer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Adapter.FriendBlockAdapter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.Friend;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 4. 20..
 */
//내위치 차단친구 관리
public class MygpsAble extends BaseActivity {

    public static Context mContext;

    private RecyclerView recyclerView;
    private ArrayList<Friend> mfriendList;
    private ProgressBar mProgressbar;
    private int currentIndex;
    private RelativeLayout relativeLayout4;
    private FriendBlockAdapter mAdapter;
    private Switch mSwitch;
    private ImageView backbtn;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password;
    private Switch aSwitch;

    private TextView tv, textView2;
    private Typeface typefaceBold, typefaceExtraBold;
    private int i = 0;
    private JsonConverter jc;
    private Activity mActivity;
    private boolean switchcheck = false;
    private final String TAG = "MygpsAble";
    private View BlockAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationblock);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_lb);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar1);
        backbtn = (ImageView) findViewById(R.id.backbtn);
        aSwitch = (Switch) findViewById(R.id.switch8);
        tv = (TextView)findViewById(R.id.tv);
        textView2 = (TextView)findViewById(R.id.textView2);
        relativeLayout4 = (RelativeLayout)findViewById(R.id.relativeLayout4);
        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv.setTypeface(typefaceExtraBold);
        textView2.setTypeface(typefaceBold);


        BlockAdapter = getLayoutInflater().inflate(R.layout.friendblock_row, null, false);
        mSwitch = (Switch) BlockAdapter.findViewById(R.id.switch1);


        button();

        jc = new JsonConverter();
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);
        mfriendList = new ArrayList<>();
        mActivity = this;
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new FriendBlockAdapter(getApplication(), mActivity, mfriendList, new FriendBlockAdapter.RecyclerItemClickListener() {
            @Override
            public void onClickListener(Friend friends, int position) {
                //changeSelected(position);
            }
        });
        mContext = this;
        recyclerView.setAdapter(mAdapter);

        getFriendsList(id);//리스트호출
    }

    private void getFriendsList(String id) {
        final String myid = id;
        mProgressbar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "로딩부분 - "+response);
                        //     Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        String pic_src, nickname, fr_id;
                        int location_release_yn = 1;
                        ArrayList<Friend> friends = new ArrayList<>();//9
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                Log.e("JSON 겟스트링 : ","성공");
                                JSONArray data = json.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    nickname = data.getJSONObject(i).has("nickname") ? data.getJSONObject(i).getString("nickname") : "";
                                    pic_src = data.getJSONObject(i).has("pic_src") ? data.getJSONObject(i).getString("pic_src") : "";
                                    fr_id = data.getJSONObject(i).has("fr_id") ? data.getJSONObject(i).getString("fr_id") : "";
                                    location_release_yn = data.getJSONObject(i).has("location_release_yn") ? data.getJSONObject(i).getInt("location_release_yn") : 3;
                                    Friend friend = new Friend(nickname, pic_src, fr_id, location_release_yn);
                                    friends.add(friend);
                                }
                                Log.e("friend : ", String.valueOf(friends));
                                /*
                                if(i == 0) {
                                    boolean allChecked = true;
                                    for (Friend friend : friends) {
                                        if (friend.getLocation_release_yn() == 1) {
                                            allChecked = false;
                                            break;
                                        }
                                    }
                                    i++;
                                    aSwitch.setChecked(allChecked);
                                }*/
                                //Log.i("초기화 아이값", String.valueOf(i));
                                mProgressbar.setVisibility(View.GONE);
                                mfriendList.clear();//add+queryd
                                mfriendList.addAll(friends);
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setSelectedPosition(0);//setSelectedPosition 4


                            } else {
                                mProgressbar.setVisibility(View.GONE);
                                Log.e("JSON 겟스트링 : ","실패");
                                //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FriendBlockAdapter.MyViewHolder.mainSwitchcheck = true;
                        switchcheck = true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        Log.e(TAG," friendList Error"+error);
                        mProgressbar.setVisibility(View.GONE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(myid, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_39_loading_block_gps_friend_list", new String[]{myid}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
        //getFriendsList(id);
    }

    private void all_block_check(){
        int j=0;
        for (Friend friend : mfriendList) {
            if(friend.getLocation_release_yn() == 0)
            {
                j++;
            }
        }
        if(j == 0){
            allStateChange(false);
            allfriendblock(id, "1");
        }
    }

    private void button() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        /*
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("스위치","터치감지하나??");
            }
        });
        */
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /*
                if (isChecked == true) {
                    if (switchcheck) {
                        FriendBlockAdapter.MyViewHolder.mainSwitchcheck = true;
                        allStateChange(true);
                        allfriendblock(id, "0");//스위치 상태변경
                        Log.e("전체스위치", "켜짐(차단)");
                    }
                } else {
                    if (switchcheck) {
                        FriendBlockAdapter.MyViewHolder.mainSwitchcheck = false;
                        allStateChange(false);
                        allfriendblock(id, "1");
                        Log.e("전체스위치", "꺼짐(해제)");
                    }
                }
                */
                if (isChecked) {
                    //FriendBlockAdapter.MyViewHolder.mainSwitchcheck = true;
                    //Log.i("이프 아이값", String.valueOf(i));

                        allStateChange(true);
                        allfriendblock(id, "0");//스위치 상태변경
                        SystemClock.sleep(500);
                        getFriendsList(id);
                        Log.e("전체스위치", "켜짐(차단)");

                }
                else {

                    //FriendBlockAdapter.MyViewHolder.mainSwitchcheck = false;
                        allStateChange(false);
                        allfriendblock(id, "1");
                        SystemClock.sleep(500);
                        getFriendsList(id);
                        Log.e("전체스위치", "꺼짐(해제)");

                }
            }
        });

    }


    private void allStateChange(boolean checked) {//all switchcheck
//        getFriendsList(id);
//        int yn = checked ? 0 : 1;
//        for (Friend friend : mfriendList) {
//            friend.setLocation_release_yn(yn);
//        }
//        mAdapter.notifyDataSetChanged();
//        mAdapter.setSelectedPosition(0);
        //isAllswitch=true;
        int yn = checked ? 0 : 1;
        for (Friend friend : mfriendList) {
            friend.setLocation_release_yn(yn);
        }
        mAdapter.notifyDataSetChanged();
    }



    private void allfriendblock(String rid, String i) {
        final String able = i;
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        if(able.equals("0")){
                            //Toast.makeText(getApplicationContext(), "전체 차단 되었습니다.", Toast.LENGTH_SHORT).show();
                        }else {
                            //Toast.makeText(getApplicationContext(), "전체 차단이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        //FriendBlockAdapter.MyViewHolder.mainSwitchcheck = true;
                    } else {//json.getString("result").equals("fail")시
                        Toast.makeText(getApplicationContext(), "변경을 할 수가 없습니다. 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG," block_gps_all_friend Error"+error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_40_block_gps_all_user", new String[]{id, able, "friend"}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }
}
