package com.movements.and.buzzerbuzzer;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.movements.and.buzzerbuzzer.Encrypt.PasswordEn;
import com.movements.and.buzzerbuzzer.Utill.BaseActivity;
import com.movements.and.buzzerbuzzer.Utill.Config;
import com.movements.and.buzzerbuzzer.IInAppBilling.IabHelper;
import com.movements.and.buzzerbuzzer.IInAppBilling.IabResult;
import com.movements.and.buzzerbuzzer.IInAppBilling.Inventory;
import com.movements.and.buzzerbuzzer.Utill.JsonConverter;
import com.movements.and.buzzerbuzzer.IInAppBilling.Purchase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by samkim on 2017. 7. 21..
 */

public class BuzzerPurchase extends BaseActivity {
    static final String TAG = "buzzerpurchase";
    private TextView purchasecnt, textView, tx3, reason1, reason2, reason3, reason4, reason5;
    private Button button1, button2, button3, button4, button5;
    private ImageView backbtn;
    private JsonConverter jc;
    private SharedPreferences setting;
    private SharedPreferences.Editor editor;
    private PasswordEn passwordEn;

    private String id, mPassword, password;

    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    static final String SKU_01 = "bzcoin_1";
    static final String SKU_05 = "bzcoin_5";
    static final String SKU_10 = "bzcoin_10";
    static final String SKU_50 = "bzcoin_50";
    static final String SKU_100 = "bzcoin_100";

    private Typeface typefaceBold, typefaceExtraBold;


    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // The helper object
    IabHelper mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        String base64EncodedPublicKey = Config.purchase;

        reason1 = (TextView)findViewById(R.id.reason1);
        reason2 = (TextView)findViewById(R.id.reason2);
        reason3 = (TextView)findViewById(R.id.reason3);
        reason4 = (TextView)findViewById(R.id.reason4);
        reason5 = (TextView)findViewById(R.id.reason5);
        tx3 = (TextView)findViewById(R.id.tx3);
        textView = (TextView) findViewById(R.id.textView);
        purchasecnt = (TextView) findViewById(R.id.purchasecnt);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button)findViewById(R.id.button5);
        backbtn = (ImageView) findViewById(R.id.backbtn);

        //외부 폰트
        typefaceBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundB.ttf");
        typefaceExtraBold = Typeface.createFromAsset(getAssets(),"NanumSquareRoundEB.ttf");

        reason1.setTypeface(typefaceBold);
        reason2.setTypeface(typefaceBold);
        reason3.setTypeface(typefaceBold);
        reason4.setTypeface(typefaceBold);
        reason5.setTypeface(typefaceBold);
        button1.setTypeface(typefaceExtraBold);
        button2.setTypeface(typefaceExtraBold);
        button3.setTypeface(typefaceExtraBold);
        button4.setTypeface(typefaceExtraBold);
        button5.setTypeface(typefaceExtraBold);
        textView.setTypeface(typefaceExtraBold);
        purchasecnt.setTypeface(typefaceExtraBold);
        tx3.setTypeface(typefaceExtraBold);

        jc = new JsonConverter();
        setting = getSharedPreferences("setting", MODE_PRIVATE);
        id = setting.getString("user_id", "");
        editor = setting.edit();
        passwordEn = new PasswordEn();

        password = setting.getString("user_pw","");
        mPassword = passwordEn.PasswordEn(password);

        button();

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);
        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "in billing setup failed:" + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

        callpurchasecnt();//이용권갯수 불러오기
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {

                Log.d(TAG, "Failed to query inventory:" + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

//            // Do we have the premium upgrade?
//            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
//            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
//            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

//            // Do we have the infinite gas plan?
//            Purchase infiniteGasPurchase = inventory.getPurchase(SKU_INFINITE_GAS);
//            mSubscribedToInfiniteGas = (infiniteGasPurchase != null &&
//                    verifyDeveloperPayload(infiniteGasPurchase));
//            Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
//                    + " infinite gas subscription.");
//            if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

            //TODO:이부분 뭐지? 원래 있떤것 주석처리하고 새로 만듬.
            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
//            Purchase gasPurchase = inventory.getPurchase(SKU_03);
//            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
//                Log.d(TAG, "We have gas. Consuming it.");
//                mHelper.consumeAsync(inventory.getPurchase(SKU_03), mConsumeFinishedListener);
//                return;
//            }
//            Purchase gasPurchase10 = inventory.getPurchase(SKU_10);
//            if (gasPurchase10 != null && verifyDeveloperPayload(gasPurchase10)) {
//                Log.d(TAG, "We have gas. Consuming it.");
//                mHelper.consumeAsync(inventory.getPurchase(SKU_10), mConsumeFinishedListener);
//                return;
//            }

            Purchase gasPurchase01 = inventory.getPurchase(SKU_01);
            if (gasPurchase01 != null && verifyDeveloperPayload(gasPurchase01)) {
                Log.d(TAG, "We have gas. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(SKU_01), mConsumeFinishedListener);
                return;
            }
            Purchase gasPurchase03 = inventory.getPurchase(SKU_05);
            if (gasPurchase03 != null && verifyDeveloperPayload(gasPurchase03)) {
                Log.d(TAG, "We have gas. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(SKU_05), mConsumeFinishedListener);
                return;
            }
            Purchase gasPurchase10 = inventory.getPurchase(SKU_10);
            if (gasPurchase10 != null && verifyDeveloperPayload(gasPurchase10)) {
                Log.d(TAG, "We have gas. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(SKU_10), mConsumeFinishedListener);
                return;
            }
            Purchase gasPurchase50 = inventory.getPurchase(SKU_50);
            if (gasPurchase50 != null && verifyDeveloperPayload(gasPurchase50)) {
                Log.d(TAG, "We have gas. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(SKU_50), mConsumeFinishedListener);
                return;
            }
            Purchase gasPurchase100 = inventory.getPurchase(SKU_100);
            if (gasPurchase100 != null && verifyDeveloperPayload(gasPurchase100)) {
                Log.d(TAG, "We have gas. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(SKU_100), mConsumeFinishedListener);
                return;
            }

//            updateUi();
//            setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                //purchase.getItemType()
                call_buyitem(purchase.getSku());//구매권 업뎃
                callpurchasecnt();
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                //  mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
                //  saveData();
                alert("구매완료 되었습니다.");
            } else {
                Log.d(TAG, "Error while consuming: " + result);
            }
//            updateUi();
//            setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }
    };

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);

                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                Log.d(TAG, "Error purchasing. Authenticity verification failed.");
                // setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");
            if (purchase.getSku().equals(SKU_01)) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase 01.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            } else if (purchase.getSku().equals(SKU_05)) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase 05.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            } else if (purchase.getSku().equals(SKU_10)) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase 10.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            } else if (purchase.getSku().equals(SKU_50)) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase 50");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            } else if (purchase.getSku().equals(SKU_100)) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase 100");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
        }
    };

    private void callpurchasecnt() {
        final String in_id = id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        String buzzer_item_cnt;
                        try {
                            JSONObject json = new JSONObject(response);
                            Log.e(TAG,response);
                            if (json.getString("result").equals("success")) {//성공시
                                JSONArray data = json.getJSONArray("data");
                                buzzer_item_cnt = data.getJSONObject(0).has("buzzer_coin_cnt") ? data.getJSONObject(0).getString("buzzer_coin_cnt") : "";
                                String str = "<font color=\"#7d51fc\">"+buzzer_item_cnt+"</font><font color=\"#282828\"> BZ코인</font>";
                                purchasecnt.setText(Html.fromHtml(str));
                                Log.e(TAG,str);
                            } else {
                                Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), " getMyinfo Error!", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_42_loading_buy_coin", new String[]{in_id}));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void call_buyitem(final String item1) {
        final String in_id = id;
        String item = item1;
        if(item1.contains("bzcoin")){
            if(item1.equals("bzcoin_1")){
                item = "1";
            }else if(item1.equals("bzcoin_5")){
                item = "5";
            }else if(item1.equals("bzcoin_10")){
                item = "10";
            }else if(item1.equals("bzcoin_50")){
                item = "50";
            }else if(item1.equals("bzcoin_100")){
                item = "100";
            }
        }
        final String finalitem = item;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            Log.e(TAG,response);
                            if (json.getString("result").equals("success")) {//성공시
                                //JSONArray data = json.getJSONArray("data");
                                setResult(1004);
                            } else {
                                Toast.makeText(getApplicationContext(), "확인 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), " getMyinfo Error!", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("param", jc.createJsonParam(id, mPassword, "android", Build.VERSION.RELEASE, Build.MODEL,"proc_43_buy_coin", new String[]{in_id, finalitem, ""}));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void button() {
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payload = "";
                mHelper.launchPurchaseFlow(BuzzerPurchase.this, SKU_01, RC_REQUEST,
                        mPurchaseFinishedListener, payload);

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payload = "";
                mHelper.launchPurchaseFlow(BuzzerPurchase.this, SKU_05, RC_REQUEST,
                        mPurchaseFinishedListener, payload);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payload = "";
                mHelper.launchPurchaseFlow(BuzzerPurchase.this, SKU_10, RC_REQUEST,
                        mPurchaseFinishedListener, payload);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payload = "";
                mHelper.launchPurchaseFlow(BuzzerPurchase.this, SKU_50, RC_REQUEST,
                        mPurchaseFinishedListener, payload);
            }
        });
        //TODO : 다섯번째 결제 버튼  -> 이부분에서만 추가 labHelper.java 는 수정 불가능
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payload = "";
                mHelper.launchPurchaseFlow(BuzzerPurchase.this, SKU_100, RC_REQUEST,
                        mPurchaseFinishedListener, payload);
            }
        });



        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }
}
