package com.movements.and.buzzerbuzzer.Encrypt;


/**
 * Created by samkim on 2017. 11. 10..
 */

public class PasswordEn {
    private String key1 = "_Buzzer2131041567055524";
    private String key2 = "2131041567055524";

    public PasswordEn() {

    }

    public String PasswordEn(String password) {
        String result = EncryptionModule.getInstatnce().running(password, key1, key2);
        return result;
    }

}
