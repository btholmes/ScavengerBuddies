package com.example.benholmes.scavengerbuddies.App.Splash;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.benholmes.scavengerbuddies.Login.LoginActivity;
import com.example.benholmes.scavengerbuddies.Main.MainActivity;
import com.example.benholmes.scavengerbuddies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LaunchActivity extends AppCompatActivity{

    ImageView logo;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = findViewById(R.id.logo);
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    protected void onResume() {
        super.onResume();
        user = mAuth.getCurrentUser();
        if(user != null){
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(user != null && user.isEmailVerified()){
                        goToMain();
                    }else{
                        animateLogo(logo);
                    }
                }
            });
        }else{
            animateLogo(logo);
        }
    }

    private void goToMain(){
        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goToLogIn(){
        Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void animateLogo(ImageView logo){

        logo.animate().setDuration(2000).setInterpolator(new LinearInterpolator())
                .rotationY(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                goToLogIn();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

}
