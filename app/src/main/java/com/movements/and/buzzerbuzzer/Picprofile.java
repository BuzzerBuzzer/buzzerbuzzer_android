package com.movements.and.buzzerbuzzer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
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
import com.movements.and.buzzerbuzzer.Adapter.MySlidingImage_Adapter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.UserPic;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 8. 3..
 */
//내 사진리스트
public class Picprofile extends BaseActivity {
    private ImageView backbtn, setbtn;
    private TextView numb_text, total_text, hal;
    //private ImageView user_pic;
    private ViewPager viewPager;
    private MySlidingImage_Adapter adapter;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String set_id, picurl, pic_num, mPassword, password;
    private JsonConverter jc;
    private int currentpostion;
    private ArrayList<UserPic> imagesArray;

    private Typeface typefaceBold, typefaceExtraBold;

    private static final String TAG = "Picprofile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_profile);

        backbtn = (ImageView) findViewById(R.id.backbtn);
        setbtn = (ImageView) findViewById(R.id.setbtn);
        numb_text = (TextView) findViewById(R.id.numb_text);
        total_text = (TextView) findViewById(R.id.total_text);
        hal = (TextView)findViewById(R.id.hal);
        //user_pic = (ImageView) findViewById(R.id.user_pic2);
        viewPager = (ViewPager) findViewById(R.id.view_pager2);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        numb_text.setTypeface(typefaceExtraBold);
        hal.setTypeface(typefaceExtraBold);
        total_text.setTypeface(typefaceExtraBold);

        setbtn.bringToFront();
        backbtn.bringToFront();
        numb_text.bringToFront();
        total_text.bringToFront();
        hal.bringToFront();

        imagesArray = new ArrayList<>();

        jc = new JsonConverter();
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();
        set_id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);
        viewPager.setClipToPadding(false);
        viewPager.setHorizontalFadingEdgeEnabled(true);
        adapter = new MySlidingImage_Adapter(this, imagesArray);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @SuppressLint("LongLogTag")
            @Override
            public void onPageSelected(int position) {
                numb_text.setText(String.valueOf(position + 1));
                currentpostion = position;
                pic_num = imagesArray.get(position).getNo();
                Log.e(TAG+" onPageSelected pic_num 확인", pic_num);

            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(adapter);
        Button();
        getimage(set_id);


    }

    private void getimage(String set_id1) {

        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "이미지 가져오기 "+response);
                String no, user_pic_src, date, totalNum = "0", num;
                ArrayList<UserPic> list2 = new ArrayList<>();
                list2.clear();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        JSONArray data = json.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            num = data.getJSONObject(i).has("num") ? data.getJSONObject(i).getString("num") : "";
                            totalNum = data.getJSONObject(i).has("totalNum") ? data.getJSONObject(i).getString("totalNum") : "";
                            no = data.getJSONObject(i).has("no") ? data.getJSONObject(i).getString("no") : "";
                            user_pic_src = data.getJSONObject(i).has("user_pic_src") ? data.getJSONObject(i).getString("user_pic_src") : "";
                            date = data.getJSONObject(i).has("date") ? data.getJSONObject(i).getString("date") : "";
                            UserPic userPic = new UserPic(no, user_pic_src);
                            list2.add(userPic);
                            Log.e(TAG+ " getimage", no+", "+ userPic.getNo()+", "+ list2.get(i).getNo());
                            //TODO 아 여기서... 경로가져오면되는구나.
                        }

                        imagesArray.clear();//add+queryd
                        imagesArray.addAll(list2);
                        Log.e(TAG+ " getimage", imagesArray.get(0).getNo()+", "+ imagesArray.get(0).getUser_pic_src());
                        adapter.notifyDataSetChanged();
                        total_text.setText(totalNum);//프로필사진 총 갯수

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                Log.e(TAG," picList Error"+error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(set_id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_16_loading_picture_list", new String[]{set_id}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }


    private void Button() {

        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] choose = new String[]{"프로필사진으로 등록", "삭제", "취소"};
                pic_num = imagesArray.get(currentpostion).getNo();
                //  Toast.makeText(getActivity(), position+"번쨰", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alert = new AlertDialog.Builder(Picprofile.this);
                alert.setTitle("")
                        .setCancelable(true)
                        .setItems(choose, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Log.e(TAG + " 사진번호", pic_num);
                                    addpic(picurl, pic_num);
                                } else if (which == 1) {
                                    delpic(picurl, pic_num);
                                } else if (which == 2) {
                                    dialog.dismiss();
                                } else {
                                }
                            }
                        });
                alert.show();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }

    private void delpic(String image, String no1) {
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        final String id = setting.getString("user_id", "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG +  " delpic", response);
                        //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                setResult(1);
                                getimage(set_id);
                                //finish();
                            } else {//json.getString("result").equals("fail")시
                                Toast.makeText(getApplicationContext(), "프로필 이미지입니다", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG," delete_user_pic Error");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_18_delete_profile_picture", new String[]{id, pic_num}));//서버 서비코드
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void addpic(String image, String no1) {
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        final String id = setting.getString("user_id", "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    String nick = null;
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG+"addpic ", response);
                        // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                setResult(1);
                                Log.e("업데이트", imagesArray.get(currentpostion).getUser_pic_src());
                                nick = setting.getString("user_nickname", null);
                                updateCurrentUserInfo(nick, imagesArray.get(currentpostion).getUser_pic_src());

                                //editor.putBoolean("use_gallery_pick", true);
                                editor.putString("set_pic_gallery_main_yj",imagesArray.get(currentpostion).getUser_pic_src());
                                editor.commit();

                                //TODO:혹시모름. 혹시모르니 일단 finish(); 주석
                                 finish();



                                //TODO: finish();쓰면 상세설정에 프로필사진 세팅안돔.
//                                Intent intent = new Intent(getApplicationContext(), ProfileSetting.class);
//                                startActivity(intent);

//                                Intent intent = new Intent(getApplicationContext(), ProfileSetting.class);
//                                intent.putExtra("set_pic_url", imagesArray.get(currentpostion).getUser_pic_src());
//                                startActivity(intent);

                            } else {//json.getString("result").equals("fail")시
                                Toast.makeText(getApplicationContext(), "Fail!!", Toast.LENGTH_LONG).show();
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
                        Log.e(TAG," change_profile_pic Error");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_17_change_profile_picture", new String[]{id, pic_num}));//서버 서비코드
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    //SendBird 닉네임 업데이트
    private void updateCurrentUserInfo(String userNickname, String picUrl) {//사진 업뎃 null 에 주소 추가 프로필 세팅사진 업뎃 할떄도 이거 쓰라..
        SendBird.updateCurrentUserInfo(userNickname, picUrl, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                // Log.d("user_pic_URL",user_pic_url);
                if (e != null) {
                    // Error!
                    Toast.makeText(
                            Picprofile.this, getString(R.string.str_sendbird_update_nickname_err),
                            Toast.LENGTH_SHORT)
                            .show();

                    return;
                }
                Log.e("업데이트", "성공");

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_exit, R.anim.bottom_down);
    }
}
