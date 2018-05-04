package com.movements.and.buzzerbuzzer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorRes;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Adapter.CountryCodesAdapter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
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

//휴대폰 변경
public class ChangePhonenum extends BaseActivity {

    private final String TAG = "ChangePhonenum";

    private Spinner mCountryCode;
    private TextView tv, alert, timeattack, confirm;
    private EditText mPhone;
    private static EditText authtx;
    private Boolean authcheck = false;
    private Button nextbtn3, confirmbtn;
    private ImageView backbtn;

    private Typeface typefaceBold, typefaceExtraBold;

    private JsonConverter jc;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password;
    private boolean phonecheck = false;

    private int mnTime = 1000 * 60 * 3;

    private CountDownTimer countDownTimer;
    private String getMyPhone;

    private ConfirmDialog dialog;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changephone);


        mCountryCode = (Spinner) findViewById(R.id.phone_cc);
        mPhone = (EditText) findViewById(R.id.mPhone);
        authtx = (EditText) findViewById(R.id.authtx);

        nextbtn3 = (Button) findViewById(R.id.nextbtn3);
        confirmbtn = (Button) findViewById(R.id.confirmbtn);
        backbtn = (ImageView) findViewById(R.id.backbtn);
        confirm = (TextView) findViewById(R.id.confirm);
        tv = (TextView) findViewById(R.id.tv);
        timeattack = (TextView)findViewById(R.id.timeattack);
        alert = (TextView)findViewById(R.id.alert);


        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        mPhone.setTypeface(typefaceExtraBold);
        tv.setTypeface(typefaceExtraBold);
        authtx.setTypeface(typefaceExtraBold);
        nextbtn3.setTypeface(typefaceExtraBold);
        confirmbtn.setTypeface(typefaceExtraBold);
        confirm.setTypeface(typefaceExtraBold);
        alert.setTypeface(typefaceExtraBold);
        timeattack.setTypeface(typefaceExtraBold);
        timeattack.bringToFront();



        jc = new JsonConverter();
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        authtx.setEnabled(false);
        confirmbtn.setTextColor(Color.rgb(218,218,218));
        confirmbtn.setBackground(getResources().getDrawable(R.drawable.smallbotton_sub_dim));
        confirmbtn.setEnabled(false);

        loadingPhoneNum();

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
        mCountryCode.setEnabled(false);
        mCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ccList.setSelected(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        // FIXME this doesn't consider creation because of configuration change
        Phonenumber.PhoneNumber myNum = getMyNumber(this);
        if (myNum != null) {//폰있을때
//            Log.d("로그", String.valueOf(myNum)); //Country Code: 82 National Number:
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
            cc.regionCode = "KR";
            cc.countryCode = util.getCountryCodeForRegion(regionCode);
            mCountryCode.setSelection(ccList.getPositionForId(cc));
            if (myNum != null) {
                mPhone.setText(String.valueOf("0" + myNum.getNationalNumber()));
            }
        }



        button();
    }

    private void button() {

        nextbtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//휴대번호입력 다음버튼
                if (mPhone.getText().toString().length() >= 8) {
                    if (countDownTimer == null) {
                        authphone(mPhone.getText().toString().trim());//전화번호 서버에 보냄
                        AuthCodeTimmer();//타이머
                    } else {
                        countDownfinish();
                    }
                } else {
                    dialog = new ConfirmDialog(ChangePhonenum.this,
                            "휴대폰 번호를 확인해주세요.", "확인");
                    dialog.setCancelable(true);
                    dialog.show();
                }

            }
        });
        confirmbtn.setOnClickListener(new View.OnClickListener() {//인증번호 확인
            @Override
            public void onClick(View v) {
                if (authtx.getText().toString().length() == 4) {
                    confirmPhoneCode(mPhone.getText().toString().trim(), authtx.getText().toString().trim());
                } else {
                    dialog = new ConfirmDialog(ChangePhonenum.this,
                            "인증번호를 확인해주세요.", "확인");
                    dialog.setCancelable(true);
                    dialog.show();
                }
            }
        });


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//전화번호변경
                if (phonecheck) {
                    phonenumupdate();

                } else {
                    dialog = new ConfirmDialog(ChangePhonenum.this,
                            "휴대폰 인증해 주세요.", "확인");
                    dialog.setCancelable(true);
                    dialog.show();
                    return;
                }
            }
        });

    }

    public void AuthCodeTimmer() {
        //value = 61;
        int delay = mnTime;
        countDownTimer = new CountDownTimer(delay, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                //int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                timeattack.setText(String.valueOf(String.format("%d:%02d", minutes, seconds)));
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                countDownTimer = null;
                if(!authcheck){

                    dialog = new ConfirmDialog(ChangePhonenum.this,
                            "인증시간이 지났습니다.\n인증번호를 다시 받아주세요.", "확인");
                    dialog.setCancelable(true);
                    dialog.show();

                    mPhone.setEnabled(true);
                    nextbtn3.setEnabled(true);
                    nextbtn3.setTextColor(Color.rgb(125,81,252));
                    nextbtn3.setBackground(getResources().getDrawable(R.drawable.smallbotton_sub));
                    mPhone.setTextColor(Color.rgb(0,0,0));
                    timeattack.setVisibility(View.GONE);
                    authtx.setTextColor(Color.rgb(218,218,218));
                    alert.setVisibility(View.INVISIBLE);
                    authtx.setText("");
                    authtx.setEnabled(false);
                    confirmbtn.setTextColor(Color.rgb(218,218,218));
                    confirmbtn.setBackground(getResources().getDrawable(R.drawable.smallbotton_sub_dim));
                    confirmbtn.setEnabled(false);
                }
            }
        };
        countDownTimer.start();
    }

    private void countDownfinish() {
        if (countDownTimer != null) {
            countDownTimer.onFinish();
//            nextbtn3.setText(getString(R.string.next));
            Log.d("resultcode", "  :" + countDownTimer);
        }
    }

    private void loadingPhoneNum() {
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
                        getMyPhone = data.getJSONObject(0).has("phone") ? data.getJSONObject(0).getString("phone") : "";
                        Log.e(TAG, getMyPhone);
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
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_46_loading_change_password", new String[]{id}));
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
                        mPhone.setEnabled(false);
                        nextbtn3.setEnabled(false);
                        nextbtn3.setTextColor(Color.rgb(218,218,218));
                        nextbtn3.setBackground(getResources().getDrawable(R.drawable.smallbotton_sub_dim));
                        confirmbtn.setTextColor(Color.rgb(125,81,252));
                        confirmbtn.setBackground(getResources().getDrawable(R.drawable.smallbotton_sub));
                        confirmbtn.setEnabled(true);
                        mPhone.setTextColor(Color.rgb(218,218,218));
                        timeattack.setVisibility(View.VISIBLE);
                        authtx.setEnabled(true);
                        authtx.setTextColor(Color.rgb(0,0,0));
                    }else if (json.getString("result").equals("fail")) {//중복시
                        dialog = new ConfirmDialog(ChangePhonenum.this,
                                "확인 후 다시 시도해주세요.", "확인");
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
//                Toast.makeText(getApplicationContext(), "다시 시도해주세요", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_07_request_auth_number", new String[]{phone}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

    private void confirmPhoneCode(final String phone, final String getcode) {
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG + "confirmPhoneCode" ,response);
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                String resultcode;
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        JSONObject row = null;
                        resultcode = data.getJSONObject(0).getString("code_result");
                        if (resultcode.equals("success")) {
                            authcheck = true;
                            countDownfinish();
                            mPhone.setEnabled(false);
                            mPhone.setTextColor(Color.rgb(218,218,218));
                            authtx.setEnabled(false);
                            authtx.setTextColor(Color.rgb(218,218,218));
                            confirm.setEnabled(true);
                            confirm.setTextColor(getResources().getColor(R.color.colorMain));
                            confirmbtn.setTextColor(Color.rgb(218,218,218));
                            confirmbtn.setBackground(getResources().getDrawable(R.drawable.smallbotton_sub_dim));
                            confirmbtn.setEnabled(false);

                            alert.setVisibility(View.INVISIBLE);
                            phonecheck = true;
                            dialog = new ConfirmDialog(ChangePhonenum.this,
                                    "인증이 완료되었습니다.\n우측 상단 확인을 눌러주세요.", "확인");
                            dialog.setCancelable(true);
                            dialog.show();
                        }
                    }else{
                        //Toast.makeText(getApplicationContext(), "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        /*
                        new AlertDialog.Builder(ChangePhonenum.this)
                                .setMessage("인증 번호가 일치하지 않습니다.")
                                .setPositiveButton("확인", null)
                                .show(); */
                        alert.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "인증 전송실패 다시 시도해주세요.", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_08_check_auth_number", new String[]{phone, getcode}));
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
            return PhoneNumberUtil.getInstance().parse(tm.getLine1Number(), regionCode);
        } catch (Exception e) {
            return null;
        }
    }


    private void phonenumupdate() {
        final String phonenum = mPhone.getText().toString().trim();
        final String auth = String.valueOf(1);
        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        //Toast.makeText(getApplicationContext(), "휴대폰번호 변경이 완료되었습니다", Toast.LENGTH_LONG).show();
                        /*
                        new AlertDialog.Builder(ChangePhonenum.this)
                                .setMessage("휴대폰번호 변경이 완료되었습니다.")
                                .setPositiveButton("확인", null)
                                .show(); */
                        finish();

                    } else {//json.getString("result").equals("fail")시
                        Toast.makeText(getApplicationContext(), "실패하였습니다.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "다시 시도 해주세요.", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_47_change_number", new String[]{id, phonenum, auth}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
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
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_exit, R.anim.bottom_down);
    }

    // SMSReceiver에서 접근해야하기 때문에 static
    public static void inputAuthNumber(String authNumber) {
        Log.e("체인지폰넘", "인증번호 받아서 들어옴");
        if (authNumber != null) {
            // editTextInputNumber에 받아온 인증번호를 입력
            Log.e("체인지폰넘", "인증번호 자동입력");
            authtx.setText(authNumber);
        }
    }

}
