package com.movements.and.buzzerbuzzer.Model;

/**
 * Created by samkim on 2017. 7. 21..
 */

public class Newsmessage {
    private String id;
    private String nickname;
    private int no;
    private String fr_id;
    private String user_pic;
    private String type;
    private String message;
    private String confirm;
    private String noti_time;
    private String frClass;


    public Newsmessage(String id, String fr_id, String user_pic, String type, String msg, String confirm, String noti_time) {
        this.id = id;
        this.fr_id = fr_id;
        this.user_pic = user_pic;
        this.type = type;
        this.message = msg;
        this.confirm = confirm;
        this.noti_time = noti_time;

    }

    public Newsmessage(String user_pic, String nickname, String frClass, int no, String id, String fr_id, String type, String message, String noti_time, String confirm) {
        this.user_pic = user_pic;
        this.nickname = nickname;
        this.no = no;
        this.id = id;
        this.fr_id = fr_id;
        this.type = type;
        this.message = message;
        this.noti_time = noti_time;
        this.confirm = confirm;
        this.frClass = frClass;
    }

    public String getNickname() {
        return nickname;
    }

    public int getNo() {
        return no;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public String getFr_id() {
        return fr_id;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public String getType() {
        return type;
    }

    public String getFrClass() {
        return frClass;
    }

    public String getConfirm() {
        return confirm;
    }

    public String getNoti_time() {
        return noti_time;
    }
}
