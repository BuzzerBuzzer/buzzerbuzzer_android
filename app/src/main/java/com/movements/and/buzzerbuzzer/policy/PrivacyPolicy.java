package com.movements.and.buzzerbuzzer.policy;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

public class PrivacyPolicy extends BaseActivity {
    private ImageView settingbtn;
    private TextView tv;
    private final String TAG = "PrivacyPolicy";

    private JsonConverter jc;

    private Typeface typefaceBold, typefaceExtraBold;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_policy);
        settingbtn = (ImageView) findViewById(R.id.settingbtn);
        tv = (TextView)findViewById(R.id.tv);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv.setTypeface(typefaceExtraBold);

        jc = new JsonConverter();

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();

        id = setting.getString("user_id","");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        button();

        WebView webView = (WebView) findViewById(R.id.web);
        webView.setWebViewClient(new WebViewClient());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        loadingTermsPage();
        webView.loadUrl(setting.getString("privarcy_policy_url",Config.URL_p));
        //webView.loadUrl(Config.URL);

    }

    public void loadingTermsPage() {




        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String privarcy_policy;


                        try {
                            JSONObject resJson = new JSONObject(response);

                            if (resJson.getString("result").equals("success")) {//성공시
                                JSONArray data = resJson.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    privarcy_policy = data.getJSONObject(i).has("privarcy_policy") ? data.getJSONObject(i).getString("privarcy_policy") : "";
                                    Log.e("privarcy_policy_url", privarcy_policy);

                                    SystemClock.sleep(1000);
                                    editor.putString("privarcy_policy_url",privarcy_policy);
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

                        //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "searching Error :" + error);
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
