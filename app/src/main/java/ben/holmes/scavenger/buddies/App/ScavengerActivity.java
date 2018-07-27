package ben.holmes.scavenger.buddies.App;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ben.holmes.scavenger.buddies.Database.Database;
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
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scavenger_base);
        database = Database.getInstance(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        fragmentManager = getSupportFragmentManager();
        fragmentLayout = findViewById(R.id.fragment_frame);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpToolbar();
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
}
