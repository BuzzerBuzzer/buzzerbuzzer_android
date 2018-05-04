package com.movements.and.buzzerbuzzer;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 7. 21..
 */
//버전정보
public class Version extends BaseActivity {
    private static final String TAG = "Version";
    private ImageView settingbtn;
    private Button updatebtn;
    private TextView versiontx, ostx;
    private TextView tv, tv2;


    private String rtn, verSion;
    private AlertDialog.Builder alt_bld;

    private Typeface typefaceBold, typefaceExtraBold;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;
    private JsonConverter jc;

    private String id, mPassword, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.version_activity);

        jc = new JsonConverter();

        settingbtn = (ImageView) findViewById(R.id.settingbtn);
        updatebtn = (Button) findViewById(R.id.updatebtn);
        versiontx = (TextView) findViewById(R.id.versiontx);

        tv = (TextView)findViewById(R.id.tv);
        tv2 = (TextView)findViewById(R.id.tv2);
        ostx = (TextView)findViewById(R.id.ostx);


        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();


        id = setting.getString("user_id","");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);


        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        updatebtn.setTypeface(typefaceExtraBold);
        tv.setTypeface(typefaceExtraBold);
        tv2.setTypeface(typefaceBold);
        ostx.setTypeface(typefaceBold);
        versiontx.setTypeface(typefaceBold);


        String versionName = BuildConfig.VERSION_NAME;
        versiontx.setText("현재 버전 " + versionName);
        // updatebtn.setEnabled(false);
        new Version1().execute();

        procLogout();
        button();

    }

    private void button() {
        settingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                marketLaunch.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.movements.and.buzzerbuzzer"));
                startActivity(marketLaunch);
            }
        });
    }
    //TODO:넘어온걸로 수정해야함.
    private void procLogout() {
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        JSONArray data = json.getJSONArray("data");
//                            User user = null;
                        for (int i = 0; i < data.length(); i++) {
//                            android_app_link = data.getJSONObject(i).has("android_app_link") ? data.getJSONObject(i).getString("android_app_link") : "";
//                            latest_version_ios = data.getJSONObject(i).has("latest_version_ios") ? data.getJSONObject(i).getString("latest_version_ios") : "";
//                            ios_app_link = data.getJSONObject(i).has("ios_app_link") ? data.getJSONObject(i).getString("ios_app_link"): "";//modify
//                            latest_version_android = data.getJSONObject(i).has("latest_version_android") ? data.getJSONObject(i).getString("latest_version_android") : "";

                        }

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
                Log.e(TAG, "push_news_yn Error :" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_48_loading_get_app_version", new String[]{""}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void longtimenoseecheck(boolean switchcheck) {
        editor.putBoolean("logtimecheck", switchcheck);
        editor.commit();
    }


    private class Version1 extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(Void... params) {
            // Confirmation of market information in the Google Play Store
            try {
                Document doc = Jsoup
                        .connect(
                                "https://play.google.com/store/apps/details?id=com.movements.and.buzzerbuzzer")
                        .get();
                Elements Version = doc.select(".content");

                for (Element v : Version) {
                    if (v.attr("itemprop").equals("softwareVersion")) {
                        rtn = v.text();

                    }
                }
                return rtn;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Version check the execution application.
            PackageInfo pi = null;
            try {
                pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            verSion = pi.versionName;
            //Log.d("버전",pi.versionName);
            rtn = result;

            if(rtn==null)
                updatebtn.setText("업데이트");
            else
                updatebtn.setText(rtn + " 업데이트");

            if (!verSion.equals(rtn) && rtn != null) {
                updatebtn.setEnabled(true);
                super.onPostExecute(result);
            }else{
                updatebtn.setEnabled(false);
                updatebtn.setText("최신버전입니다");
            }
        }
    }
}
