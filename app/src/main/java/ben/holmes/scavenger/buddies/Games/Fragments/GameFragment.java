package ben.holmes.scavenger.buddies.Games.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ben.holmes.scavenger.buddies.Games.Activities.NewGameActivity;
import ben.holmes.scavenger.buddies.Model.GameButton;
import ben.holmes.scavenger.buddies.Model.ShadowButton;
import ben.holmes.scavenger.buddies.R;


import ben.holmes.scavenger.buddies.App.ScavengerFragment;

/**
 * Created by benholmes on 5/7/18.
 */

public class GameFragment extends ScavengerFragment{

    public static final String TAG_NAME = "Games";
    public static final int TOOLBAR_COLOR = R.color.colorPrimary;

    private View view;
    private ShadowButton gameButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game, container, false);
        gameButton = view.findViewById(R.id.game_button);
        setUpGameButton();
        return view;
    }

    private void setUpGameButton(){
        gameButton.hideLeftIcon();
        gameButton.showRightIcon();
        gameButton.setRightIcon(R.drawable.ic_add);
        gameButton.setRightIconColor(R.color.white);
        gameButton.setShadowColor(R.color.colorPrimary);
        gameButton.setBorderColor(R.color.green);
        gameButton.setButtonBackgroundColor(R.color.colorAccent);
        gameButton.setTextColor(R.color.white);

        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameButton.quickClick(new ShadowButton.QuickClick() {
                    @Override
                    public void onSuccess() {
                        goToNewGameActivity();
                    }
                });
            }
        });
    }


    private void goToNewGameActivity(){
        Intent intent = new Intent(getActivity(), NewGameActivity.class);
        startActivity(intent);
    }

    @Override
    public String getToolbarTitle() {
        return TAG_NAME;
    }

    @Override
    public int getToolbarColor() {
        return TOOLBAR_COLOR;
    }

}
