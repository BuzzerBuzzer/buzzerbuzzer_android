package com.movements.and.buzzerbuzzer;

import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.BuzzerDialog;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog2;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 7. 21..
 */

public class Agree2 extends BaseActivity {

    private final String TAG = "Agree2 : ";
    private String gps_auth, push_auth;

    private ImageView btn_back, nextbtn;
    private TextView tv1, textView21, btn_agree1, btn_agree2, btn_agree3;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;
    private CheckBox agree_all, agree1, agree2, agree3;
    private String id, mPassword, password;

    private WebView webView1, webView2, webView3;

    private Typeface typefaceBold, typefaceExtraBold;
    private JsonConverter jc;

    private ConfirmDialog dialog;
    private BuzzerDialog buzzerdialog;
    private ConfirmDialog2 dialog2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree2);
        jc = new JsonConverter();

        btn_back = (ImageView) findViewById(R.id.btn_back);
        nextbtn = (ImageView) findViewById(R.id.nextbtn);
        tv1 = (TextView)findViewById(R.id.tv1);
        textView21 = (TextView)findViewById(R.id.textView21);

        btn_agree1 = (TextView)findViewById(R.id.btn_agree1);
        btn_agree2 = (TextView)findViewById(R.id.btn_agree2);
        btn_agree3= (TextView)findViewById(R.id.btn_agree3);

        agree_all = (CheckBox) findViewById(R.id.agree_all);
        agree1 = (CheckBox) findViewById(R.id.agree1);
        agree2 = (CheckBox) findViewById(R.id.agree2);
        agree3 = (CheckBox) findViewById(R.id.agree3);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv1.setTypeface(typefaceBold);
        textView21.setTypeface(typefaceExtraBold);

        btn_agree1.setTypeface(typefaceBold);
        btn_agree2.setTypeface(typefaceBold);
        btn_agree3.setTypeface(typefaceBold);

        agree_all.setTypeface(typefaceBold);
        agree1.setTypeface(typefaceBold);
        agree2.setTypeface(typefaceBold);
        agree3.setTypeface(typefaceBold);

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();

        id = setting.getString("user_id","");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        webView1 = (WebView) findViewById(R.id.web1);
        webView1.setWebViewClient(new WebViewClient());

        WebSettings webSettings1 = webView1.getSettings();
        webSettings1.setJavaScriptEnabled(true);

        webView2 = (WebView) findViewById(R.id.web2);
        webView2.setWebViewClient(new WebViewClient());

        WebSettings webSettings2 = webView2.getSettings();
        webSettings2.setJavaScriptEnabled(true);

        webView3 = (WebView) findViewById(R.id.web3);
        webView3.setWebViewClient(new WebViewClient());

        WebSettings webSettings3 = webView3.getSettings();
        webSettings3.setJavaScriptEnabled(true);

        loadingTermsPage();
        webView1.loadUrl(setting.getString("membership_terms_url",Config.URL_m));
        webView2.loadUrl(setting.getString("privarcy_policy_url",Config.URL_p));
        webView3.loadUrl(setting.getString("location_terms_url",Config.URL_g));

        button();

    }

    private void button() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(agree1.isChecked() && agree2.isChecked() && agree3.isChecked()){
                    update_terms();
                }else{
                    dialog = new ConfirmDialog(Agree2.this,
                            "필수 약관에 동의해주세요.", "확인");
                    dialog.setCancelable(true);
                    dialog.show();
                }
            }
        });

        agree_all.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agree_all.isChecked()) {
                    agree1.setChecked(true);
                    agree2.setChecked(true);
                    agree3.setChecked(true);
                    agreeCheck();
                } else {
                    agree1.setChecked(false);
                    agree2.setChecked(false);
                    agree3.setChecked(false);
                    agreeCheck();
                }
            }
        });

        agree1.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreeCheck();
            }
        });

        agree2.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreeCheck();
            }
        });

        agree3.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreeCheck();
            }
        });

        btn_agree1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView1.setVisibility(View.VISIBLE);
                webView2.setVisibility(View.GONE);
                webView3.setVisibility(View.GONE);
            }
        });

        btn_agree2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView1.setVisibility(View.GONE);
                webView2.setVisibility(View.VISIBLE);
                webView3.setVisibility(View.GONE);
            }
        });

        btn_agree3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView1.setVisibility(View.GONE);
                webView2.setVisibility(View.GONE);
                webView3.setVisibility(View.VISIBLE);
            }
        });
    }

    public void agreeCheck(){
        if (agree1.isChecked() && agree2.isChecked() && agree3.isChecked()) {
            nextbtn.setImageResource(R.drawable.signup_next);
        } else {
            nextbtn.setImageResource(R.drawable.signup_next_dim);
        }
    }

    public void loadingTermsPage() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG + "loadingTermsPage()", response);
                        String location_terms, privarcy_policy, membership_terms;
                        try {
                            JSONObject resJson = new JSONObject(response);
                            if (resJson.getString("result").equals("success")) {//성공시
                                JSONArray data = resJson.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    location_terms = data.getJSONObject(i).has("location_terms") ? data.getJSONObject(i).getString("location_terms") : "";
                                    privarcy_policy = data.getJSONObject(i).has("privarcy_policy") ? data.getJSONObject(i).getString("privarcy_policy") : "";
                                    membership_terms = data.getJSONObject(i).has("membership_terms") ? data.getJSONObject(i).getString("membership_terms") : "";
                                    SystemClock.sleep(1000);
                                    editor.putString("privarcy_policy_url",privarcy_policy);
                                    editor.putString("membership_terms_url",membership_terms);
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

    private void update_terms() {
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG + "update_terms()", response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        dialog2 = new ConfirmDialog2(Agree2.this,
                                "약관 동의가 완료되었습니다.", "확인", termsOKListener);
                        dialog2.setCancelable(true);
                        dialog2.show();
                    }else {
                        dialog = new ConfirmDialog(Agree2.this,
                                "약관 동의에 실패하였습니다.\n고객센터에 문의해 주세요.", "확인");
                        dialog.setCancelable(true);
                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "update_terms() Error" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.MODEL, Build.VERSION.RELEASE,"proc_03_2_update_terms", new String[]{id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    // 약관동의 확인 다이얼로그 클릭이벤트
    private View.OnClickListener termsOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog2.dismiss();
            // 회원가입 피니쉬로 이동
            Intent inLogin = new Intent(Agree2.this, Membership_finish.class);
            inLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            inLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(inLogin);
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
        }
    };


}
