package com.movements.and.buzzerbuzzer;

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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Adapter.FriendBlockAdapter2;
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
//내위치 차단팔로우 관리
public class MygpsFollowerAble extends BaseActivity {

    private RecyclerView recyclerView;
    private ArrayList<Friend> mfriendList;
    private ProgressBar mProgressbar;
    private FriendBlockAdapter2 mAdapter;

    private ImageView backbtn;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password;
    private Switch aSwitch;
    private int i = 0;
    private TextView tv, textView2;
    private Typeface typefaceBold, typefaceExtraBold;

    private JsonConverter jc;
    private boolean switchcheck = false;

    private static final String TAG = "MygpsFollowerAble";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationblock2);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_lb);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar1);
        backbtn = (ImageView) findViewById(R.id.backbtn);
        aSwitch = (Switch) findViewById(R.id.switch8);

        jc = new JsonConverter();
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        tv = (TextView)findViewById(R.id.tv);
        textView2 = (TextView)findViewById(R.id.textView2);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv.setTypeface(typefaceExtraBold);
        textView2.setTypeface(typefaceBold);

        mfriendList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new FriendBlockAdapter2(getApplication(), mfriendList, new FriendBlockAdapter2.RecyclerItemClickListener() {
            @Override
            public void onClickListener(Friend friends, int position) {
                //changeSelected(position);

            }
        });
        recyclerView.setAdapter(mAdapter);

        button();
        getfollowerList(id);//리스트
    }

    private void getfollowerList(String id) {
        final String myid = id;
        mProgressbar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String pic_src, nickname, fr_id;
                        int location_release_yn;
                        ArrayList<Friend> friends = new ArrayList<>();//9
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                JSONArray data = json.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    nickname = data.getJSONObject(i).has("nickname") ? data.getJSONObject(i).getString("nickname") : "";
                                    pic_src = data.getJSONObject(i).has("pic_src") ? data.getJSONObject(i).getString("pic_src") : "";
                                    fr_id = data.getJSONObject(i).has("fr_id") ? data.getJSONObject(i).getString("fr_id") : "";
                                    location_release_yn = data.getJSONObject(i).has("location_release_yn") ? data.getJSONObject(i).getInt("location_release_yn") : 3;
                                    Friend friend = new Friend(nickname, pic_src, fr_id, location_release_yn);
                                    friends.add(friend);
                                }
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

                                mProgressbar.setVisibility(View.GONE);
                                mfriendList.clear();//add+queryd
                                mfriendList.addAll(friends);
                                mAdapter.notifyDataSetChanged();
                                mAdapter.setSelectedPosition(0);//setSelectedPosition 4
                            } else {//json.getString("result").equals("fail")시
                                //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                mProgressbar.setVisibility(View.GONE);
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FriendBlockAdapter2.MyViewHolder.MAINSWITCHCHECk = true;
                        switchcheck = true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        //Toast.makeText(getApplicationContext(), " gpsable Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG," followList Error"+error);
                        mProgressbar.setVisibility(View.GONE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(myid, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_41_loading_block_gps_follow_list", new String[]{myid}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void button() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //FriendBlockAdapter2.MyViewHolder.MAINSWITCHCHECk = false;
                if (isChecked) {
//                    Toast.makeText(getApplicationContext(), "on", Toast.LENGTH_SHORT).show();
                    //FriendBlockAdapter2.MyViewHolder.MAINSWITCHCHECk = false;

                        allStateChange(true);
                        allfriendblock(id, "0");
                        SystemClock.sleep(500);
                        getfollowerList(id);

                } else {
//                    Toast.makeText(getApplicationContext(), "off", Toast.LENGTH_SHORT).show();

                    //FriendBlockAdapter2.MyViewHolder.MAINSWITCHCHECk = false;
                    allStateChange(false);
                    allfriendblock(id, "1");
                    SystemClock.sleep(500);
                    getfollowerList(id);

                }
            }
        });

    }

    private void allStateChange(boolean check) {
        //isAllswitch=true;
        int yn = check ? 0 : 1;
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
                        //FriendBlockAdapter2.MyViewHolder.MAINSWITCHCHECk = true;
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
                //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_40_block_gps_all_user", new String[]{id, able, "follow"}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FriendBlockAdapter2.MyViewHolder.MAINSWITCHCHECk = false;
    }
}
