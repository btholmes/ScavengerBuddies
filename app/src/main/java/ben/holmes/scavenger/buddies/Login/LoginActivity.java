package ben.holmes.scavenger.buddies.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ben.holmes.scavenger.buddies.App.Tools.Prefs;
import ben.holmes.scavenger.buddies.BuildConfig;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Login.LoginHelpers.FacebookLogin;
import ben.holmes.scavenger.buddies.App.PopUp.ScavengerDialog;
import ben.holmes.scavenger.buddies.Login.LoginHelpers.EmailLogin;
import ben.holmes.scavenger.buddies.Login.LoginHelpers.GoogleLogin;
import ben.holmes.scavenger.buddies.Login.LoginHelpers.LoginUtil;
import ben.holmes.scavenger.buddies.Main.MainActivity;

import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ben.holmes.scavenger.buddies.Model.ShadowButton;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.R;


public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText userEmail;
    private TextInputEditText userPass;
    private ShadowButton signInButton;

    private ShadowButton facebookButtonImposter;
    private LoginButton facebookButton;
    private SignInButton googleButton;
    private ShadowButton googleButtonImposter;

    private TextView memberText;
    private TextView signInText;

    private FacebookLogin facebookLogin;
    private GoogleLogin googleLogin;
    private EmailLogin emailLogin;

    private ProgressBar progressBar;


    private Prefs prefs;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Database database;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoginUtil.logOut();

        setContentView(R.layout.activity_login);
        database = Database.getInstance(this);

        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        prefs = new Prefs(this);
        initializeViews();
        setOnClicks();
        setFacebookLogin();
        setGoogleLogin();
        emailLogin = new EmailLogin(this, LoginActivity.this);
        setEmailRegistration();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleUser();
        setAuthListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void handleUser(){
        user = mAuth.getCurrentUser();

        if(user != null){
            user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(user != null){
                        goToMain();
                    }
                }
            });
        }
    }

    private void setAuthListener(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(user != null && user.isEmailVerified()){
                            goToMain();
                        }
                    }
                });
            }
        };
    }

    private void initializeViews(){
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        userEmail = findViewById(R.id.userEmail);
        userPass = findViewById(R.id.userPass);
        signInButton = findViewById(R.id.signInButton);
        facebookButton = findViewById(R.id.facebookButton);
        facebookButtonImposter = findViewById(R.id.facebookButtonImposter);
        googleButton = findViewById(R.id.googleButton);
        googleButtonImposter = findViewById(R.id.googleButtonImposter);
        memberText = findViewById(R.id.memberText);
        signInText = findViewById(R.id.signInText);

    }

    private void setOnClicks(){
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView view = (TextView) v;
                if(view.getText().equals("Sign In")){
                    userEmail.setError(null);
                    userPass.setError(null);
                    view.setText("Register");
                    memberText.setText("Need an account? ");
                    signInButton.setText("Sign In");
                    setEmailLogin();
                }else{
                    userEmail.setError(null);
                    userPass.setError(null);
                    view.setText("Sign In");
                    memberText.setText("Already a member? ");
                    signInButton.setText("Register");
                    setEmailRegistration();
                }
            }
        });

    }

    private void setFacebookLogin(){
        facebookLogin = new FacebookLogin(this, LoginActivity.this);
        facebookLogin.initalizeLoginButton(facebookButton);
        facebookButtonImposter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookButtonImposter.quickClick(new ShadowButton.QuickClick() {
                    @Override
                    public void onSuccess() {
                        facebookButton.performClick();
                    }
                });
            }
        });
//        facebookLogin.generateFBKeyHash();
    }

    private void setGoogleLogin(){
        googleLogin = new GoogleLogin(this, LoginActivity.this);
//        googleLogin.initializeLoginButton(googleButton);
        googleButtonImposter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleButtonImposter.quickClick(new ShadowButton.QuickClick() {
                    @Override
                    public void onSuccess() {
//                        googleButton.performClick();
                        googleLogin.signIn();
                    }
                });
            }
        });

    }

    private void handleInvalidInput(String email, String pass){
        if(email.length() <= 0){
            userEmail.setError("Must enter a valid email address");
        }
        if(pass.length() <= 0){
            userPass.setError("Password must contain at least 6 characters");
        }
    }

    private void setEmailLogin(){
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInButton.quickClick(new ShadowButton.QuickClick() {
                    @Override
                    public void onSuccess() {
                        final String email = userEmail.getText().toString();
                        String pass = userPass.getText().toString();
                        handleInvalidInput(email, pass);
                        if(email != null && email.length() > 0 && pass != null && pass.length() > 0){
                            progressBar.setVisibility(View.VISIBLE);
                            emailLogin.signInUser(email, pass, new EmailLogin.SignInCallback() {
                                @Override
                                public void onSuccess() {
                                    final FirebaseUser user = mAuth.getCurrentUser();
                                    user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(user != null){
                                                goToMain();
                                            }else{
                                                boolean emailVerificationSent = prefs.getEmailVerificationSent();
                                                if(emailVerificationSent){
                                                    progressBar.setVisibility(View.GONE);
//                                            showVerifyEmailDialog(email);
                                                }else{
                                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            prefs.setEmailVerificationSent(true);
                                                            progressBar.setVisibility(View.GONE);
//                                                    showVerifyEmailDialog(email);
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(String message) {
                                    handleError(message);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void setEmailRegistration(){
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInButton.quickClick(new ShadowButton.QuickClick() {
                    @Override
                    public void onSuccess() {
                        final String email = userEmail.getText().toString();
                        String pass = userPass.getText().toString();
                        handleInvalidInput(email, pass);
                        if(email != null && email.length() > 0 && pass != null && pass.length() > 0){
                            progressBar.setVisibility(View.VISIBLE);
                            emailLogin.createUser(email, pass, new EmailLogin.SignInCallback() {
                                @Override
                                public void onSuccess() {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            prefs.setEmailVerificationSent(true);
                                            progressBar.setVisibility(View.GONE);
//                                    showVerifyEmailDialog(email);
                                            User user = new User(mAuth.getCurrentUser().getUid(), email);
                                            database.addUser(user);
                                            goToMain();
                                        }
                                    });
                                }

                                @Override
                                public void onError(String message) {
                                    progressBar.setVisibility(View.GONE);
                                    handleError(message);
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    private void showChangePassDialog(final String email){
        final ScavengerDialog scavengerDialog = new ScavengerDialog(this);
        scavengerDialog.setHeaderText("Error");
        scavengerDialog.setBannerText("Wrong Password");
        scavengerDialog.setMessageText("The password you entered was incorrect. Would you like us to send a " +
                "password reset email to " + email + " ?");

        scavengerDialog.setAffirmativeButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scavengerDialog.showProgressBar();
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    scavengerDialog.hideProgressBar();
                                    scavengerDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Email sent successfully",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        scavengerDialog.show();
    }

    private void showVerifyEmailDialog(final String email){
        final ScavengerDialog scavengerDialog = new ScavengerDialog(this);
        scavengerDialog.setHeaderText("Final Step");
        scavengerDialog.setBannerText("Verify Email");
        scavengerDialog.setMessageText("We have sent an email verification to " + email +
        ". Please check your email and click the provided link before continuing.");

        scavengerDialog.setNegativeButtonText("Ok");
        scavengerDialog.setAffirmativeButtonText("Resend Email");

        scavengerDialog.setAffirmativeButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scavengerDialog.showProgressBar();
                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        prefs.setEmailVerificationSent(true);
                        scavengerDialog.hideProgressBar();
                        scavengerDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Verification email resent", Toast.LENGTH_LONG);
                    }
                });
            }
        });

        scavengerDialog.show();
    }

    private void handleError(String message){
        if(message.contains("email address is already in use by another account")){
            userEmail.setError("Email address is already in use. If this is your email, please sign in instead.");
        }else if(message.contains("email address is badly formatted")){
            userEmail.setError("Email must be in a format such as name@domain.com");
        }else if(message.contains("The given password is invalid")){
            userPass.setError("Password must contain at least 6 characters");
        }else if(message.contains("password is invalid or the user does not have a password")){
            userPass.setError("Incorrect password");
            showChangePassDialog(userEmail.getText().toString());
        }else if(message.contains("There is no user record corresponding to this identifier")){
            userEmail.setError("Given email does not exist. If you would like to create an account, please register.");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookLogin.callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == googleLogin.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googleLogin.firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }


    public void goToMain(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
//        finish();
    }

}
