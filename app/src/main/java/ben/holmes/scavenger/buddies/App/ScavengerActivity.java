package ben.holmes.scavenger.buddies.App;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ben.holmes.scavenger.buddies.App.Tools.FacebookUtil;
import ben.holmes.scavenger.buddies.App.Tools.Prefs;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Login.LoginActivity;
import ben.holmes.scavenger.buddies.Login.LoginHelpers.FacebookLogin;
import ben.holmes.scavenger.buddies.Login.LoginHelpers.LoginUtil;
import ben.holmes.scavenger.buddies.R;


/**
 * Created by benholmes on 5/7/18.
 */

public abstract class ScavengerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FrameLayout fragmentLayout;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Database database;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FacebookLogin facebookLogin;
    private FacebookUtil facebookUtil;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Prefs prefs;


    private static final int READ_CONTACTS = 1000;
    private static final int WRITE_EXTERNAL_STORAGE = 1001;
    private static final int READ_EXTERNAL_STORAGE = 1002;

    public static boolean CAN_READ_CONTACTS = false;
    public static boolean CAN_WRITE_EXTERNAL_STORAGE = false;
    public static boolean CAN_READ_EXTERNAL_STORAGE = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scavenger_base);
        database = Database.getInstance(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        prefs = new Prefs(this);
        facebookUtil = new FacebookUtil(this);
        reference = FirebaseDatabase.getInstance().getReference();
        fragmentManager = getSupportFragmentManager();
        fragmentLayout = findViewById(R.id.fragment_frame);
        setAuthListener();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpToolbar();
    }

    private void setAuthListener(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if(user == null){
                            LoginUtil.logOut();
                            goToLoginActivity();
                        }
                    }
                });
            }
        };
    }

    public void goToLoginActivity(){
        Intent intent = new Intent(ScavengerActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void setUpToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public Toolbar getToolbar(){
        return toolbar;
    }


    public void replaceFragment(Fragment fragment, String title){
        replaceFragment(fragment, title, 0, 0);
    }

    public int convertDpToPixels(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
    }



    public void replaceFragmentDontAdd(Fragment fragment, String title){
        replaceFragmentDontAdd(fragment, title, 0, 0);
    }


    public void replaceFragmentDontAdd(Fragment fragment, String title, int enterAnimation, int exitAnimation){
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(enterAnimation, exitAnimation);
        fragmentTransaction.replace(R.id.fragment_frame, fragment, title);
        fragmentTransaction.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


    public void replaceFragment(Fragment fragment, String title, int enterAnimation, int exitAnimation){
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setCustomAnimations(enterAnimation, exitAnimation);
        fragmentTransaction.replace(R.id.fragment_frame, fragment, title);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public Database getDatabase() {
        return database;
    }

    public DatabaseReference getDatabaseReference() {
        return reference;
    }


    public FacebookUtil getFacebookUtil() {
        return this.facebookUtil;
    }

    public void setFacebookLogin(FacebookLogin facebookLogin) {
        this.facebookLogin = facebookLogin;
    }

    public Prefs getPrefs(){
        return this.prefs;
    }


    /**
     * For facebook login in friends fragmnet
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookLogin.callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }
}
