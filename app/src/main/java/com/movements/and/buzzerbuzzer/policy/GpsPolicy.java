package com.movements.and.buzzerbuzzer.policy;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.R;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 7. 21..
 */

public class GpsPolicy extends BaseActivity {
    private ImageView settingbtn;
    private TextView tv;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password;

    private Typeface typefaceBold, typefaceExtraBold;
    private JsonConverter jc;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_policy);
        jc = new JsonConverter();
        settingbtn = (ImageView) findViewById(R.id.settingbtn);
        tv = (TextView)findViewById(R.id.tv);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();


        id = setting.getString("user_id","");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        WebView webView = (WebView) findViewById(R.id.web);
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        loadingTermsPage();
        webView.loadUrl(setting.getString("location_terms_url",Config.URL_g));


        tv.setTypeface(typefaceExtraBold);


        button();
    }

    public void loadingTermsPage() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String location_terms;


                        try {
                            JSONObject resJson = new JSONObject(response);

                            if (resJson.getString("result").equals("success")) {//성공시
                                JSONArray data = resJson.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    location_terms = data.getJSONObject(i).has("location_terms") ? data.getJSONObject(i).getString("location_terms") : "";
                                    Log.e("location_terms_url", location_terms);
                                    SystemClock.sleep(1000);
                                    editor.putString("location_terms_url",location_terms);
                                    editor.commit();
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
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_49_loading_terms_page", new String[]{}));//서버 서비코드
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }


    private void button() {
        settingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
