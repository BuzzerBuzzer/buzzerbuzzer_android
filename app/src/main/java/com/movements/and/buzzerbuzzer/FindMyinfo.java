package com.movements.and.buzzerbuzzer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 7. 17..
 */
//계정정보 조회
public class FindMyinfo extends BaseActivity {
    InputMethodManager imm;
    private LinearLayout linearLayout;
    private ImageView backbtn;
    private Button nextbtn, nextbtn2;
    private EditText idtx, pwdtx, emailtx;
    private TextView textView00, textView01, textView02;
    //외부 폰트 적용
    private Typeface typefaceBold, typefaceExtraBold;

    private ProgressBar progress_bar_findmyinfo;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private boolean success_id_check;
    private boolean success_pw_check;

    private JsonConverter jc;
    private static final String TAG = "FindMyinfo";
    private String id, password, mPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findmyinfo);
        backbtn = (ImageView) findViewById(R.id.backbtn);
        idtx = (EditText) findViewById(R.id.idtx);
        pwdtx = (EditText) findViewById(R.id.pwdtx);
        emailtx = (EditText) findViewById(R.id.emailtx);
        nextbtn = (Button) findViewById(R.id.nextbtn);
        nextbtn2 = (Button) findViewById(R.id.nextbtn2);
        textView00 = (TextView)findViewById(R.id.textView00);
        textView01 = (TextView)findViewById(R.id.textView01);
        textView02 = (TextView)findViewById(R.id.textView02);
        linearLayout = (LinearLayout) findViewById(R.id.ll);
        linearLayout.setOnClickListener(clickListener);
        jc = new JsonConverter();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        progress_bar_findmyinfo = (ProgressBar)findViewById(R.id.progress_bar_findmyinfo);
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();

        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        button();

        //외부 폰트 적용
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        textView00.setTypeface(typefaceExtraBold);
        textView01.setTypeface(typefaceBold);
        textView02.setTypeface(typefaceBold);
        nextbtn.setTypeface(typefaceExtraBold);
        nextbtn2.setTypeface(typefaceExtraBold);
        idtx.setTypeface(typefaceBold);
        pwdtx.setTypeface(typefaceBold);
        emailtx.setTypeface(typefaceBold);
    }

    private void button() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {//id찾기
            @Override
            public void onClick(View v) {
                idtx.getText();
                if (idtx.getText().toString().length() >= 5) {
                    findid(idtx.getText().toString().trim());//아뒤찾기
                }
            }
        });
        nextbtn2.setOnClickListener(new View.OnClickListener() {//비밀번호 찾기
            @Override
            public void onClick(View v) {
                pwdtx.getText();
                findpwd(pwdtx.getText().toString().trim(), emailtx.getText().toString().trim());//비번초기화
            }
        });
    }

    private void findpwd(String inputid, String inputemail) {
        progress_bar_findmyinfo.setVisibility(View.VISIBLE);
        final String _id = inputid;
        final String _email = inputemail;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG + " findpwd", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                progress_bar_findmyinfo.setVisibility(View.GONE);
                                success_pw_check = true;
                                AlertDialog.Builder alert = new AlertDialog.Builder(FindMyinfo.this);
                                alert.setTitle(R.string.infopwd)
                                        .setMessage(R.string.infopwd2)
                                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Toast.makeText(getActivity(), friends.getFr_id(), Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        });
                                alert.show();
                                if (json.getString("rows").equals("1")) {
                                    //Toast.makeText(getApplicationContext(), "ID를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                                    //success_pw_check = false;
                                }
                            } else if(json.getString("result").equals("fail") && json.getString("msg").equals("It is missed Entered email.")){
                                //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                progress_bar_findmyinfo.setVisibility(View.GONE);
                                success_pw_check = false;
                                new AlertDialog.Builder(FindMyinfo.this)
                                        .setMessage("e-mail 주소를 입력해주세요.")
                                        .setPositiveButton("확인", null)
                                        .show();
                                return;
                            }
                            else {
                                //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                progress_bar_findmyinfo.setVisibility(View.GONE);
                                success_pw_check = false;
                                new AlertDialog.Builder(FindMyinfo.this)
                                        .setMessage("확인 후 다시 시도해주세요.")
                                        .setPositiveButton("확인", null)
                                        .show();
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
                        Toast.makeText(getApplicationContext(), "인터넷 연결를 확인해주세요!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "reset_pw Error :" + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_05_reset_password", new String[]{_id, _email}));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(150000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void findid(String trim) {
        final String email = trim;
        progress_bar_findmyinfo.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                progress_bar_findmyinfo.setVisibility(View.GONE);
                                success_id_check = true;

                                AlertDialog.Builder alert = new AlertDialog.Builder(FindMyinfo.this);
                                alert.setTitle(R.string.infoemail)
                                        .setMessage(R.string.infoemail2)
                                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Toast.makeText(getActivity(), friends.getFr_id(), Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        });

                                alert.show();

                                //Toast.makeText(getApplicationContext(), "전송되었습니다.", Toast.LENGTH_SHORT).show();
                            } else {//json.getString("result").equals("fail")시
                                //Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                progress_bar_findmyinfo.setVisibility(View.GONE);
                                success_id_check = false;

                                //Toast.makeText(getApplicationContext(), "이메일을 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                                new AlertDialog.Builder(FindMyinfo.this)
                                        .setMessage("확인 후 다시 시도해주세요.")
                                        .setPositiveButton("확인", null)
                                        .show();

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
                        Toast.makeText(getApplicationContext(), "이메일 및 인터넷 연결을 확인 해 주세요.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "find_id Error :" + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_04_find_id", new String[]{email}));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(150000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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
                case R.id.ll :
                    break;

                case R.id.imageView :
                    break;
            }
        }
    };

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(idtx.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(pwdtx.getWindowToken(), 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_exit, R.anim.bottom_down);
    }

}


