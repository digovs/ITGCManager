package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.parse.ParseException;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.dialogs.ErrorMessageDialogActivity;

public abstract class ParseUtils {

    public static final String PREFS_NAME = "MyPrefsFile";

    public static void handleParseException(Context context, ParseException exception) {
        int exceptionCode = exception.getCode();
        switch (exceptionCode) {
            case ParseException.CONNECTION_FAILED:
                callErrorDialogWithMessage(context, context.getString(R.string.connection_failed));
                break;

            case ParseException.TIMEOUT:
                callErrorDialogWithMessage(context, context.getString(R.string.connection_timeout));
                break;

            case ParseException.OBJECT_NOT_FOUND:
                callErrorDialogWithMessage(context, context.getString(R.string.login_failed));
                break;

        }
    }

    public static void callErrorDialogWithMessage(Context context, String message) {
        Intent i = new Intent(context, ErrorMessageDialogActivity.class);
        i.putExtra(ErrorMessageDialogActivity.KEY_MESSAGE_TEXT, message);
        context.startActivity(i);
    }

    public static void saveStringToSession(Context context, String key, String value) {
        SharedPreferences session = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = session.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringFromSession(Context context, String key) {
        SharedPreferences session = context.getSharedPreferences(PREFS_NAME, 0);
        return session.getString(key, "");
    }


}
