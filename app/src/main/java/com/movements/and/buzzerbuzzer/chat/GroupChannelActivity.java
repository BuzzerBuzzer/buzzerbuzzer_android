package com.movements.and.buzzerbuzzer.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Buzzerprofile;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Followingprofile;
import com.movements.and.buzzerbuzzer.Followprofile;
import com.movements.and.buzzerbuzzer.FriendProfile;
import com.movements.and.buzzerbuzzer.R;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GroupChannelActivity extends BaseActivity {

    private JsonConverter jc;
    private String fr_class;
    private String id, user_id, mPassword, password;
    private SharedPreferences setting;
    private PasswordEn passwordEn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_channel);
        jc = new JsonConverter();
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_group_channel);
//        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // If started from launcher, load list of Open Channels

            Fragment fragment2 = GroupChannelListFragment.newInstance();

            FragmentManager manager2 = getSupportFragmentManager();
            manager2.popBackStack();

            manager2.beginTransaction()
                    .replace(R.id.container_group_channel2, fragment2)
                    .commit();

        }

        String channelUrl1 = getIntent().getStringExtra("groupChannelUrl");
        if(channelUrl1 != null) {
            // If started from notification
            Fragment fragment3 = GroupChatFragment.newInstance(channelUrl1);
            FragmentManager manager3 = getSupportFragmentManager();
            manager3.beginTransaction()
                    .replace(R.id.container_group_channel2, fragment3)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void chatprofile(String id){
        Log.e("그룹채널액티비티에서 받은 아이디 : ",id);
        getUser(id);
        Log.e("클래스 : ", fr_class);
        /*
        if(fr_class.equals("unknown")){
            Intent intentfr = new Intent(GroupChannelActivity.this, Buzzerprofile.class);//유저프로필
            intentfr.putExtra("id", user_id);
            intentfr.putExtra("coincheck", 1);
            startActivity(intentfr);
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
        }
        if(fr_class.equals("friend")){
            Intent intentfr = new Intent(GroupChannelActivity.this, FriendProfile.class);//유저프로필
            intentfr.putExtra("id", user_id);
            intentfr.putExtra("coincheck", 1);
            startActivity(intentfr);
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
        }
        if(fr_class.equals("following")){
            Intent intentfr = new Intent(GroupChannelActivity.this, Followingprofile.class);//유저프로필
            intentfr.putExtra("id", user_id);
            intentfr.putExtra("coincheck", 1);
            startActivity(intentfr);
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
        }
        if(fr_class.equals("follow")){
            Intent intentfr = new Intent(GroupChannelActivity.this, Followprofile.class);//유저프로필
            intentfr.putExtra("id", user_id);
            intentfr.putExtra("coincheck", 1);
            startActivity(intentfr);
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
        }

        */
    }

    public void getUser(String query) {
        final String set_id = query;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                JSONArray data = json.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    fr_class = data.getJSONObject(i).has("fr_class") ? data.getJSONObject(i).getString("fr_class") : "";

                                }
                            } else {//json.getString("result").equals("fail")시
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

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_56_search_user_id", new String[]{id, set_id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplication().getApplicationContext());
        requestQueue.add(stringRequest);

    }

}
