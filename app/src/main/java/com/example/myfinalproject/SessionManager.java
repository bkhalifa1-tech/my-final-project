package com.example.myfinalproject;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager{
    private static final String PREF_NAME="jenini_session";
    private static final String KEY_LOGIN="is_logged_in";
    private static final String KEY_USER_ID="user_id";
    private static final String KEY_EMAIL="email";
    private final SharedPreferences preferences;

    public SessionManager(Context context){
        preferences=context.getApplicationContext().getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
    }

    public void createLoginSession(long userId,String email){
        preferences.edit().putBoolean(KEY_LOGIN,true).putLong(KEY_USER_ID,userId).putString(KEY_EMAIL,email).apply();
    }
    public boolean isLoggedIn(){
        return preferences.getBoolean(KEY_LOGIN,false)&&getUserId()>0;
    }
    public long getUserId(){
        return preferences.getLong(KEY_USER_ID,-1);
    }
    public String getEmail(){
        return preferences.getString(KEY_EMAIL,"");
    }
    public void updateEmail(String email){
        preferences.edit().putString(KEY_EMAIL,email).apply();
    }
    public void logout(){
        preferences.edit().clear().apply();
    }
}
