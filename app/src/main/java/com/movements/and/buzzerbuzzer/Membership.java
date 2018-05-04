package com.movements.and.buzzerbuzzer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.IdRes;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Adapter.CountryCodesAdapter;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Utill.Patternpwd;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by samkim on 2017. 3. 6..
 */
//회원가입-인증번호, 아이디, 이메일등등
public class Membership extends BaseActivity {
    private RadioGroup group;
    private Spinner mCountryCode;
    private EditText mPhone, id_tx, pw1_tx, pw2_tx, emailtx;
    private Button nextbtn3, confirmbtn, dupbtn, dupbtn1, confirm;
    private ImageButton confirm2, backbtn;
    private String gender1, countrynum;

    private boolean pwdcheck = false;
    private boolean idcheck, emailcheck, phonecheck;

    private JsonConverter jc;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private Patternpwd mPatternpwd;
    private boolean checkpwd, checkEmail;

    private final String TAG = "Membership";

    /**
     * Compatibility method for {@link PhoneNumberUtil#getSupportedRegions()}.
     * This was introduced because crappy Honeycomb has an old version of
     * libphonenumber, therefore Dalvik will insist on we using it.
     * In case getSupportedRegions doesn't exist, getSupportedCountries will be
     * used.
     */
    @SuppressWarnings("unchecked")
    private Set<String> getSupportedRegions(PhoneNumberUtil util) {
        try {
            return (Set<String>) util.getClass()
                    .getMethod("getSupportedRegions")
                    .invoke(util);
        } catch (NoSuchMethodException e) {
            try {
                return (Set<String>) util.getClass()
                        .getMethod("getSupportedCountries")
                        .invoke(util);
            } catch (Exception helpme) {
                // ignored
            }
        } catch (Exception e) {
            // ignored
        }
        return new HashSet<>();
    }


    private int mnTime = 1000 * 60 * 3;
    private int seconds = 0, minutes = 0;

    private CountDownTimer countDownTimer = null;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backpressedTime = 0;

    private EditText authtx;
    private PasswordEn passwordEn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);
        mCountryCode = (Spinner) findViewById(R.id.phone_cc);
        mPhone = (EditText) findViewById(R.id.newpwd);
        authtx = (EditText) findViewById(R.id.newpwd1);
        id_tx = (EditText) findViewById(R.id.id_tx);
        pw1_tx = (EditText) findViewById(R.id.pw1_tx);
        pw2_tx = (EditText) findViewById(R.id.pw2_tx);
        nextbtn3 = (Button) findViewById(R.id.nextbtn3);
        confirmbtn = (Button) findViewById(R.id.confirmbtn);
        dupbtn = (Button) findViewById(R.id.dupbtn);
        dupbtn1 = (Button) findViewById(R.id.dupbtn1);
        emailtx = (EditText) findViewById(R.id.emailtx);
        confirm = (Button) findViewById(R.id.confirm);
        group = (RadioGroup) findViewById(R.id.radioGroup);
        confirm2 = (ImageButton) findViewById(R.id.confirm2);
        backbtn = (ImageButton) findViewById(R.id.backbtn);

        jc = new JsonConverter();
        mPatternpwd = new Patternpwd();
        passwordEn = new PasswordEn();//패스워드암호화

        button();


        // populate country codes
        final CountryCodesAdapter ccList = new CountryCodesAdapter(this,
                android.R.layout.simple_list_item_1,
                android.R.layout.simple_spinner_dropdown_item);
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        Set<String> ccSet = getSupportedRegions(util);
        for (String cc : ccSet)
            ccList.add(cc);

        ccList.sort(new Comparator<CountryCodesAdapter.CountryCode>() {
            public int compare(CountryCodesAdapter.CountryCode lhs, CountryCodesAdapter.CountryCode rhs) {
                return lhs.regionName.compareTo(rhs.regionName);
            }
        });
        mCountryCode.setAdapter(ccList);
        mCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ccList.setSelected(position);
                countrynum = mCountryCode.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(), countrynum, Toast.LENGTH_SHORT).show();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        // FIXME this doesn't consider creation because of configuration change
        Phonenumber.PhoneNumber myNum = getMyNumber(this);
        if (myNum != null) {
            CountryCodesAdapter.CountryCode cc = new CountryCodesAdapter.CountryCode();
            cc.regionCode = util.getRegionCodeForNumber(myNum);
            if (cc.regionCode == null) {
                cc.regionCode = util.getRegionCodeForCountryCode(myNum.getCountryCode());
            }
            mPhone.setText(String.valueOf("0" + myNum.getNationalNumber()));//http://kanzler.tistory.com/234
            mCountryCode.setSelection(ccList.getPositionForId(cc));

        } else {
            final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            final String regionCode = tm.getSimCountryIso().toUpperCase(Locale.KOREA);
            CountryCodesAdapter.CountryCode cc = new CountryCodesAdapter.CountryCode();
            //cc.regionCode = regionCode;
            cc.regionCode = "KR";
            cc.countryCode = util.getCountryCodeForRegion(regionCode);
            mCountryCode.setSelection(ccList.getPositionForId(cc));
            if (myNum != null) {
                mPhone.setText(String.valueOf("0" + myNum.getNationalNumber()));
            }
        }

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
    }

    private void button() {

        nextbtn3.setOnClickListener(new View.OnClickListener() {//인증번호
            @Override
            public void onClick(View v) {
                if (mPhone.getText().toString().length() >= 8) {
                    if (countDownTimer == null) {
                        authphone(mPhone.getText().toString().trim());//서버 번호
                        AuthCodeTimmer();
                    } else {
                        countDownfinish();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "휴대폰 번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//휴대인증확인

                if (authtx.getText().toString().length() == 4) {
                    confirmPhoneCode(mPhone.getText().toString().trim(), authtx.getText().toString().trim());//인증확인
                } else {
                    Toast.makeText(getApplicationContext(), "인증 번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dupli_id();

            }
        });
        dupbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence cs = new StringBuffer(emailtx.getText().toString().trim());
                checkEmail = isValidEmail(cs);
                if (checkEmail) {
                    dupli_email();//이메일중복체크
                } else {
                    Toast.makeText(getApplicationContext(), "이메일 입력을 확인 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        pw2_tx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(pw1_tx.getText().toString().trim()) == false) {
                    pwdcheck = false;

                } else {
                    pw1_tx.setBackgroundColor(Color.GREEN);
                    pw2_tx.setBackgroundColor(Color.GREEN);
                    pwdcheck = true;
                    checkpwd = mPatternpwd.Passwrodvalidate(pw1_tx.getText().toString().trim());
                    if (!checkpwd) {
                        Toast.makeText(Membership.this, "영문+숫자 또는 특수문자 6자 이상 12자 이하여야 합니다.", Toast.LENGTH_LONG).show();
                        pwdcheck = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {//성별체크
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_men:
                        gender1 = "m";
                        break;
                    case R.id.rb_women:
                        gender1 = "w";
                        break;
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkall();

            }
        });
        confirm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkall();
            }

        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void countDownfinish() {
        if (countDownTimer != null) {
            countDownTimer.onFinish();
            Log.d("resultcode", "  :" + countDownTimer);
        }
    }


    private void checkall() {
        if (mPhone.getText().toString().length() == 0 || mPhone == null) {
            Toast.makeText(getApplicationContext(), "휴대폰 번호를 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phonecheck) {
            Toast.makeText(getApplicationContext(), "휴대폰 인증해주세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (id_tx.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "ID을 입력하세요!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!idcheck) {
            Toast.makeText(getApplicationContext(), "ID을 중복확인해 주세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pw2_tx.getText().toString().trim().length() == 0 || !pwdcheck) {
            Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (emailtx.getText().toString().length() == 0 || emailcheck == false) {
            Toast.makeText(getApplicationContext(), "이메일 중복확인 및 입력 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (gender1 == null) {
            Toast.makeText(getApplicationContext(), "성별을 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        registerUser();//회원등록
    }

    private void confirmPhoneCode(final String phone, final String getcode) {
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                String resultcode;
                try {
                    JSONObject json = new JSONObject(response);

                    if (json.getString("result").equals("success")) {//성공시
                        JSONArray data = json.getJSONArray("data");
                        //   Log.d("code_result12",""+data);
                        JSONObject row = null;
                        row = data.getJSONObject(0);
                        //Log.d("code_result13",""+row);
                        resultcode = row.getString("code_result");
                        if (resultcode.equals("success")) {
                            countDownfinish();
                            Toast.makeText(getApplicationContext(), "인증번호가 확인되었습니다.", Toast.LENGTH_SHORT).show();
                            phonecheck = true;
                        } else {
                            Toast.makeText(getApplicationContext(), "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "인증 전송실패", Toast.LENGTH_LONG).show();
                Log.e(TAG, " surem_confirm Error" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam("surem_confirm", new String[]{getcode, phone}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void authphone(String phonenum) {
        final String phone = phonenum;
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject json = new JSONObject(response);

                    if (json.getString("result").equals("success")) {//성공시

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "휴대번호 전송실패", Toast.LENGTH_LONG).show();
                Log.e(TAG, "surem Error" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam("surem", new String[]{phone}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private boolean isValidEmail(CharSequence cs) {
        return !TextUtils.isEmpty(cs) && android.util.Patterns.EMAIL_ADDRESS.matcher(cs).matches();
    }

    private void dupli_email() {
        final String dupli_email = emailtx.getText().toString().trim();
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {
                    Toast.makeText(getApplicationContext(),"dupli_email 확인",Toast.LENGTH_SHORT).show();
                    JSONObject json = new JSONObject(response);

                    if (json.getString("result").equals("success")) {//성공시
                        JSONArray data = json.getJSONArray("data");
                        JSONObject row = null;
                        row = data.getJSONObject(0);
                        int k = row.getInt("duplicated_id");
                        if (k == 1) {
                            Toast.makeText(getApplicationContext(), "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(getApplicationContext(), "사용가능합니다", Toast.LENGTH_SHORT).show();
                            emailcheck = true;
                        }

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
                params.put("param", jc.createJsonParam("check_dup_email", new String[]{dupli_email}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }


    /**
     * Returns the (parsed) number stored in this device SIM card.
     */
    @SuppressLint("HardwareIds")
    public Phonenumber.PhoneNumber getMyNumber(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String regionCode = tm.getSimCountryIso().toUpperCase(Locale.KOREA);
            String num = tm.getLine1Number();
            num = num.replace("+82", "0");
            // Log.d("번호",num);
            //return PhoneNumberUtil.getInstance().parse(tm.getLine1Number(), regionCode);
            return PhoneNumberUtil.getInstance().parse(num, regionCode);
        } catch (Exception e) {
            return null;
        }
    }

    public void AuthCodeTimmer() {
        //value = 61;
        int delay = mnTime;
        countDownTimer = new CountDownTimer(delay, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                seconds = (int) (millisUntilFinished / 1000) % 60;
                minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                //int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                nextbtn3.setText(String.valueOf(String.format("%d:%02d", minutes, seconds)));
                //nextbtn3.setText(Integer.toString(value));
            }

            @Override
            public void onFinish() {
                nextbtn3.setText(getString(R.string.next));
                countDownTimer.cancel();
                countDownTimer = null;
            }
        };
        countDownTimer.start();
    }


    private void dupli_id() {
        final String dublecheckid = id_tx.getText().toString().trim();
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {
                    Toast.makeText(getApplicationContext(),"dupli_id 확인",Toast.LENGTH_SHORT).show();
                    JSONObject json = new JSONObject(response);

                    if (json.getString("result").equals("success")) {//성공시
                        JSONArray data = json.getJSONArray("data");
                        JSONObject row = null;
                        row = data.getJSONObject(0);
                        int k = row.getInt("duplicated_id");
                        if (k == 1) {
                            Toast.makeText(getApplicationContext(), "이미 존재하는 ID입니다.", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(getApplicationContext(), "사용가능합니다", Toast.LENGTH_SHORT).show();
                            idcheck = true;
                        }

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
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam("check_dup_id", new String[]{dublecheckid}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void registerUser() {
        final String phonenum = mPhone.getText().toString().trim();
        final String id = id_tx.getText().toString().trim();
        final String password = pw1_tx.getText().toString().trim();
        final String mPassword = passwordEn.PasswordEn(password);
        final String email = emailtx.getText().toString().trim();
        final String gender = gender1;

        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        Toast.makeText(getApplicationContext(), "가입 성공하였습니다.", Toast.LENGTH_LONG).show();
                        Intent inLogin = new Intent(Membership.this, ProfileSetting.class);
                        inLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        inLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        inLogin.putExtra("gps_base_check", false);
                        editor.putString("user_id", id);
                        editor.putString("user_pw", password);
                        editor.putBoolean("Auto_Login_enabled", true);
                        editor.commit();
                        startActivity(inLogin);
                    } else {//json.getString("result").equals("fail")시
                        Toast.makeText(getApplicationContext(), "회원가입 실패!!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "회원가입 실패하였습니다. 인터넷 확인해주세요!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "membership Error" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam("membership", new String[]{id, mPassword, email, phonenum, gender, "1", "1", "1", ""}));
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
            Toast.makeText(getApplicationContext(), "한번 더 뒤로가기를 누르면 로그인화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            countDownTimer.cancel();
        } catch (Exception e) {
        }
        countDownTimer = null;
    }

}
