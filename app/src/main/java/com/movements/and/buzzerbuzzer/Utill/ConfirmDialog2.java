package com.movements.and.buzzerbuzzer.Utill;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.movements.and.buzzerbuzzer.R;

/**
 * Created by Administrator on 2018-03-31.
 */

public class ConfirmDialog2 extends Dialog{
    private TextView mTitleView;
    private TextView mOkBtn;

    private String mTitle;
    private String mOk;

    private View.OnClickListener mOkClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.confirm_dialog);


        mTitleView = (TextView) findViewById(R.id.dialog_text);
        mOkBtn = (TextView) findViewById(R.id.dialog_btn1);

        // 제목과 내용을 생성자에서 셋팅한다.
        mTitleView.setText(mTitle);
        mOkBtn.setText(mOk);

        // 클릭 이벤트 셋팅
        if (mOkClickListener != null) {
            mOkBtn.setOnClickListener(mOkClickListener);
        } else {

        }
    }

    public ConfirmDialog2(Context context, String content, String oktext, View.OnClickListener okBtnListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = content;
        this.mOk = oktext;
        this.mOkClickListener = okBtnListener;
    }

    /*
    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public ConfirmDialog(Context context, String content, String oktext,
                         View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = content;
        this.mOk = oktext;
        this.mOkClickListener = singleListener;
    }
    */

}
