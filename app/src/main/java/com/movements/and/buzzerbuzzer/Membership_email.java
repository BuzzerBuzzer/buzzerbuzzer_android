package com.movements.and.buzzerbuzzer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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

public class Membership_email extends BaseActivity {

    InputMethodManager imm;

    private TextView tv_email_main, tv_email, pwrule, again, next, next2;
    private ImageView btn_back, nextbtn;
    private EditText email_tx;
    private Button chk_btn;

    private Typeface typefaceBold, typefaceExtraBold;

    private boolean checkEmail;
    private boolean emailcheck;
    private boolean emailregex;
    private JsonConverter jc;

    private final String TAG = "Membership";
    private RelativeLayout emailrl;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password, email;
    private ConfirmDialog dialog;

    private Patternpwd mPatternpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_email);

        emailrl = (RelativeLayout)findViewById(R.id.emailrl);
        tv_email_main = (TextView)findViewById(R.id.tv_email_main);
        tv_email = (TextView)findViewById(R.id.tv_email);
        pwrule = (TextView)findViewById(R.id.pwrule);
        again = (TextView)findViewById(R.id.again);
        next = (TextView)findViewById(R.id.next);
        next2 = (TextView)findViewById(R.id.next2);

        btn_back = (ImageView)findViewById(R.id.btn_back);
        nextbtn = (ImageView)findViewById(R.id.nextbtn);

        email_tx = (EditText)findViewById(R.id.email_tx);
        email_tx.setText(LoginActivity.email_temp);
        chk_btn = (Button)findViewById(R.id.chk_btn);


        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv_email_main.setTypeface(typefaceExtraBold);
        tv_email.setTypeface(typefaceBold);
        pwrule.setTypeface(typefaceBold);
        again.setTypeface(typefaceBold);
        next.setTypeface(typefaceBold);
        next2.setTypeface(typefaceBold);
        email_tx.setTypeface(typefaceExtraBold);
        chk_btn.setTypeface(typefaceExtraBold);

        //TODO 나중에 주석 제거
        nextbtn.setEnabled(false);

        emailrl.setOnClickListener(clickListener);
        tv_email.setOnClickListener(clickListener);
        pwrule.setOnClickListener(clickListener);
        again.setOnClickListener(clickListener);

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();

        jc = new JsonConverter();

        id = setting.getString("user_id","");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);


        chk_btn.setBackgroundResource(R.drawable.largebutton_sub_dim);
        chk_btn.setTextColor(getApplication().getResources().getColor(R.color.grey));
        chk_btn.setEnabled(false);

        button();
    }

    private boolean isValidEmail(CharSequence cs) {
        return !TextUtils.isEmpty(cs) && android.util.Patterns.EMAIL_ADDRESS.matcher(cs).matches();
    }

    private void dupli_email() {
        final String dupli_email = email_tx.getText().toString().trim();
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject json = new JSONObject(response);

                    if (json.getString("result").equals("success")) {//성공시
//                        JSONArray data = json.getJSONArray("data");
//                        JSONObject row = null;
//                        row = data.getJSONObject(0);
//                        int k = row.getInt("duplicated_id");
//                        if (k == 1) {
//                            //Toast.makeText(getApplicationContext(), "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show();
//                            again.setVisibility(View.VISIBLE);
//                        } else {
//                            again.setVisibility(View.INVISIBLE);
//                            Toast.makeText(getApplicationContext(), "사용가능합니다", Toast.LENGTH_SHORT).show();
//                            emailcheck = true;
//                            nextbtn.setImageResource(R.drawable.signup_next);
//                            nextbtn.setEnabled(true);
//                        }

                        if(json.getString("msg").equals("Email is available")){
                            again.setVisibility(View.INVISIBLE);
                            //Toast.makeText(getApplicationContext(), "사용가능합니다", Toast.LENGTH_SHORT).show();
                            emailcheck = true;

                            //중복확인 버튼 색, 문구, 테두리 변경, 비활성화
                            chk_btn.setBackgroundResource(R.drawable.largebutton_sub_dim);
                            chk_btn.setTextColor(getApplication().getResources().getColor(R.color.grey));
//                            chk_btn.setTextColor(Color.parseColor("#eaeaea"));
                            chk_btn.setText("사용가능한 이메일 입니다.");
                            chk_btn.setEnabled(false);

                            nextbtn.setImageResource(R.drawable.signup_next);
                            nextbtn.setEnabled(true);
                            email = email_tx.getText().toString().trim();
                            imm.hideSoftInputFromWindow(chk_btn.getWindowToken(), 0);
                        } else {
//                            again.setVisibility(View.VISIBLE);
//                            Toast.makeText(getApplicationContext(), "중복된 이메일입니다.", Toast.LENGTH_SHORT).show();
//                            emailcheck = false;
//                            nextbtn.setImageResource(R.drawable.signup_next_dim);
//                            nextbtn.setEnabled(false);
                        }

                    }else
                    {
                        again.setVisibility(View.VISIBLE);
                        //Toast.makeText(getApplicationContext(), "중복된 이메일입니다.", Toast.LENGTH_SHORT).show();
                        emailcheck = false;
                        nextbtn.setImageResource(R.drawable.signup_next_dim);
                        nextbtn.setEnabled(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "check_dup_email Error" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("param", jc.createJsonParam("check_dup_email", new String[]{dupli_email}));
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_09_check_email", new String[]{dupli_email}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }



    public void button(){

        //이전 페이지로 이동
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Membership_phone.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.side_on2, R.anim.side_off2);
            }
        });

        //다음 성별 선택 화면
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.email_temp = email_tx.getText().toString().trim();
                Intent getIntent = getIntent();
                //Log.i("ID : ",getIntent.getStringExtra("ID"));
                Intent intent = new Intent(getApplicationContext(), Membership_gender.class);
                intent.putExtra("ID",LoginActivity.id_temp);
                intent.putExtra("PW", LoginActivity.pw_temp);
                intent.putExtra("PHONE",LoginActivity.phone_temp);
                intent.putExtra("EMAIL", LoginActivity.email_temp);
                startActivityForResult(intent, 82);
                finish();
                overridePendingTransition(R.anim.side_on, R.anim.side_off);
            }
        });


        //이메일 확인 요청 버튼
        chk_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence cs = new StringBuffer(email_tx.getText().toString().trim());
                checkEmail = isValidEmail(cs);  //이메일 형식이 맞는지
                if (checkEmail) {
                    dupli_email();//이메일중복체크
                } else {
                    //Toast.makeText(getApplicationContext(), "이메일 입력을 확인 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        email_tx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //맞는 비밀번호 패턴일 경우 넥스트버튼을 다시 활성화시킴
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //emailregex = mPatternpwd.Emailvalidate(email_tx.getText().toString().trim());
                //if(Pattern.matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", email_tx.getText().toString().trim())){
                if(android.util.Patterns.EMAIL_ADDRESS.matcher(email_tx.getText().toString().trim()).matches()){
                    chk_btn.setBackgroundResource(R.drawable.largebutton_sub);
                    chk_btn.setTextColor(getApplication().getResources().getColor(R.color.colorMain));
                    chk_btn.setEnabled(true);
                }
                else{
                    chk_btn.setBackgroundResource(R.drawable.largebutton_sub_dim);
                    chk_btn.setTextColor(getApplication().getResources().getColor(R.color.grey));
                    chk_btn.setEnabled(false);
                }

                final float scale = getResources().getDisplayMetrics().density;
                if(email_tx.getText().toString().length() >= 15)
                    email_tx.setTextSize(20); // 글자 수 15 이상이면 20dp 로 바꿈

                if(emailcheck){
                    if(!s.toString().trim().equals(email)){
                        emailcheck = false;
                        nextbtn.setImageResource(R.drawable.signup_next_dim);
                        nextbtn.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            hideKeyboard();
            switch (v.getId())
            {
                case R.id.emailrl :
                    break;

                case R.id.tv_email :
                    break;

                case R.id.pwrule :
                    break;

                case R.id.again :
                    break;
            }
        }
    };

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(emailrl.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tv_email.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(pwrule.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(again.getWindowToken(), 0);
    }

}
