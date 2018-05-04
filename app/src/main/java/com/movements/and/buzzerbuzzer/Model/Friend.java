package com.movements.and.buzzerbuzzer.Model;

/**
 * Created by samkim on 2017. 8. 7..
 */

public class Friend {
    private String pic_src;
    private String nickname;
    private String fr_id;
    private int fr_gauge_angle;
    private int gauge_type_user;
    private String condition_msg;
    private int location_release_yn;
    private String distance;
    private boolean touch;

    public Friend() {
    }


    public Friend(String pic_src, String nickname, String fr_id, int fr_gauge_angle, int gauge_type_user, String condition_msg, int location_release_yn) {
        this.pic_src=pic_src;
        this.nickname=nickname;
        this.fr_id=fr_id;
        this.fr_gauge_angle=fr_gauge_angle;
        this.gauge_type_user=gauge_type_user;
        this.condition_msg=condition_msg;
        this.location_release_yn=location_release_yn;
    }

    public Friend(String nickname, String pic_src, String fr_id, int location_release_yn) {
        this.pic_src=pic_src;
        this.nickname=nickname;
        this.fr_id=fr_id;
        this.location_release_yn=location_release_yn;
    }


    public Friend(String pic_src, String nickname, String fr_id, String condition_msg, int location_release_yn) {
        this.pic_src=pic_src;
        this.nickname=nickname;
        this.fr_id=fr_id;
        this.condition_msg=condition_msg;
        this.location_release_yn=location_release_yn;
    }

    public Friend(String id, String user_pic) {
        this.pic_src=user_pic;
        this.fr_id=id;
    }

    public Friend(String id, String user_pic, String distance, String fr_id) {
        this.pic_src=user_pic;
        this.fr_id=id;
        this.distance=distance;
        this.fr_id=fr_id;
    }

    public int getFr_gauge_angle() {
        return fr_gauge_angle;
    }

    public String getPic_src() {
        return pic_src;
    }

    public String getNickname() {
        return nickname;
    }

    public String getFr_id() {
        return fr_id;
    }

    public String getDistance() {
        return distance;
    }

    public int getGauge_type_user() {
        return gauge_type_user;
    }

    public int getLocation_release_yn() {
        return location_release_yn;
    }

    public String getCondition_msg() {
        return condition_msg;
    }

    public void setLocation_release_yn(int location_release_yn) {
        this.location_release_yn = location_release_yn;
    }

    public boolean isTouch() {
        return touch;
    }

    public void setTouch(boolean touch) {
        this.touch = touch;
    }
}
