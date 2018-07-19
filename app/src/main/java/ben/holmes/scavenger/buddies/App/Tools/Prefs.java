package ben.holmes.scavenger.buddies.App.Tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.login.LoginResult;
import com.google.gson.Gson;

public class Prefs {

    public static final String DEFAULT_SHARED_PREFS = "Default directory where prefs are stored";
    public static final String EMAIL_VERIFICATION_SENT = "boolean stores if email verification has been sent";
    public static final String FACEBOOK_LOGIN_RESULT = "Json String Login Result from Facebook Login ";
    public static final String HAS_FACEBOOK_LOGIN = "boolean stores if they have logged in with facebook ";
    public static final String SELECTED_WHEEL_FRAGMENT = "int stores current state of wheel";

    private Context ctx;
    private SharedPreferences prefs;

    public Prefs(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences(DEFAULT_SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public void setEmailVerificationSent(boolean set){
        prefs.edit().putBoolean(EMAIL_VERIFICATION_SENT, set).commit();
    }

    public boolean getEmailVerificationSent(){
        return prefs.getBoolean(EMAIL_VERIFICATION_SENT, false);
    }

    public void saveFacebookLoginResult(LoginResult loginResult){
        prefs.edit().putString(FACEBOOK_LOGIN_RESULT, new Gson().toJson(loginResult)).commit();
    }

    public LoginResult getFacebookLoginResult(){
        String json = prefs.getString(FACEBOOK_LOGIN_RESULT, null);
        if(json == null) return null;
        LoginResult loginResult = new Gson().fromJson(json, LoginResult.class);
        return  loginResult;
    }

    public void putHasLoggedInFacebook(boolean loggedIn){
        prefs.edit().putBoolean(HAS_FACEBOOK_LOGIN, loggedIn).commit();
    }

    public boolean getHasLoggedInFacebook(){
        return prefs.getBoolean(HAS_FACEBOOK_LOGIN, false);
    }

    public void storeSelectedWheel(int selected){
        prefs.edit().putInt(SELECTED_WHEEL_FRAGMENT, selected).commit();
    }

    public int getSelectedWheel(){
        return prefs.getInt(SELECTED_WHEEL_FRAGMENT, -1);
    }

}
