package com.movements.and.buzzerbuzzer.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.movements.and.buzzerbuzzer.Model.Friend;
import com.movements.and.buzzerbuzzer.R;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.util.ArrayList;


/**
 * Created by samkim on 2017. 3. 23..
 */

public class FriendsAdapter2 extends RecyclerView.Adapter<FriendsAdapter2.MyViewHolder> {
    //private Context mContext;
    private Activity activity;
    private ArrayList<Friend> mFriendList;
    private FriendsAdapter2.RecyclerItemClickListener listener;
    private FriendsAdapter2.RecyclerItemLongClickListener listener2;
    private int selectedPosition;

    //final static String TAG = "Friends";
    public FriendsAdapter2(FragmentActivity activity, ArrayList<Friend> mFriendList, RecyclerItemClickListener recyclerItemClickListener, RecyclerItemLongClickListener recyclerItemLongClickListener) {
        this.activity = activity;
        this.mFriendList = mFriendList;
        this.listener = recyclerItemClickListener;
        this.listener2 = recyclerItemLongClickListener;
    }

    @Override
    public FriendsAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_row, parent, false);
        // Log.e("FriendsAdapter ","   2222");
        return new FriendsAdapter2.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // Log.e("FriendsAdapter ","   3333");
        Friend friends = mFriendList.get(position);
        if (friends != null) {
            // Log.e("FriendsAdapter", "friends null 아님");
            holder.user_id.setText(friends.getNickname());
            holder.sts_msg.setVisibility(View.INVISIBLE);
            if (friends.getCondition_msg().length() > 0) {
                holder.sts_msg.setText(friends.getCondition_msg());
                holder.sts_msg.setVisibility(View.VISIBLE);
            }


            // 롱클릭시 프래그먼트에서 notifydatachanged를 돌려서 포지션 아닌 아이템들은 없앤다
            if(selectedPosition == position){

            }else{
                holder.red.setVisibility(View.GONE);
            }


            // Picasso.with(activity).load(friends.getPic_src()).placeholder(R.mipmap.ic_launcher).into(holder.user_pic);
            if (friends.getPic_src().isEmpty()) {//조인쿼리 성공시 제거해야함
                //holder.user_pic.setImageResource(R.drawable.nopic__s_m);
            } else {
                //Picasso.with(activity).load(friends.getPic_src()).placeholder(R.drawable.nopic__s_m).into(holder.user_pic);
                Glide.with(activity).load(friends.getPic_src()).placeholder(R.drawable.nopic__s_m).into(holder.user_pic);
            }
            //           Log.d("게이지테스트2",""+((float)friends.getFr_gauge_angle()/360)*100);
            holder.crpv.setStartAngle(0);
            holder.crpv.setStrokeWidthDp(3);
            holder.crpv.setPercent(0);
            holder.crpv.setFgColorEnd(Color.parseColor("#7d51fc"));
            holder.crpv.setFgColorStart(Color.parseColor("#7d51fc"));
//            //Log.d("게이지테스트", "" + ((float) friends.getFr_gauge_angle() / 360) * 100);
//            if (friends.getLocation_release_yn() == 1) {
//                holder.crpv.setPercent(((float) friends.getFr_gauge_angle() / 360) * 100);
//            }


            switch (friends.getGauge_type_user()) {
                case 1:
                    holder.crpv.setPercent(((float) friends.getFr_gauge_angle() / 360) * 100);
                    // Log.d("게이지타입1", "아뒤 :" + friends.getNickname() + " : " + String.valueOf(friends.getGauge_type_user()));
//                    holder.crpv.setFgColorEnd(Color.parseColor("#7d62d9"));
//                    holder.crpv.setFgColorStart(Color.parseColor("#ff69b4"));
                    break;

                case 2:
                    // Log.d("게이지타입2", "아뒤 :" + friends.getNickname() + " : " + String.valueOf(friends.getGauge_type_user()));
                    holder.crpv.setPercent(((float) friends.getFr_gauge_angle() / 360) * 100);
                    holder.crpv.setFgColorEnd(Color.parseColor("#27a2ff"));
                    holder.crpv.setFgColorStart(Color.parseColor("#27a2ff"));
                    break;

                case 3:
                    holder.crpv.setPercent(((float) friends.getFr_gauge_angle() / 360) * 100);
                    holder.crpv.setFgColorEnd(Color.parseColor("#27a2ff"));
                    holder.crpv.setFgColorStart(Color.parseColor("#27a2ff"));
                    // Log.d("게이지타입3", "아뒤 :" + friends.getNickname() + " : " + String.valueOf(friends.getGauge_type_user()));
                    break;
            }


            holder.bind(friends, listener);
            holder.bind2(friends, listener2);
        }else{
            // Log.e("FriendsAdapter", "friends null");
        }

    }


    @Override
    public int getItemCount() {

        return this.mFriendList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView user_id, sts_msg;
        private ImageView user_pic, img_best, gauge;
        private TextView red;             //롱클릭에 대한 콘텍스트 메뉴
        private ColorfulRingProgressView crpv;
        Typeface typeface;


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public MyViewHolder(View itemView) {
            super(itemView);

            user_id = (TextView) itemView.findViewById(R.id.age_tx);
            sts_msg = (TextView) itemView.findViewById(R.id.sts_msg);

            typeface = Typeface.createFromAsset(itemView.getContext().getAssets(),"NanumSquareRoundB.ttf");
            red = (TextView)itemView.findViewById(R.id.red);
            red.setTypeface(typeface);

            user_pic = (ImageView) itemView.findViewById(R.id.user_pic2);
            img_best = (ImageView) itemView.findViewById(R.id.img_best);
            user_pic.setBackground(new ShapeDrawable(new OvalShape()));
            user_pic.setClipToOutline(true);
            crpv = (ColorfulRingProgressView) itemView.findViewById(R.id.crpv);

            user_id.setTypeface(typeface);
            sts_msg.setTypeface(typeface);
        }

        public void bind(final Friend friends, final FriendsAdapter2.RecyclerItemClickListener listener) {//RecyclerItemClickListener 추가후 4
            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickListener(friends, getLayoutPosition());
                }
            });

        }

        public void bind2(final Friend friends, final FriendsAdapter2.RecyclerItemLongClickListener listener2) {
            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    friends.getGauge_type_user();
                    listener2.onLongClickListener(friends, getLayoutPosition(), itemView);
                    red.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        }
    }

    public interface RecyclerItemClickListener {//RecyclerItemClickListener 1

        void onClickListener(Friend friends, int position);
    }

    public interface RecyclerItemLongClickListener {
        void onLongClickListener(Friend friends, int position, View itemView);
    }

    public void setSelectedPosition(int currentIndex) {//setSelectedPosition 2
        this.selectedPosition = currentIndex;
    }

    public int getSelectedPosition() {//setSelectedPosition 6 다시changeSelectedPlace로
        return selectedPosition;
    }

    public View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition =firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition ) {
            return listView.getAdapter().getView(position, listView.getChildAt(position), listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

}