package com.movements.and.buzzerbuzzer.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.movements.and.buzzerbuzzer.ChangePhonenum;
import com.movements.and.buzzerbuzzer.Membership_phone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by samkim on 2017. 11. 1..
 */

public class SMSReceiver extends BroadcastReceiver {
    static final String logTag = "SmsReceiver";

    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // SMS를 받았을 경우에만 반응하도록 if문을 삽입
        Log.e("SMS리시버", "들어옴");
        if (intent.getAction().equals(ACTION)) {

            Log.e("SMS리시버", "이프문 들어옴");
            StringBuilder sms = new StringBuilder();    // SMS문자를 저장할 곳
            Bundle bundle = intent.getExtras();         // Bundle객체에 문자를 받아온다
            if (bundle == null) {
                return;
            }

            if (bundle != null) {
                // 번들에 포함된 문자 데이터를 객체 배열로 받아온다
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                if (pdusObj == null) {
                    return;
                }
                // SMS를 받아올 SmsMessage 배열을 만든다
                SmsMessage[] messages = new SmsMessage[pdusObj.length];

                for (int i = 0; i < messages.length; i++) {
//                // SMS를 받아올 SmsMessage 배열을 만든다
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    // SmsMessage의 static메서드인 createFromPdu로 pdusObj의
                    // 데이터를 message에 담는다
                    // 이 때 pdusObj는 byte배열로 형변환을 해줘야 함

                    final String address = messages[i].getOriginatingAddress();
                    if(!address.equals("07077690529")){
                    //    Log.e(logTag, "DisplayOriginatingAddress : " + messages[0].getDisplayOriginatingAddress());
                        return;
                    }
                }

                // SmsMessage배열에 담긴 데이터를 append메서드로 sms에 저장
                for (SmsMessage smsMessage : messages) {
                    // getMessageBody메서드는 문자 본문을 받아오는 메서드
                    sms.append(smsMessage.getMessageBody());
                }

                String smsBody = sms.toString(); // StringBuilder 객체 sms를 String으로 변환
               // Log.e(logTag, "smsBody : "+ smsBody);

                // "\\d{6}" : 일반적으로 인증번호는 6자리 숫자로 \\d는 숫자, {6}는 자리수이다
                Pattern pattern = Pattern.compile("\\d{4}");

                // matcher에 smsBody와 위에서 만든 Pattern 객체를 매치시킨다
                Matcher matcher = pattern.matcher(smsBody);
                //Log.e(logTag, "matcher : "+ matcher);
                String authNumber = null;

                // 패턴과 일치하는 문자열이 있으면 그 첫번째 문자열을 authNumber에 담는다
                if (matcher.find()) {
                    Log.e("SMS리시버", "인증번호 찾음");
                    authNumber = matcher.group(0);
                }
                //Log.e(logTag, "authNumber : "+ authNumber);
                if (authNumber != null) {
                   // Log.e(logTag, "authNumber if : "+ authNumber);
                   // static 메서드인 inputAuthNumber를 통해 authNumber를 넘긴다
                   //Membership.inputAuthNumber(authNumber);
                    Log.e("SMS리시버", "인증번호 넘기기 실행");
                    try{
                        ChangePhonenum.inputAuthNumber(authNumber);
                    }catch (NullPointerException e){

                    }

                    try{
                        Membership_phone.inputAuthNumber(authNumber);
                    }catch (NullPointerException e){

                    }


                }

            }

        }
    }


}
