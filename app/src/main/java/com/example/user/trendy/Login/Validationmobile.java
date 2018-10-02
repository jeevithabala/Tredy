package com.example.user.trendy.Login;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validationmobile {
    private static final String PHONE_REGEX = "^[0-9][0-9]{9}$";
    private static final String PHONE_MSG = "invalid mobile";
    public static boolean isPhoneNumber(EditText editText, boolean required) {
        return isValidmobile(editText, PHONE_REGEX, PHONE_MSG, required);
    }
    public static boolean isValidmobile(EditText editText, String regex, String errMsg, boolean required) {
        String text = editText.getText().toString().trim();
        editText.setError(null);
        if ( required && !hasText(editText) ) return false;
        if (required && !Pattern.matches(regex, text)) {
            //editText.setError(errMsg);
            return false;
        };
        return true;
    }


    public static boolean hasText(EditText editText) {
        String text = editText.getText().toString().trim();
        editText.setError(null);
        if (text.length() == 0) {
            //editText.setError(PHONE_MSG);
            return false;
        }
        return true;
    }
}
