package ben.holmes.scavenger.buddies.Games.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.Games.Fragments.NewGameFragment;
import ben.holmes.scavenger.buddies.Model.ShadowButton;
import ben.holmes.scavenger.buddies.R;

public class NewGameActivity extends ScavengerActivity {


    private boolean selectFriendShown = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            replaceFragmentDontAdd(new NewGameFragment(), NewGameFragment.TAG_NAME);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setSelectFriendShown(boolean isShown){
        selectFriendShown = isShown;
    }

    @Override
    public void onBackPressed() {

        if(selectFriendShown){
            ViewGroup view = findViewById(android.R.id.content);
            View selectFriend = view.findViewById(R.id.select_friend);
            view.removeView(selectFriend);
            selectFriendShown = false;
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    public void replaceFragmentDontAdd(Fragment fragment, String title){
        replaceFragmentDontAdd(fragment, title, 0, 0);
    }


    public void replaceFragmentDontAdd(Fragment fragment, String title, int enterAnimation, int exitAnimation){
        FragmentTransaction fragmentTransaction = getFragManager().beginTransaction();

        fragmentTransaction.setCustomAnimations(enterAnimation, exitAnimation);
        fragmentTransaction.replace(R.id.fragment_frame, fragment, title);
        fragmentTransaction.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public void replaceFragment(Fragment fragment, String title){
        replaceFragment(fragment, title, 0, 0);
    }

    public void replaceFragment(Fragment fragment, String title, int enterAnimation, int exitAnimation){
        FragmentTransaction fragmentTransaction = getFragManager().beginTransaction();

        fragmentTransaction.setCustomAnimations(enterAnimation, exitAnimation);
        fragmentTransaction.replace(R.id.fragment_frame, fragment, title);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }


}
