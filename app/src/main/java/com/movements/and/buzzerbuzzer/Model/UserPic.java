package com.movements.and.buzzerbuzzer.Model;

/**
 * Created by samkim on 2017. 8. 9..
 */

public class UserPic {
    private String no;
    private String user_pic_src;
    private String date;

    public UserPic(String no, String user_pic_src, String date) {

        this.no = no;
        this.user_pic_src = user_pic_src;
        this.date = date;
    }

    public UserPic(String no, String user_pic_src) {
        this.no = no;
        this.user_pic_src = user_pic_src;
    }

    public String getNo() {
        return no;
    }

    public String getUser_pic_src() {
        return user_pic_src;
    }

    public String getDate() {
        return date;
    }
}
