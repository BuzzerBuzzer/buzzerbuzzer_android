package com.movements.and.buzzerbuzzer.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by hanyugyeong on 2018-01-30.
 */

public class SpinnerAdapter extends ArrayAdapter<CharSequence> {

    Typeface typefaceBold = Typeface.createFromAsset(getContext().getAssets(),"NanumSquareRoundB.ttf");
    Typeface typefaceExtraBold = Typeface.createFromAsset(getContext().getAssets(),"NanumSquareRoundEB.ttf");


    public SpinnerAdapter(Context context, int id, String[] arr) {
        super(context, id, arr);

        Log.e("SpinnerAdapter index", arr[1]);
    }
/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(typefaceBold);

        return view;
    }*/

    // Affects opened state of the spinner
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(typefaceBold);


        return view;
    }

}
