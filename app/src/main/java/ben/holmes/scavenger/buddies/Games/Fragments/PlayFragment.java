package ben.holmes.scavenger.buddies.Games.Fragments;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Api;

import java.util.Random;

import ben.holmes.scavenger.buddies.App.ScavengerFragment;
import ben.holmes.scavenger.buddies.App.Tools.Prefs;
import ben.holmes.scavenger.buddies.Camera.CameraHelper;
import ben.holmes.scavenger.buddies.Clarifai.Clarifai;
import ben.holmes.scavenger.buddies.Model.ShadowButton;
import ben.holmes.scavenger.buddies.Model.SpinWheel;
import ben.holmes.scavenger.buddies.R;

public class PlayFragment extends ScavengerFragment {

    public static final String TAG_NAME = "Gameplay";
    public static final int TOOLBAR_COLOR = R.color.colorPrimary;
    public static final String ROTATED_STATE = "rotation state of wheel";

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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        setUpGame();

    }

    private void setDefaultValues(Bundle bundle){
        if(bundle == null)
            return;

        fiveWords = bundle.getBoolean(FIVE_WORD_KEY, false);
        rotatedState = bundle.getInt(ROTATED_STATE, 0);

        if(fiveWords) dividers = 5;
        else dividers = 10;

    }


    private void setUpGame(){
        Clarifai clarifai = new Clarifai(getContext());
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_play, container, false);
        spinWheel = rootView.findViewById(R.id.spin_wheel);
        spinWheel.setDividers(dividers);
        spinWheelCenter = rootView.findViewById(R.id.spin_wheel_center);
        upArrow = rootView.findViewById(R.id.up_arrow);
        takePictureButton = rootView.findViewById(R.id.take_picture);

        setTakePictureClick();
        setDefaultSelected();
        setUpWheel();
        return rootView;
    }



    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        setDefaultValues(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FIVE_WORD_KEY, fiveWords);
        outState.putInt(ROTATED_STATE, rotatedState);

//        fragment = new PlayFragment();
//        Bundle bundle = new Bundle();
//        bundle.putBoolean(FIVE_WORD_KEY, fiveWords);
//        bundle.putInt(ROTATED_STATE, rotatedState);
//
//        if(fragment.isAdded())
//            getActivity().getSupportFragmentManager().putFragment(bundle, TAG_NAME, fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
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
    }

    @Override
    public int getToolbarColor() {
        return TOOLBAR_COLOR;
    }

    @Override
    public String getToolbarTitle() {
        return TAG_NAME;
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
        if(selected == -1){
            selected = (int)Math.ceil(270/spinWheel.getSweepAngle());
        }
        return selected;
    }

    private void storeNextSelectedWheel(int totalRotation){
        rotatedState = (rotatedState + totalRotation) % 360;
        int nextAngle = (270-rotatedState) % 360;
        int next = (int)Math.floor((double)nextAngle/spinWheel.getSweepAngle()) + 1;
        if(next == 0)
            next = dividers;

        storeSelectedWheel(next);
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

                if(Build.VERSION.SDK_INT >= 19 && false){
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
                                    if(delta <= 15 && delta >= 0 && progress > 0){
                                        upArrow.animate().rotation(delta);
                                    }else
                                        upArrow.animate().rotation(0);

                                }
                            }).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) { }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            int selected = getSelectedWheel();
                            int a = selected;
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
                            int selected = getSelectedWheel();
                            int a = selected;
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



    private void setTakePictureClick(){
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraHelper helper = new CameraHelper(getContext(), getActivity());
                helper.openCamera(0);
//                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT
//                );
//                getActivity().addContentView(helper.cameraPreview, params);
            }
        });
    }

}
