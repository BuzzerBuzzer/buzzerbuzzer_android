package com.movements.and.buzzerbuzzer.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.movements.and.buzzerbuzzer.Model.Friend;
import com.movements.and.buzzerbuzzer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by samkim on 2017. 5. 29..
 */
//서칭어댑터
public class BuzzerAdapter extends RecyclerView.Adapter<BuzzerAdapter.MyViewHolder> {
    private Context mContext;

    private ArrayList<Friend> mFriendList;
    private BuzzerAdapter.RecyclerItemClickListener listener;
    private int selectedPosition;

    public BuzzerAdapter(Context mContext, ArrayList<Friend> mPlaceList, RecyclerItemClickListener recyclerItemClickListener) {
        this.mContext = mContext;
        this.mFriendList = mPlaceList;
        this.listener = recyclerItemClickListener;
    }


    @Override
    public BuzzerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.buzzerfriends_row, parent, false);
        int width = parent.getMeasuredWidth() / 3;
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) v.getLayoutParams();
        params.width = width;
        v.setLayoutParams(params);
        MyViewHolder holder = new MyViewHolder(v);
        v.setBackgroundResource(R.drawable.gallerly);
//        v.setClipToOutline(true);
        return holder;
//        return new BuzzerAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Friend user = mFriendList.get(position);
        if (user != null) {
            holder.user_pic.setImageResource(R.drawable.nopic__s_m);
            if (user.getPic_src().isEmpty()) {
            } else {
                Glide.with(mContext).load(user.getPic_src()).into(holder.user_pic);
            }
            holder.bind(user, listener);
        }
    }


    @Override
    public int getItemCount() {
        return this.mFriendList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView user_pic;


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public MyViewHolder(View itemView) {
            super(itemView);
            user_pic = (ImageView) itemView.findViewById(R.id.user_pic2);

        }

        public void bind(final Friend user, final BuzzerAdapter.RecyclerItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickListener(user, getLayoutPosition());
                }
            });
        }
    }

    public interface RecyclerItemClickListener {

        void onClickListener(Friend user, int position);
    }

    public void setSelectedPosition(int currentIndex) {//setSelectedPosition 2
        this.selectedPosition = currentIndex;
    }

    public int getSelectedPosition() {//setSelectedPosition 6 다시changeSelectedPlace로
        return selectedPosition;
    }


}