package ben.holmes.scavenger.buddies.Login.LoginHelpers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailLogin {

    private Context ctx;
    private Activity activity;
    private FirebaseAuth mAuth;

    public EmailLogin(Context ctx, Activity activity){

        this.ctx = ctx;
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();

    }

    public interface SignInCallback{
        void onSuccess();
        void onError(String message);
    }

    public void createUser(String email, String password, final SignInCallback callback ){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            Log.e("Error creating user", task.getException().getMessage());
                            callback.onError(task.getException().getMessage());
                        }

                    }
                });
    }


    public void signInUser(String email, String password, final SignInCallback callback){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            Log.e("Error signing in user", task.getException().getMessage());
                            callback.onError(task.getException().getMessage());
                        }
                    }
                });
    }



}
