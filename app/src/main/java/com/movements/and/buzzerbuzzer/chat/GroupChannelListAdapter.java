package com.movements.and.buzzerbuzzer.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.R;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.chatutils.DateUtils;
import com.movements.and.buzzerbuzzer.chatutils.FileUtils;
import com.movements.and.buzzerbuzzer.chatutils.ImageUtils;
import com.movements.and.buzzerbuzzer.chatutils.TextUtils;
import com.movements.and.buzzerbuzzer.chatutils.TypingIndicator;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;
import com.sendbird.android.shadow.com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Displays a list of Group Channels within a SendBird application.
 */
public class GroupChannelListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private String TAG = "GroupChannelListAdapter";

    private List<GroupChannel> mChannelList;
    private static Context mContext;
    private List<User> mUsers;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(GroupChannel channel);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(GroupChannel channel);
    }

    public GroupChannelListAdapter(Context context) {
        mChannelList = new ArrayList<>();
        mUsers = new ArrayList<>();
        mContext = context;
    }

    public void load() {
        try {
            File appDir = new File(mContext.getCacheDir(), SendBird.getApplicationId());
            appDir.mkdirs();

            File dataFile = new File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().getUserId() + "channel_list") + ".data");

            String content = FileUtils.loadFromFile(dataFile);
            String [] dataArray = content.split("\n");

            // Reset channel list, then add cached data.
            mChannelList.clear();
            for(int i = 0; i < dataArray.length; i++) {
                mChannelList.add((GroupChannel) BaseChannel.buildFromSerializedData(Base64.decode(dataArray[i], Base64.DEFAULT | Base64.NO_WRAP)));
            }

            notifyDataSetChanged();
        } catch(Exception e) {
            // Nothing to load.
        }
    }

    public void save() {
        try {
            StringBuilder sb = new StringBuilder();
            if (mChannelList != null && mChannelList.size() > 0) {
                // Convert current data into string.
                GroupChannel channel = null;
                for (int i = 0; i < Math.min(mChannelList.size(), 100); i++) {
                    channel = mChannelList.get(i);
                    sb.append("\n");
                    sb.append(Base64.encodeToString(channel.serialize(), Base64.DEFAULT | Base64.NO_WRAP));
                }
                // Remove first newline.
                sb.delete(0, 1);

                String data = sb.toString();
                String md5 = TextUtils.generateMD5(data);

                // Save the data into file.
                File appDir = new File(mContext.getCacheDir(), SendBird.getApplicationId());
                appDir.mkdirs();

                File hashFile = new File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().getUserId() + "channel_list") + ".hash");
                File dataFile = new File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().getUserId() + "channel_list") + ".data");

                try {
                    String content = FileUtils.loadFromFile(hashFile);
                    // If data has not been changed, do not save.
                    if(md5.equals(content)) {
                        return;
                    }
                } catch(IOException e) {
                    // File not found. Save the data.
                }

                FileUtils.saveToFile(dataFile, data);
                FileUtils.saveToFile(hashFile, md5);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_group_channel, parent, false);

        return new ChannelHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((ChannelHolder)holder).bind(mContext, mChannelList.get(position), mItemClickListener, mItemLongClickListener);
    }

    @Override
    public int getItemCount() {
        return mChannelList.size();
    }


    public void setGroupChannelList(List<GroupChannel> channelList) {
        Log.e("셋그룹채널리스트","여기다!!");
        mChannelList = channelList;

        for(int i=0; i< mChannelList.size(); i++){
            if(mChannelList.get(i).getMemberCount() == 1){
                Log.e("이거 방 하나짜리다!!", String.valueOf(mChannelList.get(i).getMembers()));
                //mChannelList.remove(mChannelList.get(i));

                mChannelList.get(i).leave(new GroupChannel.GroupChannelLeaveHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {
                            // Error!
                            return;
                        }
                    }
                });
                //i--;
                notifyDataSetChanged();
            }
        }
        notifyDataSetChanged();
    }

    public void addLast(GroupChannel channel) {
        Log.e("로드넥스트채널리스트", "어댑터");
        mChannelList.add(channel);
        notifyDataSetChanged();
    }

    // If the channel is not in the list yet, adds it.
    // If it is, finds the channel in current list, and replaces it.
    // Moves the updated channel to the front of the list.
    public void updateOrInsert(BaseChannel channel) {
        if (!(channel instanceof GroupChannel)) {
            Log.v(GroupChannelListAdapter.class.getSimpleName(), "!(channel instanceof GroupChannel)");
            return;
        }

        GroupChannel groupChannel = (GroupChannel) channel;

        for (int i = 0; i < mChannelList.size(); i++) {
            if (mChannelList.get(i).getUrl().equals(groupChannel.getUrl())) {
                mChannelList.remove(mChannelList.get(i));
                mChannelList.add(0, groupChannel);
                notifyDataSetChanged();
                Log.v(GroupChannelListAdapter.class.getSimpleName(), "Channel replaced.");
                return;
            }
        }

        mChannelList.add(0, groupChannel);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    /**
     * A ViewHolder that contains UI to display information about a GroupChannel.
     */
    private static class ChannelHolder extends RecyclerView.ViewHolder {
        private String TAG = "GroupChannelListAdapter";

        private SharedPreferences setting;
        private SharedPreferences.Editor editor;
        private JsonConverter jc;

        TextView topicText, lastMessageText, unreadCountText, dateText, memberCountText;
        ImageView coverImage;
        LinearLayout typingIndicatorContainer;

        Typeface typefaceBold, typefaceExtraBold;

        private PasswordEn passwordEn;

        private String id, mPassword, password;
        private String k, friend_id;
        private String fri_pic_src, fri_id, fri_nick;

        ChannelHolder(View itemView) {
            super(itemView);

            //외부 폰트
            typefaceBold = Typeface.createFromAsset(itemView.getContext().getAssets(),"NanumSquareRoundB.ttf");
            typefaceExtraBold = Typeface.createFromAsset(itemView.getContext().getAssets(),"NanumSquareRoundEB.ttf");

            setting = mContext.getSharedPreferences("setting",Context.MODE_PRIVATE);
//                    mContext.getSharedPreferences("setting", Context.MODE_PRIVATE);
            editor = setting.edit();
            passwordEn = new PasswordEn();

            id = setting.getString("user_id","");
            password = setting.getString("user_pw","");
            mPassword = passwordEn.PasswordEn(password);

            topicText = (TextView) itemView.findViewById(R.id.text_group_channel_list_topic);
            lastMessageText = (TextView) itemView.findViewById(R.id.text_group_channel_list_message);
            unreadCountText = (TextView) itemView.findViewById(R.id.text_group_channel_list_unread_count);
            dateText = (TextView) itemView.findViewById(R.id.text_group_channel_list_date);
            //memberCountText = (TextView) itemView.findViewById(R.id.text_group_channel_list_member_count);
            coverImage = (ImageView) itemView.findViewById(R.id.image_group_channel_list_cover);

            typingIndicatorContainer = (LinearLayout) itemView.findViewById(R.id.container_group_channel_list_typing_indicator);

            topicText.setTypeface(typefaceBold);
            lastMessageText.setTypeface(typefaceBold);
            unreadCountText.setTypeface(typefaceExtraBold);
            dateText.setTypeface(typefaceExtraBold);

            jc = new JsonConverter();//제이슨컨퍼터//

//            friend_id = null;


        }

        /**
         * Binds views in the ViewHolder to information contained within the Group Channel.
         * @param context
         * @param channel
         * @param clickListener A listener that handles simple clicks.
         * @param longClickListener A listener that handles long clicks.
         */
        @SuppressLint("LongLogTag")
        void bind(final Context context, final GroupChannel channel,
                  @Nullable final OnItemClickListener clickListener,
                  @Nullable final OnItemLongClickListener longClickListener) {

            //TODO:바로아래 방이름임.
            //topicText.setText(fri_nick);
//            topicText.setText(TextUtils.getGroupChannelTitle(channel));
//            Log.e("방제 ",TextUtils.getGroupChannelTitle(channel));
            //memberCountText.setText(String.valueOf(channel.getMemberCount()));

            List<Member> members = channel.getMembers();
            StringBuffer names = new StringBuffer();
            ArrayList<String> test = new ArrayList<String>();
            test.clear();
            //TODO:맴버 수,......음..
            Log.e(TAG, "방 맴버수 "  + members.size());



            for (Member member : members) {

                Log.e(TAG+" 284", String.valueOf(members.size()));

                if (member.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                    continue;
                }

                Log.d(" 290",  member.getUserId());

                names.append(", " + member.getNickname());
                test.add(member.getUserId());
            }

            String[] arr = test.toArray(new String[test.size()]);
            try{
                friend_id = arr[0];
            }catch(ArrayIndexOutOfBoundsException e){
                return;
            }

            Log.e(TAG, friend_id.toString());

            if(!friend_id.isEmpty()){
                getchatList();
            }

            k=names.delete(0, 2).toString();
            Log.d("채팅닉2", k);
            Log.d("채팅ID2", friend_id);



            //TODO:이부분인가?

            //ImageUtils.displayRoundImageFromUrl(context, channel.getCoverUrl(), coverImage);
            //ImageUtils.displayRoundImageFromUrl(context, getSender().getProfileUrl(), profileImage);

            //ImageUtils.displayRoundImageFromUrl(context, user..getProfileUrl(), coverImage);

//            Log.d(TAG + "이미지 URL 확인", channel.getInviter().getProfileUrl());

            int unreadCount = channel.getUnreadMessageCount();
            // If there are no unread messages, hide the unread count badge.
            if (unreadCount == 0) {
                unreadCountText.setVisibility(View.INVISIBLE);
//                editor.putBoolean("chat_channel_new_message", false);
//                editor.putBoolean("news_push", false);
//                editor.commit();
            } else {
                unreadCountText.setVisibility(View.VISIBLE);
                unreadCountText.setText(String.valueOf(channel.getUnreadMessageCount()));
//                editor.putBoolean("chat_channel_new_message", true);
//                editor.commit();
//                Log.e(TAG +" chat_channel_new_message", String.valueOf(setting.getBoolean("chat_channel_new_message", false)));
            }
            //topicText.setText(fri_nick);

            BaseMessage lastMessage = channel.getLastMessage();
            if (lastMessage != null) {
                // Display information about the most recently sent message in the channel.
                dateText.setText(String.valueOf(DateUtils.formatDateTime(lastMessage.getCreatedAt())));

                // Bind last message text according to the type of message. Specifically, if
                // the last message is a File Message, there must be special formatting.
                if (lastMessage instanceof UserMessage) {
                    lastMessageText.setText(((UserMessage) lastMessage).getMessage());
                    //topicText.setText(fri_nick);
                } else if (lastMessage instanceof AdminMessage) {
                    lastMessageText.setText(((AdminMessage) lastMessage).getMessage());
                } else {
                    String lastMessageString = String.format(
                            context.getString(R.string.group_channel_list_file_message_text),
                            ((FileMessage) lastMessage).getSender().getNickname());
                    lastMessageText.setText(lastMessageString);
                }
            }

            /*
             * Set up the typing indicator.
             * A typing indicator is basically just three dots contained within the layout
             * that animates. The animation is implemented in the {@link TypingIndicator#animate() class}
             */
            ArrayList<ImageView> indicatorImages = new ArrayList<>();
            indicatorImages.add((ImageView) typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_1));
            indicatorImages.add((ImageView) typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_2));
            indicatorImages.add((ImageView) typingIndicatorContainer.findViewById(R.id.typing_indicator_dot_3));

            TypingIndicator indicator = new TypingIndicator(indicatorImages, 600);
            indicator.animate();

            // debug
//            typingIndicatorContainer.setVisibility(View.VISIBLE);
//            lastMessageText.setText(("Someone 님이 입력중입니다"));

            // If someone in the channel 님이 입력중입니다, display the typing indicator.
            if (channel.isTyping()) {
                //typingIndicatorContainer.setVisibility(View.VISIBLE);
                //lastMessageText.setText(("상대방이 메세지를 입력중입니다"));
            } else {
                // Display typing indicator only when someone 님이 입력중입니다
                typingIndicatorContainer.setVisibility(View.GONE);
            }

            // Set an OnClickListener to this item.
            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onItemClick(channel);
                    }
                });
            }

            // Set an OnLongClickListener to this item.
            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onItemLongClick(channel);

                        // return true if the callback consumed the long click
                        return true;
                    }
                });
            }
            //topicText.setText(fri_nick);
        }

        private void getchatList() {//로그인
//            final String id = idex.getText().toString().trim();
//            final String password = pwdex.getText().toString().trim();
//            final String mPassword = passwordEn.PasswordEn(password);
//            final String userNickName = setting.getString("user_nickname", null);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
                public String nickname, f_id, pic_src;

                @Override
                public void onResponse(String response) {

                    Log.e(TAG+"263",response);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("result").equals("success")) {//성공시
                            // if(json.getString("data").equals("login_result"))
                            JSONArray data = json.getJSONArray("data");
//                            JSONObject row = null;
//                            row = data.getJSONObject(0);
                            for (int i = 0; i < data.length(); i++) {
                                nickname = data.getJSONObject(i).has("nickname") ? data.getJSONObject(i).getString("nickname") : "";
                                pic_src = data.getJSONObject(i).has("pic_src") ? data.getJSONObject(i).getString("pic_src") : "";
                                f_id = data.getJSONObject(i).has("id") ? data.getJSONObject(i).getString("id") : "";
                            }
//
                            //TODO:JSONArray 이용할꺼임. 위위위위 위를 봐
                            fri_pic_src = pic_src;
                            fri_id = id;
                            fri_nick = nickname;

                            JsonObject obj = new JsonObject();

                            obj.addProperty("cover_url", fri_nick);
                            topicText.setText(fri_nick);
                            ImageUtils.displayRoundImageFromUrl(mContext, fri_pic_src, coverImage);

                        } else {//json.getString("result").equals("fail")시
//                            Toast.makeText(getApplicationContext(), "기존 계정과 일치하지 않습니다. 계정이 없으시면 계정을 만들 수 있습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(getApplicationContext(), "로그인 실패 인터넷 연결을 확인해주세요!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "login Error" + error);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //params.put("param", jc.createJsonParam("login", new String[]{id, mPassword}));//login 서비스코드 id,mPassword(암호화된 비밀번호)
                    params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_59_get_chat_list_data", new String[]{friend_id}));//login 서비스코드 id,mPassword(암호화된 비밀번호)
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(itemView.getContext());
            requestQueue.add(stringRequest);
        }

    }
}
