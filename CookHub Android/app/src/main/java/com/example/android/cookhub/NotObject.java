package com.example.android.cookhub;

/**
 * Created by archi on 27-10-2017.
 */

public class NotObject {
    public String mText1;
    public String mText2;
    //private String mText3;

    NotObject (String text1, String text2){
        mText1 = text1;
        mText2 = text2;
        //mText3 = text3;
    }

    public String getmText1() {
        return mText1;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }
}
