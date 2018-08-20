package ben.holmes.scavenger.buddies.App;

import android.content.ComponentCallbacks2;
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
import io.realm.Realm;


/**
 * Created by benholmes on 5/7/18.
 */

public abstract class ScavengerActivity extends AppCompatActivity implements ComponentCallbacks2 {

    private Toolbar toolbar;

    private Database database;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FacebookLogin facebookLogin;
    private FacebookUtil facebookUtil;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Prefs prefs;

    private Realm realm;

    private FrameLayout fragmentLayout;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

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
        database = Database.getInstance();
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
        realm = Realm.getDefaultInstance();
    }

    public Realm getRealm(){
        return realm == null ? Realm.getDefaultInstance() : realm;
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

    public FrameLayout getFragmentLayout() {
        return fragmentLayout;
    }

    public FragmentManager getFragManager() {
        return fragmentManager;
    }

    public FragmentTransaction getFragmentTransaction() {
        return fragmentTransaction;
    }

    public void goToLoginActivity(){
        Intent intent = new Intent(ScavengerActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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



    public int convertDpToPixels(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
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

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        // Determine which lifecycle or system event was raised.
        switch (level) {

            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:

                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:

                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:

                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */

                break;

            default:
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                break;
        }
    }
}
