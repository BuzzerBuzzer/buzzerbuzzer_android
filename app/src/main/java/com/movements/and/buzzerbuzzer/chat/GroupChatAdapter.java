package com.movements.and.buzzerbuzzer.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Buzzerprofile;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Followingprofile;
import com.movements.and.buzzerbuzzer.Followprofile;
import com.movements.and.buzzerbuzzer.FriendList;
import com.movements.and.buzzerbuzzer.FriendProfile;
import com.movements.and.buzzerbuzzer.Notice;
import com.movements.and.buzzerbuzzer.OpeningActivity;
import com.movements.and.buzzerbuzzer.R;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.chatutils.DateUtils;
import com.movements.and.buzzerbuzzer.chatutils.FileUtils;
import com.movements.and.buzzerbuzzer.chatutils.ImageUtils;
import com.movements.and.buzzerbuzzer.chatutils.TextUtils;
import com.movements.and.buzzerbuzzer.chatutils.UrlPreviewInfo;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.movements.and.buzzerbuzzer.FriendList.id;
import static com.movements.and.buzzerbuzzer.FriendList.mPassword;


public class GroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String URL_PREVIEW_CUSTOM_TYPE = "url_preview";

    private static final int VIEW_TYPE_USER_MESSAGE_ME = 10;
    private static final int VIEW_TYPE_USER_MESSAGE_OTHER = 11;
    private static final int VIEW_TYPE_FILE_MESSAGE_ME = 20;
    private static final int VIEW_TYPE_FILE_MESSAGE_OTHER = 21;
    private static final int VIEW_TYPE_FILE_MESSAGE_IMAGE_ME = 22;
    private static final int VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER = 23;
    private static final int VIEW_TYPE_FILE_MESSAGE_VIDEO_ME = 24;
    private static final int VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER = 25;
    private static final int VIEW_TYPE_ADMIN_MESSAGE = 30;

    private Context mContext;
    private GroupChannel mChannel;
    private List<BaseMessage> mMessageList;

    private OnItemClickListener mItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    private ArrayList<String> mFailedMessageIdList = new ArrayList<>();
    private Hashtable<String, Uri> mTempFileMessageUriTable = new Hashtable<>();
    private boolean mIsMessageListLoading;

    private SharedPreferences setting;
    private JsonConverter jc;
    private static Typeface typefaceBold;
    private Typeface typefaceExtraBold;
    interface OnItemLongClickListener {
        void onUserMessageItemLongClick(UserMessage message);

        void onFileMessageItemLongClick(FileMessage message);

        void onAdminMessageItemLongClick(AdminMessage message);
    }

    interface OnItemClickListener {
        void onProfilePicClick(String id, String frclass);
        void onUserMessageItemClick(UserMessage message);

        void onFileMessageItemClick(FileMessage message);
    }


    GroupChatAdapter(Context context) {
        mMessageList = new ArrayList<>();
        mContext = context;
    }

    void setContext(Context context) {
        mContext = context;
    }

    public void load(String channelUrl) {
        try {
            File appDir = new File(mContext.getCacheDir(), SendBird.getApplicationId());
            appDir.mkdirs();

            File dataFile = new File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().getUserId() + channelUrl) + ".data");

            String content = FileUtils.loadFromFile(dataFile);
            String[] dataArray = content.split("\n");

            mChannel = (GroupChannel) GroupChannel.buildFromSerializedData(Base64.decode(dataArray[0], Base64.DEFAULT | Base64.NO_WRAP));



            // Reset message list, then add cached messages.
            mMessageList.clear();
            for (int i = 1; i < dataArray.length; i++) {
                mMessageList.add(BaseMessage.buildFromSerializedData(Base64.decode(dataArray[i], Base64.DEFAULT | Base64.NO_WRAP)));
            }

            notifyDataSetChanged();
        } catch (Exception e) {
            // Nothing to load.
        }
    }

    public void save() {
        try {
            StringBuilder sb = new StringBuilder();
            if (mChannel != null) {
                // Convert current data into string.
                sb.append(Base64.encodeToString(mChannel.serialize(), Base64.DEFAULT | Base64.NO_WRAP));
                BaseMessage message = null;
                for (int i = 0; i < Math.min(mMessageList.size(), 100); i++) {
                    message = mMessageList.get(i);
                    if (!isTempMessage(message)) {
                        sb.append("\n");
                        sb.append(Base64.encodeToString(message.serialize(), Base64.DEFAULT | Base64.NO_WRAP));
                    }
                }

                String data = sb.toString();
                String md5 = TextUtils.generateMD5(data);

                // Save the data into file.
                File appDir = new File(mContext.getCacheDir(), SendBird.getApplicationId());
                appDir.mkdirs();

                File hashFile = new File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().getUserId() + mChannel.getUrl()) + ".hash");
                File dataFile = new File(appDir, TextUtils.generateMD5(SendBird.getCurrentUser().getUserId() + mChannel.getUrl()) + ".data");

                try {
                    String content = FileUtils.loadFromFile(hashFile);
                    // If data has not been changed, do not save.
                    if (md5.equals(content)) {
                        return;
                    }
                } catch (IOException e) {
                    // File not found. Save the data.
                }

                FileUtils.saveToFile(dataFile, data);
                FileUtils.saveToFile(hashFile, md5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inflates the correct layout according to the View Type.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        System.out.println("viewType :: " + viewType);
        typefaceBold = Typeface.createFromAsset(mContext.getResources().getAssets(),"NanumSquareRoundB.ttf");
        switch (viewType) {
            case VIEW_TYPE_USER_MESSAGE_ME:
                View myUserMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_user_me, parent, false);
                return new MyUserMessageHolder(myUserMsgView);
            case VIEW_TYPE_USER_MESSAGE_OTHER:
                View otherUserMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_user_other, parent, false);
                return new OtherUserMessageHolder(otherUserMsgView);
            case VIEW_TYPE_ADMIN_MESSAGE:
                View adminMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_admin, parent, false);
                return new AdminMessageHolder(adminMsgView);
            case VIEW_TYPE_FILE_MESSAGE_ME:
                View myFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_me, parent, false);
                return new MyFileMessageHolder(myFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_OTHER:
                View otherFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_other, parent, false);
                return new OtherFileMessageHolder(otherFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_ME:
                View myImageFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_image_me, parent, false);
                return new MyImageFileMessageHolder(myImageFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER:
                View otherImageFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_image_other, parent, false);
                return new OtherImageFileMessageHolder(otherImageFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_VIDEO_ME:
                View myVideoFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_video_me, parent, false);
                return new MyVideoFileMessageHolder(myVideoFileMsgView);
            case VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER:
                View otherVideoFileMsgView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_group_chat_file_video_other, parent, false);
                return new OtherVideoFileMessageHolder(otherVideoFileMsgView);

            default:
                return null;

        }
    }

    /**
     * Binds variables in the BaseMessage to UI components in the ViewHolder.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseMessage message = mMessageList.get(position);
        boolean isContinuous = false;
        boolean isNewDay = false;
        boolean isTempMessage = false;
        boolean isFailedMessage = false;
        Uri tempFileMessageUri = null;
        jc = new JsonConverter();

        // If there is at least one item preceding the current one, check the previous message.
        if (position < mMessageList.size() - 1) {
            BaseMessage prevMessage = mMessageList.get(position + 1);

            // If the date of the previous message is different, display the date before the message,
            // and also set isContinuous to false to show information such as the sender's nickname
            // and profile image.
            if (!DateUtils.hasSameDate(message.getCreatedAt(), prevMessage.getCreatedAt())) {
                isNewDay = true;
                isContinuous = false;
            } else {
                isContinuous = isContinuous(message, prevMessage);
            }
        } else if (position == mMessageList.size() - 1) {
            isNewDay = true;
        }

        isTempMessage = isTempMessage(message);
        tempFileMessageUri = getTempFileMessageUri(message);
        isFailedMessage = isFailedMessage(message);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_USER_MESSAGE_ME:
                ((MyUserMessageHolder) holder).bind(mContext, (UserMessage) message, mChannel, isContinuous, isNewDay, isTempMessage, isFailedMessage, mItemClickListener, mItemLongClickListener);
                break;
            case VIEW_TYPE_USER_MESSAGE_OTHER:
                ((OtherUserMessageHolder) holder).bind(mContext, (UserMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener, mItemLongClickListener);
                break;
            case VIEW_TYPE_ADMIN_MESSAGE:
                ((AdminMessageHolder) holder).bind(mContext, (AdminMessage) message, mChannel, isNewDay);
                break;
            case VIEW_TYPE_FILE_MESSAGE_ME:
                ((MyFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_OTHER:
                ((OtherFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_ME:
                ((MyImageFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER:
                ((OtherImageFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_VIDEO_ME:
                ((MyVideoFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isTempMessage, isFailedMessage, tempFileMessageUri, mItemClickListener);
                break;
            case VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER:
                ((OtherVideoFileMessageHolder) holder).bind(mContext, (FileMessage) message, mChannel, isNewDay, isContinuous, mItemClickListener);
                break;
            default:
                break;
        }
    }

    /**
     * Declares the View Type according to the type of message and the sender.
     */
    @Override
    public int getItemViewType(int position) {

        System.out.println("getItemViewType");

        BaseMessage message = mMessageList.get(position);

        if (message instanceof UserMessage) {
            UserMessage userMessage = (UserMessage) message;
            // If the sender is current user
            if(userMessage != null){
                if (userMessage.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                    return VIEW_TYPE_USER_MESSAGE_ME;
                } else {
                    return VIEW_TYPE_USER_MESSAGE_OTHER;
                }
            }
        } else if (message instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) message;
            if (fileMessage.getType().toLowerCase().startsWith("image")) {
                // If the sender is current user
                if (fileMessage.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                    return VIEW_TYPE_FILE_MESSAGE_IMAGE_ME;
                } else {
                    return VIEW_TYPE_FILE_MESSAGE_IMAGE_OTHER;
                }
            } else if (fileMessage.getType().toLowerCase().startsWith("video")) {
                if (fileMessage.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                    return VIEW_TYPE_FILE_MESSAGE_VIDEO_ME;
                } else {
                    return VIEW_TYPE_FILE_MESSAGE_VIDEO_OTHER;
                }
            } else {
                if (fileMessage.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                    return VIEW_TYPE_FILE_MESSAGE_ME;
                } else {
                    return VIEW_TYPE_FILE_MESSAGE_OTHER;
                }
            }
        } else if (message instanceof AdminMessage) {
            return VIEW_TYPE_ADMIN_MESSAGE;
        }



        return -1;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    void setChannel(GroupChannel channel) {
        mChannel = channel;
    }

    public boolean isTempMessage(BaseMessage message) {
        return message.getMessageId() == 0;
    }

    public boolean isFailedMessage(BaseMessage message) {
        if (!isTempMessage(message)) {
            return false;
        }

        if (message instanceof UserMessage) {
            int index = mFailedMessageIdList.indexOf(((UserMessage) message).getRequestId());
            if (index >= 0) {
                return true;
            }
        } else if (message instanceof FileMessage) {
            int index = mFailedMessageIdList.indexOf(((FileMessage) message).getRequestId());
            if (index >= 0) {
                return true;
            }
        }

        return false;
    }


    public Uri getTempFileMessageUri(BaseMessage message) {
        if (!isTempMessage(message)) {
            return null;
        }

        if (!(message instanceof FileMessage)) {
            return null;
        }

        return mTempFileMessageUriTable.get(((FileMessage) message).getRequestId());
    }

    public void markMessageFailed(String requestId) {
        mFailedMessageIdList.add(requestId);
        notifyDataSetChanged();
    }

    public void removeFailedMessage(BaseMessage message) {
        if (message instanceof UserMessage) {
            mFailedMessageIdList.remove(((UserMessage) message).getRequestId());
            mMessageList.remove(message);
        } else if (message instanceof FileMessage) {
            mFailedMessageIdList.remove(((FileMessage) message).getRequestId());
            mTempFileMessageUriTable.remove(((FileMessage) message).getRequestId());
            mMessageList.remove(message);
        }

        notifyDataSetChanged();
    }

    public void markMessageSent(BaseMessage message) {
        Object msg = null;

        System.out.println("markMessageSent :: 1");
        for (int i = mMessageList.size() - 1; i >= 0; i--) {
            msg = mMessageList.get(i);
            System.out.println("msg :: 2" + msg);
            if (message instanceof UserMessage && msg instanceof UserMessage) {
                System.out.println("msg :: msg");
                if (((UserMessage) msg).getRequestId().equals(((UserMessage) message).getRequestId())) {
                    mMessageList.set(i, message);
                    System.out.println("msg :: " + message);
                    notifyDataSetChanged();
                    return;
                }
            } else if (message instanceof FileMessage && msg instanceof FileMessage) {
                System.out.println("msg :: file");
                if (((FileMessage) msg).getRequestId().equals(((FileMessage) message).getRequestId())) {
                    mTempFileMessageUriTable.remove(((FileMessage) message).getRequestId());
                    mMessageList.set(i, message);
                    System.out.println("msg :: file" + message);
                    notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    void addTempFileMessageInfo(FileMessage message, Uri uri) {
        mTempFileMessageUriTable.put(message.getRequestId(), uri);
    }

    void addFirst(BaseMessage message) {
        mMessageList.add(0, message);
        notifyDataSetChanged();
    }

    void delete(long msgId) {
        for (BaseMessage msg : mMessageList) {
            if (msg.getMessageId() == msgId) {
                mMessageList.remove(msg);
                notifyDataSetChanged();
                break;
            }
        }
    }

    private synchronized boolean isMessageListLoading() {
        return mIsMessageListLoading;
    }

    private synchronized void setMessageListLoading(boolean tf) {
        mIsMessageListLoading = tf;
    }

    /**
     * Notifies that the user has read all (previously unread) messages in the channel.
     * Typically, this would be called immediately after the user enters the chat and loads
     * its most recent messages.
     */
    public void markAllMessagesAsRead() {
        if (mChannel != null) {
            mChannel.markAsRead();
        }
        notifyDataSetChanged();
    }

    /**
     * Load old message list.
     *
     * @param limit
     * @param handler
     */
    public void loadPreviousMessages(int limit, final BaseChannel.GetMessagesHandler handler) {
        if (isMessageListLoading()) {
            return;
        }

        long oldestMessageCreatedAt = Long.MAX_VALUE;
        if (mMessageList.size() > 0) {
            oldestMessageCreatedAt = mMessageList.get(mMessageList.size() - 1).getCreatedAt();
        }

        setMessageListLoading(true);
        mChannel.getPreviousMessagesByTimestamp(oldestMessageCreatedAt, false, limit, true, BaseChannel.MessageTypeFilter.ALL, null, new BaseChannel.GetMessagesHandler() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e) {
                if (handler != null) {
                    handler.onResult(list, e);
                }

                setMessageListLoading(false);
                if (e != null) {
                    e.printStackTrace();
                    return;
                }

                for (BaseMessage message : list) {
                    mMessageList.add(message);
                }

                notifyDataSetChanged();
            }
        });
    }

    /**
     * Replaces current message list with new list.
     * Should be used only on initial load or refresh.
     */
    public void loadLatestMessages(int limit, final BaseChannel.GetMessagesHandler handler) {
        if (isMessageListLoading()) {
            return;
        }

        setMessageListLoading(true);
        mChannel.getPreviousMessagesByTimestamp(Long.MAX_VALUE, true, limit, true, BaseChannel.MessageTypeFilter.ALL, null, new BaseChannel.GetMessagesHandler() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e) {
                if (handler != null) {
                    handler.onResult(list, e);
                }

                setMessageListLoading(false);
                if (e != null) {
                    e.printStackTrace();
                    return;
                }

                if (list.size() <= 0) {
                    return;
                }

                for (BaseMessage message : mMessageList) {
                    if (isTempMessage(message) || isFailedMessage(message)) {
                        list.add(0, message);
                    }
                }

                mMessageList.clear();

                for (BaseMessage message : list) {

                    mMessageList.add(message);
                }

                notifyDataSetChanged();
            }
        });
    }

    public void setItemLongClickListener(OnItemLongClickListener listener) {
        mItemLongClickListener = listener;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * Checks if the current message was sent by the same person that sent the preceding message.
     * <p>
     * This is done so that redundant UI, such as sender nickname and profile picture,
     * does not have to displayed when not necessary.
     */
    private boolean isContinuous(BaseMessage currentMsg, BaseMessage precedingMsg) {
        // null check
        if (currentMsg == null || precedingMsg == null) {
            return false;
        }

        if (currentMsg instanceof AdminMessage && precedingMsg instanceof AdminMessage) {
            return true;
        }

        User currentUser = null, precedingUser = null;

        if (currentMsg instanceof UserMessage) {
            currentUser = ((UserMessage) currentMsg).getSender();
        } else if (currentMsg instanceof FileMessage) {
            currentUser = ((FileMessage) currentMsg).getSender();
        }

        if (precedingMsg instanceof UserMessage) {
            precedingUser = ((UserMessage) precedingMsg).getSender();
        } else if (precedingMsg instanceof FileMessage) {
            precedingUser = ((FileMessage) precedingMsg).getSender();
        }

        // If admin message or
        return !(currentUser == null || precedingUser == null)
                && currentUser.getUserId().equals(precedingUser.getUserId());


    }


    private static class AdminMessageHolder extends RecyclerView.ViewHolder {
        private TextView messageText, dateText;
        InputMethodManager imm;
        AdminMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_group_chat_message);
            dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        void bind(Context context, AdminMessage message, GroupChannel channel, boolean isNewDay) {
            messageText.setText(message.getMessage());

            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }
        }
    }

    private static class MyUserMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, readReceiptText, dateText;
        ViewGroup urlPreviewContainer;
        TextView urlPreviewSiteNameText, urlPreviewTitleText, urlPreviewDescriptionText;
        ImageView urlPreviewMainImageView;
        View padding, dateText2;
        RelativeLayout relativeLayout;
        InputMethodManager imm;

        MyUserMessageHolder(final View itemView) {
            super(itemView);
            imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            messageText = (TextView) itemView.findViewById(R.id.text_group_chat_message);
            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            dateText2 = itemView.findViewById(R.id.text_group_chat_date2);

            urlPreviewContainer = (ViewGroup) itemView.findViewById(R.id.url_preview_container);
            urlPreviewSiteNameText = (TextView) itemView.findViewById(R.id.text_url_preview_site_name);
            urlPreviewTitleText = (TextView) itemView.findViewById(R.id.text_url_preview_title);
            urlPreviewDescriptionText = (TextView) itemView.findViewById(R.id.text_url_preview_description);
            urlPreviewMainImageView = (ImageView) itemView.findViewById(R.id.image_url_preview_main);
            padding = itemView.findViewById(R.id.view_group_chat_padding);
        }

        void bind(Context context, final UserMessage message, GroupChannel channel, boolean isContinuous, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, final OnItemClickListener clickListener, final OnItemLongClickListener longClickListener) {
            messageText.setText(message.getMessage());
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));

            if (isFailedMessage) {
                readReceiptText.setText(R.string.str_sendbird_message_failed);
                readReceiptText.setVisibility(View.VISIBLE);
            } else if (isTempMessage) {
                readReceiptText.setText(R.string.str_sendbird_message_sending);
                readReceiptText.setVisibility(View.VISIBLE);
            } else {

                // Since setChannel is set slightly after adapter is created
                if (channel != null) {
                    int readReceipt = channel.getReadReceipt(message);
                    if (readReceipt > 0) {
                        readReceiptText.setVisibility(View.VISIBLE);
                        readReceiptText.setText(String.valueOf(readReceipt));
                    } else {
                        readReceiptText.setVisibility(View.INVISIBLE);
                    }
                }
            }


            // If continuous from previous message, remove extra padding.
            if (isContinuous) {
                padding.setVisibility(View.GONE);
            } else {
                padding.setVisibility(View.VISIBLE);
            }

            // If the message is sent on a different date than the previous one, display the date.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText2.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
                dateText2.setVisibility(View.GONE);
            }

            urlPreviewContainer.setVisibility(View.GONE);
            if (message.getCustomType().equals(URL_PREVIEW_CUSTOM_TYPE)) {
                try {
                    urlPreviewContainer.setVisibility(View.VISIBLE);
                    final UrlPreviewInfo previewInfo = new UrlPreviewInfo(message.getData());
                    urlPreviewSiteNameText.setText("@" + previewInfo.getSiteName());
                    urlPreviewTitleText.setText(previewInfo.getTitle());
                    urlPreviewDescriptionText.setText(previewInfo.getDescription());
                    ImageUtils.displayImageFromUrl(context, previewInfo.getImageUrl(), urlPreviewMainImageView);
                } catch (JSONException e) {
                    urlPreviewContainer.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onUserMessageItemClick(message);
                        imm.hideSoftInputFromWindow(itemView.getWindowToken(), 0);
                    }
                });
            }

            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onUserMessageItemLongClick(message);
                        return true;
                    }
                });
            }
        }

    }

    private static class OtherUserMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, nicknameText, timeText, readReceiptText, dateText;
        ImageView profileImage;
        InputMethodManager imm;
        View dateText2;

        ViewGroup urlPreviewContainer;
        TextView urlPreviewSiteNameText, urlPreviewTitleText, urlPreviewDescriptionText;
        ImageView urlPreviewMainImageView;
        RelativeLayout rl;

        private JsonConverter jc;
        private SharedPreferences setting;

        private PasswordEn passwordEn;

        private String mPassword, password, fr_class;

        public OtherUserMessageHolder(final View itemView) {
            super(itemView);

            imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            messageText = (TextView) itemView.findViewById(R.id.text_group_chat_message);
            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            nicknameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            dateText2 = itemView.findViewById(R.id.text_group_chat_date2);

            nicknameText.setTypeface(typefaceBold);
            urlPreviewContainer = (ViewGroup) itemView.findViewById(R.id.url_preview_container);
            urlPreviewSiteNameText = (TextView) itemView.findViewById(R.id.text_url_preview_site_name);
            urlPreviewTitleText = (TextView) itemView.findViewById(R.id.text_url_preview_title);
            urlPreviewDescriptionText = (TextView) itemView.findViewById(R.id.text_url_preview_description);
            urlPreviewMainImageView = (ImageView) itemView.findViewById(R.id.image_url_preview_main);
//            rl = (RelativeLayout) itemView.findViewById(R.id.chatingk);
//            rl.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(itemView.getContext(), "hideKeyboard", Toast.LENGTH_LONG).show();
//                }
//            });

        }


        void bind(Context context, final UserMessage message, GroupChannel channel, boolean isNewDay, boolean isContinuous, final OnItemClickListener clickListener, final OnItemLongClickListener longClickListener) {

            // Since setChannel is set slightly after adapter is created
            if (channel != null) {
                int readReceipt = channel.getReadReceipt(message);
                if (readReceipt > 0) {
                    readReceiptText.setVisibility(View.VISIBLE);
                    readReceiptText.setText(String.valueOf(readReceipt));
                } else {
                    readReceiptText.setVisibility(View.INVISIBLE);
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText2.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
                dateText2.setVisibility(View.GONE);
            }

            // Hide profile image and nickname if the previous message was also sent by current sender.
            if (isContinuous) {
                profileImage.setVisibility(View.INVISIBLE);
                nicknameText.setVisibility(View.GONE);
                ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), profileImage);//추가
            } else {
                profileImage.setVisibility(View.VISIBLE);
                ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), profileImage);//추가

                nicknameText.setVisibility(View.VISIBLE);
                nicknameText.setText(message.getSender().getNickname());
            }

            messageText.setText(message.getMessage());
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));

            urlPreviewContainer.setVisibility(View.GONE);
            if (message.getCustomType().equals(URL_PREVIEW_CUSTOM_TYPE)) {
                try {
                    urlPreviewContainer.setVisibility(View.VISIBLE);
                    UrlPreviewInfo previewInfo = new UrlPreviewInfo(message.getData());
                    urlPreviewSiteNameText.setText("@" + previewInfo.getSiteName());
                    urlPreviewTitleText.setText(previewInfo.getTitle());
                    urlPreviewDescriptionText.setText(previewInfo.getDescription());
                    ImageUtils.displayImageFromUrl(context, previewInfo.getImageUrl(), urlPreviewMainImageView);
                } catch (JSONException e) {
                    urlPreviewContainer.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //getUser(message.getSender().getUserId());
                    clickListener.onProfilePicClick(message.getSender().getUserId(), "클래스");
                    //OpeningActivity.chatProfileCheck = true;
                    //OpeningActivity.chatProfileId = message.getSender().getUserId();
                }
            });

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onUserMessageItemClick(message);
                        imm.hideSoftInputFromWindow(itemView.getWindowToken(), 0);
                    }
                });
            }
            if (longClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longClickListener.onUserMessageItemLongClick(message);
                        return true;
                    }
                });
            }

        }

        private void getUser(String query) {
            setting = itemView.getContext().getSharedPreferences("setting", itemView.getContext().MODE_PRIVATE);
            final String set_id = query;
            final String id = setting.getString("user_id", "");
            passwordEn = new PasswordEn();
            password = setting.getString("user_pw","");
            mPassword = passwordEn.PasswordEn(password);
            Log.e("아이디 패스워드 : ", id + " " + password);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,

                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            String pic_src = null;
                            String nickname = null;
                            String condition_msg = null;
                            String user_id = null;
                            Log.d("온리스폰스 안까지 ", "왔다");
                            try {
                                Log.e("트라이까지 왔는가?","왔다");
                                JSONObject json = new JSONObject(response);
                                if (json.getString("result").equals("success")) {//성공시
                                    JSONArray data = json.getJSONArray("data");
                                    for (int i = 0; i < data.length(); i++) {
                                        fr_class = data.getJSONObject(i).has("fr_class") ? data.getJSONObject(i).getString("fr_class") : "";
                                        pic_src = data.getJSONObject(i).has("pic_src") ? data.getJSONObject(i).getString("pic_src") : "";
                                        nickname = data.getJSONObject(i).has("nickname") ? data.getJSONObject(i).getString("nickname") : "";
                                        condition_msg = data.getJSONObject(0).has("condition_msg") ? data.getJSONObject(0).getString("condition_msg") : "";
                                        user_id = data.getJSONObject(i).getString("id");//modify
                                    }
                                } else {//json.getString("result").equals("fail")시
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {//서버에러
                            //Toast.makeText(getApplicationContext(), " 유저불러오기 Error!", Toast.LENGTH_LONG).show();
                            Log.e(TAG, " searching_id Error" + error);
                            //addfiend.setVisibility(View.GONE);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_56_search_user_id", new String[]{id, set_id}));
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(itemView.getContext());
            requestQueue.add(stringRequest);

        }

    }

    private static class MyFileMessageHolder extends RecyclerView.ViewHolder {
        TextView fileNameText, timeText, readReceiptText, dateText;

        public MyFileMessageHolder(View itemView) {
            super(itemView);

            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            fileNameText = (TextView) itemView.findViewById(R.id.text_group_chat_file_name);
            readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
        }

        void bind(Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, Uri tempFileMessageUri, final OnItemClickListener listener) {
            fileNameText.setText(message.getName());
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));

            if (isFailedMessage) {
                readReceiptText.setText(R.string.str_sendbird_message_failed);
                readReceiptText.setVisibility(View.VISIBLE);
            } else if (isTempMessage) {
                readReceiptText.setText(R.string.str_sendbird_message_sending);
                readReceiptText.setVisibility(View.VISIBLE);
            } else {
                if (channel != null) {
                    int readReceipt = channel.getReadReceipt(message);
                    if (readReceipt > 0) {
                        readReceiptText.setVisibility(View.VISIBLE);
                        readReceiptText.setText(String.valueOf(readReceipt));
                    } else {
                        readReceiptText.setVisibility(View.INVISIBLE);
                    }
                }

            }
            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
        }
    }

    private static class OtherFileMessageHolder extends RecyclerView.ViewHolder {
        TextView nicknameText, timeText, fileNameText, fileSizeText, dateText, readReceiptText;
        ImageView profileImage;

        public OtherFileMessageHolder(View itemView) {
            super(itemView);

            nicknameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            fileNameText = (TextView) itemView.findViewById(R.id.text_group_chat_file_name);
//            fileSizeText = (TextView) itemView.findViewById(R.id.text_group_chat_file_size);

            profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
            readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
        }

        void bind(Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isContinuous, final OnItemClickListener listener) {
            fileNameText.setText(message.getName());
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));
//            fileSizeText.setText(String.valueOf(message.getSize()));

            // Since setChannel is set slightly after adapter is created, check if null.
            if (channel != null) {
                int readReceipt = channel.getReadReceipt(message);
                if (readReceipt > 0) {
                    readReceiptText.setVisibility(View.VISIBLE);
                    readReceiptText.setText(String.valueOf(readReceipt));
                } else {
                    readReceiptText.setVisibility(View.INVISIBLE);
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            // Hide profile image and nickname if the previous message was also sent by current sender.
            if (isContinuous) {
                profileImage.setVisibility(View.INVISIBLE);
                nicknameText.setVisibility(View.GONE);
            } else {
                profileImage.setVisibility(View.VISIBLE);
                ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), profileImage);

                nicknameText.setVisibility(View.VISIBLE);
                nicknameText.setText(message.getSender().getNickname());
            }

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
        }
    }

    /**
     * A ViewHolder for file messages that are images.
     * Displays only the image thumbnail.
     */
    private static class MyImageFileMessageHolder extends RecyclerView.ViewHolder {

        TextView timeText, readReceiptText, dateText;
        ImageView fileThumbnailImage;

        public MyImageFileMessageHolder(View itemView) {
            super(itemView);

            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            fileThumbnailImage = (ImageView) itemView.findViewById(R.id.image_group_chat_file_thumbnail);
            readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
        }

        void bind(Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, Uri tempFileMessageUri, final OnItemClickListener listener) {
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));

            if (isFailedMessage) {
                readReceiptText.setText(R.string.str_sendbird_message_failed);
                readReceiptText.setVisibility(View.VISIBLE);
            } else if (isTempMessage) {
                readReceiptText.setText(R.string.str_sendbird_message_sending);
                readReceiptText.setVisibility(View.VISIBLE);
            } else {
                // Since setChannel is set slightly after adapter is created, check if null.
                if (channel != null) {
                    int readReceipt = channel.getReadReceipt(message);
                    if (readReceipt > 0) {
                        readReceiptText.setVisibility(View.VISIBLE);
                        readReceiptText.setText(String.valueOf(readReceipt));
                    } else {
                        readReceiptText.setVisibility(View.INVISIBLE);
                    }
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            if (isTempMessage) {
                ImageUtils.displayImageFromUrl(context, tempFileMessageUri.toString(), fileThumbnailImage);
            } else {
                // Get thumbnails from FileMessage
                ArrayList<FileMessage.Thumbnail> thumbnails = (ArrayList<FileMessage.Thumbnail>) message.getThumbnails();

                // If thumbnails exist, get smallest (first) thumbnail and display it in the message
                if (thumbnails.size() > 0) {
                    if (message.getType().toLowerCase().contains("gif")) {
                        ImageUtils.displayGifImageFromUrl(context, message.getUrl(), fileThumbnailImage, thumbnails.get(0).getUrl());
                    } else {
                        ImageUtils.displayImageFromUrl(context, thumbnails.get(0).getUrl(), fileThumbnailImage);
                    }
                } else {
                    if (message.getType().toLowerCase().contains("gif")) {
                        ImageUtils.displayGifImageFromUrl(context, message.getUrl(), fileThumbnailImage, (String) null);
                    } else {
                        ImageUtils.displayImageFromUrl(context, message.getUrl(), fileThumbnailImage);
                    }
                }
            }

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
        }
    }

    private static class OtherImageFileMessageHolder extends RecyclerView.ViewHolder {

        TextView timeText, nicknameText, readReceiptText, dateText;
        ImageView profileImage, fileThumbnailImage;

        public OtherImageFileMessageHolder(View itemView) {
            super(itemView);

            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            nicknameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            fileThumbnailImage = (ImageView) itemView.findViewById(R.id.image_group_chat_file_thumbnail);
            profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
        }

        void bind(Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isContinuous, final OnItemClickListener listener) {
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));

            // Since setChannel is set slightly after adapter is created, check if null.
            if (channel != null) {
                int readReceipt = channel.getReadReceipt(message);
                if (readReceipt > 0) {
                    readReceiptText.setVisibility(View.VISIBLE);
                    readReceiptText.setText(String.valueOf(readReceipt));
                } else {
                    readReceiptText.setVisibility(View.INVISIBLE);
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            // Hide profile image and nickname if the previous message was also sent by current sender.
            if (isContinuous) {
                profileImage.setVisibility(View.INVISIBLE);
                nicknameText.setVisibility(View.GONE);
            } else {
                profileImage.setVisibility(View.VISIBLE);
                ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), profileImage);

                nicknameText.setVisibility(View.VISIBLE);
                nicknameText.setText(message.getSender().getNickname());
            }

            // Get thumbnails from FileMessage
            ArrayList<FileMessage.Thumbnail> thumbnails = (ArrayList<FileMessage.Thumbnail>) message.getThumbnails();

            // If thumbnails exist, get smallest (first) thumbnail and display it in the message
            if (thumbnails.size() > 0) {
                if (message.getType().toLowerCase().contains("gif")) {
                    ImageUtils.displayGifImageFromUrl(context, message.getUrl(), fileThumbnailImage, thumbnails.get(0).getUrl());
                } else {
                    ImageUtils.displayImageFromUrl(context, thumbnails.get(0).getUrl(), fileThumbnailImage);
                }
            } else {
                if (message.getType().toLowerCase().contains("gif")) {
                    ImageUtils.displayGifImageFromUrl(context, message.getUrl(), fileThumbnailImage, (String) null);
                } else {
                    ImageUtils.displayImageFromUrl(context, message.getUrl(), fileThumbnailImage);
                }
            }

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
        }
    }

    /**
     * A ViewHolder for file messages that are videos.
     * Displays only the video thumbnail.
     */
    private static class MyVideoFileMessageHolder extends RecyclerView.ViewHolder {

        TextView timeText, readReceiptText, dateText;
        ImageView fileThumbnailImage;

        public MyVideoFileMessageHolder(View itemView) {
            super(itemView);

            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            fileThumbnailImage = (ImageView) itemView.findViewById(R.id.image_group_chat_file_thumbnail);
            readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
        }

        void bind(Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isTempMessage, boolean isFailedMessage, Uri tempFileMessageUri, final OnItemClickListener listener) {
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));

            if (isFailedMessage) {
                readReceiptText.setText(R.string.str_sendbird_message_failed);
                readReceiptText.setVisibility(View.VISIBLE);
            } else if (isTempMessage) {
                readReceiptText.setText(R.string.str_sendbird_message_sending);
                readReceiptText.setVisibility(View.VISIBLE);
            } else {
                // Since setChannel is set slightly after adapter is created, check if null.
                if (channel != null) {
                    int readReceipt = channel.getReadReceipt(message);
                    if (readReceipt > 0) {
                        readReceiptText.setVisibility(View.VISIBLE);
                        readReceiptText.setText(String.valueOf(readReceipt));
                    } else {
                        readReceiptText.setVisibility(View.INVISIBLE);
                    }
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            // Get thumbnails from FileMessage
            ArrayList<FileMessage.Thumbnail> thumbnails = (ArrayList<FileMessage.Thumbnail>) message.getThumbnails();

            // If thumbnails exist, get smallest (first) thumbnail and display it in the message
            if (thumbnails.size() > 0) {
                ImageUtils.displayImageFromUrl(context, thumbnails.get(0).getUrl(), fileThumbnailImage);
            }

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
        }
    }

    private static class OtherVideoFileMessageHolder extends RecyclerView.ViewHolder {

        TextView timeText, nicknameText, readReceiptText, dateText;
        ImageView profileImage, fileThumbnailImage;

        public OtherVideoFileMessageHolder(View itemView) {
            super(itemView);

            timeText = (TextView) itemView.findViewById(R.id.text_group_chat_time);
            nicknameText = (TextView) itemView.findViewById(R.id.text_group_chat_nickname);
            fileThumbnailImage = (ImageView) itemView.findViewById(R.id.image_group_chat_file_thumbnail);
            profileImage = (ImageView) itemView.findViewById(R.id.image_group_chat_profile);
            readReceiptText = (TextView) itemView.findViewById(R.id.text_group_chat_read_receipt);
            dateText = (TextView) itemView.findViewById(R.id.text_group_chat_date);
        }

        void bind(Context context, final FileMessage message, GroupChannel channel, boolean isNewDay, boolean isContinuous, final OnItemClickListener listener) {
            timeText.setText(DateUtils.formatTime(message.getCreatedAt()));

            // Since setChannel is set slightly after adapter is created, check if null.
            if (channel != null) {
                int readReceipt = channel.getReadReceipt(message);
                if (readReceipt > 0) {
                    readReceiptText.setVisibility(View.VISIBLE);
                    readReceiptText.setText(String.valueOf(readReceipt));
                } else {
                    readReceiptText.setVisibility(View.INVISIBLE);
                }
            }

            // Show the date if the message was sent on a different date than the previous message.
            if (isNewDay) {
                dateText.setVisibility(View.VISIBLE);
                dateText.setText(DateUtils.formatDate(message.getCreatedAt()));
            } else {
                dateText.setVisibility(View.GONE);
            }

            // Hide profile image and nickname if the previous message was also sent by current sender.
            if (isContinuous) {
                profileImage.setVisibility(View.INVISIBLE);
                nicknameText.setVisibility(View.GONE);
            } else {
                profileImage.setVisibility(View.VISIBLE);
                ImageUtils.displayRoundImageFromUrl(context, message.getSender().getProfileUrl(), profileImage);

                nicknameText.setVisibility(View.VISIBLE);
                nicknameText.setText(message.getSender().getNickname());
            }

            // Get thumbnails from FileMessage
            ArrayList<FileMessage.Thumbnail> thumbnails = (ArrayList<FileMessage.Thumbnail>) message.getThumbnails();

            // If thumbnails exist, get smallest (first) thumbnail and display it in the message
            if (thumbnails.size() > 0) {
                ImageUtils.displayImageFromUrl(context, thumbnails.get(0).getUrl(), fileThumbnailImage);
            }

            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFileMessageItemClick(message);
                    }
                });
            }
        }
    }

}