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
import com.movements.and.buzzerbuzzer.Adapter.FriendsAdapter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.Friend;
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
//팔로우화면
public class FollowFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int TAB_ID = 2;
    private RecyclerView recycler;
    private ProgressBar mProgressbar;
    private DividerItemDecoration dividerItemDecoration;
    private FriendsAdapter mAdapter;
    private ArrayList<Friend> mFriendList;
    private int currentIndex;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password;
    private JsonConverter jc;
    private ConfirmDialog dialog;
    //private ArrayList<String> friendsList;
    //private ArrayList<String> followingList;
    private ArrayList<String> followList;
    private final String TAG = "FollowFragment";

    GroupChannelListQuery filteredQuery;
    List<String> userIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.follow, container, false);

        filteredQuery = GroupChannel.createMyGroupChannelListQuery();
        userIds = new ArrayList<>();


        followList = getArguments().getStringArrayList("followList");
        jc = new JsonConverter();
        recycler = (RecyclerView) view.findViewById(R.id.recyclerview_fr);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        mProgressbar = (ProgressBar) view.findViewById(R.id.progressBar4);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((FriendList) getActivity()).getFriendList(id,2);
                ((FriendList) getActivity()).getFollowingList(id,2);
                ((FriendList) getActivity()).getFollowList(id,2);
                ((FriendList) getActivity()).loadingMainList(id,2);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        dividerItemDecoration = new DividerItemDecoration(recycler.getContext(), new LinearLayoutManager(getActivity()).getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.lin_gray));
        recycler.addItemDecoration(dividerItemDecoration);
        mFriendList = new ArrayList<>();
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new FriendsAdapter(getActivity(), mFriendList, new FriendsAdapter.RecyclerItemClickListener() {
            @Override
            public void onClickListener(Friend friends, int position) {
                //changeSelected(position);//setSelectedPosition 8

                Intent intentfr = new Intent(getContext(), Followprofile.class);
                intentfr.putExtra("id", friends.getFr_id());
                // Log.d("test",friends.getFr_id());
                startActivity(intentfr);
                getActivity().overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);

            }
        }, new FriendsAdapter.RecyclerItemLongClickListener() {
            @Override
            public void onLongClickListener(final Friend friend, final int position, View itemView) {
                ((FriendList)getActivity()).addbtnOff();
                final TextView red = (TextView)itemView.findViewById(R.id.red);
                final TextView grey = (TextView)itemView.findViewById(R.id.grey);

                grey.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.e("차단버튼","클릭됨");

                        gpsblock(friend.getFr_id(), 0);//gps 블락
                        red.setVisibility(View.GONE);
                        grey.setVisibility(View.GONE);
                        ((FriendList)getActivity()).addbtnOn();
                    }
                });

                red.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.e("삭제버튼","클릭됨");

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
                        dialog = new ConfirmDialog(getActivity(),
                                "삭제되었습니다.", "확인");
                        dialog.setCancelable(true);
                        dialog.show();
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


        passwordEn = new PasswordEn();

        recycler.setAdapter(mAdapter);
        setting = getActivity().getSharedPreferences("setting", getActivity().MODE_PRIVATE);
        editor = setting.edit();

        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);
        getFollowList();//팔로우리스트호출


        return view;
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
                //params.put("param", jc.createJsonParam("201", new String[]{in_id, user_id}));//서버 서비코드
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_32_delete_user", new String[]{in_id, user_id}));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
        SystemClock.sleep(500);
        ((FriendList) getActivity()).getFriendList(id, 2);
        ((FriendList) getActivity()).getFollowingList(id, 2);
        ((FriendList) getActivity()).getFollowList(id, 2);
        ((FriendList) getActivity()).loadingMainList(id, 2);
    }

    private void gpsblock(String fid, int i) {
        final String user_id = fid;
        final String in_location_release_yn = String.valueOf(i);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {
                                //Toast.makeText(getActivity(), "내위치 차단 하였습니다.", Toast.LENGTH_SHORT).show();
                                dialog = new ConfirmDialog(getActivity(),
                                        "내 위치가 차단되었습니다.", "확인");
                                dialog.setCancelable(true);
                                dialog.show();
                                /*
                                new android.support.v7.app.AlertDialog.Builder(getActivity())
                                        .setMessage("내 위치가 차단되었습니다.")
                                        .setPositiveButton("확인", null)
                                        .show();  */
                            } else {//json.getString("result").equals("fail")시
                                //Toast.makeText(getActivity(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(getActivity(), "Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, " block_gps_friend Error :" + error);

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_31_block_gps_individual_user", new String[]{id, user_id, in_location_release_yn}));//서버 서비코드
                Log.i("개별유저차단", String.valueOf(params));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

//    private void Gpsblock(String fr_id1) {
//        final String fr_id = fr_id1;
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
//                        try {
//                            JSONObject json = new JSONObject(response);
//                            if (json.getString("result").equals("success")) {//성공시
//                                Toast.makeText(getActivity(), "삭제하였습니다.", Toast.LENGTH_SHORT).show();
//                                //((FriendList) getActivity()).CallVolley();
//                                ((FriendList) getActivity()).getData(id, TAB_ID);
//                            } else {//json.getString("result").equals("fail")시
//                                Toast.makeText(getActivity(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {//서버에러
//                        //Toast.makeText(getActivity(), "Error!", Toast.LENGTH_LONG).show();
//                        Log.e(TAG, "block_gps_follow Error :" + error);
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("param", jc.createJsonParam("block_gps_follow", new String[]{id, fr_id, "0", ""}));//서버 서비코드
//                return params;
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//        requestQueue.add(stringRequest);
//    }


    private void getFollowList() {
        JSONObject row;
        try {
            if(followList == null){
                return;
            }else {
                for (int i = 0; i < followList.size(); i++) {
                    row = new JSONObject(followList.get(i));
                    Friend friend = new Friend(
                            row.getString("pic_src"),
                            row.getString("nickname"),
                            row.getString("fr_id"),
                            row.getString("condition_msg"),
                            row.getInt("location_release_yn")
                    );
                    mFriendList.add(friend);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mProgressbar.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
        mAdapter.setSelectedPosition(0);//setSelectedPosition 4
    }

    @Override
    public void onRefresh() {
        FriendFragment.check_real = false;
        ((FriendList) getActivity()).getFollowList(id, 2);
    }
}