package ben.holmes.scavenger.buddies.App.Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ben.holmes.scavenger.buddies.App.PopUp.ScavengerDialog;
import ben.holmes.scavenger.buddies.Main.MainActivity;
import ben.holmes.scavenger.buddies.R;

public class CustomBottomView extends RelativeLayout {

//    private final String FACEBOOK_MESSENGER_PACKAGE = "com.facebook.katana";
    // To share app on facebook messenger
    public static final String GOOGLE_PLAY_STORE_URI = "https://play.google.com/store/apps/details?id=edu.iastate.MyState";
    public static  final String FACEBOOK_MESSENGER_PACKAGE = "com.facebook.orca";
    public static  final String TWITTER_MESSENGER_PACKAGE = "com.twitter.android";
    public static  final String SMS_MESSENGER_PACKAGE = "vnd.android-dir/mms-sms";
    public static  final String EMAIL_MESSENGER_PACKAGE = "message/rfc822";
    private Context ctx;
    private View root;
    private LinearLayout sms;
    private LinearLayout twitter;
    private LinearLayout facebook;
    private LinearLayout email;


    public CustomBottomView(Context ctx){
        super(ctx);
        sharedConstructor(ctx, null);
    }

    public CustomBottomView(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
        sharedConstructor(ctx, attrs);
    }

    private void sharedConstructor(Context ctx, @Nullable AttributeSet attrs){
        this.ctx = ctx;
        root = LayoutInflater.from(ctx).inflate(R.layout.custom_bottom_view, this);
        sms = root.findViewById(R.id.sms);
        twitter = root.findViewById(R.id.twitter);
        facebook = root.findViewById(R.id.facebook);
        email = root.findViewById(R.id.email);
        init();
    }


    private void sendToApplicaiton(String app){
        Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(app);
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Check this app out: "
                            + GOOGLE_PLAY_STORE_URI
            );
            shareIntent.setType("text/plain");
            shareIntent.setPackage(app);
            try{
                ctx.startActivity(shareIntent);
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            // The application does not exist
            // Open GooglePlay or use the default system picker
            if(app.contains("facebook")){
                showDialog("Please download the Facebook app in order to invite your friends");
            }else if(app.contains("twitter")){
                showDialog("Please download the Twitter app in order to invite your friends");
            }
        }
    }

    private void showDialog(String message){
        final ScavengerDialog dialog = new ScavengerDialog(ctx);
        dialog.setMessageText(message);
        dialog.showSingleOkButton();
        dialog.hideHeader();
        dialog.setBannerText("App Not Found");
        dialog.setSingleOkButtonClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void sendSMS(){
        Intent shareIntent = new Intent(Intent.ACTION_VIEW);
        shareIntent.setType(SMS_MESSENGER_PACKAGE);
        shareIntent.putExtra("sms_body",
                "Check this app out: "
                        + GOOGLE_PLAY_STORE_URI
        );
        try{
            ctx.startActivity(shareIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendEmail(){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType(EMAIL_MESSENGER_PACKAGE);
        email.putExtra(Intent.EXTRA_SUBJECT, "Hey check this out");
        email.putExtra(Intent.EXTRA_TEXT,
                "Check this app out: "
                        + GOOGLE_PLAY_STORE_URI
        );
        try{
            ctx.startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void init(){
        sms.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });
        twitter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToApplicaiton(TWITTER_MESSENGER_PACKAGE);
            }
        });
        facebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToApplicaiton(FACEBOOK_MESSENGER_PACKAGE);
            }
        });
        email.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    public void show(Activity activity){
//        if(activity instanceof MainActivity)
//            ((MainActivity)activity).setViewpagerCanSwipe(false);

        setVisibility(VISIBLE);
//        Animation slideUp = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
//        startAnimation(slideUp);
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ctx, R.color.black));
    }

    public void hide(Activity activity){
//        if(activity instanceof MainActivity)
//            ((MainActivity)activity).setViewpagerCanSwipe(true);

//        Animation slideDown = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
//        startAnimation(slideDown);
        setVisibility(GONE);

        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ctx, R.color.colorPrimary));
    }

}
