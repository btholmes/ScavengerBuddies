package ben.holmes.scavenger.buddies.Games.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.Model.ShadowButton;
import ben.holmes.scavenger.buddies.R;

public class NewGameActivity extends ScavengerActivity {

    private Toolbar toolbar;
    private ShadowButton opponentButton;
    private ShadowButton friendButton;
    private ShadowButton wordCountFIve;
    private ShadowButton wordCountTen;
    private ShadowButton playNow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        initViews();
        setUpToolbar();

        setOpponentButtonDesign();
        setOpponentClicks();

        setWordCountDesign();
        setWordCountClicks();

        setPlayNowDesign();
        setPlayNowClicks();

    }

    private void initViews(){
        toolbar = findViewById(R.id.toolbar);
        opponentButton = findViewById(R.id.opponent_button);
        friendButton = findViewById(R.id.friend_button);
        wordCountFIve = findViewById(R.id.word_count_five);
        wordCountTen = findViewById(R.id.word_count_ten);
        playNow = findViewById(R.id.play_now);
    }

    private void setUpToolbar(){
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Game");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private void setOpponentClicks(){

        opponentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opponentButton.setClicked();
                opponentButton.setChecked();
                friendButton.setElevated();
                friendButton.setUnChecked();

            }
        });

        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendButton.setClicked();
                friendButton.setChecked();
                opponentButton.setElevated();
                opponentButton.setUnChecked();
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

    private void setWordCountClicks(){
        wordCountFIve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordCountFIve.setClicked();
                wordCountFIve.setChecked();
                wordCountTen.setElevated();
                wordCountTen.setUnChecked();

            }
        });
        wordCountTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordCountTen.setClicked();
                wordCountTen.setChecked();
                wordCountFIve.setElevated();
                wordCountFIve.setUnChecked();
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

    private void goToGame(){

    }


}
