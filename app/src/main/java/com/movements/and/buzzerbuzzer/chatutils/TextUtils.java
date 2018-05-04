package com.movements.and.buzzerbuzzer.chatutils;

import android.util.Log;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TextUtils {
    private static String TAG = "TextUtils";
    private static String friend_nick;

    public static String getGroupChannelTitle(GroupChannel channel) {

        List<String> test = new ArrayList<String>();
        test.clear();

        List<Member> rootmembers = channel.getMembers();

        //List<Member> members = new ArrayList<Member>(new HashSet<Member>(rootmembers));

        List<Member> members = new ArrayList<Member>();
        for (int i = 0; i < rootmembers.size(); i++) {
            if (!members.contains(rootmembers.get(i))) {
                members.add(rootmembers.get(i));
            }
        }

        Log.e(TAG+" members 사이즈", String.valueOf(members.size()));

        if (members.size() < 2) {
            return "No Members";
        } else if (members.size() == 2) {
            StringBuffer names = new StringBuffer();

            //TODO: 이부분 손보면 아이폰처럼 가능.
            for (Member member : members) {
                if (member.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                    continue;
                }
//                Log.e(TAG+"두명",member.getNickname());

                test.add(member.getNickname());
                names.append(", " + member.getNickname());
            }
//            return names.delete(0, 2).toString();
        } else {
            int count = 0;
            StringBuffer names = new StringBuffer();

            for (Member member : members) {
                if (member.getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                    continue;
                }

//                Log.e(TAG+"두명 이상",member.getNickname() + ", " + members.size());

                count++;
                names.append(", " + member.getNickname());
                test.add(member.getNickname());

                if(count >= 10) {
                    break;
                }
            }
//            Log.e("두명이상일때 notdelete return",names.toString());
//            Log.e("두명이상일때 최종 return",names.delete(0, 2).toString());
//            return names.delete(0, 2).toString();
        }


        //String[] arr = test.toArray(new String[test.size()]);



        List<String> final_member = new ArrayList<String>(new HashSet<String>(test));

        if(!final_member.isEmpty()){
            for(int i = 0; i < final_member.size(); i++){
                if(final_member.get(i).isEmpty()){
                    continue;
                }
                friend_nick = final_member.get(i);
            }
        }

//        friend_nick = final_member.get(0);

        Log.e(TAG, friend_nick);

        return friend_nick;
    }

    /**
     * Calculate MD5
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String generateMD5(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(data.getBytes());
        byte messageDigest[] = digest.digest();

        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++)
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

        return hexString.toString();
    }
}
