package com.movements.and.buzzerbuzzer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Adapter.FriendsAdapter2;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.Friend;
import com.movements.and.buzzerbuzzer.Utill.BuzzerDialog;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.SendBirdException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samkim on 2017. 3. 10..
 */
//팔로잉
public class FollowingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int TAB_ID = 1;

    private RecyclerView recycler;
    private ProgressBar mProgressbar;
    private DividerItemDecoration dividerItemDecoration;

    private FriendsAdapter2 mAdapter;

    private ArrayList<Friend> mFriendList;
    private int currentIndex;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password;

    private JsonConverter jc;

    private ArrayList<String> followingList;
    GroupChannelListQuery filteredQuery;
    List<String> userIds;
    private  final String TAG = "FollowingFragment";

    private BuzzerDialog dialog;
    private ConfirmDialog dialog2;

    private Boolean deleteCheck=false, blockCheck=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.following, container, false);

        filteredQuery = GroupChannel.createMyGroupChannelListQuery();
        userIds = new ArrayList<>();


        followingList = getArguments().getStringArrayList("followingList");
//        for(int i = 0; i < followingList.size(); i ++){
//            Log.d(FollowingFragment.class.getSimpleName(), followingList.get(i));
//        }

        jc = new JsonConverter();


        passwordEn = new PasswordEn();

        setting = getActivity().getSharedPreferences("setting", getActivity().MODE_PRIVATE);
        editor = setting.edit();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        recycler = (RecyclerView) view.findViewById(R.id.recyclerview_fr);
        mProgressbar = (ProgressBar) view.findViewById(R.id.progressBar4);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((FriendList) getActivity()).getFriendList(id,1);
                ((FriendList) getActivity()).getFollowingList(id,1);
                ((FriendList) getActivity()).getFollowList(id,1);
                ((FriendList) getActivity()).loadingMainList(id,1);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        dividerItemDecoration = new DividerItemDecoration(recycler.getContext(), new LinearLayoutManager(getActivity()).getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.lin_gray));
        recycler.addItemDecoration(dividerItemDecoration);
        mFriendList = new ArrayList<>();

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new FriendsAdapter2(getActivity(), mFriendList, new FriendsAdapter2.RecyclerItemClickListener() {
            @Override
            public void onClickListener(Friend friends, int position) {
                //changeSelected(position);//setSelectedPosition 8

                Intent intentfr = new Intent(getContext(), Followingprofile.class);
                intentfr.putExtra("id", friends.getFr_id());
                // Log.d("test",friends.getFr_id());
                startActivity(intentfr);
                getActivity().overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);

            }
        }, new FriendsAdapter2.RecyclerItemLongClickListener() {
            @Override
            public void onLongClickListener(final Friend friend, final int position, View itemView) {
                ((FriendList)getActivity()).addbtnOff();
                final TextView red = (TextView)itemView.findViewById(R.id.red);

                red.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        userIds.add(friend.getFr_id());
                        filteredQuery.setUserIdsExactFilter(userIds);
                        filteredQuery.next(new GroupChannelListQuery.GroupChannelListQueryResultHandler() {

                            @Override
                            public void onResult(List<GroupChannel> list, SendBirdException e) {
                                Log.e("롱클릭 일때 임","해당 친구와 채널 갯수 " + list.size());
                                if(list.size() != 0) {
                                    list.get(0).hide(new GroupChannel.GroupChannelHideHandler() {

                                        @Override
                                        public void onResult(SendBirdException e) {
                                            if (e != null) {
// Error!
                                                return;
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        Delete(friend, position);//친구삭제

                        dialog2 = new ConfirmDialog(getActivity(),
                                "삭제되었습니다.", "확인");
                        dialog2.setCancelable(true);
                        dialog2.show();

                        ((FriendList)getActivity()).addbtnOn();
                    }
                });
                // 다른 롱클릭시 이전 다른 버튼을 없애준다
                mAdapter.setSelectedPosition(position);
                for(int i=0; i < mFriendList.size(); i++){
                    if(i == position){
                    }else{
                        mAdapter.notifyItemChanged(i);
                    }
                }
            }
        });
        recycler.setAdapter(mAdapter);
        getFriendsList();//팔로잉 리스트 불러오기
        return view;
    }

    private void getFriendsList() {
        //TODO:location_release_yn 체크여부 반영 해야 할것.
        JSONObject row;
        try {
            if(followingList == null){
                Log.e("followingList", "null임?");
                return;
            }else {
                String pic_src, nickname, fr_id, condition_msg;
                int gauge_type_user, fr_gauge_angle, location_release_yn = 1;
                ArrayList<Friend> friends = new ArrayList<>();//9
                for (int i = 0; i < followingList.size(); i++) {
                    row = new JSONObject(followingList.get(i));
                    pic_src = row.getString("pic_src");
                    nickname = row.getString("nickname");
                    fr_id = row.getString("fr_id");
                    fr_gauge_angle = row.getInt("fr_gauge_angle");
                    gauge_type_user = row.getInt("gauge_type_user");
                    condition_msg = row.getString("condition_msg");
                    //                        location_release_yn = row.getInt("location_release_yn");

                    Friend friend = new Friend(pic_src, nickname, fr_id, fr_gauge_angle, gauge_type_user, condition_msg, location_release_yn);
                    friends.add(friend);
                    Log.e("followingList.length", String.valueOf(friends.size()));


                }

                mProgressbar.setVisibility(View.GONE);
                mFriendList.clear();//add+queryd
                mFriendList.addAll(friends);
                mAdapter.notifyDataSetChanged();
                mAdapter.setSelectedPosition(0);//setSelectedPosition 4
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void changeSelected(int position) {
        mAdapter.notifyItemChanged(mAdapter.getSelectedPosition());
        currentIndex = position;
        mAdapter.setSelectedPosition(currentIndex);
        mAdapter.notifyItemChanged(currentIndex);
    }


    @Override
    public void onRefresh() {
//        ((FriendList) getActivity()).getFollowingList(id, TAB_ID);
        FriendFragment.check_real = false;
        ((FriendList) getActivity()).getFollowingList(id, 1);
        Log.e(TAG, String.valueOf(getActivity()));
    }


    private void Delete(final Friend friend, final int position) {
        final String in_id = id;
        final String user_id = friend.getFr_id();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시

                                mFriendList.remove(position);
                                mAdapter.notifyDataSetChanged();
                                //mAdapter.setSelectedPosition(0);//현재 위치유지하고 싶으면 postion-1을 파라미터에...//문제 있으면 if처리

                            } else {//json.getString("result").equals("fail")시
                                //Toast.makeText((FriendList) getActivity(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
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
                        Log.e(TAG, " delete_user_friend_list Error :" + error);
                        //Toast.makeText(getActivity(), "Error!", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_32_delete_user", new String[]{in_id, user_id}));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        SystemClock.sleep(500);
        ((FriendList) getActivity()).getFriendList(id, 1);
        ((FriendList) getActivity()).getFollowingList(id, 1);
        ((FriendList) getActivity()).getFollowList(id, 1);
        ((FriendList) getActivity()).loadingMainList(id, 1);
    }

    public void deleteDialog(){
        dialog = new BuzzerDialog(getActivity(),
                "정말 삭제하시겠습니까?", "확인", "취소", deleteOKListener, cancelListener);
        dialog.setCancelable(true);
        dialog.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener deleteOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            deleteCheck = true;
            dialog.dismiss();
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

}

