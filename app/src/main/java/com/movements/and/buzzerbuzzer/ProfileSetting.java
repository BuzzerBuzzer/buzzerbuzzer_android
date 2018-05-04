package com.movements.and.buzzerbuzzer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Model.DataPart;
import com.movements.and.buzzerbuzzer.Navermap.NMapViewer;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.Utill.ConfirmDialog;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.Utill.MultiPartRequest;
import com.movements.and.buzzerbuzzer.Utill.Patternpwd;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 07.18..
 */
//내프로필설정 사진, 닉네임, 상태메세지, 위치 설정하기등등
public class ProfileSetting extends BaseActivity {
    private Patternpwd mPatternpwd;
    private LinearLayout bottomBackground;
    private ImageView topBackground;
    private ImageView back, profilegallery, camera, skin;
    private TextView myprofilesetting, skintv, revise, nicknametv, messagetv, basicinfo, emailtv, phonetv, idtv,
                    basiclocatv, locatexttv, currentlocation, location, nickname, message, email, phone, id;
    private EditText line1, line2, line3, line4, line5;
    private Button locationbutton, saginlist;

    private Typeface typefaceBold, typefaceExtraBold;


    private static final int MILLISINFUTURE = 720*60000;
    private static final int COUNT_DOWN_INTERVAL = 60000;

    private int count = 719, hour, min;
    private CountDownTimer countDownTimer;
    private ConfirmDialog dialog;

    //여기서부터 원래 코드
//    private ImageButton settingbtn;
//    private Button closebtn, userpic_btn, baselocationbtn;
//    private EditText nickname, locate, state_msg;
//    private ImageView user_pic;
//    private TextView phone_number, idtx, emailtx;


    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;
    private String _id, _phone, _email, _nickname, condition_msg, pic_src;
    private String user_pic_URL, mPassword, password;
    private long set_time;

    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int CURRENT_MESSAGE = 5;
    private static final int MY_NICKNAME = 6;
    private static final int REQUEST_IMAGE_CROP = 100;
    private Uri mImageCaptureUri;

    private boolean checkfirst;
    private boolean checkchatpic = false;
    private boolean checkNaverfix;
    private String changeNickName;
    private JsonConverter jc;
    private Bitmap selectImg;
    private boolean checkpic;
    private static final String TAG = "ProfileSetting";

    private ConfirmDialog dialog3;
//    private Drawable d;
//    private File f;
    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setting);


        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        topBackground = (ImageView) findViewById(R.id.topBackground);
        bottomBackground = (LinearLayout)findViewById(R.id.bottomBackground);

        back = (ImageView)findViewById(R.id.back);
        profilegallery = (ImageView)findViewById(R.id.profilegallery);
        camera = (ImageView)findViewById(R.id.camera);

        //글씨체만을 위한 id
        myprofilesetting = (TextView)findViewById(R.id.myprofilesetting);
        skintv = (TextView)findViewById(R.id.skintv);
        revise = (TextView)findViewById(R.id.revise);
        nicknametv = (TextView)findViewById(R.id.nicknametv);
        messagetv = (TextView)findViewById(R.id.messagetv);
        basicinfo = (TextView)findViewById(R.id.basicinfo);
        emailtv = (TextView)findViewById(R.id.basicinfo);
        phonetv = (TextView)findViewById(R.id.phonetv);
        idtv = (TextView)findViewById(R.id.idtv);
        basiclocatv = (TextView)findViewById(R.id.basiclocatv);
        locatexttv = (TextView)findViewById(R.id.locatexttv);
        currentlocation = (TextView)findViewById(R.id.currentlocation);
        location = (TextView)findViewById(R.id.location);
        //

        skin = (ImageView) findViewById(R.id.skin);

        nickname = (TextView)findViewById(R.id.nickname);
        message = (TextView)findViewById(R.id.message);
        email = (TextView)findViewById(R.id.email);
        phone = (TextView)findViewById(R.id.phone);
        id = (TextView)findViewById(R.id.id);

        nickname.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        message.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});

        //위치 등록 버튼
       locationbutton = (Button)findViewById(R.id.locationbutton);
        //사진 리스트 개수 버튼
        saginlist = (Button)findViewById(R.id.saginlist);
        saginlist.setEnabled(false);
        saginlist.setTypeface(typefaceExtraBold);

        myprofilesetting.setTypeface(typefaceExtraBold);
        skintv.setTypeface(typefaceBold);
        revise.setTypeface(typefaceExtraBold);
        nicknametv.setTypeface(typefaceBold);
        messagetv.setTypeface(typefaceBold);
        basicinfo.setTypeface(typefaceExtraBold);
        emailtv.setTypeface(typefaceBold);
        phonetv.setTypeface(typefaceBold);
        idtv.setTypeface(typefaceBold);
        basiclocatv.setTypeface(typefaceExtraBold);
        locatexttv.setTypeface(typefaceBold);
        currentlocation.setTypeface(typefaceExtraBold);
        location.setTypeface(typefaceExtraBold);


        nickname.setTypeface(typefaceBold);
        message.setTypeface(typefaceBold);
        email.setTypeface(typefaceBold);
        phone.setTypeface(typefaceBold);
        id.setTypeface(typefaceBold);

        locationbutton.setTypeface(typefaceExtraBold);

        passwordEn = new PasswordEn();




//TODO 사진 리스트의 개수를 나타내는 saginlist 아직 아무것도 안한 상태




       // settingbtn = (ImageButton) findViewById(R.id.settingbtn);
        //closebtn = (Button) findViewById(R.id.closebtn);
        //userpic_btn = (Button) findViewById(R.id.userpic_btn);

        //user_pic = (ImageView) findViewById(R.id.user_pic2);
        //topBackground.setBackground(new ShapeDrawable(new OvalShape()));
        //topBackground.setClipToOutline(true);

        //phone_number = (TextView) findViewById(R.id.newpwd);
        //idtx = (TextView) findViewById(R.id.idtx);
        //emailtx = (TextView) findViewById(R.id.emailtx);

        //state_msg = (EditText) findViewById(R.id.state_msg);
        //nickname = (EditText) findViewById(R.id.nickname);
        //baselocationbtn = (Button) findViewById(R.id.baselocationbtn);
        jc = new JsonConverter();

        setting = getSharedPreferences("setting", MODE_PRIVATE);
        _id = setting.getString("user_id", "");
        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);
        checkfirst = setting.getBoolean("firtmembership", false);//최초가입시
        Intent intent = getIntent();
        checkNaverfix = intent.getExtras().getBoolean("gps_base_check");
        changeNickName = intent.getExtras().getString("NICKNAME");
        editor = setting.edit();
        saginlist.setText(setting.getString("my_total_pic_num", "0"));
        Button();
        calluserinfo();//내 정보 가져 오기

        //TODO:shredPreferences로 위치설정한 시스템 시간 가져와서 12시간 지나는걸 계산해야함.

        Log.e(TAG+"저장된 set_Location", String.valueOf(setting.getBoolean("set_Location",false)));

        if(setting.getBoolean("set_Location", false)) {
            calTime(setting.getLong("set_location_time", 0), secTime());
        }
        nickname.setHint("13글자까지만 설정 가능합니다");

    }




    public long firstTime(){

        Calendar cal=Calendar.getInstance();
        Date startDate=cal.getTime();
        long startTime = startDate.getTime();

        Log.e("로케이션 등록 시간 ", "동작");
        Log.e("로케이션 등록 시간 ", String.valueOf(startTime));

        return startTime;
    }

    private long secTime(){
        Calendar cal=Calendar.getInstance();
        Date endDate=cal.getTime();
        long endTime = endDate.getTime();

        Log.e("액티비티 호출 시간 ", "동작");

        return endTime;
    }

    private void calTime(long t1, long t2){

        //TODO: if문으로 sib가 1000미만 됬을때 하면 다시 사용가능하게
        // 일단 flag 달아서 버튼이 눌리고 설정이 되면 true,
        // 현재시간 받아오고 sib 43200000 - 현재시간 으로 설정
        // true일 때, 해당 엑티비티 불러오면 sib기반으로 계산 시간 보여주고
        // 액티비티 호출됬을 때, sib이 1000 미만이면 버튼 활성화 하고 false
        // * 중요한거
        // 버튼 눌렸을때의 시스템 시간을 시스템공유해야함.
        // 엑티비티 호출 때, 계산하는데 시스템에 공유된 시간으로 계산해야하고
        // sib이 1000미만이면 공유된 시간을 초기화해야함.
        /*예로
            1시에 설정, 2시에 확인. 그럼 시간차는 1시간. 이게 mills. 시간이 지날수록 mills은 커짐.
            그럼 mills은 지난 시간이라고 봐도 되고, 43200000은 12시간을 초로 환산한거
            12시간 - mills 은 설정까지 남은시간이라고 봐도 되겠지? 이게 sib야..
            그럼 남은 시간이 1초 미만일때 바꿔주면 되겠지?
         */
        long mills = t2 - t1;

        long sib = 43200000 - mills;

        long rhh = sib/3600000;
        long rmm = sib/60000;
        long rmmm = rmm%60;

        if((sib < 1000)){
            editor.putBoolean("set_Location", false);
            editor.commit();
            locationbutton.setText("위치 설정하기");
            locationbutton.setEnabled(true);
        }else if(sib > 1000){
            locationbutton.setText("재설정까지 " + rhh + ":" + rmmm + "남았습니다.");
            locationbutton.setBackgroundResource(R.drawable.largebutton_sub_dim);
            locationbutton.setTextColor(getApplication().getResources().getColor(R.color.grey));
            locationbutton.setEnabled(false);
        }
    }

    private void Button() {
        //뒤로가기 버튼
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nickname.getText().toString().equals("닉네임을 입력해보세요.") || nickname.getText().toString().equals("")) {
                    //Log.d("결제", String.valueOf(nickname.getText().toString().trim().length()));

                    dialog = new ConfirmDialog(ProfileSetting.this,
                            "내 닉네임을 설정해주세요.", "확인");
                    dialog.setCancelable(true);
                    dialog.show();
                    /*
                    new AlertDialog.Builder(ProfileSetting.this)
                            .setMessage("내 닉네임을 설정해주세요.")
                            .setPositiveButton("확인", null)
                            .show();*/

                    return;
                }

                if (checkpic == false) {
//                    Toast.makeText(getApplicationContext(), "사진을 업로드 해주세요.", Toast.LENGTH_SHORT).show();
                    dialog = new ConfirmDialog(ProfileSetting.this,
                            "내 프로필사진을 설정해주세요.", "확인");
                    dialog.setCancelable(true);
                    dialog.show();
                    return;
                }
                //TODO 뭔데이거 설정 후 true로 안바뀜. 임시 주석 처리 함.
                if (checkNaverfix == false) {

                    dialog = new ConfirmDialog(ProfileSetting.this,
                            "내 기본위치를 설정해주세요.", "확인");
                    dialog.setCancelable(true);
                    dialog.show();

                    return;
                }
                calluserinfo();
//                updateuser();//유저정보 업뎃
                //내 프로필 화면으로 이동

                if(OpeningActivity.isTuto == true){
                    Intent intent = new Intent(getApplicationContext(), FriendList.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                    finish();
                }else{
                    Intent intent = new Intent(getApplicationContext(), Myprofile.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                    finish();
                }
            }
        });

//        settingbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentset = new Intent(getApplicationContext(), Allsetting.class);
//                startActivity(intentset);
//
//            }
//        });
        //카메라 버튼
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("use_gallery_pick", false);
                editor.commit();
                doTakeAlbumAction();
            }
        });

//        if(setting.getString("set_Location", "false") == "false"){
            locationbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(ProfileSetting.this, NMapViewer.class);//내 기본위치 설정
                    startActivityForResult(intent, 3);
                    overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
                }
            });
//        }
        //위치설정


        //스킨
        skin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //닉네임 설정
        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CurrentNickName.class);
                startActivityForResult(intent, MY_NICKNAME); //닉네임을 입력하고 받음
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });
        //상메 설정
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CurrentMessage.class);
                startActivityForResult(intent, CURRENT_MESSAGE); //상태메세지를 입력하고 받음
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);
            }
        });

        //사진리스트
        profilegallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intset = new Intent(ProfileSetting.this, Picprofile.class);
                intset.putExtra("user_id", _id);
                startActivityForResult(intset, 1);
                overridePendingTransition(R.anim.bottom_up, R.anim.bottom_exit);

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

        Log.e(TAG +" onActivityResult", "intent requestCode 반응함?");

        switch (requestCode) {
            //100
            case REQUEST_IMAGE_CROP:
                Intent Intenti = getIntent();
                // 이 부분을 변경 왜? imageview 를 layout 으로 바꿔서 x -> 그러려고 햇으나 일단 다시 돌려놓음
                topBackground.setImageURI(Uri.parse(Intenti.getStringExtra(("output"))));
          //      Uri path = Uri.parse(Intenti.getStringExtra("output"));
//                f = new File(getRealPathFromURI(path));
//                d = Drawable.createFromPath(f.getAbsolutePath());
//                topBackground.setBackground(d);
                break;
            case PICK_FROM_ALBUM:

                calluserinfo();

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
                    topBackground.setImageURI(mImageCaptureUri);
                    profilegallery.setImageURI(mImageCaptureUri);
                    // Log.d("결제안2", String.valueOf(mImageCaptureUri));
                    checkpic = true;
                    Log.e(TAG , "onActivityResult 3");
                    updateuserpic();
                }
                break;
            case CURRENT_MESSAGE:
                SystemClock.sleep(500);
                message.setText(data.getStringExtra("CURRENT_MESSAGE"));
//                updateuser();
                calluserinfo();
                break;
            case MY_NICKNAME:
                SystemClock.sleep(500);
                nickname.setText(data.getStringExtra("NICKNAME"));
//                updateuser();
                calluserinfo();
                //updateCurrentUserInfo(setting.getString("user_nickname", String.valueOf(id)), user_pic_URL);
                updateCurrentUserInfo(data.getStringExtra("NICKNAME"), user_pic_URL);
                editor.putString("user_nickname", data.getStringExtra("NICKNAME"));
                editor.commit();
                break;

        }
        if (requestCode == 3 || requestCode == 1000) {
            checkNaverfix = true;

            editor.putBoolean("gps_base_check", checkNaverfix);
            try {
                location.setText(data.getStringExtra("LOCATION"));
            }catch (NullPointerException e){

            }
            editor.commit();
        }
    }

    private void updateuserpic() {
        final String userid = _id;
        //   Log.d("아뒤", id);
        MultiPartRequest multiPartRequest = new MultiPartRequest(Request.Method.POST, Config.REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //  Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                Log.d(TAG+" 405", response);
                String user_pic = null;
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("result").equals("success")) {//성공시
                        //SystemClock.sleep(1000);
                        user_pic = json.getString("pic_url");
                        Log.e(TAG+" 450", user_pic);
//                        JSONArray data = json.getJSONArray("data");
//                        JSONObject row = null;
//                        row = data.getJSONObject(0);
//                        user_pic = row.getString("pic_url");
                        user_pic_URL = user_pic;
                        int a = 1 + Integer.parseInt(setting.getString("my_total_pic_num","0"));
                        editor.putString("my_total_pic_num", String.valueOf(a));
                        editor.putString("user_pic_URL",user_pic_URL);
                        editor.commit();
                        saginlist.setText(setting.getString("my_total_pic_num", "0"));
                        checkchatpic = true;

                        updateCurrentUserInfo(setting.getString("user_nickname", null), user_pic_URL);
                    } else {//json.getString("result").equals("fail")시
                        dialog3 = new ConfirmDialog(ProfileSetting.this,
                                "확인 후 다시 시도해주세요.", "확인");
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
                //Toast.makeText(getApplicationContext(), "다시 시도해주세요", Toast.LENGTH_LONG).show();
                Log.e(TAG," add_profile_pic error"+ error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(_id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_15_add_profile_picture", new String[]{userid, ""}));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<String, DataPart>();
                params.put("img", new DataPart(_id + ".jpg", getFileDataFromBitmap(selectImg), "image/jpeg"));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multiPartRequest);
    }


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
        String url = _id + String.valueOf(System.currentTimeMillis()) + ".jpg";
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

    private void calluserinfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    private String base_gps_address;

                    @Override
                    public void onResponse(String response) {
                        //ArrayList<User> list = new ArrayList<User>();
//                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, response);
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray data = json.getJSONArray("data");
//                            User user = null;
                            for (int i = 0; i < data.length(); i++) {
                                pic_src = data.getJSONObject(i).has("pic_src") ? data.getJSONObject(i).getString("pic_src") : "";
                                _phone = data.getJSONObject(i).has("phone") ? data.getJSONObject(i).getString("phone") : "";
                                _id = data.getJSONObject(i).has("id") ? data.getJSONObject(i).getString("id"): "";//modify
                                _email = data.getJSONObject(i).has("email") ? data.getJSONObject(i).getString("email") : "";
                                _nickname = data.getJSONObject(i).has("nickname") ? data.getJSONObject(i).getString("nickname") : "";
                                condition_msg = data.getJSONObject(i).has("condition_msg") ? data.getJSONObject(i).getString("condition_msg") : "메세지를 입력해 보세요.";
                                base_gps_address = data.getJSONObject(i).has("base_gps_address") ? data.getJSONObject(i).getString("base_gps_address") : "";
                            }
                            if (pic_src.isEmpty()) {
                               topBackground.setImageResource(R.drawable.nopic__s_m);
                            } else {
                                //TODO 이 부분 top
                                Glide.with(getApplicationContext()).load(pic_src).into(topBackground);
                                Glide.with(getApplicationContext()).load(pic_src).into(profilegallery);
                                checkpic = true;
                            }
                            setting.getString("user_nickname","닉네임을 입력해보세요.");
                            if(condition_msg.equals("") || condition_msg.equals(null)) {
                                message.setText("메세지를 입력해 보세요.");
                            }else
                                message.setText(condition_msg);

                            if(_nickname.equals("") || _nickname.equals(null)) {
                                nickname.setText("닉네임을 입력해보세요.");
                            }else
                                nickname.setText(_nickname);

                            location.setText(base_gps_address);
                            phone.setText(_phone);
                            email.setText(_email);
                            id.setText(_id);                    //프로필 수정에서 id 보이도록.
                            editor.putString("user_pic_URL",pic_src);
                            editor.commit();
                            //updateCurrentUserInfo(_nickname, setting.getString("user_pic_URL", null));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {//서버에러
                        Log.e(TAG," add_user_request_follow error"+ error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(_id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_13_loading_my_profile_setting", new String[]{_id}));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //SendBird 닉네임 업데이트
    @SuppressLint("LongLogTag")
    private void updateCurrentUserInfo(final String userNickname, final String picUrl) {//사진 업뎃 null 에 주소 추가 프로필 세팅사진 업뎃 할떄도 이거 쓰라..
        final String usernick = userNickname;
        String PicUrl = picUrl;
        Log.e("센드버드업데이트 ", "들어옴");
        SendBird.updateCurrentUserInfo(userNickname, picUrl, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null) {
                    Log.d("넘버", "SendBirdException e" + "  : " + e);
                    // Error!
//                    Toast.makeText(
//                            Picprofile.this, getString(R.string.str_sendbird_update_nickname_err),
//                            Toast.LENGTH_SHORT)
//                            .show();

                    return;
                }
                //editor.putString("user_nickname", userNickname);
                //editor.commit();
            }
        });
    }

    //유경이가 구글링한거
    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onResume() {
        super.onResume();
//        calluserinfo();
        Log.e(TAG+"저장된 set_Location", String.valueOf(setting.getBoolean("set_Location",false)));
        if(setting.getBoolean("set_Location", false)) {
            calTime(setting.getLong("set_location_time", 0), secTime());
        }
    }

}
