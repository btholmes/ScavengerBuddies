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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Games.Fragments.NewGameFragment;
import ben.holmes.scavenger.buddies.Games.Fragments.PlayFragment;
import ben.holmes.scavenger.buddies.Games.NewGameState;
import ben.holmes.scavenger.buddies.Model.Game;
import ben.holmes.scavenger.buddies.Model.ShadowButton;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.R;
import io.realm.Realm;

public class NewGameActivity extends ScavengerActivity {


    private boolean selectFriendShown = false;
    private NewGameFragment newGameFragmentDelegate;

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
        }else if(false){

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

    public void setNewGameFragmentDelegate(NewGameFragment delegate){
        this.newGameFragmentDelegate = delegate;
    }

    public NewGameFragment getNewGameFragmentDelegate() {
         return this.newGameFragmentDelegate;
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

    private void showNoUsers(){

    }

    private void getFriend(final boolean fiveWordKey){
        Database.getInstance(this).getRandomFriend(new Database.UserCallback() {
            @Override
            public void onComplete(User user) {
                if(user == null)
                    showNoUsers();
                else
                    createGame(user, fiveWordKey);
            }
        });
    }

    public interface WordCallback{
        void onComplete(List<String> words);
    }

    private void getWords(final WordCallback callback, int count){
        Database database = getDatabase();

        database.getTags(new Database.TagCallback() {
            @Override
            public void onComplete(List<String> list) {
                callback.onComplete(list);
            }
        }, count);
    }


    public void goToGame(@Nullable User friend, boolean fiveWordKey){
        if(friend == null)
            getFriend(fiveWordKey);
        else
            createGame(friend, fiveWordKey);

    }

    private void createGame(final User friend, final boolean fiveWordKey){
        final Database database = getDatabase();
        int wordCount = 5;
        if(!fiveWordKey)
            wordCount = 10;
        getWords(new WordCallback() {
            @Override
            public void onComplete(List<String> words) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Game game = new Game(currentUser.getUid(), friend.getUid(), words );
                database.addGameToFirebase(game);
                moveOn(game);
            }
        }, wordCount);
    }

    public void moveOn(Game game){

        PlayFragment fragment = new PlayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PlayFragment.GAME_KEY, game);
        fragment.setArguments(bundle);

        replaceFragment(fragment,
                        PlayFragment.TAG_NAME,
                        R.anim.slide_in_right,
                        R.anim.slide_out_right);
    }


    public void destroyNewGameState(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if(realm.where(NewGameState.class).equalTo("id", 0).count() > 0)
            realm.where(NewGameState.class).equalTo("id", 0).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

}
