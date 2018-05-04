package com.movements.and.buzzerbuzzer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.IInAppBilling.Base64;
import com.movements.and.buzzerbuzzer.Model.DataPart;
import com.movements.and.buzzerbuzzer.Navermap.NMapViewer;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.BuzzerDialog;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog2;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.Utill.MultiPartRequest;
import com.movements.and.buzzerbuzzer.Utill.MultiPartRequest2;
import com.movements.and.buzzerbuzzer.Utill.Patternpwd;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 07.18..
 */
//내프로필설정 사진, 닉네임, 상태메세지, 위치 설정하기등등
public class Declare extends BaseActivity {
    private static final String TAG = "Declare : ";
    InputMethodManager imm;

    private LinearLayout target_user, selectLin, lin_wrap, singo_lin1;
    private ImageView btn_back, search_target_btn, target_user_pic, attach;
    private EditText search_target_edit, singo_reason_edit;
    private TextView target_user_nick, target_user_msg;
    private TextView singo_tv1, singo_tv2, singo_tv3, singo_tv4, singo_tv5;
    private RadioButton radioButton1, radioButton2, radioButton3;
    private RadioGroup radioGroup;
    private Button singo_btn;

    private Typeface typefaceBold, typefaceExtraBold;

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;
    private String id, mPassword, password;;
    private JsonConverter jc;

    private ConfirmDialog2 dialog;
    private BuzzerDialog dialog2;
    private ConfirmDialog dialog3;

    private Boolean isSearching = false;
    private Boolean isAttach = false;
    private int selectRadio = 0;

    private static String set_id = null;
    private static String nickname = null;
    private static String pic_src = null;
    private static String condition_msg = null;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int CURRENT_MESSAGE = 5;
    private static final int MY_NICKNAME = 6;
    private static final int REQUEST_IMAGE_CROP = 100;
    private Uri mImageCaptureUri;
    private Bitmap selectImg;
    private ContentLoadingProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singo);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        jc = new JsonConverter();
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        passwordEn = new PasswordEn();
        id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        lin_wrap = (LinearLayout) findViewById(R.id.lin_wrap);
        target_user = (LinearLayout) findViewById(R.id.target_user);
        singo_lin1 = (LinearLayout) findViewById(R.id.singo_lin1);
        selectLin = (LinearLayout) findViewById(R.id.selectLin);

        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progress_bar_singo);
        singo_btn = (Button) findViewById(R.id.singo_btn);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        attach = (ImageView) findViewById(R.id.attach);
        target_user_pic = (ImageView) findViewById(R.id.target_user_pic);
        target_user_pic.setBackground(new ShapeDrawable(new OvalShape()));
        target_user_pic.setClipToOutline(true);
        search_target_btn = (ImageView) findViewById(R.id.search_target_btn);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) findViewById(R.id.radioButton3);
        search_target_edit = (EditText) findViewById(R.id.search_target_edit);
        singo_reason_edit = (EditText) findViewById(R.id.singo_reason_edit);
        target_user_nick = (TextView) findViewById(R.id.target_user_nick);
        target_user_msg = (TextView) findViewById(R.id.target_user_msg);

        singo_tv1 = (TextView) findViewById(R.id.singo_tv1);
        singo_tv2 = (TextView) findViewById(R.id.singo_tv2);
        singo_tv3 = (TextView) findViewById(R.id.singo_tv3);
        singo_tv4 = (TextView) findViewById(R.id.singo_tv4);
        singo_tv5 = (TextView) findViewById(R.id.singo_tv5);

        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        singo_tv1.setTypeface(typefaceExtraBold);
        singo_tv2.setTypeface(typefaceBold);
        singo_tv3.setTypeface(typefaceBold);
        singo_tv4.setTypeface(typefaceBold);
        singo_tv5.setTypeface(typefaceBold);
        singo_btn.setTypeface(typefaceExtraBold);
        radioButton1.setTypeface(typefaceBold);
        radioButton2.setTypeface(typefaceBold);
        radioButton3.setTypeface(typefaceBold);
        search_target_edit.setTypeface(typefaceBold);
        singo_reason_edit.setTypeface(typefaceBold);
        target_user_nick.setTypeface(typefaceBold);
        target_user_msg.setTypeface(typefaceBold);

        lin_wrap.setOnClickListener(clickListener);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton1:
                        // 비방 욕설
                        hideKeyboard();
                        selectRadio = 1;
                        radioButton1.setTextColor(Color.BLACK);
                        radioButton2.setTextColor(getApplication().getResources().getColor(R.color.grey02));
                        radioButton3.setTextColor(getApplication().getResources().getColor(R.color.grey02));
                        singo_tv3.setVisibility(View.GONE);
                        singo_reason_edit.setVisibility(View.GONE);
                        break;
                    case R.id.radioButton2:
                        // 음란물 게시
                        hideKeyboard();
                        selectRadio = 2;
                        radioButton2.setTextColor(Color.BLACK);
                        radioButton1.setTextColor(getApplication().getResources().getColor(R.color.grey02));
                        radioButton3.setTextColor(getApplication().getResources().getColor(R.color.grey02));
                        singo_tv3.setVisibility(View.GONE);
                        singo_reason_edit.setVisibility(View.GONE);
                        break;
                    case R.id.radioButton3:
                        //기타
                        hideKeyboard();
                        selectRadio = 3;
                        radioButton3.setTextColor(Color.BLACK);
                        radioButton1.setTextColor(getApplication().getResources().getColor(R.color.grey02));
                        radioButton2.setTextColor(getApplication().getResources().getColor(R.color.grey02));
                        singo_tv3.setVisibility(View.VISIBLE);
                        singo_reason_edit.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        // 불량유저 검색 텍스트 입력시
        search_target_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    search_target_btn.setImageResource(R.drawable.ico_search_on);
                    search_target_edit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                } else {
                    search_target_btn.setImageResource(R.drawable.ico_search_off);
                    search_target_edit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        button();
        hideKeyboard();
    }

    @Override
    public void onBackPressed() {
        if(isSearching == true){
            searching_off();
            search_target_edit.setEnabled(true);
            target_user.setVisibility(View.GONE);
            isSearching = false;
        }else{
            super.onBackPressed();
        }
    }

    public void button(){

        // 뒤로가기 버튼
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSearching == true){
                    searching_off();
                    search_target_edit.setEnabled(true);
                    target_user.setVisibility(View.GONE);
                    isSearching = false;
                }else{
                    onBackPressed();
                }
            }
        });

        // 신고하기 버튼
        singo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 빈칸이 있을 경우
                if(target_user.getVisibility() == View.GONE){
                    dialog3 = new ConfirmDialog(Declare.this,
                            "먼저 불량유저를\n검색 후 선택해주세요.", "확인");
                    dialog3.setCancelable(true);
                    dialog3.show();
                }else if(selectRadio == 0){
                    dialog3 = new ConfirmDialog(Declare.this,
                            "신고 사유를 선택해주세요.", "확인");
                    dialog3.setCancelable(true);
                    dialog3.show();
                }else if(selectRadio == 3 && singo_reason_edit.getText().toString().trim().equals("")){
                    dialog3 = new ConfirmDialog(Declare.this,
                            "기타 사유를 입력해주세요.", "확인");
                    dialog3.setCancelable(true);
                    dialog3.show();
                }else if(isAttach == false){
                    dialog3 = new ConfirmDialog(Declare.this,
                            "첨부파일을 업로드해주세요.", "확인");
                    dialog3.setCancelable(true);
                    dialog3.show();
                }else {
                    // 신고하기 서비스코드 처리함수 불러오기
                    mProgressBar.setVisibility(View.VISIBLE);
                    String reason = singo_reason_edit.getText().toString().trim();
                    singoConfirm(selectRadio, reason);
                }
            }
        });

        // 유저 검색 입력창 클릭시
        search_target_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_target_edit.setCursorVisible(true);
            }
        });

        // 유저 검색 버튼
        search_target_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 불량유저 서치 버튼 처리
                String search = search_target_edit.getText().toString().trim();
                if (search.length() > 0) {
                    // 불량유저 서치 실행
                    getUser(search);
                } else {
                    // 제대로 입력 경고
                }
            }
        });

        // 불량유저 클릭버튼
        target_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 불량유저 서칭 후 클릭시
                if(isSearching == true){
                    searchDialog();
                    isSearching = false;
                }
            }
        });

        // 첨부하기 버튼
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakeAlbumAction();
            }
        });
    }

    private void doTakeAlbumAction() {
        //Log.i(TAG, "doTakeAlbumAction()");
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            //100
            case REQUEST_IMAGE_CROP:
                Intent Intenti = getIntent();
                attach.setImageURI(Uri.parse(Intenti.getStringExtra(("output"))));
                break;
            case PICK_FROM_ALBUM:
                //calluserinfo();
                Log.e(TAG , "PICK_FROM_ALBUM 1");

                Log.e(TAG , "use_gallery_pick "+setting.getBoolean("use_gallery_pick", true));
                if(!setting.getBoolean("use_gallery_pick", false)){
                    if (data != null) {
                        Log.e(TAG , "PICK_FROM_ALBUM 2");
                        //  Log.d(TAG, "PICK_FROM_ALBUM");
                        // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
                        // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
                        mImageCaptureUri = data.getData();
                        File original_file = getImageFile(mImageCaptureUri);
                        mImageCaptureUri = createSaveCropFile();
                        File copy_file = new File(mImageCaptureUri.getPath());
                        // Log.d("PICK_FROM_ALBUM", original_file + "  " + copy_file);

                        // SD카드에 저장된 파일을 이미지 Crop을 위해 복사한다.
                        copyFile(original_file, copy_file);

                        // Log.d(TAG, "PICK_FROM_CAMERA");

                        // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                        // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
                        Log.e(TAG , "PICK_FROM_ALBUM 3");
                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(mImageCaptureUri, "image/*");

//                    // Crop한 이미지를 저장할 Path
                        intent.putExtra("output", mImageCaptureUri);

                        // Return Data를 사용하면 번들 용량 제한으로 크기가 큰 이미지는
                        // 넘겨 줄 수 없다.
                        //		   intent.putExtra("return-data", true);
                        startActivityForResult(intent, CROP_FROM_CAMERA);
                    }
                }else
                    break;

                Log.e(TAG , "PICK_FROM_ALBUM 4");
            case CROP_FROM_CAMERA:

                Log.e(TAG , "onActivityResult 1");
                try {
                    selectImg = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageCaptureUri);
                    Log.e(TAG , "onActivityResult 2");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG , String.valueOf(e));
                }catch (NullPointerException e){
                    Log.e(TAG,"사진 선택 안하고 시스템 백키 Null 에러", e);
                    return;
                }
                if (requestCode == CROP_FROM_CAMERA && resultCode != RESULT_CANCELED) {//add
                    //topbackground 수정
//                    f = new File(getRealPathFromURI(mImageCaptureUri));
//                    d = Drawable.createFromPath(f.getAbsolutePath());
//                    topBackground.setBackground(d);
                    //topBackground.setBackground(d);
                    attach.setImageURI(mImageCaptureUri);
                    isAttach = true;
                    // Log.d("결제안2", String.valueOf(mImageCaptureUri));
                    Log.e(TAG , "onActivityResult 3");
                }
                break;
            case CURRENT_MESSAGE:
                break;
            case MY_NICKNAME:
                break;

        }
    }

    public void getUser(String query) {
        final String set_nick = query;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //search_noti.setVisibility(View.VISIBLE);
                        Log.d(TAG+" getUser", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("result").equals("success")) {//성공시
                                JSONArray data = json.getJSONArray("data");

                                set_id = data.getJSONObject(0).has("id") ? data.getJSONObject(0).getString("id") : "";
                                pic_src = data.getJSONObject(0).has("pic_src") ? data.getJSONObject(0).getString("pic_src") : "";
                                nickname = data.getJSONObject(0).has("nickname") ? data.getJSONObject(0).getString("nickname") : "";
                                condition_msg = data.getJSONObject(0).has("condition_msg") ? data.getJSONObject(0).getString("condition_msg") : "";

                                target_user_nick.setText(nickname);
                                target_user_msg.setText(condition_msg);
                                if (pic_src.isEmpty()) {
                                } else {
                                    Glide.with(getApplicationContext()).load(pic_src).into(target_user_pic);
                                }

                                hideKeyboard();
                                search_target_edit.setCursorVisible(false);
                                search_target_edit.setEnabled(false);
                                searching_on();
                                isSearching = true;

                            } else {//json.getString("result").equals("fail")시
                                dialog3 = new ConfirmDialog(Declare.this,
                                        "불량유저 닉네임을\n확인 후 다시 입력해주세요.", "확인");
                                dialog3.setCancelable(true);
                                dialog3.show();
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
                        Log.e(TAG, " searching_id Error" + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_66_search_nickname_id", new String[]{set_nick}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void singoConfirm(final int selectRadio, final String reason){

        /*
        String _nick = nickname;
        FileOutputStream fos = new FileOutputStream("data.txt");
        DataOutputStream dos = new DataOutputStream(fos);
        dos.writeUTF(_nick);

        FileInputStream fis = new FileInputStream("data.txt");
        final DataInputStream dis = new DataInputStream(fis);
        */

        MultiPartRequest2 multiPartRequest2 = new MultiPartRequest2(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG+" 190",response);
                //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        mProgressBar.setVisibility(View.GONE);
                        Log.e("신고하기",json.getString("msg"));
                        singoDialog();
                    }else{
                        Log.e("신고하기",json.getString("msg"));
                        mProgressBar.setVisibility(View.GONE);
                        dialog3 = new ConfirmDialog(Declare.this,
                                "죄송합니다. 신고하기 서비스 점검중이므로\n고객센터의 문의하기를 이용해주세요.", "확인");
                        dialog3.setCancelable(true);
                        dialog3.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "user_profile error" + error);
                dialog3 = new ConfirmDialog(Declare.this,
                        "확인 후 다시 시도해주세요.", "확인");
                dialog3.setCancelable(true);
                dialog3.show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_67_add_declare_user", new String[]{id, set_id, nickname, String.valueOf(selectRadio), reason}));
                Log.e("최종 파람", String.valueOf(params));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<String, DataPart>();
                params.put("img", new DataPart(id + ".jpg", getFileDataFromBitmap(selectImg), "image/jpeg"));
                Log.e(TAG, "사진 업로드 부분 사진경로" + getFileDataFromBitmap(selectImg).toString());
                Log.e("최종 파람", String.valueOf(params));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest2);
    }

    View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            hideKeyboard();
            switch (v.getId())
            {
                case R.id.lin_wrap :
                    break;
                case R.id.singo_btn :
                    break;
                case R.id.attach :
                    break;
                case R.id.search_target_btn :
                    break;
            }
        }
    };


    private File getImageFile(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        if (uri == null) {
            uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }

    /**
     * Crop된 이미지가 저장될 파일을 만든다.
     *
     * @return Uri
     */
    private Uri createSaveCropFile() {
        Uri uri;
        String url = id + String.valueOf(System.currentTimeMillis()) + ".jpg";
        uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), url));//getExternalStorageDirectory()외부 저장소의 최상위 경로를 반환합니다.
        return uri;
    }

    /**
     * 파일 복사
     *
     * @param srcFile  : 복사할 File
     * @param destFile : 복사될 File
     * @return
     */
    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static byte[] getFileDataFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void searching_on(){
        target_user.setVisibility(View.VISIBLE);
        singo_tv1.setVisibility(View.GONE);
        singo_tv2.setVisibility(View.GONE);
        search_target_btn.setVisibility(View.GONE);
        selectLin.setVisibility(View.GONE);
    }

    private void searching_off(){
        singo_tv1.setVisibility(View.VISIBLE);
        singo_tv2.setVisibility(View.VISIBLE);
        search_target_btn.setVisibility(View.VISIBLE);
        selectLin.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(search_target_edit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(singo_reason_edit.getWindowToken(), 0);
    }

    public void singoDialog(){
        dialog = new ConfirmDialog2(Declare.this,
                "접수 되었습니다. 감사합니다.", "확인", singoOKListener);
        dialog.setCancelable(true);
        dialog.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener singoOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
            Intent intent = new Intent(getApplicationContext(), FriendList.class);
            startActivity(intent);
            overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            finish();
        }
    };

    public void searchDialog(){
        dialog2 = new BuzzerDialog(Declare.this,
                "선택하신 유저가 정확합니까?", "신고", "취소", searchOKListener, cancelListener);
        dialog2.setCancelable(true);
        dialog2.show();
    }
    //다이얼로그 클릭이벤트
    private View.OnClickListener searchOKListener = new View.OnClickListener() {
        public void onClick(View v) {
            // 불량유저 맞을시 처리
            searching_off();
            search_target_btn.setEnabled(false);
            search_target_btn.setImageResource(R.drawable.ico_search_off);
            dialog2.dismiss();
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View v) {
            searching_off();
            search_target_edit.setEnabled(true);
            target_user.setVisibility(View.GONE);
            dialog2.dismiss();
        }
    };

}
