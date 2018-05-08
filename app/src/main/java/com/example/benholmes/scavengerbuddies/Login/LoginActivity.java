package com.example.benholmes.scavengerbuddies.Login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.benholmes.scavengerbuddies.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText userEmail;
    private TextInputEditText userPass;
    private Button signInButton;
    private Button facebookButton;
    private Button googleButton;

    private TextView memberText;
    private TextView signInText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setOnClicks();

    }


    private void initializeViews(){
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        userEmail = findViewById(R.id.userEmail);
        userPass = findViewById(R.id.userPass);
        signInButton = findViewById(R.id.signInButton);
        facebookButton = findViewById(R.id.facebookButton);
        googleButton = findViewById(R.id.googleButton);
        memberText = findViewById(R.id.memberText);
        signInText = findViewById(R.id.signInText);
    }

    private void setOnClicks(){
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView view = (TextView) v;
                if(view.getText().equals("Sign In")){
                    view.setText("Register");
                    memberText.setText("Need an account? ");
                    signInButton.setText("Sign In");
                }else{
                    view.setText("Sign In");
                    memberText.setText("Already a member? ");
                    signInButton.setText("Register");
                }
            }
        });

    }

}
