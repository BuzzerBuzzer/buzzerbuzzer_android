package com.movements.and.buzzerbuzzer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.movements.and.buzzerbuzzer.Utill.BaseActivity;

/**
 * Created by samkim on 2017. 9. 26..
 */
//팝업
public class Firstintro extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_activity);
        Button cancel = (Button) findViewById(R.id.Cancel);
        Button ok = (Button) findViewById(R.id.Ok);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Ok", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
