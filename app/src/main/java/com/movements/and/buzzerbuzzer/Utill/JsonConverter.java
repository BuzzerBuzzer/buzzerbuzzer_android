package com.movements.and.buzzerbuzzer.Utill;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MONO-RAMA.
 * User     : ManSu Kim
 * Project  : VolleyTest
 * Package  : com.monorama.volleytest.util
 * Date     : 2017-06-20
 * Time     : 오후 4:25
 */

public class JsonConverter {

    public String createJsonParam(String user_id, String user_pw, String deviceOS, String deviceOSVer, String deviceName,
                                  String serviceCode, String[] datas){
        JSONObject param = new JSONObject();
        JSONArray dataArr = new JSONArray();
        try {
            param.put("userId", user_id);
            param.put("userPw", user_pw);
            param.put("deviceOS", deviceOS);
            param.put("deviceOSVer", deviceOSVer);
            param.put("deviceName", deviceName);
            param.put("serviceCode", serviceCode);
            for(String data : datas){
                dataArr.put(data);
            }
            param.put("data", dataArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param.toString();
    }

    public String createJsonParam(String serviceCode, String[] datas) {
        JSONObject param = new JSONObject();
        JSONArray dataArr = new JSONArray();
        try {

            param.put("serviceCode", serviceCode);
            for(String data : datas){
                dataArr.put(data);
            }
            param.put("data", dataArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param.toString();
    }
}
