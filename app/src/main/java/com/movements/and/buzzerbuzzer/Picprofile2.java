package com.movements.and.buzzerbuzzer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
public class Picprofile2 extends BaseActivity {
    private ImageView backbtn, setbtn;
    private TextView numb_text, total_text, hal;
    private ImageView user_pic;

    private ViewPager viewPager;
    private MySlidingImage_Adapter adapter;


    private SharedPreferences setting;
    private String set_id, user_id;
    private JsonConverter jc;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password;
    private int currentpostion;

    private ArrayList<UserPic> imagesArray;
    private Typeface typefaceBold, typefaceExtraBold;

    private static final String TAG = "Picprofile2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_profile2);
        backbtn = (ImageView) findViewById(R.id.backbtn);
        setbtn = (ImageView) findViewById(R.id.setbtn);
        numb_text = (TextView) findViewById(R.id.numb_text);
        total_text = (TextView) findViewById(R.id.total_text);
        hal = (TextView) findViewById(R.id.hal);
        user_pic = (ImageView) findViewById(R.id.user_pic2);
        viewPager = (ViewPager) findViewById(R.id.view_pager2);
        Intent intent = getIntent();
        user_id = intent.getExtras().getString("user_id");

        imagesArray = new ArrayList<>();
        jc = new JsonConverter();
//        set_id = setting.getString("user_id", "");
        viewPager.setClipToPadding(false);
        viewPager.setHorizontalFadingEdgeEnabled(true);
        adapter = new MySlidingImage_Adapter(this, imagesArray);
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();

        id = setting.getString("user_id","");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        numb_text.setTypeface(typefaceExtraBold);
        hal.setTypeface(typefaceExtraBold);
        total_text.setTypeface(typefaceExtraBold);

        setbtn.bringToFront();
        setbtn.setVisibility(View.INVISIBLE);
        backbtn.bringToFront();
        numb_text.bringToFront();
        total_text.bringToFront();
        hal.bringToFront();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                numb_text.setText(String.valueOf(position + 1));
                currentpostion = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(adapter);
        Button();
        getimage(user_id);
    }

    private void getimage(final String set_id1) {
        final String set_id = set_id1;

        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String no, user_pic_src, date, totalNum = null, num;
                ArrayList<UserPic> list2 = new ArrayList<>();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        JSONArray data = json.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            totalNum = data.getJSONObject(i).has("totalNum") ? data.getJSONObject(i).getString("totalNum") : "";
                            num = data.getJSONObject(i).has("num") ? data.getJSONObject(i).getString("num") : "";
                            date = data.getJSONObject(i).has("date") ? data.getJSONObject(i).getString("date") : "";
                            no = data.getJSONObject(i).has("no") ? data.getJSONObject(i).getString("no") : "";
                            user_pic_src = data.getJSONObject(i).has("user_pic_src") ? data.getJSONObject(i).getString("user_pic_src") : "";

                            UserPic userPic = new UserPic(num, user_pic_src);
                            list2.add(userPic);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                imagesArray.clear();//add+queryd
                imagesArray.addAll(list2);
                adapter.notifyDataSetChanged();
                total_text.setText(totalNum);//프로필사진 총 갯수
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                Log.e(TAG, " picList Error" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_16_loading_picture_list", new String[]{set_id}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);

    }

    private void Button() {

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


}
