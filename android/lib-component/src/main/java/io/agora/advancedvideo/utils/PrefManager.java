package io.agora.advancedvideo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import io.agora.advancedvideo.Constants;

public class PrefManager {
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }
}
