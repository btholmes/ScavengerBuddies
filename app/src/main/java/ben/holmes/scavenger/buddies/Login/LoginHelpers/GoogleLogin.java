package ben.holmes.scavenger.buddies.Login.LoginHelpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import ben.holmes.scavenger.buddies.Login.LoginActivity;
import ben.holmes.scavenger.buddies.R;


public class GoogleLogin implements View.OnClickListener {

    private Context ctx;
    private Activity activity;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    public GoogleLogin(Context ctx, Activity activity){

        this.ctx = ctx;
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(ctx.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }


    public void initializeLoginButton(SignInButton signInButton){
        for(int i =0; i <signInButton.getChildCount(); i++){
            View v = signInButton.getChildAt(i);
            if(v instanceof TextView){
                ((TextView) v).setText("Continue with Google");
                ((TextView)v).setTextColor(ContextCompat.getColor(ctx, R.color.black));
                break;
            }
        }

        signInButton.setOnClickListener(this);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            ((LoginActivity)activity).goToMain();

//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("Google Auth Failed", "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }




    @Override
    public void onClick(View v) {
        signIn();
    }
}
