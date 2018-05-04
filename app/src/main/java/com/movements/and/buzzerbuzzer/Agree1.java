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
import android.widget.Button;
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

public class Agree1 extends BaseActivity {

    private final String TAG = "Agree1 : ";
    private String gps_auth, push_auth;

    private Button agreebtn;
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;

    private Typeface typefaceBold, typefaceExtraBold;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree1);

        agreebtn = (Button) findViewById(R.id.agreebtn);
        tv1 = (TextView)findViewById(R.id.tv1);
        tv2 = (TextView)findViewById(R.id.tv2);
        tv3 = (TextView)findViewById(R.id.tv3);
        tv4 = (TextView)findViewById(R.id.tv4);
        tv5 = (TextView)findViewById(R.id.tv5);
        tv6 = (TextView)findViewById(R.id.tv6);
        tv7 = (TextView)findViewById(R.id.tv7);
        tv8 = (TextView)findViewById(R.id.tv8);
        tv9 = (TextView)findViewById(R.id.tv9);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv1.setTypeface(typefaceBold);
        tv2.setTypeface(typefaceBold);
        tv3.setTypeface(typefaceBold);
        tv4.setTypeface(typefaceBold);
        tv5.setTypeface(typefaceBold);
        tv6.setTypeface(typefaceBold);
        tv7.setTypeface(typefaceBold);
        tv8.setTypeface(typefaceBold);
        tv9.setTypeface(typefaceBold);

        agreebtn.setTypeface(typefaceBold);

        button();

    }

    private void button() {

        agreebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent inLogin = new Intent(Agree1.this, LoginActivity.class);
            inLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            inLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(inLogin);
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });
    }
}
