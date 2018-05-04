package com.movements.and.buzzerbuzzer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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

public class Membership_finish extends BaseActivity {

    private final String TAG = "Membership_finish : ";
    private String gps_auth, push_auth;

    TextView tv1, gotomain, tv2, tv3;
    ImageView nextchk;
    private Typeface typefaceBold, typefaceExtraBold;

    private JsonConverter jc;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;
    private String id, mPassword, password;

    private ConfirmDialog dialog;
    private BuzzerDialog buzzerdialog;
    private ConfirmDialog2 dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_finish);
        jc = new JsonConverter();

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();

        id = setting.getString("user_id","");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        tv1 = (TextView)findViewById(R.id.tv1);
        tv2 = (TextView)findViewById(R.id.tv2);
        tv3 = (TextView)findViewById(R.id.tv3);
        gotomain = (TextView)findViewById(R.id.gotomain);
        nextchk = (ImageView)findViewById(R.id.nextchk) ;

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv1.setTypeface(typefaceExtraBold);
        tv2.setTypeface(typefaceExtraBold);
        tv3.setTypeface(typefaceBold);
        String str = "<font color=\"#888888\">지금부터 </font>" +
                "<font color=\"#7d51fc\">부저부저</font><font color=\"#888888\">에서 주변에 가까운 친구들을<br>" +
                "한 눈에 볼 수 있습니다.</font>";
        tv3.setText(Html.fromHtml(str));
        gotomain.setTypeface(typefaceExtraBold);
        gotomain.setVisibility(View.GONE);
        button();
        check_all_auth();
    }

    public void button(){

        gotomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileSetting.class);
                intent.putExtra("gps_base_check", false);   //gender 의 resisterUser 에서 inLogin 에 false 를 주석 처리하고 추가
                startActivityForResult(intent, 82);
                finish();
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });

        //간단 튜토리얼 페이지로 이동
        nextchk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gps_auth.equals("0")){
                    gpsDialog();
                }else if(push_auth.equals("0")){
                    pushDialog();
                }else {
                    // 권한 모두 있으니 바로 튜토리얼로 넘어가기
                    move_tutorial();
                }
            }
        });
    }

    private void move_tutorial(){
        Intent intent = new Intent(getApplicationContext(), Simple_tutorial.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
    }

    private void update_all_auth() {
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG + "update_all_auth()", response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {
                        // 권한 업데이트 성공시 튜토리얼로 넘어가기
                        move_tutorial();
                    }else {
                        dialog = new ConfirmDialog(Membership_finish.this,
                                "권한 업데이트에 실패하였습니다.\n고객센터에 문의해 주세요.", "확인");
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
                Log.e(TAG, "update_all_auth() Error" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.MODEL, Build.VERSION.RELEASE,"proc_03_update_all_auth", new String[]{id, gps_auth, push_auth}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void check_all_auth() {
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG + "check_all_auth()", response);
                String gps_auth0, push_auth0;
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        JSONArray data = json.getJSONArray("data");
                        gps_auth0 = data.getJSONObject(0).has("gps_auth") ? data.getJSONObject(0).getString("gps_auth") : "";
                        push_auth0 = data.getJSONObject(0).has("push_auth") ? data.getJSONObject(0).getString("push_auth") : "";
                        if(gps_auth0.equals("yes")){
                            gps_auth = "1";
                        }else{
                            gps_auth = "0";
                        }
                        if(push_auth0.equals("yes")){
                            push_auth = "1";
                        }else{
                            push_auth = "0";
                        }

                        // Log.e(TAG + "GPS 권한 체크 결과", String.valueOf(gps_auth));
                        // Log.e(TAG + "푸시 권한 체크 결과", String.valueOf(push_auth));

                    }else {
                        dialog = new ConfirmDialog(Membership_finish.this,
                                "권한 체크에 실패하였습니다.\n고객센터에 문의해 주세요.", "확인");
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
                Log.e(TAG, "check_all_auth() Error" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.MODEL, Build.VERSION.RELEASE,"proc_02_check_all_auth", new String[]{id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    // GPS동의 확인 다이얼로그
    public void gpsDialog(){
        buzzerdialog = new BuzzerDialog(Membership_finish.this,
                "GPS 사용에 동의하십니까?", "동의함", "동의안함", gpsOKListener, gpsNOListener);
        buzzerdialog.setCancelable(true);
        buzzerdialog.show();
    }
    private View.OnClickListener gpsOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            //GPS 동의함
            gps_auth = "1";
            buzzerdialog.dismiss();
            if(push_auth.equals("0")){
                pushDialog();
            }else{
                update_all_auth();
            }
        }
    };
    private View.OnClickListener gpsNOListener = new View.OnClickListener() {
        public void onClick(View v) {
            //GPS 동의안함
            gps_auth = "0";
            buzzerdialog.dismiss();
            if(push_auth.equals("0")){
                pushDialog();
            }else{
                update_all_auth();
            }
        }
    };

    // 푸시 동의 확인 다이얼로그
    public void pushDialog(){
        buzzerdialog = new BuzzerDialog(Membership_finish.this,
                "푸시 사용에 동의하십니까?", "동의함", "동의안함", pushOKListener, pushNOListener);
        buzzerdialog.setCancelable(true);
        buzzerdialog.show();
    }
    private View.OnClickListener pushOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            // 푸시 동의함
            push_auth = "1";
            buzzerdialog.dismiss();
            update_all_auth();
        }
    };
    private View.OnClickListener pushNOListener = new View.OnClickListener() {
        public void onClick(View v) {
            // 푸시 동의안함
            push_auth = "0";
            buzzerdialog.dismiss();
            update_all_auth();
        }
    };
}
