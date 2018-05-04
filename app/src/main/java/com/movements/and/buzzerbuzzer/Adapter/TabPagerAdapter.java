package com.movements.and.buzzerbuzzer.Adapter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.movements.and.buzzerbuzzer.FollowFragment;
import com.movements.and.buzzerbuzzer.FollowingFragment;
import com.movements.and.buzzerbuzzer.FriendFragment;

import java.util.ArrayList;

/**
 * Created by samkim on 2017. 3. 10..
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    // Count number of tabs
    private int tabCount;
    private ArrayList<String> friendsList;//추가
    private ArrayList<String> followList;//추가
    private ArrayList<String> followingList;//추가

    private Bundle bundle;

    public TabPagerAdapter(FragmentManager fm, int tabCount, ArrayList<String> friendsList, ArrayList<String> followList, ArrayList<String> followingList) {//ArrayList<String> friendsList, ArrayList<String> followList, ArrayList<String> followingList추가
        super(fm);
        this.tabCount = tabCount;
        this.friendsList = friendsList;
        this.followList = followList;
        this.followingList = followingList;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                FriendFragment friend = new FriendFragment();
                bundle = new Bundle();
                bundle.putStringArrayList("friendsList", friendsList);
                friend.setArguments(bundle);
                return friend;
            case 1:
                FollowingFragment following = new FollowingFragment();
                bundle = new Bundle();
                bundle.putStringArrayList("followingList", followingList);
                following.setArguments(bundle);
                //Log.e("TabPagerAdapter","여기 됨?");
                return following;
            case 2:
                FollowFragment follow = new FollowFragment();
                bundle = new Bundle();
                bundle.putStringArrayList("followList", followList);
                follow.setArguments(bundle);
                return follow;
        }

        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        try {
            super.finishUpdate(container);
        } catch (NullPointerException nullPointerException) {
            //System.out.println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
        } catch(IllegalStateException illegalStateException ){

        }

    }
}


