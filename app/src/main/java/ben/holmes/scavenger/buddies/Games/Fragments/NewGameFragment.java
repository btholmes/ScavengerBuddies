package ben.holmes.scavenger.buddies.Games.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.App.ScavengerFragment;
import ben.holmes.scavenger.buddies.Clarifai.Clarifai;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Games.Activities.NewGameActivity;
import ben.holmes.scavenger.buddies.Games.NewGameState;
import ben.holmes.scavenger.buddies.Model.FriendItem;
import ben.holmes.scavenger.buddies.Model.Game;
import ben.holmes.scavenger.buddies.Model.ShadowButton;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.R;
import io.realm.Realm;

public class NewGameFragment extends ScavengerFragment {


    public static final String TAG_NAME = "New Game";
    public static final int TOOLBAR_COLOR = R.color.colorPrimary;

    private View rootView;
    private ShadowButton opponentButton;
    private ShadowButton friendButton;
    private ShadowButton wordCountFIve;
    private ShadowButton wordCountTen;
    private ShadowButton playNow;

    private NewGameState newGameState;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new_game, container, false);
        return  rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        setUp();
        restoreState();
    }

    private NewGameState getState(Realm realm){
        return realm.where(NewGameState.class).equalTo("id", 0).findFirst();
    }

    private void restoreState(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        NewGameState state = getState(realm);

        if(state == null) {
            realm.commitTransaction();
            realm.close();
            return;
        }

        if(state.isFiveWordKey())
            wordCountFIve.setChecked();
        else if(state.isTenWordKey())
            wordCountTen.setChecked();

        if(state.isFriendKey())
            friendButton.setChecked();
        else if(state.isOpponentKey())
            opponentButton.setChecked();

        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveState();
    }

    private NewGameState createState(Realm realm){
        if(realm.where(NewGameState.class).equalTo("id", 0).count() > 0)
            return realm.where(NewGameState.class).equalTo("id", 0).findFirst();

        NewGameState state = realm.createObject(NewGameState.class, 0);
        return state;
    }

    private void saveState(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        NewGameState state = createState(realm);
        state.setFiveWordKey(wordCountFIve.isChecked());
        state.setTenWordKey(wordCountTen.isChecked());
        state.setFriendKey(friendButton.isChecked());
        state.setOpponentKey(opponentButton.isChecked());

        realm.copyToRealmOrUpdate(state);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setUp(){
        displayHomeBackButton();
        initViews();

        setOpponentButtonDesign();
        setOpponentClicks();

        setWordCountDesign();
        setWordCountClicks();

        setPlayNowDesign();
        setPlayNowClicks();
    }


    private void initViews(){
        opponentButton = rootView.findViewById(R.id.opponent_button);
        friendButton = rootView.findViewById(R.id.friend_button);
        wordCountFIve = rootView.findViewById(R.id.word_count_five);
        wordCountTen = rootView.findViewById(R.id.word_count_ten);
        playNow = rootView.findViewById(R.id.play_now);
    }



    public String getToolbarTitle(){
        return TAG_NAME;
    }


    public int getToolbarColor(){
        return TOOLBAR_COLOR;
    }


    private void setOpponentButtonDesign(){
        opponentButton.setText("Random");
        opponentButton.setTextColor(R.color.white);
        opponentButton.setTextColor(R.color.white);
        opponentButton.setLeftIcon(R.drawable.ic_clock);
        opponentButton.setLeftIconColor(R.color.white);
        opponentButton.hideRightIcon();
        opponentButton.hideLeftIcon();
//        opponentButton.showLeftIcon();
        opponentButton.setElevated();



        friendButton.setText("Friend");
        friendButton.setTextColor(R.color.white);
        friendButton.setLeftIconColor(R.color.white);
        friendButton.hideRightIcon();
        friendButton.hideLeftIcon();
//        friendButton.showLeftIcon();
        friendButton.setElevated();

    }

    private void setOpponentButtonChecked(){
        opponentButton.setClicked();
        opponentButton.setChecked();
        friendButton.setElevated();
        friendButton.setUnChecked();
    }

    private void setFriendButtonChecked(){
        friendButton.setClicked();
        friendButton.setChecked();
        opponentButton.setElevated();
        opponentButton.setUnChecked();
    }

    private void setOpponentClicks(){

        opponentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setOpponentButtonChecked();
            }
        });

        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFriendButtonChecked();
            }
        });

    }

    private void setWordCountDesign(){
        wordCountFIve.setText("5 Words");
        wordCountFIve.setTextColor(R.color.white);
        wordCountFIve.setLeftIcon(R.drawable.ic_clock);
        wordCountFIve.setLeftIconColor(R.color.white);
        wordCountFIve.hideRightIcon();
        wordCountFIve.hideLeftIcon();
//        wordCountFIve.showLeftIcon();

        wordCountTen.setText("10 Words");
        wordCountTen.setTextColor(R.color.white);
        wordCountTen.setLeftIcon(R.drawable.ic_clock);
        wordCountTen.setLeftIconColor(R.color.white);
        wordCountTen.hideRightIcon();
        wordCountTen.hideLeftIcon();
//        wordCountTen.showLeftIcon();
    }

    private void setFiveWordsChecked(){
        wordCountFIve.setClicked();
        wordCountFIve.setChecked();
        wordCountTen.setElevated();
        wordCountTen.setUnChecked();
    }

    private void setTenWordsChecked(){
        wordCountTen.setClicked();
        wordCountTen.setChecked();
        wordCountFIve.setElevated();
        wordCountFIve.setUnChecked();
    }

    private void setWordCountClicks(){
        wordCountFIve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFiveWordsChecked();

            }
        });
        wordCountTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTenWordsChecked();
            }
        });
    }


    private void setPlayNowDesign(){

        playNow.setText("Play Game");
        playNow.setTextColor(R.color.white);
        playNow.setLeftIcon(R.drawable.ic_clock);
        playNow.setLeftIconColor(R.color.white);
        playNow.hideRightIcon();
        playNow.showLeftIcon();
        playNow.hideLeftIcon();

    }

    private void setPlayNowClicks(){

        playNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNow.quickClick(new ShadowButton.QuickClick() {
                    @Override
                    public void onSuccess() {
                        goToGame();
                    }
                });
            }
        });

    }


    private void handleSelectFriend(){
        ((NewGameActivity)getActivity()).setNewGameFragmentDelegate(this);

        SelectFriendFragment fragment = new SelectFriendFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(SelectFriendFragment.CALLING_FRAGMENT, NewGameFragment.class.hashCode());
        bundle.putBoolean(FRIEND_KEY, friendButton.isChecked());
        bundle.putBoolean(FIVE_WORD_KEY, wordCountFIve.isChecked());

        fragment.setArguments(bundle);
        if(friendButton.isChecked()){
            ((NewGameActivity)getActivity())
                    .replaceFragment(fragment,
                            SelectFriendFragment.TAG_NAME,
                            R.anim.slide_up,
                            R.anim.slide_out_right);
        }
    }


    private void goToGame(){
        if(friendButton.isChecked())
            handleSelectFriend();
        else
            ((NewGameActivity)getActivity()).goToGame(null,  wordCountFIve.isChecked());
    }



}
