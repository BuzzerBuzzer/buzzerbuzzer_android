package com.movements.and.buzzerbuzzer;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CurrentNickName extends BaseActivity {

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;

    private JsonConverter jc;

    private ImageView btn_back, delete_btn;
    private TextView tv, check;
    private EditText tx;
    private PasswordEn passwordEn;

    private String _id, mPassword, password;

    private Typeface typefaceBold, typefaceExtraBold;

    private String TAG = "CurrentNickName";
    private InputMethodManager imm;
    private RelativeLayout nickrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_nick_name);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        nickrl = (RelativeLayout)findViewById(R.id.nickrl);
        btn_back = (ImageView)findViewById(R.id.btn_back);
        delete_btn = (ImageView)findViewById(R.id.delete_btn);
        tv = (TextView)findViewById(R.id.tv);
        check = (TextView) findViewById(R.id.check);
        tx = (EditText)findViewById(R.id.tx);

          //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv.setTypeface(typefaceExtraBold);
        check.setTypeface(typefaceExtraBold);
        tx.setTypeface(typefaceExtraBold);

        jc = new JsonConverter();

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();

        passwordEn = new PasswordEn();
        _id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        nickrl.setOnClickListener(clickListener);
        tv.setOnClickListener(clickListener);

        check.setEnabled(false);

        button();
    }

    public void button(){
        tx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(tx.getText().toString().length() > 0) {  //입력글자 0 보다 크면 확인버튼 활성화
                    check.setEnabled(true);
                    check.setTextColor(R.color.colorMain);
                }
                else {
                    check.setEnabled(false);
                    check.setTextColor(R.color.grey);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                setResult(RESULT_CANCELED, intent); //취소
                finish();

            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNick(_id);

                //updateCurrentUserInfo(tx.getText().toString().trim(), setting.getString("user_pic_URL", null));

                Intent intent = getIntent();
                intent.putExtra("NICKNAME", tx.getText().toString().trim());
                Log.e("NICKNAME", tx.getText().toString().trim());
                setResult(6, intent);
                finish();

            }
        });



        //아이디 에디터 삭제
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check.setEnabled(false);
                check.setTextColor(R.id.grey);
                tx.setText(null);
            }
        });

    }

    private void setNick(String _id) {
        final String id = _id;
        final String _nickname = tx.getText().toString().trim();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시

                                String msg = json.getString("msg");
                                Log.d(TAG,msg);

                                editor.putString("user_nickname",_nickname);
                                editor.commit();
                                Log.d(TAG, setting.getString("user_nickname","안들어갔나봄"));

                            } else {//json.getString("result").equals("fail")시
                                String msg = json.getString("msg");
                                Log.d(TAG,msg);

                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        Log.e(TAG," user_nickname_update error"+ error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_20_save_nickname", new String[]{id, _nickname}));
                //Log.e("proc_20_save_nickname", String.valueOf(params));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            hideKeyboard();
            switch (v.getId())
            {
                case R.id.nickrl :
                    break;

                case R.id.tv :
                    break;
            }
        }
    };

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(nickrl.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_exit, R.anim.bottom_down);
    }
}
