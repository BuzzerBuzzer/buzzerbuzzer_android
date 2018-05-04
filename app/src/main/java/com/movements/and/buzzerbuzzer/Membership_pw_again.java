package com.movements.and.buzzerbuzzer;

import android.content.Context;
import android.content.Intent;
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

import com.movements.and.buzzerbuzzer.Utill.BaseActivity;


//비밀번호 재입력
public class Membership_pw_again extends BaseActivity {

    InputMethodManager imm;

    private TextView tv_id_pw, tv_pw, again;
    private EditText pw_tx;
    private ImageView btn_back, nextbtn, delete_btn;
    RelativeLayout pwrl;

    private Typeface typefaceBold, typefaceExtraBold;

    private boolean checkSamePw;
    private String checkedPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_pw_again);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        tv_id_pw = (TextView)findViewById(R.id.tv_id_pw);
        tv_pw = (TextView)findViewById(R.id.tv_pw);
        again = (TextView)findViewById(R.id.again);
        pw_tx = (EditText)findViewById(R.id.pw_tx);
        btn_back = (ImageView)findViewById(R.id.btn_back);
        nextbtn = (ImageView)findViewById(R.id.nextbtn);
        delete_btn = (ImageView)findViewById(R.id.delete_btn);
        pwrl = (RelativeLayout)findViewById(R.id.pwrl);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv_id_pw.setTypeface(typefaceExtraBold);
        tv_pw.setTypeface(typefaceBold);
        again.setTypeface(typefaceBold);

        pw_tx.setTypeface(typefaceExtraBold);

        pwrl.setOnClickListener(clickListener);
        tv_pw.setOnClickListener(clickListener);
        again.setOnClickListener(clickListener);

        Intent intent = getIntent();
        checkedPassword = intent.getStringExtra("PW");

        nextbtn.setEnabled(false);


        button();
    }


    public void button(){

        //이전 페이지(처음 비밀번호 입력화면)으로
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Membership_pw.class);
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
                again.setVisibility(View.VISIBLE);
                checkSamePw = false;
            }
        });

        pw_tx.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkSamePw = false;
                if(s.toString().trim().equals(checkedPassword)){
                    nextbtn.setImageResource(R.drawable.signup_next);
                    nextbtn.setEnabled(true);
                    again.setVisibility(View.INVISIBLE);
                    checkSamePw = true;
                }
                else{
                    nextbtn.setImageResource(R.drawable.signup_next_dim);
                    nextbtn.setEnabled(false);
                    again.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //핸드폰 번호 등록 페이지로 이동
                LoginActivity.pw_temp = pw_tx.getText().toString().trim();
                Intent getIntent = getIntent();
                //Log.i("ID : ",getIntent.getStringExtra("ID"));
                Intent intent = new Intent(getApplicationContext(), Membership_phone.class);
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
                case R.id.pwrl :
                    break;

                case R.id.tv_pw :
                    break;
                case R.id.again :
                    break;
            }
        }
    };

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(pwrl.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(tv_pw.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(again.getWindowToken(), 0);
    }

}
