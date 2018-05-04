package com.movements.and.buzzerbuzzer.Utill;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.movements.and.buzzerbuzzer.R;

/**
 * Created by Administrator on 2018-03-31.
 */

public class TutoDialog extends Dialog{
    private TextView mTitleView;
    private TextView mOkBtn;

    private String mTitle;
    private String mOk;
    private FrameLayout tuto_close;
    private View.OnClickListener mOkClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend_list_tutorial);

        tuto_close = (FrameLayout) findViewById(R.id.tuto_close);


        // 클릭 이벤트 셋팅
        if (mOkClickListener != null) {
            tuto_close.setOnClickListener(mOkClickListener);
        } else {

        }

        tuto_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public TutoDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
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
