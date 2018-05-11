package ben.holmes.scavenger.buddies.App.Tools;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    public static final String DEFAULT_SHARED_PREFS = "Default directory where prefs are stored";
    public static final String EMAIL_VERIFICATION_SENT = "boolean stores if email verification has been sent";

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
}
