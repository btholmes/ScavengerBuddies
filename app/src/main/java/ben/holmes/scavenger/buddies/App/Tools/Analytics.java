package ben.holmes.scavenger.buddies.App.Tools;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {

    private Context ctx;
    private FirebaseAnalytics firebaseAnalytics;

    public Analytics(Context ctx){
        this.ctx = ctx;
        firebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
    }



    public void logNewGame(String challengerEmail, String defenderEmail){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "New Game");
        bundle.putString("Challenger_Email", challengerEmail);
        bundle.putString("Defender_Email", defenderEmail);
        firebaseAnalytics.logEvent("New_Game", bundle);
    }


}
