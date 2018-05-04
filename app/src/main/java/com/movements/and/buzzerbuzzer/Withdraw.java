package com.movements.and.buzzerbuzzer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.movements.and.buzzerbuzzer.Utill.BuzzerDialog;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 7. 20..
 */
//탈퇴화면
public class Withdraw extends BaseActivity {
    //    private TextView reason1, reason2, reason3, reason4, reason5;

    InputMethodManager imm;
    private JsonConverter jc;
    private TextView tv, tv2, textView2;
    private RadioButton reason1, reason2, reason3, reason4, reason5;
    private EditText noti6;
    private Button withdrawbtn;
    private ImageView settingbtn;
    private String num, noti, id, mPassword, password;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private RadioGroup radioGroup;
    private static final String TAG = "Withdraw";

    private Typeface typefaceBold, typefaceExtraBold;
    private LinearLayout withll;

    private BuzzerDialog dialog;
    private ConfirmDialog dialog2, dialog3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        jc = new JsonConverter();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        tv = (TextView)findViewById(R.id.tv);
        tv2 = (TextView)findViewById(R.id.tv2);
        textView2 = (TextView)findViewById(R.id.textView2);
        withll = (LinearLayout)findViewById(R.id.withll);

        reason1 = (RadioButton)findViewById(R.id.reason1);
        reason2 = (RadioButton)findViewById(R.id.reason2);
        reason3 = (RadioButton)findViewById(R.id.reason3);
        reason4 = (RadioButton)findViewById(R.id.reason4);
        reason5 = (RadioButton)findViewById(R.id.reason5);


        withdrawbtn = (Button) findViewById(R.id.withdrawbtn);
        settingbtn = (ImageView) findViewById(R.id.settingbtn);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        noti6 = (EditText) findViewById(R.id.noti6);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv.setTypeface(typefaceExtraBold);
        tv2.setTypeface(typefaceBold);
        textView2.setTypeface(typefaceBold);
        reason1.setTypeface(typefaceBold);
        reason2.setTypeface(typefaceBold);
        reason3.setTypeface(typefaceBold);
        reason4.setTypeface(typefaceBold);
        reason5.setTypeface(typefaceBold);
        withdrawbtn.setTypeface(typefaceExtraBold);
        noti6.setTypeface(typefaceBold);

        withll.setOnClickListener(clickListener);
        tv.setOnClickListener(clickListener);
        tv2.setOnClickListener(clickListener);

        button();
    }

    private void button() {
        withdrawbtn.setOnClickListener(new View.OnClickListener() {//탈퇴하기
            @Override
            public void onClick(View v) {
                noti = noti6.getText().toString();
                if (num!=null|| noti6.getText().toString().trim().length()>=1){
                    withdrawDialog();
                    /*
                    new AlertDialog.Builder(Withdraw.this, R.style.BuzzerAlertStyle)
                            .setMessage("정말 탈퇴하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    withdrawconfirm(id, num, noti);//탈퇴하기
                                }

                            })
                            .setNegativeButton("취소", null)
                            .show();*/
                }else {
                    //Toast.makeText(getApplicationContext(), "탈퇴 사유를 선택 하거나 의견을 적어주세요", Toast.LENGTH_LONG).show();

                    dialog2 = new ConfirmDialog(Withdraw.this,
                            "탈퇴 사유를 선택하거나\n의견을 적어주세요.", "확인");
                    dialog2.setCancelable(true);
                    dialog2.show();

                    /*
                    dialog2 = new ConfirmDialog(Withdraw.this,
                            "탈퇴 사유를 선택하거나\n의견을 적어주세요.", "확인", ok1Listener);
                    dialog2.setCancelable(true);
                    dialog2.show();
                    */

                    /*
                    new AlertDialog.Builder(Withdraw.this)
                            .setMessage("탈퇴 사유를 선택 하거나 의견을 적어주세요")
                            .setPositiveButton("확인", null)
                            .show();*/
                }
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {//탈퇴사유
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.reason1:
                        num = String.valueOf(1);
                        break;
                    case R.id.reason2:
                        num = String.valueOf(2);
                        break;
                    case R.id.reason3:
                        num = String.valueOf(3);
                        break;
                    case R.id.reason4:
                        num = String.valueOf(4);
                        break;
                    case R.id.reason5:
                        num = String.valueOf(5);
                        break;
                }
                if(radioGroup.getCheckedRadioButtonId() != -1) {
                    reason1.setButtonDrawable(R.drawable.btn_radio_off);
                    reason2.setButtonDrawable(R.drawable.btn_radio_off);
                    reason3.setButtonDrawable(R.drawable.btn_radio_off);
                    reason4.setButtonDrawable(R.drawable.btn_radio_off);
                    reason5.setButtonDrawable(R.drawable.btn_radio_off);
                    reason1.setTextColor(getApplication().getResources().getColor(R.color.grey02));
                    reason2.setTextColor(getApplication().getResources().getColor(R.color.grey02));
                    reason3.setTextColor(getApplication().getResources().getColor(R.color.grey02));
                    reason4.setTextColor(getApplication().getResources().getColor(R.color.grey02));
                    reason5.setTextColor(getApplication().getResources().getColor(R.color.grey02));
                    if (reason1.isChecked()) {
                        reason1.setButtonDrawable(R.drawable.btn_radio_on);
                        reason1.setTextColor(getApplication().getResources().getColor(R.color.Black));
                    }
                    else if(reason2.isChecked()) {
                        reason2.setButtonDrawable(R.drawable.btn_radio_on);
                        reason2.setTextColor(getApplication().getResources().getColor(R.color.Black));
                    }
                    else if(reason3.isChecked()) {
                        reason3.setButtonDrawable(R.drawable.btn_radio_on);
                        reason3.setTextColor(getApplication().getResources().getColor(R.color.Black));
                    }
                    else if(reason4.isChecked()) {
                        reason4.setButtonDrawable(R.drawable.btn_radio_on);
                        reason4.setTextColor(getApplication().getResources().getColor(R.color.Black));
                    }
                    else if(reason5.isChecked()) {
                        reason5.setButtonDrawable(R.drawable.btn_radio_on);
                        reason5.setTextColor(getApplication().getResources().getColor(R.color.Black));
                    }
                }
            }
        });

        settingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void withdrawconfirm(String id1, String num1, String noti1) {
        final String id = id1;
        final String num = num1;
        final String noti = noti1;

        StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG+" 190",response);
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시

                        /*
                        dialog2 = new ConfirmDialog(Withdraw.this,
                                "탈퇴처리 되었습니다.\n이용해 주셔서 감사합니다.", "확인");
                        dialog2.setCancelable(true);
                        dialog2.show();
                        */

                        Intent intlogout = new Intent(getApplicationContext(), LoginActivity.class);
                        intlogout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intlogout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        editor.clear();
                        editor.commit();
                        startActivity(intlogout);
                        // Toast.makeText(getApplicationContext(), "탈퇴처리 되었습니다.", Toast.LENGTH_LONG).show();
                        /*
                        new AlertDialog.Builder(Withdraw.this)
                                .setMessage("탈퇴처리 되었습니다.")
                                .setPositiveButton("확인", null)
                                .show();  */

                    } else {//json.getString("result").equals("fail")시
                        // Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        dialog2 = new ConfirmDialog(Withdraw.this,
                                "확인 후 다시 시도해주세요.", "확인");
                        dialog2.setCancelable(true);
                        dialog2.show();
                        /*
                        new AlertDialog.Builder(Withdraw.this)
                                .setMessage("다시 시도해주세요.")
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
                Log.e(TAG, "user_profile error" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_50_withdraw_user", new String[]{id, num, noti}));
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
                case R.id.withll :
                    break;

                case R.id.tv :
                    break;

                case R.id.tv2 :
                    break;
            }
        }
    };

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(withll.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tv2.getWindowToken(), 0);
    }


    public void withdrawDialog(){
        dialog = new BuzzerDialog(Withdraw.this,
                "정말 탈퇴하시겠습니까?", "확인", "취소", withdrawOKListener, cancelListener);
        dialog.setCancelable(true);
        dialog.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener withdrawOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            withdrawconfirm(id, num, noti);//탈퇴하기
            dialog.dismiss();
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };
/*
    private View.OnClickListener ok1Listener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog2.dismiss();
        }
    };
*/
}
