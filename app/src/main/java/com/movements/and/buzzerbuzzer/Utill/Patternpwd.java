package com.movements.and.buzzerbuzzer.Utill;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by samkim on 2017. 10. 16..
 */

public class Patternpwd {
    //private static final String Passwrod_PATTERN = "^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{8,16}$";
    //private static final String Passwrod_PATTERN = "^[A-Za-z0-9_@./#&+-]*.{6,20}$";//6~20자 영분
    //private static final String Passwrod_PATTERN = "^[A-Za-z0-9]*.{6,20}$";//6~20자 영분
    private static final String Email_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{6,20}$";//6~20자 이메일
    //private static final String Email_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{6,20}$";//6~20자 이메일
   // private static final String Passwrod_PATTERN = "^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$";//6~20자 비밀번호
    private final String Passwrod_PATTERN = "^(?=.*[a-zA-Z])((?=.*\\d)|(?=.*\\W)).{8,20}$";//6~20자 비밀번호
    private final String Id_PATTERN = "^[a-zA-Z]{1}[a-zA-Z0-9]{3,19}$";
    private final String Nick_PATTERN = "^[a-zA-Z]{1}[a-zA-Z0-9]{0,13}$";
    private final String State_PATTERN = "^[a-zA-Z]{1}[a-zA-Z0-9]{0,25}$";

    public boolean Passwrodvalidate(final String hex) {
        Pattern pattern = Pattern.compile(Passwrod_PATTERN);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public boolean Idvalidate(final String hex) {
        Pattern pattern = Pattern.compile(Id_PATTERN);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public boolean Emailvalidate(final String hex) {
        Pattern pattern = Pattern.compile(Email_PATTERN);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public boolean Nickvalidate(final String hex) {
        Pattern pattern = Pattern.compile(Nick_PATTERN);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    public boolean Statevalidate(final String hex) {
        Pattern pattern = Pattern.compile(State_PATTERN);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }


}
