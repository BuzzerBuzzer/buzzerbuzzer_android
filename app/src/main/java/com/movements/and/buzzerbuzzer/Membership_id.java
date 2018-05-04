package com.movements.and.buzzerbuzzer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.Utill.Patternpwd;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Membership_id extends BaseActivity {

    InputMethodManager imm;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private final String TAG = "Membership";
    private JsonConverter jc;
    private TextView tv_id_pw, tv_id;
    private Button chk_btn;
    private ImageView btn_back, nextbtn, delete_btn;
    private EditText id_tx;

    private Typeface typefaceBold, typefaceExtraBold;

    private boolean idcheck;
    private long backpressedTime = 0;
    private boolean id_chk;
    private Patternpwd mPatternpwd;

    private final long FINISH_INTERVAL_TIME = 2000;
    private RelativeLayout rl;
    private String id, mPassword, password;

    private ConfirmDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_id);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        passwordEn = new PasswordEn();//패스워드암호화

        rl = (RelativeLayout)findViewById(R.id.rl);
        tv_id_pw = (TextView)findViewById(R.id.tv_id_pw);
        tv_id = (TextView)findViewById(R.id.tv_id);
        chk_btn = (Button)findViewById(R.id.chk_btn);
        btn_back = (ImageView)findViewById(R.id.btn_back);
        nextbtn = (ImageView) findViewById(R.id.nextbtn);
        delete_btn = (ImageView)findViewById(R.id.delete_btn);
        id_tx = (EditText)findViewById(R.id.id_tx);
        id_tx.setText(LoginActivity.id_temp);
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv_id_pw.setTypeface(typefaceExtraBold);
        tv_id.setTypeface(typefaceBold);
        chk_btn.setTypeface(typefaceExtraBold);
        id_tx.setTypeface(typefaceExtraBold);

        rl.setOnClickListener(clickListener);
        tv_id.setOnClickListener(clickListener);

        id = setting.getString("user_id","");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        jc = new JsonConverter();
        button();

        chk_btn.setBackgroundResource(R.drawable.largebutton_sub_dim);
        chk_btn.setTextColor(getApplication().getResources().getColor(R.color.grey));
        chk_btn.setEnabled(false);
        chk_btn.setText("중복확인");

        mPatternpwd = new Patternpwd();


    }


    private void dupli_id() {

        final String dublecheckid = id_tx.getText().toString().trim();


        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {

                    JSONObject json = new JSONObject(response);
                    Log.d(TAG +" 93 : ", response);

                    if (json.getString("result").equals("success")) {//성공시

                        String msg = json.getString("msg");

                        idcheck = true;

                        //중복확인 버튼 색, 문구, 테두리 변경, 비활성화
                        chk_btn.setBackgroundResource(R.drawable.largebutton_sub_dim);
                        chk_btn.setTextColor(getApplication().getResources().getColor(R.color.grey));
                        chk_btn.setText("사용 가능한 아이디 입니다.");
                        chk_btn.setEnabled(false);

                        //다음 페이지 이미지(버튼) 변경
                        nextbtn.setImageResource(R.drawable.signup_next);
                        imm.hideSoftInputFromWindow(rl.getWindowToken(), 0);
                    }else {
                        dialog = new ConfirmDialog(Membership_id.this,
                                "이미 존재하는 ID 입니다.", "확인");
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
                // Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "check_dup_id Error" + error);
                dialog = new ConfirmDialog(Membership_id.this,
                        "인터넷 연결 상태를\n확인해주세요.", "확인");
                dialog.setCancelable(true);
                dialog.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.MODEL, Build.VERSION.RELEASE,"proc_06_check_id", new String[]{dublecheckid}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }


    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backpressedTime;
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backpressedTime = tempTime;
            // Toast.makeText(getApplicationContext(), "한번 더 뒤로가기를 누르면 로그인화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void button() {

        //아이디 중복 체크
        chk_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id_tx.getText().toString().trim().length() == 0) {
                    // Toast.makeText(getApplicationContext(), "ID을 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }
                dupli_id();         //중복 검사
            }
        });

        //뒤로가기
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Membership_info.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.side_on2, R.anim.side_off2);
            }
        });

        //다음페이지(비밀번호 입력 페이지)
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!idcheck) {
                    // Toast.makeText(getApplicationContext(), "ID을 중복확인해 주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginActivity.id_temp = id_tx.getText().toString().trim();
                Intent intent = new Intent(getApplicationContext(), Membership_pw.class);
                intent.putExtra("ID", LoginActivity.id_temp);
                startActivityForResult(intent, 82);
                finish();
                overridePendingTransition(R.anim.side_on, R.anim.side_off);

            }
        });

        //아이디 에디터 삭제
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_tx.setText(null);

                idcheck = false;

                //중복확인 버튼 색, 문구, 테두리 변경, 비활성화
                chk_btn.setBackgroundResource(R.drawable.largebutton_sub);
                chk_btn.setTextColor(getApplication().getResources().getColor(R.color.colorMain));
                chk_btn.setEnabled(true);
                chk_btn.setText("중복확인");

                //다음 페이지 이미지(버튼) 변경
                nextbtn.setImageResource(R.drawable.signup_next_dim);
            }
        });

        id_tx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //맞는 비밀번호 패턴일 경우 넥스트버튼을 다시 활성화시킴
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Pattern.matches("^[0-9]+$", id_tx.getText().toString().trim())){
                    try{
                        dialog = new ConfirmDialog(Membership_id.this,
                                "아이디는 영문자로 시작해 주세요.", "확인");
                        dialog.setCancelable(true);
                        dialog.show();
                    }catch (NullPointerException e){

                    }
                }
                id_chk = mPatternpwd.Idvalidate(id_tx.getText().toString().trim());
                if(id_chk) {
                    chk_btn.setBackgroundResource(R.drawable.largebutton_sub);
                    chk_btn.setTextColor(getApplication().getResources().getColor(R.color.colorMain));
                    chk_btn.setEnabled(true);
                    chk_btn.setText("중복확인");
                }
                else{
                    chk_btn.setBackgroundResource(R.drawable.largebutton_sub_dim);
                    chk_btn.setTextColor(getApplication().getResources().getColor(R.color.grey));
                    chk_btn.setEnabled(false);
                    chk_btn.setText("중복확인");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public void onStop(){
        super.onStop();


    }
    public void onRestart(){
        super.onRestart();
    }

    View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            hideKeyboard();
            switch (v.getId())
            {
                case R.id.rl :
                    break;

                case R.id.tv_id :
                    break;
            }
        }
    };

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(rl.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tv_id.getWindowToken(), 0);
    }

}
