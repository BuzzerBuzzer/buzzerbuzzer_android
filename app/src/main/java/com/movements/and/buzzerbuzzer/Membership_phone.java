package com.movements.and.buzzerbuzzer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.movements.and.buzzerbuzzer.Adapter.CountryCodesAdapter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

//폰 번호 인증번호
public class Membership_phone extends BaseActivity {

    InputMethodManager imm;

    private TextView tv_phone, tv_chknumber, timeattack,wrong_number, complete_join;
    private ImageView nextbtn, btn_back;
    private Button chk_btn, complete_chk;
    private EditText phone_tx;
    private Boolean authcheck = false;
    private Typeface typefaceBold, typefaceExtraBold;
    private static EditText idnumber_tx;

    private Spinner mCountryCode;
    private String countrynum;
    private JsonConverter jc;

    private CountDownTimer countDownTimer = null;
    private int mnTime = 1000 * 60 * 3;
    private int seconds = 0, minutes = 0;
    private boolean phonecheck;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;

    private PasswordEn passwordEn;

    private final String TAG = "Membership";
    private RelativeLayout phonerl;

    private String id, mPassword, password;
    private ConfirmDialog dialog;

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
        setContentView(R.layout.activity_membership_phone);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        tv_phone = (TextView)findViewById(R.id.tv_phone);
        tv_chknumber = (TextView)findViewById(R.id.tv_chknumber);
        timeattack = (TextView)findViewById(R.id.timeattack);
        wrong_number = (TextView)findViewById(R.id.wrong_number);
        complete_join = (TextView)findViewById(R.id.complete_join);
        phonerl = (RelativeLayout)findViewById(R.id.phonerl);

        chk_btn = (Button)findViewById(R.id.chk_btn);
        complete_chk = (Button) findViewById(R.id.complete_chk);

        nextbtn = (ImageView)findViewById(R.id.nextbtn);
        btn_back = (ImageView)findViewById(R.id.btn_back);

        phone_tx = (EditText)findViewById(R.id.phone_tx);
        idnumber_tx = (EditText)findViewById(R.id.idnumber_tx);

        //스피너
        mCountryCode = (Spinner)findViewById(R.id.phone_cc);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv_phone.setTypeface(typefaceExtraBold);
        phone_tx.setTypeface(typefaceExtraBold);
        idnumber_tx.setTypeface(typefaceExtraBold);
        chk_btn.setTypeface(typefaceExtraBold);
        complete_chk.setTypeface(typefaceExtraBold);

        tv_chknumber.setTypeface(typefaceBold);
        timeattack.setTypeface(typefaceBold);
        wrong_number.setTypeface(typefaceBold);
        complete_join.setTypeface(typefaceBold);

        tv_chknumber.setOnClickListener(clickListener);
        phonerl.setOnClickListener(clickListener);
        chk_btn.setOnClickListener(clickListener);


        //TODO:임시로 그냥 넘기는것 이거 배포알땐 풀어야됨.
        nextbtn.setEnabled(false);
        jc = new JsonConverter();

        passwordEn = new PasswordEn();

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
        mCountryCode.setEnabled(false);
        mCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ccList.setSelected(position);
                countrynum = mCountryCode.getSelectedItem().toString();
//                Toast.makeText(getApplicationContext(), countrynum, Toast.LENGTH_SHORT).show();
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
            phone_tx.setText(String.valueOf("0" + myNum.getNationalNumber()));//http://kanzler.tistory.com/234
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
                phone_tx.setText(String.valueOf("0" + myNum.getNationalNumber()));
            }
        }

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        id = setting.getString("user_id","");
        password = setting.getString("user_pw","");
        if (password == null) {
            mPassword = password;
        }else {
            mPassword = passwordEn.PasswordEn(password);
        }
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
                        Log.i("code_result12",""+data);
                        JSONObject row = null;
                        row = data.getJSONObject(0);
                        Log.i("code_result13",""+row);
                        resultcode = row.getString("code_result");
                        if (resultcode.equals("success")) {
                            authcheck = true;
                            countDownfinish();
                            //Toast.makeText(getApplicationContext(), "인증번호가 확인되었습니다.", Toast.LENGTH_SHORT).show();
                            tv_chknumber.setText("인증번호가 확인되었습니다.");
                            phonecheck = true;
                            nextbtn.setEnabled(true);
                            nextbtn.setImageResource(R.drawable.signup_next);
                            idnumber_tx.setEnabled(false);
                            idnumber_tx.setTextColor(getApplication().getResources().getColor(R.color.grey));
                            complete_chk.setBackgroundResource(R.drawable.largebutton_sub_dim);
                            complete_chk.setEnabled(false);
                            complete_chk.setTextColor(getApplication().getResources().getColor(R.color.grey));
                            wrong_number.setVisibility(View.INVISIBLE);
                        } else {
                            Log.e("인증번호","틀렸음");
                            wrong_number.setVisibility(View.VISIBLE);
                            //Toast.makeText(getApplicationContext(), "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();

                            dialog = new ConfirmDialog(Membership_phone.this,
                                    "인증 번호가 일치하지 않습니다.", "확인");
                            dialog.setCancelable(true);
                            dialog.show();
                            /*
                            new AlertDialog.Builder(Membership_phone.this)
                                    .setMessage("인증 번호가 일치하지 않습니다.")
                                    .setPositiveButton("확인", null)
                                    .show();  */
                        }
                    }else{
                        wrong_number.setVisibility(View.VISIBLE);
                        //Toast.makeText(getApplicationContext(), "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        /*
                        new AlertDialog.Builder(Membership_phone.this)
                                .setMessage("인증 번호가 일치하지 않습니다.")
                                .setPositiveButton("확인", null)
                                .show();  */
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
//                params.put("param", jc.createJsonParam("surem_confirm", new String[]{getcode, phone}));
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_08_check_auth_number", new String[]{phone, getcode}));
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
                Log.d(TAG+" authCheck", response);
                try {
                    JSONObject json = new JSONObject(response);

                    if (json.getString("result").equals("success")) {//성공시
                        AuthCodeTimmer();   //내 생각에 이것은 타이머
                        //인증번호 전송 성공
                        chk_btn.setBackgroundResource(R.drawable.largebutton_sub_dim);  //전송버튼 색 변경
                        chk_btn.setEnabled(false);
                        chk_btn.setText("인증번호가 전송되었습니다.");
                        chk_btn.setTextColor(getApplication().getResources().getColor(R.color.grey));
                        phone_tx.setTextColor(getApplication().getResources().getColor(R.color.grey));
                        phone_tx.setEnabled(false);
                        //chk_btn.setTextColor(R.color.grey);
                        tv_chknumber.setVisibility(View.VISIBLE);
                        complete_chk.setVisibility(View.VISIBLE);
                        complete_chk.setEnabled(true);
                        idnumber_tx.setEnabled(true);
                        idnumber_tx.setVisibility(View.VISIBLE);
                        timeattack.setVisibility(View.VISIBLE);
                        idnumber_tx.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }else if (json.getString("result").equals("fail")) {//중복시

                        dialog = new ConfirmDialog(Membership_phone.this,
                                "중복된 번호입니다.", "확인");
                        dialog.setCancelable(true);
                        dialog.show();
                        /*
                        new AlertDialog.Builder(Membership_phone.this)
                                .setMessage("중복된 번호입니다.")
                                .setPositiveButton("확인", null)
                                .show();  */
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "휴대번호 전송실패", Toast.LENGTH_LONG).show();
                Log.e(TAG, "surem Error" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("param", jc.createJsonParam("surem", new String[]{phone}));
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_07_request_auth_number", new String[]{phone}));
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }

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

    private void countDownfinish() {
        if (countDownTimer != null) {
            countDownTimer.onFinish();
            Log.d("resultcode", "  :" + countDownTimer);
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
                timeattack.setText(String.valueOf(String.format("%d:%02d", minutes, seconds)));

            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                countDownTimer = null;
                if(!authcheck){

                    dialog = new ConfirmDialog(Membership_phone.this,
                            "인증시간이 지났습니다.\n인증번호를 다시 받아주세요.", "확인");
                    dialog.setCancelable(true);
                    dialog.show();
                    /*
                    new AlertDialog.Builder(Membership_phone.this)
                            .setMessage("인증시간이 지났습니다. 인증번호를 다시 받아주세요.")
                            .setPositiveButton("확인", null)
                            .show();  */

                    phone_tx.setEnabled(true);
                    chk_btn.setEnabled(true);
                    chk_btn.setText("인증번호 전송");
                    chk_btn.setTextColor(Color.rgb(125,81,252));
                    chk_btn.setBackground(getResources().getDrawable(R.drawable.smallbotton_sub));
                    phone_tx.setTextColor(Color.rgb(0,0,0));
                    timeattack.setVisibility(View.GONE);
                    idnumber_tx.setEnabled(false);
                    tv_chknumber.setVisibility(View.GONE);
                    complete_chk.setVisibility(View.GONE);
                    complete_chk.setEnabled(false);
                    idnumber_tx.setEnabled(false);
                    idnumber_tx.setVisibility(View.GONE);
                    timeattack.setVisibility(View.GONE);
                    wrong_number.setVisibility(View.INVISIBLE);
                    idnumber_tx.setText("");

                }
            }
        };
        countDownTimer.start();
    }

    public void button(){
        //이전 화면으로 이동(비밀번호 입력 페이지)
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Membership_pw.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.side_on2, R.anim.side_off2);
            }
        });

        //이메일 입력 화면으로 이동
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.phone_temp = phone_tx.getText().toString().trim();
                Intent getIntent = getIntent();
                //Log.i("ID : ",getIntent.getStringExtra("ID"));
                Intent intent = new Intent(getApplicationContext(), Membership_email.class);
                intent.putExtra("ID",LoginActivity.id_temp);
                intent.putExtra("PW", LoginActivity.pw_temp);
                intent.putExtra("PHONE",LoginActivity.phone_temp);
                startActivityForResult(intent,82);
                finish();
                overridePendingTransition(R.anim.side_on, R.anim.side_off);
            }
        });

        //휴대폰 인증번호 전송 요청 버튼
        chk_btn.setOnClickListener(new View.OnClickListener() {//인증번호
            @Override
            public void onClick(View v) {
                if (phone_tx.getText().toString().length() >= 8) {
                    if (countDownTimer == null) {
                        authphone(phone_tx.getText().toString().trim());//서버 번호
                        //AuthCodeTimmer();
                    } else {
                        countDownfinish();
                    }

                } else {
                    //Toast.makeText(getApplicationContext(), "휴대폰 번호를 확인해주세요", Toast.LENGTH_SHORT).show();

                    dialog = new ConfirmDialog(Membership_phone.this,
                            "휴대폰 번호를 확인해주세요.", "확인");
                    dialog.setCancelable(true);
                    dialog.show();
                    /*
                    new AlertDialog.Builder(Membership_phone.this)
                            .setMessage("휴대폰 번호를 확인해주세요.")
                            .setPositiveButton("확인", null)
                            .show(); */
                }
            }
        });

        //인증번호 확인 요청 버튼
        complete_chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//휴대인증확인

                if (idnumber_tx.getText().toString().length() == 4) {
                    //인증번호 확인 함수 호출
                    confirmPhoneCode(phone_tx.getText().toString().trim(), idnumber_tx.getText().toString().trim());//인증확인

                } else {
                    //잘못된 인증번호 입력
                    //Toast.makeText(getApplicationContext(), "인증 번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                    dialog = new ConfirmDialog(Membership_phone.this,
                            "인증 번호를 확인해주세요.", "확인");
                    dialog.setCancelable(true);
                    dialog.show();
                    /*
                    new AlertDialog.Builder(Membership_phone.this)
                            .setMessage("인증 번호를 확인해주세요.")
                            .setPositiveButton("확인", null)
                            .show();   */
                }
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
                case R.id.phonerl :
                    break;
                case R.id.chk_btn :
                    break;

                case R.id.tv_chknumber :
                    break;
            }
        }
    };

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(phonerl.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(chk_btn.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tv_chknumber.getWindowToken(), 0);
    }

    public static void inputAuthNumber(String authNumber) {
        Log.e("체인지폰넘", "인증번호 받아서 들어옴");
        if (authNumber != null) {
            // editTextInputNumber에 받아온 인증번호를 입력
            Log.e("체인지폰넘", "인증번호 자동입력");
            idnumber_tx.setText(authNumber);
        }
    }
}

