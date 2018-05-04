package com.movements.and.buzzerbuzzer;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.movements.and.buzzerbuzzer.Utill.BaseActivity;

public class Membership_info extends BaseActivity {

    private TextView tv1, tv2, tv3, tv4;
    private ImageView nextbtn,btn_back;
    private Typeface typefaceBold, typefaceExtraBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_info);

        tv1 = (TextView)findViewById(R.id.tv1);
        tv2 = (TextView)findViewById(R.id.tv2);
        tv3 = (TextView)findViewById(R.id.tv3);
        tv4 = (TextView)findViewById(R.id.tv4);
        nextbtn = (ImageView)findViewById(R.id.nextbtn);
        btn_back = (ImageView)findViewById(R.id.btn_back);
        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        tv1.setTypeface(typefaceExtraBold);


        String str1 = "<font color=\"#282828\">안녕하세요!<br>오프라인 인맥관리 서비스</font><br>" +
                "<font color=\"#7d51fc\">부저부저</font> <font color=\"#282828\">입니다.</font>";
        tv2.setText(Html.fromHtml(str1));
        tv2.setTypeface(typefaceExtraBold);

        String str2 = "<font color=\"#888888\">회원가입은 </font>" +
                "<font color=\"#7d51fc\">총 4단계</font><font color=\"#888888\">로 진행됩니다.<br>" +
                "진행을 원하시면 다음을 눌러주세요.</font>";
        tv3.setText(Html.fromHtml(str2));
        tv3.setTypeface(typefaceBold);

        String str3 = "<font color=\"#888888\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " +
                "다음단계</font><br>" +
                "<font color=\"#7d51fc\">아이디 / 비밀번호 등록</font>" +
                "<font color=\"#888888\">으로</font>";
        tv4.setText(Html.fromHtml(str3));
        tv4.setTypeface(typefaceBold);

        nextbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), Membership_id.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.side_on, R.anim.side_off);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.side_on2, R.anim.side_off2);
            }
        });
    }
}
