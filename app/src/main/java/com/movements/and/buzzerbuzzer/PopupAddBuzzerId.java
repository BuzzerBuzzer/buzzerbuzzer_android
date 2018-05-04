package com.movements.and.buzzerbuzzer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Adapter.UserAddAdapter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.User;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
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
 * Created by samkim on 2017. 3. 16..
 */
//친구추천 및 유저검색화면
public class PopupAddBuzzerId extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    //private Button addfiend;
    InputMethodManager imm;
    private EditText search_bar;
    private ImageView search_go_btn;
    private ImageView user_pic;

    private TextView id_tx, tv, usermessage, tx;
    private Typeface typefaceBold, typefaceExtraBold;

    private ImageView btn_back;

    private SharedPreferences setting;

    private RecyclerView recyclerview_frrecomand;
    private ArrayList<User> mUserList;
    private UserAddAdapter mAdapter;

    private LinearLayout lin2;

    private InputMethodManager inputMethodManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private JsonConverter jc;
    private PasswordEn passwordEn;

    private String id, user_id, mPassword, password;

    private DividerItemDecoration dividerItemDecoration;
    //private RelativeLayout search_noti;

    private static final String TAG = "PopupAddBuzzerId > ";
    private LinearLayout lin1;
    private String fr_class;
    private RelativeLayout user_search;

    private Boolean phonenumCheck = false;
    private ConfirmDialog dialog;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupaddbuzzerid_activity);
        jc = new JsonConverter();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        search_bar = (EditText) findViewById(R.id.search_bar1);
        search_go_btn = (ImageView) findViewById(R.id.search_go_btn);
        //addfiend = (Button) findViewById(R.id.addfiend);
        recyclerview_frrecomand = (RecyclerView) findViewById(R.id.recyclerview_frrecomand);
        id_tx = (TextView) findViewById(R.id.id_tx);
        user_pic = (ImageView) findViewById(R.id.user_pic2);
        //search_noti = (RelativeLayout) findViewById(R.id.search_noti);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        user_pic.setBackground(new ShapeDrawable(new OvalShape()));
        user_pic.setClipToOutline(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        lin2 = (LinearLayout) findViewById(R.id.lin2);
        lin1 = (LinearLayout) findViewById(R.id.lin1);
        user_search = (RelativeLayout) findViewById(R.id.user_search);

        tv = (TextView)findViewById(R.id.tv);
        usermessage = (TextView)findViewById(R.id.usermessage);
        tx = (TextView)findViewById(R.id.tx);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        id_tx.setTypeface(typefaceExtraBold);
        tv.setTypeface(typefaceExtraBold);
        usermessage.setTypeface(typefaceBold);
        tx.setTypeface(typefaceExtraBold);
        search_bar.setTypeface(typefaceExtraBold);

        lin1.setOnClickListener(clickListener);
        tv.setOnClickListener(clickListener);

        btn_back.bringToFront();
        btn_back.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        //TODO: unknown - Popupprofile, friend - FriendProfile, following - FollowingProfile, follow - FollowProfile

        user_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_tx.performClick();
            }
        });

        id_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fr_class.equals("unknown")){
                    Intent intentfr = new Intent(getApplicationContext(), Buzzerprofile.class);//유저프로필
                    intentfr.putExtra("id", user_id);
                    intentfr.putExtra("coincheck", 1);
                    startActivity(intentfr);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
                if(fr_class.equals("friend")){
                    Intent intentfr = new Intent(getApplicationContext(), FriendProfile.class);//유저프로필
                    intentfr.putExtra("id", user_id);
                    intentfr.putExtra("coincheck", 1);
                    startActivity(intentfr);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
                if(fr_class.equals("following")){
                    Intent intentfr = new Intent(getApplicationContext(), Followingprofile.class);//유저프로필
                    intentfr.putExtra("id", user_id);
                    intentfr.putExtra("coincheck", 1);
                    startActivity(intentfr);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
                if(fr_class.equals("follow")){
                    Intent intentfr = new Intent(getApplicationContext(), Followprofile.class);//유저프로필
                    intentfr.putExtra("id", user_id);
                    intentfr.putExtra("coincheck", 1);
                    startActivity(intentfr);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }


            }
        });

        user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fr_class.equals("unknown")){
                    Intent intentfr = new Intent(getApplicationContext(), Buzzerprofile.class);//유저프로필
                    intentfr.putExtra("id", user_id);
                    intentfr.putExtra("coincheck", 1);
                    startActivity(intentfr);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
                if(fr_class.equals("friend")){
                    Intent intentfr = new Intent(getApplicationContext(), FriendProfile.class);//유저프로필
                    intentfr.putExtra("id", user_id);
                    intentfr.putExtra("coincheck", 1);
                    startActivity(intentfr);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
                if(fr_class.equals("following")){
                    Intent intentfr = new Intent(getApplicationContext(), Followingprofile.class);//유저프로필
                    intentfr.putExtra("id", user_id);
                    intentfr.putExtra("coincheck", 1);
                    startActivity(intentfr);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
                if(fr_class.equals("follow")){
                    Intent intentfr = new Intent(getApplicationContext(), Followprofile.class);//유저프로필
                    intentfr.putExtra("id", user_id);
                    intentfr.putExtra("coincheck", 1);
                    startActivity(intentfr);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }


            }
        });

//        addfiend.setOnClickListener(new View.OnClickListener() {//친구등록
//            @Override
//            public void onClick(View v) {
//                registerUser(id, user_id);
//            }
//        });


        search_bar.addTextChangedListener(new TextWatcher() {
            String search = search_bar.getText().toString().trim();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        search_go_btn.setImageResource(R.drawable.ico_search_on);
                        //inputMethodManager.hideSoftInputFromWindow(search_bar.getWindowToken(), 0);//검색후 자판하이드
                        getUser(search);
                    } else {
                        //Toast.makeText(getApplicationContext(), R.string.search_input, Toast.LENGTH_SHORT).show();
                        search_go_btn.setImageResource(R.drawable.ico_search_off);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        search_bar.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                String search = search_bar.getText().toString().trim();
//                //Enter key Action
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    if (search.length() > 0) {
//                        search_go_btn.setImageResource(R.drawable.ico_search_on);
//                        inputMethodManager.hideSoftInputFromWindow(search_bar.getWindowToken(), 0);//검색후 자판하이드
//                        getUser(search);
//                    } else {
//                        Toast.makeText(getApplicationContext(), R.string.search_input, Toast.LENGTH_SHORT).show();
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });

        mUserList = new ArrayList<>();
        mUserList.clear();
//
//        recommandfri(phoneArr);//친구추천

        Log.e(TAG, String.valueOf(mUserList.size()));

        dividerItemDecoration = new DividerItemDecoration(recyclerview_frrecomand.getContext(), new LinearLayoutManager(getApplicationContext()).getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.lin_gray));
        recyclerview_frrecomand.addItemDecoration(dividerItemDecoration);
        recyclerview_frrecomand.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mAdapter = new UserAddAdapter(getApplicationContext(), mUserList, new UserAddAdapter.RecyclerItemClickListener() {
            @Override
            public void onClickListener(User user, int position) {
                Intent intentfr = new Intent(getApplicationContext(), Buzzerprofile.class);
                intentfr.putExtra("id", user.getId());
                intentfr.putExtra("coincheck", 1);
                startActivity(intentfr);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });
        recyclerview_frrecomand.setAdapter(mAdapter);

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        search_go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = search_bar.getText().toString().trim();
                if (search.length() > 0) {
                    inputMethodManager.hideSoftInputFromWindow(search_bar.getWindowToken(), 0);
                    getUser(search);//유저검색
                } else {
                    Toast.makeText(getApplicationContext(), R.string.search_input, Toast.LENGTH_SHORT).show();
                }

            }
        });
        if(OpeningActivity.isPhonenumLoading) {
            Set<String> slist = setting.getStringSet("phoneNums", null);
            if (!slist.isEmpty()) {
                phonenumCheck = true;
                List<String> plist = new ArrayList<String>(slist);
                Log.e(TAG + "plist.size()", String.valueOf(plist.size()));
                recommandfri();//친구추천
            } else {
                Log.e(TAG + "전화번호리스트", "비어있음");
            }
        }else{
            dialog = new ConfirmDialog(PopupAddBuzzerId.this,
                    "추천 친구가 아직 로딩중입니다.\n잠시 후 다시 시도해주세요.", "확인");
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    public void chatprofile(String id){
        getUser(id);
        if(fr_class.equals("unknown")){
            Intent intentfr = new Intent(getApplicationContext(), Buzzerprofile.class);//유저프로필
            intentfr.putExtra("id", user_id);
            intentfr.putExtra("coincheck", 1);
            startActivity(intentfr);
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
        }
        if(fr_class.equals("friend")){
            Intent intentfr = new Intent(getApplicationContext(), FriendProfile.class);//유저프로필
            intentfr.putExtra("id", user_id);
            intentfr.putExtra("coincheck", 1);
            startActivity(intentfr);
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
        }
        if(fr_class.equals("following")){
            Intent intentfr = new Intent(getApplicationContext(), Followingprofile.class);//유저프로필
            intentfr.putExtra("id", user_id);
            intentfr.putExtra("coincheck", 1);
            startActivity(intentfr);
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
        }
        if(fr_class.equals("follow")){
            Intent intentfr = new Intent(getApplicationContext(), Followprofile.class);//유저프로필
            intentfr.putExtra("id", user_id);
            intentfr.putExtra("coincheck", 1);
            startActivity(intentfr);
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
        }
    }

    public void getUser(String query) {
        final String set_id = query;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String pic_src = null;
                        String nickname = null;
                        String condition_msg = null;
                        //search_noti.setVisibility(View.VISIBLE);
                        Log.d(TAG+" getUser", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                JSONArray data = json.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    fr_class = data.getJSONObject(i).has("fr_class") ? data.getJSONObject(i).getString("fr_class") : "";
                                    pic_src = data.getJSONObject(i).has("pic_src") ? data.getJSONObject(i).getString("pic_src") : "";
                                    nickname = data.getJSONObject(i).has("nickname") ? data.getJSONObject(i).getString("nickname") : "";
                                    condition_msg = data.getJSONObject(0).has("condition_msg") ? data.getJSONObject(0).getString("condition_msg") : "";
                                    user_id = data.getJSONObject(i).getString("id");//modify
                                    user_pic.setImageResource(R.drawable.nopic__s_m);

//                                    lin2.setVisibility(View.INVISIBLE);

                                    user_pic.setVisibility(View.VISIBLE);
                                    if (pic_src.isEmpty()) {
                                    } else {
                                        Picasso.with(getApplicationContext()).load(pic_src).into(user_pic);
                                    }
                                    id_tx.setText(nickname);
                                    id_tx.setVisibility(View.VISIBLE);
                                    usermessage.setText(condition_msg);
                                    usermessage.setVisibility(View.VISIBLE);

                                    //addfiend.setVisibility(View.VISIBLE);
                                    //addfiend.getBackground().setColorFilter(getResources().getColor(R.color.buzzerfollowbtn), PorterDuff.Mode.SRC_ATOP);
                                    //addfiend.setText("팔로우");
                                    //addfiend.setEnabled(true);
                                }
                            } else {//json.getString("result").equals("fail")시
                                //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                //addfiend.setVisibility(View.GONE);
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
                        //Toast.makeText(getApplicationContext(), " 유저불러오기 Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, " searching_id Error" + error);
                        //addfiend.setVisibility(View.GONE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_56_search_user_id", new String[]{id, set_id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void recommandfri() {
        //final String phonenum = query;

        recyclerview_frrecomand.setAdapter(mAdapter);
        Set<String> slist = setting.getStringSet("phoneNums", null);
        List<String> plist = new ArrayList<String>(slist);
        //Log.d(TAG +"plist.size()", String.valueOf(plist.size()));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            String id, pic_src, nickname, phone;
            @Override
            public void onResponse(String response) {
                Log.d(TAG+" 359", response);
                ArrayList<User> userArrayList = new ArrayList<>();//9
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        JSONArray data = json.getJSONArray("data");
                        Log.d(TAG+" recommandfri", "여기는?");
                        for (int i = 0; i < data.length(); i++) {
                            Log.d(TAG+" recommandfri", "여기는?2");
                            id = data.getJSONObject(i).has("id") ? data.getJSONObject(i).getString("id") : "";
                            pic_src = data.getJSONObject(i).has("pic_src") ? data.getJSONObject(i).getString("pic_src") : "";
                            nickname = data.getJSONObject(i).has("nickname") ? data.getJSONObject(i).getString("nickname") : "";
                            phone = data.getJSONObject(i).has("phone") ? data.getJSONObject(i).getString("phone") : "";
                            User user = new User(id, pic_src, nickname, phone);
                            userArrayList.add(user);
                            Log.e("userArrayList 사이즈", String.valueOf(userArrayList.size())+", "+userArrayList.get(i).getId()+", "+userArrayList.get(i).getPhonenum());
                        }
                        mUserList.clear();
                        mUserList.addAll(userArrayList);
                        Log.e("userArrayList 사이즈", String.valueOf(mUserList.size())+", "+mUserList.get(0).getId()+", "+mUserList.get(0).getPhonenum());
                        mAdapter.notifyDataSetChanged();
                        mAdapter.setSelectedPosition(0);

                    } else {//json.getString("result").equals("fail")시
                        //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();

                        mUserList.clear();
                        mAdapter.notifyDataSetChanged();
                        mAdapter.setSelectedPosition(0);

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
                        //Toast.makeText(getApplicationContext(), "phonenum Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG," add_phone_follow Error"+error);
                    }
                }) {
            @SuppressLint("LongLogTag")
            @Override
            protected Map<String, String> getParams() {

                Set<String> slist = setting.getStringSet("phoneNums", null);
                if(!slist.isEmpty())
                {

                }
                List<String> plist = new ArrayList<String>(slist);
                String[] pNums = new String[plist.size() + 1];
                pNums[0] = id;
                for(int i = 0; i < plist.size(); i++){
                    pNums[i+1] = plist.get(i);
//                    Log.d(TAG+" phoneFirNum", String.valueOf(pNums[i+1]));
                }
                Log.d(TAG+" phoneFirNum pNums[1]", String.valueOf(pNums[1]));
                Log.d(TAG+" phoneFirNum pNums[0]", String.valueOf(pNums[0]));

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_55_loading_phone_number_user", pNums));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }


    //TODO 이 코드 유저프로필로 옮겨야될듯함
    private void registerUser(String id1, String fid1) {
        final String id = id1;
        final String fid = fid1;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
//                                ((FriendList) FriendList.mContext).getData(id, 0);
//                                addfiend.getBackground().setColorFilter(getResources().getColor(R.color.err_btn), PorterDuff.Mode.SRC_ATOP);
//                                addfiend.setText("팔로잉");
//                                addfiend.setEnabled(false);
                            } else {
                                Toast.makeText(getApplicationContext(), "다시시도해주세요", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        Log.e(TAG," add_user_request_follow Error"+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam("add_user_request_follow", new String[]{id, fid, ""}));//서버 서비코드
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //해당 어댑터를 서버와 통신한 값이 나오면 됨
                mAdapter.notifyDataSetChanged();
                mAdapter.setSelectedPosition(0);
                // ((FriendList)getActivity()).CallVolley();
                if(phonenumCheck){
                    recommandfri();
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, 3000);
    }


    View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            hideKeyboard();
            switch (v.getId())
            {
                case R.id.lin1 :
                    break;

                case R.id.tv :
                    break;

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        String search = search_bar.getText().toString().trim();
        getUser(search);
        if(phonenumCheck) {
            recommandfri();
        }
    }

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(lin1.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);;
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_exit, R.anim.bottom_down);
    }
}
