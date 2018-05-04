package com.movements.and.buzzerbuzzer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.movements.and.buzzerbuzzer.Utill.Patternpwd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 7. 24..
 */
//비밀번호 변경
public class ChangePwd extends BaseActivity {

    InputMethodManager imm;
    private RelativeLayout relativeLayout4;
    private LinearLayout llll;
    private TextView tv, alert;
    private EditText currentpwd, newpwd, newpwd1;
    private TextView confirm;
    private ImageView backbtn;

    private Typeface typefaceBold, typefaceExtraBold;

    private String id, mPassword, password;

    private JsonConverter jc;
    private Patternpwd mPatternpwd;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private boolean pwdcheck = false;
    private boolean checkpwd;
    private PasswordEn passwordEn;
    private final String TAG = "ChangePwd";
    private String getMyPassword;
    private boolean checkSamePw;
    private boolean patternCheckpwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepwd);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv = (TextView)findViewById(R.id.tv);
        alert = (TextView)findViewById(R.id.alert);
        currentpwd = (EditText) findViewById(R.id.currentpwd);
        newpwd = (EditText) findViewById(R.id.newpwd);
        newpwd1 = (EditText) findViewById(R.id.newpwd1);
        confirm = (TextView) findViewById(R.id.confirm);
        backbtn = (ImageView) findViewById(R.id.backbtn);
        relativeLayout4 = (RelativeLayout) findViewById(R.id.relativeLayout4);
        llll = (LinearLayout) findViewById(R.id.llll);


        tv.setTypeface(typefaceExtraBold);
        alert.setTypeface(typefaceExtraBold);
        confirm.setTypeface(typefaceExtraBold);
        currentpwd.setTypeface(typefaceExtraBold);
        newpwd.setTypeface(typefaceExtraBold);
        newpwd1.setTypeface(typefaceExtraBold);


        jc = new JsonConverter();
        mPatternpwd = new Patternpwd();//비밀번호패턴
        passwordEn = new PasswordEn();//패스워드암호화

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        relativeLayout4.setOnClickListener(clickListener);
        tv.setOnClickListener(clickListener);
        llll.setOnClickListener(clickListener);

        loadingPassword();
        button();

    }

    private void button() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentpwd.getText().toString().length() == 0 || newpwd.getText().toString().length() == 0 || newpwd1.getText().toString().length() == 0 ) {
                    Toast.makeText(getApplicationContext(), "현재 비밀번호와 신규비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                //유경이가 추가
                if (!pwdcheck){
                    alert.setText("입력한 신규 비밀번호가 맞지 않습니다.");
                    alert.setVisibility(View.VISIBLE);
                }
                else{
                    registerUser();//비번변경업뎃
                }
            }
        });

        currentpwd.addTextChangedListener(new TextWatcher() {

            public String cPassword;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkSamePw = false;
                cPassword = passwordEn.PasswordEn(currentpwd.getText().toString().trim());
                Log.e("비번 비교",cPassword +", "+getMyPassword);
//                if(cPassword == getMyPassword){
                if(cPassword.toString().trim().equals(getMyPassword)){
                    checkSamePw = true;
                    alert.setVisibility(View.INVISIBLE);
                }
                else{
                    alert.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        newpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                patternCheckpwd = mPatternpwd.Passwrodvalidate(newpwd.getText().toString().trim());
                if(patternCheckpwd) {
                    alert.setText("사용 가능한 비밀번호 입니다.");
                    alert.setVisibility(View.INVISIBLE);
                }
                else{
                    alert.setText("알파벳 숫자 조합 8자리 이상이여야 합니다.");
                    alert.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        
        newpwd1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(newpwd.getText().toString().trim())) {
                    pwdcheck = true;
                    confirm.setEnabled(true);
                    alert.setVisibility(View.INVISIBLE);
                } else {
                    pwdcheck = false;
                    confirm.setEnabled(false);
                    alert.setText("입력한 신규 비밀번호가 맞지 않습니다.");
                    alert.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadingPassword() {
//        String cpwd = currentpwd.getText().toString().trim();//현재 비밀번호
//        final String CurrentPassword = passwordEn.PasswordEn(cpwd);
//        final String pwd = newpwd.getText().toString().trim();//신규 비밀번호
//        final String mPassword = passwordEn.PasswordEn(pwd);
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        Log.e(TAG, response);

                        JSONArray data = json.getJSONArray("data");
                        getMyPassword = data.getJSONObject(0).has("password") ? data.getJSONObject(0).getString("password") : "";
                        Log.e(TAG, getMyPassword);
                    } else {//json.getString("result").equals("fail")시
                        
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "다시 시도해주세요!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "update_pwd Error :" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_44_loading_change_password", new String[]{id}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void registerUser() {
        final String cpwd = currentpwd.getText().toString().trim();//현재 비밀번호
        final String CurrentPassword = passwordEn.PasswordEn(cpwd);
        final String pwd = newpwd.getText().toString().trim();//신규 비밀번호
        final String newPassword = passwordEn.PasswordEn(pwd);
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG+ " registerUser", response);
                // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        editor.putString("user_pw", pwd);
                        editor.commit();
                        finish();

                        Log.e(TAG + " registerUser", "이전 비번 : " + cpwd + ", 새 비번 : "  + setting.getString("user_pw",null));
                    } else {//json.getString("result").equals("fail")시
                        alert.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "비밀번호가 변경을 할 수가 없습니다. 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "다시 시도해주세요!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "update_pwd Error :" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_45_change_password", new String[]{id, newPassword}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            hideKeyboard();
            switch (v.getId())
            {
                case R.id.relativeLayout4 :
                    break;

                case R.id.tv :
                    break;
                case R.id.llll :
                    break;
            }
        }
    };

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(relativeLayout4.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(llll.getWindowToken(), 0);
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_exit, R.anim.bottom_down);
    }
}
