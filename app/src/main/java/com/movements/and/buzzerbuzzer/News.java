package com.movements.and.buzzerbuzzer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Adapter.NoticeAdapter;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.Newsmessage;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 7. 28..
 */
//뉴스피드
public class News extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static int NEWS_NOTI_READ = 0;
    private RecyclerView recycler;
    private ProgressBar mProgressbar;
    private DividerItemDecoration dividerItemDecoration;

    private int currentIndex;
    private JsonConverter jc;
    private ArrayList<Newsmessage> mNoticeList;
    private NoticeAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password;

    private static final String TAG = "News";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);
        jc = new JsonConverter();
        mNoticeList = new ArrayList<>();

        recycler = (RecyclerView) findViewById(R.id.recyclerview_msg);
        dividerItemDecoration = new DividerItemDecoration(recycler.getContext(), new LinearLayoutManager(getApplicationContext()).getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.lin_gray));
        recycler.addItemDecoration(dividerItemDecoration);
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new NoticeAdapter(getApplicationContext(), mNoticeList, new NoticeAdapter.RecyclerItemClickListener() {

            @Override
            public void onClickListener(Newsmessage frienewsmessagends, int position) {
                changeSelected(position);
                // Log.d("체크", String.valueOf(frienewsmessagends.getNo()));
                checknew(frienewsmessagends.getNo());

                if((frienewsmessagends.getFrClass()).equals("friend")){
                    Intent intset = new Intent(News.this, FriendProfile.class);
                    intset.putExtra("id", frienewsmessagends.getId());
                    startActivity(intset);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
                else if(frienewsmessagends.getFrClass().equals("following")){
                    Intent intset = new Intent(News.this, Followingprofile.class);
                    intset.putExtra("id", frienewsmessagends.getId());
                    startActivity(intset);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
                else if(frienewsmessagends.getFrClass().equals("follow")){
                    Intent intset = new Intent(News.this, Followprofile.class);
                    intset.putExtra("id", frienewsmessagends.getId());
                    startActivity(intset);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }else{
                    Intent intset = new Intent(News.this, Userprofile.class);
                    intset.putExtra("id", frienewsmessagends.getId());
                    startActivity(intset);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
            }
        }, new NoticeAdapter.RecyclerItemLongClickListener() {
            @Override
            public void onLongClickListener(final Newsmessage newsmessage, int position) {
            }
        });
        recycler.setAdapter(mAdapter);
        mProgressbar = (ProgressBar) findViewById(R.id.progressBar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(News.this);

        setting = getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = setting.edit();
        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        getmsg(id);//newslist call

        if(NEWS_NOTI_READ == 1){
            onRefresh();
        }

    }

    public void runOnUiThreadStart(){
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                mAdapter.setSelectedPosition(0);
                getmsg(id);
                mSwipeRefreshLayout.setRefreshing(false);
                NEWS_NOTI_READ = 0;
                //Log.e("news activity 리플레쉬", "동작!!!");
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void checknew(int no1) {
        final String no = String.valueOf(no1);
        mProgressbar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG+" 체크뉴스", response);
                        ArrayList<Newsmessage> list = new ArrayList<Newsmessage>();

                        try {
                            JSONObject resJson = new JSONObject(response);
                            if (resJson.getString("result").equals("success")) {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mProgressbar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressbar.setVisibility(View.GONE);
                        //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG," confirm_news Error"+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_61_check_news", new String[]{no}));//서버 서비코드
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }


    private void getmsg(String id) {
        final String rid = id;

        mProgressbar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String user_pic, nickname, id, fr_id, type, message, confirm, noti_time, confirm_yn, fr_class;
                        int no, length, y_count = 0, n_count=0;
                        //     Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        ArrayList<Newsmessage> list = new ArrayList<Newsmessage>();
                        ArrayList<Newsmessage> resultList = new ArrayList<Newsmessage>();
                        try {
                            JSONObject resJson = new JSONObject(response);
                            Log.e(TAG +" getmsg",response);
                            JSONArray data = resJson.getJSONArray("data");
                            Newsmessage newsmessage = null;
                            if(data.length() > 99){
                                length = 99;
                            }else
                                length = data.length();
                            for (int i = 0; i < length; i++) {
                                fr_class = data.getJSONObject(i).has("fr_class") ? data.getJSONObject(i).getString("fr_class") : "";
                                user_pic = data.getJSONObject(i).has("pic_src") ? data.getJSONObject(i).getString("pic_src") : "";
                                nickname = data.getJSONObject(i).has("nickname") ? data.getJSONObject(i).getString("nickname") : "";
                                no = data.getJSONObject(i).has("no") ? data.getJSONObject(i).getInt("no") : -1;
                                id = data.getJSONObject(i).has("id") ? data.getJSONObject(i).getString("id") : "";
                                fr_id = data.getJSONObject(i).has("fr_id") ? data.getJSONObject(i).getString("fr_id") : "";
                                type = data.getJSONObject(i).has("type") ? data.getJSONObject(i).getString("type") : "";
                                message = data.getJSONObject(i).has("message") ? data.getJSONObject(i).getString("message") : "";
                                noti_time = data.getJSONObject(i).has("noti_time") ? data.getJSONObject(i).getString("noti_time") : "";
                                confirm_yn = data.getJSONObject(i).has("confirm_yn") ? data.getJSONObject(i).getString("confirm_yn") : "";
                                newsmessage = new Newsmessage(user_pic, nickname, fr_class, no, id, fr_id, type, message, noti_time, confirm_yn);
                                list.add(newsmessage);
                                if(confirm_yn.equals("1"))
                                    y_count += 1;
                                else
                                    n_count +=1;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mProgressbar.setVisibility(View.GONE);
                        mNoticeList.clear();
                        mNoticeList.addAll(list);

                        Log.e("전체 노티", String.valueOf(mNoticeList.size()));
                        Log.e("읽은 뉴스 노티", String.valueOf(y_count));
                        Log.e("읽지 않은 뉴스 노티", String.valueOf(n_count));

                        editor.putInt("news_noti_size", mNoticeList.size());
                        editor.putInt("read_noti_count", y_count);
                        editor.putInt("unread_noti_count", n_count);

                        if(y_count == mNoticeList.size()){
                            editor.putBoolean("news_noti_allread", true);
                            editor.putBoolean("news_push", false);
                        }else
                            editor.putBoolean("news_noti_allread", false);

                        editor.commit();

                        mAdapter.notifyDataSetChanged();
                        mAdapter.setSelectedPosition(0);

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mProgressbar.setVisibility(View.GONE);
                        //Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG," notifi_news Error"+error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //  Log.d("파라", id);
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(rid, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_60_get_news_list_data", new String[]{rid}));//서버 서비코드
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void changeSelected(int position) {
        mAdapter.notifyItemChanged(mAdapter.getSelectedPosition());
        currentIndex = position;
        mAdapter.setSelectedPosition(currentIndex);
        mAdapter.notifyItemChanged(currentIndex);
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //해당 어댑터를 서버와 통신한 값이 나오면 됨
                mAdapter.notifyDataSetChanged();
                mAdapter.setSelectedPosition(0);
                getmsg(id);
                mSwipeRefreshLayout.setRefreshing(false);

                Log.e("news activity 리플레쉬", "동작!!!");

            }
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getmsg(id);
        NEWS_NOTI_READ = 0;

    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
    }
}

