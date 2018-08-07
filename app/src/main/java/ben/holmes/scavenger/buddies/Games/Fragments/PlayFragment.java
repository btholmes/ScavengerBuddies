package ben.holmes.scavenger.buddies.Games.Fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import ben.holmes.scavenger.buddies.App.PopUp.ScavengerDialog;
import ben.holmes.scavenger.buddies.App.ScavengerFragment;
import ben.holmes.scavenger.buddies.App.Tools.CircleTransform;
import ben.holmes.scavenger.buddies.App.Tools.Prefs;
import ben.holmes.scavenger.buddies.Camera.Activities.Camera2Activity;
import ben.holmes.scavenger.buddies.Camera.Activities.Camera2OpenGL;
import ben.holmes.scavenger.buddies.Camera.Activities.CameraActivity;
import ben.holmes.scavenger.buddies.Clarifai.Clarifai;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Games.Activities.NewGameActivity;
import ben.holmes.scavenger.buddies.Model.Game;
import ben.holmes.scavenger.buddies.Model.ShadowButton;
import ben.holmes.scavenger.buddies.Model.SpinWheel;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.R;

public class PlayFragment extends ScavengerFragment {

    public static final String TAG_NAME = "Gameplay";
    public static final int TOOLBAR_COLOR = R.color.colorPrimary;
    public static final String ROTATED_STATE = "rotation state of wheel";
    public static final String FRIEND_KEY = "bundle key stores friend if user selected one";
    public static final String GAME_KEY = "bundle key stores game obj";

    private Game game;

    private LinearLayout playerWordCount;
    private LinearLayout opponentWordCount;

    private PlayFragment fragment;
    private Clarifai clarifai;

    private View rootView;
    private SpinWheel spinWheel;
    private TextView spinWheelCenter;
    private ImageView upArrow;
    private Prefs prefs;
    private int dividers;
    private boolean fiveWords;
    private int rotatedState;
    private ShadowButton takePictureButton;

    private TextView wordText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        prefs = new Prefs(getContext());
        Bundle bundle;

        if(savedInstanceState != null){
             fragment = (PlayFragment) getActivity().getSupportFragmentManager().getFragment(savedInstanceState, TAG_NAME);
             bundle = fragment.getArguments();
             setDefaultValues(bundle);
        }else{
            bundle = getArguments();
            setDefaultValues(bundle);
        }
    }

    private void setDefaultValues(Bundle bundle){
        if(bundle == null)
            return;

        game = (Game)bundle.getSerializable(GAME_KEY);
        fiveWords = game.getWords().size() == 5;

        rotatedState = bundle.getInt(ROTATED_STATE, 0);

        if(fiveWords) dividers = 5;
        else dividers = 10;

    }


    private void setUpGame(){
        Clarifai clarifai = new Clarifai(getContext());

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_play_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.chat:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_play, container, false);
        wordText = rootView.findViewById(R.id.wordText);
        spinWheel = rootView.findViewById(R.id.spin_wheel);
        spinWheel.setDividers(dividers);
        spinWheelCenter = rootView.findViewById(R.id.spin_wheel_center);
        upArrow = rootView.findViewById(R.id.up_arrow);
        takePictureButton = rootView.findViewById(R.id.take_picture);
        playerWordCount = rootView.findViewById(R.id.player_word_count);
        opponentWordCount = rootView.findViewById(R.id.opponent_word_count);
        setWordCount();
        setPlayerInfo();
        setOpponentInfo();

        setTakePictureClick();
        setDefaultSelected();
        setUpWheel();
        return rootView;
    }

    private void hideSpinWheel(){
        spinWheel.setVisibility(View.GONE);
        spinWheelCenter.setVisibility(View.GONE);
        upArrow.setVisibility(View.GONE);
    }

    private void showSpinWheel(){
        wordText.setVisibility(View.GONE);
        wordText.setAlpha(0);
        spinWheel.setVisibility(View.VISIBLE);
        spinWheelCenter.setVisibility(View.VISIBLE);
        upArrow.setVisibility(View.VISIBLE);
    }

    private void markWord(boolean wasFound, int word, boolean player){
        if(player){
            if(wasFound){
                TextView textView = (TextView) rootView.findViewWithTag("player_word" + word);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.Green));
            }else{
                TextView textView = (TextView) rootView.findViewWithTag("player_word" + word);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.Red));
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }else{
            if(wasFound){
                TextView textView = (TextView) rootView.findViewWithTag("opponent_word" + word);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.Green));
            }else{
                TextView textView = (TextView) rootView.findViewWithTag("opponent_word" + word);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.Red));
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }

    private void showPictureButton(){
        takePictureButton.setVisibility(View.VISIBLE);
    }

    private void hidePictureButton(){
        takePictureButton.setVisibility(View.GONE);
    }

    private void setPlayerInfo(){
        final ImageView image = rootView.findViewById(R.id.image);
        final TextView text = rootView.findViewById(R.id.text);
        Database.getInstance().getUser(new Database.UserCallback() {
            @Override
            public void onComplete(User user) {
                if(user.getPhotoUrl() != null && user.getPhotoUrl().length() > 0)
                    Picasso.with(getContext()).load(user.getPhotoUrl()).transform(new CircleTransform()).into(image);
                else
                    image.setImageResource(R.drawable.ic_generic_account);

                if(user.getDisplayName() == null || user.getDisplayName().length() <= 0){
                    text.setText(user.getNameHash());
                }else
                    text.setText(user.getDisplayName());
            }
        }, FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private void setOpponentInfo(){
        final ImageView image = rootView.findViewById(R.id.opponent_image);
        final TextView text = rootView.findViewById(R.id.opponent_text);
        String uid = game.getOpponent();
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid))
            uid = game.getChallenger();
        Database.getInstance().getUser(new Database.UserCallback() {
            @Override
            public void onComplete(User user) {
                if(user.getPhotoUrl() != null && user.getPhotoUrl().length() > 0)
                    Picasso.with(getContext()).load(user.getPhotoUrl()).transform(new CircleTransform()).into(image);
                else
                    image.setImageResource(R.drawable.ic_generic_account);

                if(user.getDisplayName() == null || user.getDisplayName().length() <= 0){
                    text.setText(user.getNameHash());
                }else
                    text.setText(user.getDisplayName());
            }
        }, uid);

    }

    private void setWordCount(){
        addPlayerWordCount(true);
        addPlayerWordCount(false);
    }

    private void addPlayerWordCount(boolean playerCount){
        if(playerCount)
            playerWordCount.removeAllViews();
        else
            opponentWordCount.removeAllViews();
        List<String> wordList = game.getWords();
        for(int i = 1; i <= wordList.size(); i++){
            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins((int)getResources().getDimension(R.dimen.spacing_small), (int)getResources().getDimension(R.dimen.spacing_small),
                    (int)getResources().getDimension(R.dimen.spacing_xsmall), (int)getResources().getDimension(R.dimen.spacing_small));
            textView.setLayoutParams(params);
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            textView.setText(i + "");
            if(playerCount){
                textView.setTag("player_word" + i);
                playerWordCount.addView(textView);
            }else{
                textView.setTag("opponent_word" + i);
                opponentWordCount.addView(textView);
            }
        }
    }



    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        setDefaultValues(savedInstanceState);
    }


//    TODO Where to save the game before the view is destroyed?

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(GAME_KEY, game);
        outState.putInt(ROTATED_STATE, rotatedState);

        Database.getInstance().addGameToFirebase(game);

//        fragment = new PlayFragment();
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(FIVE_WORD_KEY, fiveWords);
//        bundle.putInt(ROTATED_STATE, rotatedState);
//
//        if(fragment.isAdded())
//            getActivity().getSupportFragmentManager().putFragment(bundle, TAG_NAME, fragment);
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayHomeBackButton();
        ((NewGameActivity)getActivity()).destroyNewGameState();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((NewGameActivity)getActivity()).destroyNewGameState();
    }


    @Override
    public int getToolbarColor() {
        return TOOLBAR_COLOR;
    }

    @Override
    public String getToolbarTitle() {
        return "ROUND " + game.getRound() + "/" + game.getWords().size();
    }

    private void setDefaultSelected(){
        int angle = 360/spinWheel.getDividers();
        double value = 270.0/angle;
        double defaultSelected = Math.ceil(value);
        prefs.storeSelectedWheel(Integer.valueOf((int)defaultSelected));
    }


    private int getSelectedWheel(){
        int selected = -1;
        selected = prefs.getSelectedWheel();
//        if(selected == -1){
//            selected = (int)Math.ceil(270/spinWheel.getSweepAngle());
//        }
        return selected;
    }

    private void storeNextSelectedWheel(int totalRotation){
        rotatedState = (rotatedState + totalRotation) % 360;
        int nextAngle = (270-rotatedState) % 360;
        int next = (int)Math.floor((double)nextAngle/spinWheel.getSweepAngle()) + 1;

        storeSelectedWheel(next);
        String word = getWord(next);
    }

    private void showTheWord(){
        String word = getWord(getSelectedWheel());
        hideSpinWheel();
        showPictureButton();
        animateWord(word);
    }

    private void animateWord(String word){
        wordText.setText(word);
        wordText.setVisibility(View.VISIBLE);
        wordText.animate().alpha(1f).setDuration(250);
    }


    /**
     * In java modulus isn't modulus, it's the remainder, which means -1 % 2 = -1 instead
     * of 1.. To fix this use (((n % m) + m ) % m)
     * @param next
     */
    private String getWord(int next){
        int listSize = game.getWords().size();
        if(next == 0)
            next = listSize;

        int index = next;
        if(index > listSize || index < 0){
            if(index > listSize)
                index  = index % listSize;
            else
                index = (((index % listSize) + listSize) % listSize);
        }

        if(index > 0)
            index = index -1;

        String word = game.getWords().get(index);
        Toast.makeText(getContext(), "Next : " + next + " index: " + index + " " + word , Toast.LENGTH_LONG).show();

        return word;

    }


    private void storeSelectedWheel(int selected){
        prefs.storeSelectedWheel(selected);
    }

    private boolean onLine = false;
    private void setUpWheel(){
        spinWheel.animate().rotation(rotatedState);

        spinWheelCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                final int totalRotation = (360 * 3 )+ random.nextInt(360) + 1;
                storeNextSelectedWheel(totalRotation);

                if(Build.VERSION.SDK_INT >= 19){
                    spinWheel.animate().rotationBy(totalRotation)
                            .setInterpolator(new DecelerateInterpolator())
                            .setDuration(5000)
                            .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int segmentSpan = 360 / spinWheel.getDividers();
                                    int hitPoint = 270 % segmentSpan;
                                    final float progress = (float)animation.getAnimatedValue();
                                    int rotationPoint = (int)Math.floor(totalRotation * progress);
                                    int alpha = (270 + rotationPoint) % segmentSpan;
                                    int delta = (hitPoint - alpha);
//                                    if(delta <= 15 && delta >= 0 && progress > 0){
//                                        upArrow.animate().rotation(delta);
//                                    }else
//                                        upArrow.animate().rotation(0);

                                }
                            }).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            spinWheelCenter.setEnabled(false);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            int selected = getSelectedWheel();
                            int a = selected;
                            spinWheelCenter.setEnabled(true);
                            showTheWord();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) { }

                        @Override
                        public void onAnimationRepeat(Animator animation) { }
                    });
                }else{
                    spinWheel.animate().rotationBy(totalRotation)
                            .setInterpolator(new DecelerateInterpolator())
                            .setDuration(5000).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
//                            upArrow.setPivotX(upArrow.getPivotX()-upArrow.getMeasuredWidth()/2);
                            upArrow.setPivotY(upArrow.getMeasuredHeight());
                            upArrow.animate().rotationBy(-360).setDuration(5000);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            showTheWord();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }

//                spinWheelAnimation(spinWheel);

//                spinWheel.fillSegment(rotation);
            }
        });
    }


    private void showSelection(){
        final ScavengerDialog dialog = new ScavengerDialog(getContext());
        dialog.hideHeader();
        dialog.setHeaderText("Select");
        dialog.setMessageText("Chose a camera interface");
        dialog.setNegativeButtonText("Camera");
        dialog.setAffirmativeButtonText("Camera2");
        dialog.setNegativeButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CameraActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.setAffirmativeButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), Camera2Activity.class);
                Intent intent = new Intent(getContext(), Camera2OpenGL.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setTakePictureClick(){
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CameraHelper helper = new CameraHelper(getContext(), getActivity());
//                helper.openCamera(0);

//                Intent intent = new Intent(getContext(), CameraActivity.class);
//                startActivity(intent);

//                Intent intent = new Intent(getContext(), Camera2Activity.class);
//                startActivity(intent);

                showSelection();

//                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT
//                );
//                getActivity().addContentView(helper.cameraPreview, params);
            }
        });
    }

}
