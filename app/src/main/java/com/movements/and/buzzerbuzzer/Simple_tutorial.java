package com.movements.and.buzzerbuzzer;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.movements.and.buzzerbuzzer.Utill.BaseActivity;


//간단 튜토리얼 화면
public class Simple_tutorial extends BaseActivity {

    TextView simple, tv, tv2, tv3, far1, far2, far3;
    Button location_resi;
    ImageView image, radiusProgress;
    ProgressBar thumbnail;

    private Typeface typefaceBold, typefaceExtraBold;
    private long m_temp;
    private CountDownTimer timer2, timer3, timer4;
    private int currentProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_tutorial);

        simple = (TextView)findViewById(R.id.simple);
        tv = (TextView)findViewById(R.id.tv);
        tv2 = (TextView)findViewById(R.id.tv2);
        tv3 = (TextView)findViewById(R.id.tv3);
        far1 = (TextView)findViewById(R.id.far1);
        far2 = (TextView)findViewById(R.id.far2);
        far3 = (TextView)findViewById(R.id.far3);

        far1.setVisibility(View.GONE);
        far2.setVisibility(View.GONE);
        far3.setVisibility(View.GONE);

        location_resi = (Button)findViewById(R.id.location_resi);
        image = (ImageView)findViewById(R.id.image);
        radiusProgress = (ImageView)findViewById(R.id.radiusProgress);
        thumbnail = (ProgressBar)findViewById(R.id.thumbnail);

        //이미지를 동그랗게 자르기 위해 추가한 부분
        image.setBackground(new ShapeDrawable(new OvalShape()));
        image.setClipToOutline(true);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        simple.setTypeface(typefaceExtraBold);
        tv.setTypeface(typefaceExtraBold);
        tv2.setTypeface(typefaceBold);
        tv3.setTypeface(typefaceBold);
        far1.setTypeface(typefaceBold);
        far2.setTypeface(typefaceBold);
        far3.setTypeface(typefaceBold);

        String str = "<font color=\"#888888\">상대방이 가까울수록 </font>" +"<font color=\"#7d51fc\">프로필의 보라색 게이지</font>" +
                "<font color=\"#888888\">가<br>" +
                "시계 방향으로 채워집니다.</font>";
        tv2.setText(Html.fromHtml(str));
        String str2 = "<font color=\"#7d51fc\">게이지가 꽉 찬 </font>" +
                "<font color=\"#888888\">친구에게<br>" +
                "대화를 걸어 얼굴을 보는 건 어떨까요?!</font>";
        tv3.setText(Html.fromHtml(str2));

        location_resi.setTypeface(typefaceExtraBold);

        button();

        final RelativeLayout.LayoutParams params
                = (RelativeLayout.LayoutParams) radiusProgress.getLayoutParams();

        final float scale = getResources().getDisplayMetrics().density;

        new CountDownTimer(1000,1){
            @Override
            public void onTick(long m) {
                thumbnail.setProgress((int) ((1000 - m) / 19));
                Log.d("현재 프로그레스", String.valueOf(thumbnail.getProgress()));
            }
            @Override
            public void onFinish() {
                far1.setVisibility(View.VISIBLE);
                currentProgress = thumbnail.getProgress();
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        timer2.start();
                    }
                }, 1000);
            }
        }.start();

        timer2 = new CountDownTimer(1000,1){
            @Override
            public void onTick(long m) {
                thumbnail.setProgress((int) ((1000 - m) / 40) + currentProgress);
                Log.d("현재 프로그레스", String.valueOf(thumbnail.getProgress()));
            }
            @Override
            public void onFinish() {
                far1.setVisibility(View.GONE);
                far2.setVisibility(View.VISIBLE);
                currentProgress = thumbnail.getProgress();
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        timer3.start();
                    }
                }, 1000);
            }
        };

        timer3 = new CountDownTimer(1000,1){
            @Override
            public void onTick(long m) {
                thumbnail.setProgress((int) ((1000 - m) / 40) + currentProgress);
                Log.d("현재 프로그레스", String.valueOf(thumbnail.getProgress()));
            }
            @Override
            public void onFinish() {
                far2.setVisibility(View.GONE);
                far3.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        timer4.start();
                    }
                }, 1000);
            }
        };

        timer4 = new CountDownTimer(1000,1){
            @Override
            public void onTick(long m) {
                    thumbnail.setProgress(100);
                    radiusProgress.setAlpha((float)(m/1000.0));
                    Log.d("m의값",(float)(m/1000.0)+"");
                    params.height = (int)(scale*(100+(100-m/10)));
                    params.width = (int)(scale*(100+(100-m/10)));
                    radiusProgress.setLayoutParams(params);

                radiusProgress.setLayoutParams(params);
            }

            @Override
            public void onFinish() {
                tv3.setVisibility(View.VISIBLE);
                radiusProgress.setVisibility(View.INVISIBLE);
                location_resi.setVisibility(View.VISIBLE);
            }
        };

    }

    public void button(){
        location_resi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpeningActivity.isTuto = true; // 프렌즈리스트 튜토리얼 띄우기 트루
                Intent intent = new Intent(getApplicationContext(), ProfileSetting.class);
                intent.putExtra("gps_base_check", false);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });
    }

}
