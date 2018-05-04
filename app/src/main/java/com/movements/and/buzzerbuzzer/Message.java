package com.movements.and.buzzerbuzzer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.chat.GroupChannelListFragment;
import com.movements.and.buzzerbuzzer.chat.GroupChatFragment;

/**
 * Created by samkim on 2017. 7. 28..
 */
//채팅 리스트
public class Message extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_channel);
        Log.e("Message", "onCreate");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // If started from launcher, load list of Open Channels
            Fragment fragment2 = GroupChannelListFragment.newInstance();

            FragmentManager manager2 = getSupportFragmentManager();
            manager2.popBackStack();

            manager2.beginTransaction()
                    .replace(R.id.container_group_channel2, fragment2)
                    .commit();

            Log.e("Message", "savedInstanceState == null");

        }

        String channelUrl1 = getIntent().getStringExtra("groupChannelUrl");
        if (channelUrl1 != null) {
            // If started from notification
            Fragment fragment3 = GroupChatFragment.newInstance(channelUrl1);
            FragmentManager manager3 = getSupportFragmentManager();
            manager3.beginTransaction()
                    .replace(R.id.container_group_channel2, fragment3)
                    .addToBackStack(null)
                    .commit();
            Log.e("Message", "channelUrl1 != null");
        }
    }


}
