package com.movements.and.buzzerbuzzer.Utill;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by samkim on 2017. 7. 10..
 */

public class GetAddressGeoCoder {
    /** 위도와 경도 기반으로 주소를 리턴하는 메서드*/
    public String getAddressGeoCoder(Context context, LatLng latLng) {
        String s = "";
        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        //주소 목록을 담기 위한 HashMap
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException ioException) {
            s = "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            s = "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            if (s.isEmpty()) {
                s = "주소 미발견";
            }
        } else {
            Address address = addresses.get(0);
            Log.e("location", address.getAddressLine(0).toString());
            s = address.getAddressLine(0).toString();
        }
        return s;
    }


    public static String getAddressGeoCoder(FragmentActivity activity, Location location) {
        String s = "";
        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        //주소 목록을 담기 위한 HashMap
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioException) {
            s = "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            s = "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            if (s.isEmpty()) {
                s = "주소 미발견";
            }
        } else {
            Address address = addresses.get(0);
            Log.e("location", address.getAddressLine(0).toString());
            //s = address.getAddressLine(0).toString();
            s = address.getLocality() + " " + address.getThoroughfare();

        }
//        Log.d("실제주소", String.valueOf(s.length()));
//        Log.e("실제주소", s);
        return s;
    }

}
