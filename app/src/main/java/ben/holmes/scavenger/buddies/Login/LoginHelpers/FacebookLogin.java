package ben.holmes.scavenger.buddies.Login.LoginHelpers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
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

import ben.holmes.scavenger.buddies.App.PopUp.ScavengerDialog;
import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.App.Tools.FacebookUtil;
import ben.holmes.scavenger.buddies.App.Tools.Prefs;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Friends.FriendsFragment;
import ben.holmes.scavenger.buddies.Login.LoginActivity;
import ben.holmes.scavenger.buddies.Main.MainActivity;
import ben.holmes.scavenger.buddies.Main.adapter.PageFragmentAdapter;
import ben.holmes.scavenger.buddies.Model.FacebookProfile;
import ben.holmes.scavenger.buddies.Model.User;

public class FacebookLogin {

    private Context ctx;
    private Activity activity;
    public CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private Prefs prefs;
    private FirebaseUser currentUser;


    public FacebookLogin(Context ctx, Activity activity){
        this.ctx = ctx;
        this.activity = activity;
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
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

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FacebookUtil facebookUtil = new FacebookUtil(ctx);
                            facebookUtil.getBasicInfoFromFacebook(new FacebookUtil.FacebookBasicInfoCallback() {
                                @Override
                                public void onComplete(String email, String firstName, String lastName, String profileUrl) {
//                                    FacebookProfile facebookProfile = new FacebookProfile(email, firstName, lastName, profileUrl);
//                                    prefs.saveFacebookProfile(facebookProfile);
                                    if(currentUser == null)
                                        currentUser = mAuth.getCurrentUser();

                                    prefs.setFacebookConnected(currentUser.getUid(), true);
                                    User user = new User(currentUser.getUid(), currentUser.getEmail());
                                    user.setFirstName(firstName);
                                    user.setLastName(lastName);
                                    user.setPhotoUrl(profileUrl);
                                    user.setDisplayName(firstName + " " + lastName);
                                    Database.getInstance(ctx).addUser(user);
                                    ((LoginActivity)activity).goToMain();
                                }
                            });
                        } else {
                            Log.e("Facebook signin failed", "signInWithCredential:failure", task.getException());
                            if(task.getException().toString().contains("FirebaseAuthUserCollisionException")){
//                            An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated
                                if(activity instanceof ScavengerActivity)
                                    showErrorDialog(credential);
                                else if(activity instanceof LoginActivity)
                                    showUserCollisionError();
                            }
                        }
                    }
                });
    }

    private void showUserCollisionError(){
        final ScavengerDialog dialog = new ScavengerDialog(ctx);
        dialog.setHeaderText("Error");
        dialog.setBannerText("User Collision");
        dialog.setMessageText("An account already exists with the same email address but different sign-in credentials. Please " +
                "sign in with the provider you used previously. We can link your account to facebook from inside the app.");
        dialog.showSingleOkButton();
        dialog.setSingleOkButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUtil.logOut();
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    private void showErrorDialog(final AuthCredential credential){
        final ScavengerDialog dialog = new ScavengerDialog(ctx);
        dialog.setHeaderText("Error");
        dialog.setBannerText("User Collision");
        dialog.setMessageText("An account already exists with the same email address but different sign-in credentials. Would" +
                " you like to add Facebook as a sign in provider?");
        dialog.setAffirmativeButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FacebookUtil facebookUtil = new FacebookUtil(ctx);
                facebookUtil.getBasicInfoFromFacebook(new FacebookUtil.FacebookBasicInfoCallback() {
                    @Override
                    public void onComplete(String email, String firstName, String lastName, String profileUrl) {
                        if(email.length() <= 0 || email == null){
                            failedToHandleUserCollision(dialog, facebookUtil, credential);
                        }else{
//                            Successfully retrieved the email
//                            FacebookProfile facebookProfile = new FacebookProfile(email, firstName, lastName, profileUrl);
//                            prefs.saveFacebookProfile(facebookProfile);
                            if(currentUser == null)
                                currentUser = FirebaseAuth.getInstance().getCurrentUser();

                            Database.getInstance(ctx).updateUserName(firstName, lastName);
                            Database.getInstance(ctx).updateUserPhotoUrl(profileUrl);


                            prefs.setFacebookConnected(currentUser.getUid(), true);
                            facebookUtil.linkFacebookCredential(credential, activity, new FacebookUtil.FacebookCredentialCallback() {
                                @Override
                                public void onComplete(boolean isSuccessful) {
                                    if(isSuccessful){
                                        dialog.dismiss();
                                    }else{
                                        failedToHandleUserCollision(dialog, facebookUtil, credential);
                                    }
                                }
                            });
                            if(activity instanceof ScavengerActivity){
                                //update friends fragment to hide facebook connect button
                                MainActivity mainActivity = (MainActivity)activity;
                                PageFragmentAdapter adapter = (PageFragmentAdapter) mainActivity.getViewPager().getAdapter();
                                FriendsFragment friendsFragment = (FriendsFragment)adapter.getItem(1);
                                friendsFragment.updatePage();
                            }
                        }
                    }
                });
            }
        });
        dialog.show();
    }


    private void failedToHandleUserCollision(final ScavengerDialog dialog, FacebookUtil facebookUtil, AuthCredential credential){
        if(currentUser == null)
            currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser == null) {
            dialog.showSingleOkButton();
            dialog.setMessageText("We could not link your account with the facebook provider. Try signing " +
                    "in with the provider you used previously. We can link you from inside the app.");
            dialog.setSingleOkButtonClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginUtil.logOut();
                    dialog.dismiss();
                }
            });
        }
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
