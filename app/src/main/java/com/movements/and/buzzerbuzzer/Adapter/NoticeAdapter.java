package com.movements.and.buzzerbuzzer.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.Newsmessage;
import com.movements.and.buzzerbuzzer.News;
import com.movements.and.buzzerbuzzer.R;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by samkim on 2017. 3. 23..
 */
//뉴스 어뎁터
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> {
    private Context activity;
    private ArrayList<Newsmessage> mNoticeList;
    private NoticeAdapter.RecyclerItemClickListener listener;
    private NoticeAdapter.RecyclerItemLongClickListener listener2;
    private int selectedPosition;
    Typeface typeface;

    private JsonConverter jc;

    private PasswordEn passwordEn;

    public NoticeAdapter(Context activity, ArrayList<Newsmessage> mNoticeList, RecyclerItemClickListener recyclerItemClickListener, RecyclerItemLongClickListener recyclerItemLongClickListener) {
        this.activity = activity;
        this.mNoticeList = mNoticeList;
        this.listener = recyclerItemClickListener;
        this.listener2 = recyclerItemLongClickListener;
    }


    @Override
    public NoticeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Log.d(TAG, "==========onCreateViewHolder==========");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.noti_row, parent, false);
        typeface = Typeface.createFromAsset(parent.getContext().getAssets(),"NanumSquareRoundB.ttf");

        return new NoticeAdapter.MyViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Newsmessage newsmessage = mNoticeList.get(position);
        String type, setMessage, b, c, d = null;;
        SpannableStringBuilder sp = null;

//        Date date = null;
//        holder.df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            date = holder.df.parse(newsmessage.getNoti_time());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        long epoch = date.getTime();
//        holder.v.setReferenceTime(epoch);

        //TODO: news 탭 메세지, 이미지 등 커스텀 부분.
        if (newsmessage != null) {

            //TODO: news 메세지 추출 후 부분 색상 적용.
            Log.e("메세지 추출 " , newsmessage.getMessage());
            if(newsmessage.getMessage().contains("팔로우")){
                sp = new SpannableStringBuilder(newsmessage.getMessage());
                sp.setSpan(new ForegroundColorSpan(Color.rgb(39,162,255)),
                        newsmessage.getMessage().indexOf("팔로우"), newsmessage.getMessage().indexOf("팔로우")+3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.sts_msg.setText(sp);
            }else if(newsmessage.getMessage().contains("프로필 사진")){
                sp = new SpannableStringBuilder(newsmessage.getMessage());
                sp.setSpan(new ForegroundColorSpan(Color.rgb(39,162,255)),
                        newsmessage.getMessage().indexOf("프로필 사진"), newsmessage.getMessage().indexOf("프로필 사진")+6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.sts_msg.setText(sp);
            }else
                holder.sts_msg.setText(newsmessage.getMessage());

            //TODO: 읽은 뉴스 배경 색 변경.
            if(!newsmessage.getConfirm().equals("0")){
                holder.ll.setBackgroundColor(Color.WHITE);
            }else
                holder.ll.setBackgroundColor(Color.rgb(218,218,218));

            holder.sts_msg.setTypeface(typeface);
            holder.user_pic.setImageResource(R.drawable.nopic__s_m);
            type = newsmessage.getType();

            if(type == "follow"){
                holder.follow.setVisibility(View.GONE);
                holder.following.setVisibility(View.GONE);
            }else {
                holder.follow.setVisibility(View.GONE);
                holder.following.setVisibility(View.GONE);
            }


            if (newsmessage.getUser_pic().isEmpty()) {
            } else {
                Glide.with(activity).load(newsmessage.getUser_pic()).into(holder.user_pic);
            }

            holder.bind(newsmessage, listener);
            holder.bind2(newsmessage, listener2);
        }
    }


    @Override
    public int getItemCount() {
//        return 0;
        return this.mNoticeList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView user_id, sts_msg, following, follow;
        private ImageView user_pic;
        private RelativeTimeTextView v;
        private SimpleDateFormat df;
        private LinearLayout ll;
        Typeface typeface;

        private SharedPreferences setting;
        private SharedPreferences.Editor editor;

        public MyViewHolder(View itemView) {
            super(itemView);
            //TODO: Null error 남 그래서 일단 주석.

            ll = (LinearLayout) itemView.findViewById(R.id.notill);
            typeface = Typeface.createFromAsset(itemView.getContext().getAssets(),"NanumSquareRoundEB.ttf");
            sts_msg = (TextView) itemView.findViewById(R.id.sts_msg);
            user_pic = (ImageView) itemView.findViewById(R.id.user_pic2);
            follow = (TextView) itemView.findViewById(R.id.follow);
            following = (TextView) itemView.findViewById(R.id.following);
            user_pic.setBackground(new ShapeDrawable(new OvalShape()));
            user_pic.setClipToOutline(true);
            sts_msg.setTypeface(typeface);
            setting = itemView.getContext().getSharedPreferences("setting", MODE_PRIVATE);
            editor = setting.edit();
//            v = (RelativeTimeTextView) itemView.findViewById(R.id.timestamp);

        }
        public void bind(final Newsmessage newsmessage, final NoticeAdapter.RecyclerItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int readCount = 0, addedCount;
                    News.NEWS_NOTI_READ = 1;
                    if(newsmessage.getConfirm().equals("0")){

                        readCount++;
                    }
                    listener.onClickListener(newsmessage, getLayoutPosition());
                    Log.e("카운트 테스트 1", String.valueOf(readCount));

                    addedCount = setting.getInt("read_noti_count", 0);
                    Log.e("카운트 테스트 addedCount", String.valueOf(setting.getInt("read_noti_count", 0)));

                    if(addedCount != setting.getInt("news_noti_size", 0)){
                        addedCount += readCount;
                        Log.e("카운트 테스트 같지않으면 추가", String.valueOf(addedCount)+", "+ setting.getInt("news_noti_size", 0));
                        if(addedCount == setting.getInt("news_noti_size", 0)){
                            editor.putBoolean("news_noti_allread", true);
                            editor.putBoolean("news_push", false);
                            editor.commit();
                        }
                    }

                    Log.e("카운트 테스트 2", String.valueOf(addedCount));
                }
            });
        }

        public void bind2(final Newsmessage newsmessage, final NoticeAdapter.RecyclerItemLongClickListener listener2) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener2.onLongClickListener(newsmessage, getLayoutPosition());
                    // 이메서드에서 이벤트에대한 처리를 끝냈음
                    //    그래서 다른데서는 처리할 필요없음 true
                    // 여기서 이벤트 처리를 못했을 경우는 false
                    return false;
                }
            });
        }
    }



    public interface RecyclerItemClickListener {

        void onClickListener(Newsmessage frienewsmessagends, int position);
    }

    public interface RecyclerItemLongClickListener {
        void onLongClickListener(Newsmessage newsmessage, int position);
    }

    public void setSelectedPosition(int currentIndex) {//setSelectedPosition 2
        this.selectedPosition = currentIndex;
    }

    public int getSelectedPosition() {//setSelectedPosition 6 다시changeSelectedPlace로
        return selectedPosition;
    }



}