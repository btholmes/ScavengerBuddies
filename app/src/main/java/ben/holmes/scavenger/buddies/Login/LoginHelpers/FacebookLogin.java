package ben.holmes.scavenger.buddies.Login.LoginHelpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.util.Arrays;

import ben.holmes.scavenger.buddies.App.Tools.Prefs;
import ben.holmes.scavenger.buddies.Login.LoginActivity;

public class FacebookLogin {

    private Context ctx;
    private Activity activity;
    public CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private Prefs prefs;


    public FacebookLogin(Context ctx, Activity activity){
        this.ctx = ctx;
        this.activity = activity;
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        prefs = new Prefs(ctx);
    }

    public void initalizeLoginButton(LoginButton loginButton){

        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        // If you are using in a fragment, call loginButton.setFragment(this);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("Facebook Login", "facebook:onSuccess:" + loginResult.getAccessToken());
                handleFacebookAccessToken(loginResult.getAccessToken());
                prefs.saveFacebookLoginResult(loginResult);
                prefs.putHasLoggedInFacebook(true);

            }

            @Override
            public void onCancel() {
                Log.d("Facebook Cancel", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Facebook Error", "facebook:onError", error);
                // ...
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            ((LoginActivity)activity).goToMain();

                        } else {
                            Log.e("Facebook signin failed", "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }


    public void getUserFriends(){


//        boolean hasLoggedInFacebook = prefs.getHasLoggedInFacebook();
//        if(!hasLoggedInFacebook) return;

        LoginResult loginResult = prefs.getFacebookLoginResult();
        AccessToken accessToken = loginResult.getAccessToken();
        String userId = loginResult.getAccessToken().getUserId();
        String path = "/" + userId + "/friends";

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                path,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here
                        GraphResponse copy = response;

                    }
                });

        request.executeAsync();

    }

    /**
     * For some reason, generating the key hash through a terminal only works on first login,
     * after that it always fails.. So this method of generating a hash is supposed to work on
     * successive attempts.
     */
    public void generateFBKeyHash(){
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(
                    "ben.holmes.scavenger.buddies",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
