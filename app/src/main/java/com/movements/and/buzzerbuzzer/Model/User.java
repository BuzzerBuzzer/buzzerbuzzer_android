package com.movements.and.buzzerbuzzer.Model;

/**
 * Created by samkim on 2017. 4. 13..
 */

public class User {
    private String id;
    private String password;
    private String email;
    private String register_form;
    private String facebook_id;
    private String facebook_id_key;
    private String kakaotalk_id;
    private String gender;
    private String age;
    private String nickname;
    private String user_pic;
    private String phonenum;
    private String regist_day;
    private String withdraw_day;
    private String condition_message;
    private String cut_day;
    private String declare_cnt;
    private String device_id;

    public User() {

    }

    public User(String id, String password, String email, String register_form, String facebook_id, String facebook_id_key, String kakaotalk_id, String gender, String age, String user_pic, String regist_day, String withdraw_day, String condition_message, String cut_day, String declare_cnt, String device_id) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.register_form = register_form;
        this.facebook_id = facebook_id;
        this.facebook_id_key = facebook_id_key;
        this.kakaotalk_id = kakaotalk_id;
        this.gender = gender;
        this.age = age;
        this.user_pic = user_pic;
        this.regist_day = regist_day;
        this.withdraw_day = withdraw_day;
        this.condition_message = condition_message;
        this.cut_day = cut_day;
        this.declare_cnt = declare_cnt;
        this.device_id = device_id;

    }

    public User(String id) {

    }

    public User(String id, String pic_src, String nickname, String phone) {
        this.id = id;
        this.user_pic = pic_src;
        this.nickname = nickname;
        this.phonenum = phone;

    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getRegister_form() {
        return register_form;
    }

    public String getFacebook_id() {
        return facebook_id;
    }

    public String getFacebook_id_key() {
        return facebook_id_key;
    }

    public String getKakaotalk_id() {
        return kakaotalk_id;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public String getRegist_day() {
        return regist_day;
    }

    public String getWithdraw_day() {
        return withdraw_day;
    }

    public String getCondition_message() {
        return condition_message;
    }

    public String getCut_day() {
        return cut_day;
    }

    public String getDeclare_cnt() {
        return declare_cnt;
    }

    public String getDevice_id() {
        return device_id;
    }

}
