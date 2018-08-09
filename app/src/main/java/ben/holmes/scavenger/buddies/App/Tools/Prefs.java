package ben.holmes.scavenger.buddies.App.Tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.login.LoginResult;
import com.google.gson.Gson;

import ben.holmes.scavenger.buddies.Model.FacebookProfile;

public class Prefs {

    public static final String DEFAULT_SHARED_PREFS = "Default directory where prefs are stored";
    public static final String EMAIL_VERIFICATION_SENT = "boolean stores if email verification has been sent";
    public static final String FACEBOOK_LOGIN_RESULT = "Json String Login Result from Facebook Login ";
    public static final String HAS_FACEBOOK_LOGIN = "boolean stores if they have logged in with facebook ";
    public static final String SELECTED_WHEEL_FRAGMENT = "int stores current state of wheel";
    public static final String FACEBOOK_PROFILE = "FacebookProfile model stores facebook users email name and profile pic";
    public static final String FACEBOOK_CONNECTED = "Boolean stores if facebook is connected";
    public static final String CHAT_ENABLED = "Boolean stores if chat is still enables.. it is by default";
    public static final String PUSH_NOTIFICATIONS_ENABLED = "Boolean stores if push notifications are enabled.. are by default";
    public static final String EMAIL_NOTIFICATIONS_ENABLED = "Boolean stores if email notifications are enabled.. are not by default";

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

    public void saveFacebookProfile(FacebookProfile facebookProfile){
        prefs.edit().putString(FACEBOOK_PROFILE, new Gson().toJson(facebookProfile)).commit();
    }

    public FacebookProfile getFacebookProfile(){
        String json = prefs.getString(FACEBOOK_PROFILE, null);
        if(json == null) return null;
        FacebookProfile facebookProfile = new Gson().fromJson(json, FacebookProfile.class);
        return facebookProfile;
    }


    public void setFacebookConnected(String userID, boolean isConnected){
        prefs.edit().putBoolean(userID, isConnected).commit();
    }

    public boolean getFacebookConnected(String userId){
        return prefs.getBoolean(userId, false);
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

    public void setPushNotificationsEnabled(boolean enabled){
        prefs.edit().putBoolean(PUSH_NOTIFICATIONS_ENABLED, enabled).commit();
    }

    public boolean getPushNotificationsEnabled(){
        return prefs.getBoolean(PUSH_NOTIFICATIONS_ENABLED, true);
    }

    public void setChatEnabled(boolean enabled){
        prefs.edit().putBoolean(CHAT_ENABLED, enabled).commit();
    }

    public boolean getChatEnabled(){
        return prefs.getBoolean(CHAT_ENABLED, true);
    }

    public void setEmailNotificationsEnabled(boolean enabled){
        prefs.edit().putBoolean(EMAIL_NOTIFICATIONS_ENABLED, enabled).commit();
    }

    public boolean getEmailNotificationsEnabled(){
        return prefs.getBoolean(EMAIL_NOTIFICATIONS_ENABLED, false);
    }


}
