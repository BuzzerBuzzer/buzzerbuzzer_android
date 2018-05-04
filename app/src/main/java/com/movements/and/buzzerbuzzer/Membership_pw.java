package com.movements.and.buzzerbuzzer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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

import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Patternpwd;

import static com.movements.and.buzzerbuzzer.FriendList.id;


//비밀번호 입력
public class Membership_pw extends BaseActivity {

    InputMethodManager imm;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private TextView tv_id_pw, tv_pw, pwrule;
    private ImageView btn_back, nextbtn, delete_btn;
    private EditText pw_tx;
    private Typeface typefaceBold, typefaceExtraBold;

    private boolean pwdcheck = false;
    private boolean checkpwd;
    private Patternpwd mPatternpwd; //비밀번호 패턴
    private RelativeLayout rl,rl2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_pw);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        tv_id_pw = (TextView)findViewById(R.id.tv_id_pw);
        tv_pw = (TextView)findViewById(R.id.tv_pw);
        pwrule = (TextView)findViewById(R.id.pwrule);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        nextbtn = (ImageView) findViewById(R.id.nextbtn);
        delete_btn = (ImageView) findViewById(R.id.delete_btn);
        pw_tx = (EditText)findViewById(R.id.pw_tx);
        rl = (RelativeLayout)findViewById(R.id.rl);
        rl2 = (RelativeLayout)findViewById(R.id.rl2);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv_id_pw.setTypeface(typefaceExtraBold);
        tv_pw.setTypeface(typefaceBold);
        pw_tx.setTypeface(typefaceExtraBold);
        pwrule.setTypeface(typefaceBold);

        rl.setOnClickListener(clickListener);
        rl2.setOnClickListener(clickListener);
        tv_pw.setOnClickListener(clickListener);
        pwrule.setOnClickListener(clickListener);

        nextbtn.setEnabled(false);

        mPatternpwd = new Patternpwd();
        button();
    }

    private void button() {

        //이전 페이지(아이디 입력화면)으로
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Membership_id.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.side_on2, R.anim.side_off2);
            }
        });

        //비밀번호 에디터 텍스트 삭제
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw_tx.setText(null);
                nextbtn.setImageResource(R.drawable.signup_next_dim);
                nextbtn.setEnabled(false);
            }
        });


        //비밀번호 패턴 확인
        pw_tx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //맞는 비밀번호 패턴일 경우 넥스트버튼을 다시 활성화시킴
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkpwd = mPatternpwd.Passwrodvalidate(pw_tx.getText().toString().trim());
                if(checkpwd) {
                    nextbtn.setImageResource(R.drawable.signup_next);
                    nextbtn.setEnabled(true);
                }
                else{
                    nextbtn.setImageResource(R.drawable.signup_next_dim);
                    nextbtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //다음 페이지 이동
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                pwdcheck = true;
//                checkpwd = mPatternpwd.Passwrodvalidate(pw_tx.getText().toString().trim());
//                //TODO : 비밀번호 패턴 10자리 이상? 혹은 6~12?
//                if (!checkpwd) {
//                    Toast.makeText(getApplicationContext(), "영문+숫자 또는 특수문자 6자 이상 12자 이하여야 합니다.", Toast.LENGTH_LONG).show();
//                    pwdcheck = false;
//                    nextbtn.setImageResource(R.drawable.signup_next_dim);
//                    nextbtn.setEnabled(false);
//                }
//                //비밀번호 패턴 안맞거나 비밀번호를 입력하지 않았을 때
//                if (pw_tx.getText().toString().trim().length() == 0 || !pwdcheck) {
//                    return;
//                }
                //비밀번호 재입력 페이지로 이동
                LoginActivity.pw_temp = pw_tx.getText().toString().trim();
                Intent getIntent = getIntent();
                //Log.i("ID : ",getIntent.getStringExtra("ID"));
                Intent intent = new Intent(getApplicationContext(), Membership_pw_again.class);
                intent.putExtra("ID",LoginActivity.id_temp);
                intent.putExtra("PW", LoginActivity.pw_temp);
                startActivityForResult(intent,82);
                finish();
                overridePendingTransition(R.anim.side_on, R.anim.side_off);
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
                case R.id.rl :
                    break;

                case R.id.rl2 :
                    break;
                case R.id.tv_pw :
                    break;
                case R.id.pwrule :
                    break;
            }
        }
    };

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(rl.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(rl2.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tv_pw.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(pwrule.getWindowToken(), 0);
    }
}
