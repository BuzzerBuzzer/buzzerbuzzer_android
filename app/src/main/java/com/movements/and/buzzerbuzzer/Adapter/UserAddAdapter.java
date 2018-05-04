package com.movements.and.buzzerbuzzer.Adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.movements.and.buzzerbuzzer.Model.User;
import com.movements.and.buzzerbuzzer.R;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by samkim on 2017. 3. 23..
 */

public class UserAddAdapter extends RecyclerView.Adapter<UserAddAdapter.MyViewHolder> {
    private static final String TAG = "UserAddAdapter";

    private Context mContext;
    private JsonConverter jc;
    private ArrayList<User> mUserList;
    private UserAddAdapter.RecyclerItemClickListener listener;
    private int selectedPosition;
    private SharedPreferences setting;

    public UserAddAdapter(Context mContext, ArrayList<User> mPlaceList, RecyclerItemClickListener recyclerItemClickListener) {
        this.mContext = mContext;
        this.mUserList = mPlaceList;
        this.listener = recyclerItemClickListener;

//        Log.e("생성자 mUserList 사이즈", String.valueOf(mUserList.size())+", "+mUserList.get(1).getId()+", "+mUserList.get(1).getPhonenum());

        Log.e(TAG, "생성자");
        for(int i = 0; i <mUserList.size(); i++){
            Log.e("생성자 mUserList 사이즈", String.valueOf(mUserList.size())+", "+mUserList.get(i).getId()+", "+mUserList.get(i).getPhonenum());
        }
    }


    @Override
    public UserAddAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendfind_row, parent, false);
        Log.e(TAG, "뷰 생성");

        return new UserAddAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Log.e(TAG,"onBindViewHolder");

        jc = new JsonConverter();
        final User user = mUserList.get(position);
        Log.e(TAG, user.getNickname());
        if (user != null) {

            holder.user_id.setText(user.getNickname());
            holder.user_pic.setImageResource(R.drawable.nopic__s_m);
            holder.sts_msg.setText(user.getCondition_message());
            if (user.getUser_pic().isEmpty()) {

            } else {
                Picasso.with(mContext).load(user.getUser_pic()).into(holder.user_pic);
            }

            holder.bind(user, listener);
        }
    }


    @SuppressLint("LongLogTag")
    @Override
    public int getItemCount() {

        Log.e(TAG, String.valueOf(mUserList.size()));
        for(int i = 0; i <mUserList.size(); i++){
            Log.e("getItemCount mUserList 사이즈", String.valueOf(mUserList.size())+", "+mUserList.get(i).getId()+", "+mUserList.get(i).getPhonenum());
        }
//        return 0;
        return this.mUserList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView user_id, sts_msg;
        private ImageView user_pic;
        Typeface typeface;
        //private Button add_btn;

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public MyViewHolder(View itemView) {
            super(itemView);

            Log.e(TAG, "뷰 홀더");

            typeface = Typeface.createFromAsset(itemView.getContext().getAssets(),"NanumSquareRoundB.ttf");

            //Log.d(TAG, "==========MyViewHolder==========");
            user_id = (TextView) itemView.findViewById(R.id.age_tx);
            sts_msg = (TextView) itemView.findViewById(R.id.sts_msg);
            user_id.setTypeface(typeface);
            sts_msg.setTypeface(typeface);
            //add_btn = (Button) itemView.findViewById(R.id.add_btn);
            user_pic = (ImageView) itemView.findViewById(R.id.user_pic2);
            user_pic.setBackground(new ShapeDrawable(new OvalShape()));
            user_pic.setClipToOutline(true);


        }

        public void bind(final User user, final UserAddAdapter.RecyclerItemClickListener listener) {
            Log.e(TAG,"bind");
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickListener(user, getLayoutPosition());
                }
            });

        }

    }

    public interface RecyclerItemClickListener {

        void onClickListener(User user, int position);

    }

    public void setSelectedPosition(int currentIndex) {//setSelectedPosition 2
        this.selectedPosition = currentIndex;
    }

    public int getSelectedPosition() {//setSelectedPosition 6 다시changeSelectedPlace로
        return selectedPosition;
    }

}