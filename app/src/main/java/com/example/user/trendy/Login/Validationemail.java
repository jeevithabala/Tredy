package com.example.user.trendy.Login;

import android.widget.EditText;

import java.util.regex.Pattern;

public class Validationemail {
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String EMAIL_MSG = "invalid email";
    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValidemail(editText, EMAIL_REGEX, EMAIL_MSG, required);
    }
    public static boolean isValidemail(EditText editText, String regex, String errMsg, boolean required) {
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
            //editText.setError(EMAIL_MSG);
            return false;
        }
        return true;
    }
}
