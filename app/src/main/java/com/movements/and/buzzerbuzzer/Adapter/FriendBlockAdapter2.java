package com.movements.and.buzzerbuzzer.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.Friend;
import com.movements.and.buzzerbuzzer.R;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by samkim on 2017. 3. 23..
 */

public class FriendBlockAdapter2 extends RecyclerView.Adapter<FriendBlockAdapter2.MyViewHolder> {
    private Context mContext;

    private ArrayList<Friend> mFriendList;
    private FriendBlockAdapter2.RecyclerItemClickListener listener;
    private int selectedPosition;
    private static final String TAG = "FriendBlockAdapter2";

    public FriendBlockAdapter2(Context mylocation, ArrayList<Friend> mfriendList, RecyclerItemClickListener recyclerItemClickListener) {
        this.mContext = mylocation;
        this.mFriendList = mfriendList;
        this.listener = recyclerItemClickListener;
    }


    @Override
    public FriendBlockAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendblock_row, parent, false);

        return new FriendBlockAdapter2.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Friend friends = mFriendList.get(position);

        if (friends != null) {

            holder.user_id.setText(friends.getNickname());

            holder.user_pic.setImageResource(R.drawable.nopic__s_m);
            if (friends.getPic_src().isEmpty()) {
            } else {
                Picasso.with(mContext).load(friends.getPic_src()).placeholder(R.drawable.nopic__s_m).into(holder.user_pic);
            }


            holder.aSwitch.setChecked(false);
            switch (friends.getLocation_release_yn()) {
                case 1:
                    holder.aSwitch.setChecked(false);
                    break;
                case 0:
                    holder.aSwitch.setChecked(true);
                    break;

            }

            holder.bind(friends, listener);
            holder.bind2(friends);
        }
    }




    @Override
    public int getItemCount() {
        return this.mFriendList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {


        private TextView user_id, sts_msg;
        private ImageView user_pic;
        private Switch aSwitch;

        private Friend friends;
        private SharedPreferences setting;
        private JsonConverter jc;
        private PasswordEn passwordEn;

        private String mPassword, password;

        public static boolean MAINSWITCHCHECk = false;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public MyViewHolder(View itemView) {
            super(itemView);
            //Log.d(TAG, "==========MyViewHolder==========");
            user_id = (TextView) itemView.findViewById(R.id.age_tx);
            sts_msg = (TextView) itemView.findViewById(R.id.sts_msg);

            user_pic = (ImageView) itemView.findViewById(R.id.user_pic2);
            aSwitch = (Switch) itemView.findViewById(R.id.switch1);

            user_pic.setBackground(new ShapeDrawable(new OvalShape()));
            user_pic.setClipToOutline(true);
            jc = new JsonConverter();
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //Toast.makeText(mContext, "on", Toast.LENGTH_SHORT).show();
                        if (friends != null) {
                            if (MAINSWITCHCHECk) {
                                friendblock(friends.getFr_id(), "0");
                            }
                        }
                    } else {
                        if (friends != null) {
                            if (MAINSWITCHCHECk) {
                                friendblock(friends.getFr_id(), "1");
                            }
                        }
                    }
                }
            });
        }

        public void bind(final Friend friends, final FriendBlockAdapter2.RecyclerItemClickListener listener) {//RecyclerItemClickListener 추가후 4
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickListener(friends, getLayoutPosition());
                }
            });

        }

        public void bind2(Friend friends) {
            this.friends = friends;
        }

        private void friendblock(String s, String s1) {
            setting = itemView.getContext().getSharedPreferences("setting", itemView.getContext().MODE_PRIVATE);

            passwordEn = new PasswordEn();
            password = setting.getString("user_pw","");
            mPassword = passwordEn.PasswordEn(password);
            final String id = setting.getString("user_id", "");
            final String fr_id = s;

            final String able = s1;
            StringRequest multiPartRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("result").equals("success")) {//성공시
                            if(able.equals("0")){
                                //Toast.makeText(itemView.getContext(), "차단 되었습니다.", Toast.LENGTH_SHORT).show();
                            }else {
                                //Toast.makeText(itemView.getContext(), "차단이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                            //mActivity.setResult(1);
                        } else {//json.getString("result").equals("fail")시
                            Toast.makeText(itemView.getContext(), "변경을 할 수가 없습니다. 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(itemView.getContext(), "" + error, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "block_gps_follow Error :" + error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_31_block_gps_individual_user", new String[]{id, fr_id, able, ""}));
                    return params;
                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(itemView.getContext());
            requestQueue.add(multiPartRequest);

        }
    }

    public interface RecyclerItemClickListener {

        void onClickListener(Friend friends, int position);
    }

    public void setSelectedPosition(int currentIndex) {//setSelectedPosition 2
        this.selectedPosition = currentIndex;
    }

    public int getSelectedPosition() {//setSelectedPosition 6 다시changeSelectedPlace로
        return selectedPosition;
    }


}