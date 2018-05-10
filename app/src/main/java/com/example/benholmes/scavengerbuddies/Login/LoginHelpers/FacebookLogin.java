package com.example.benholmes.scavengerbuddies.Login.LoginHelpers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class FacebookLogin {

    private Context ctx;
    private Activity activity;
    public CallbackManager callbackManager;
    private FirebaseAuth mAuth;


    public FacebookLogin(Context ctx, Activity activity){
        this.ctx = ctx;
        this.activity = activity;
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
    }

    public void initalizeLoginButton(LoginButton loginButton){

        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        // If you are using in a fragment, call loginButton.setFragment(this);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook Login", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
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

                        } else {
                            Log.e("Facebook signin failed", "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }



}
