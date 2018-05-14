package ben.holmes.scavenger.buddies.Games.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Random;

import ben.holmes.scavenger.buddies.App.ScavengerFragment;
import ben.holmes.scavenger.buddies.Model.SpinWheel;
import ben.holmes.scavenger.buddies.R;

public class PlayFragment extends ScavengerFragment {

    public static final String TAG_NAME = "Gameplay";
    public static final int TOOLBAR_COLOR = R.color.colorPrimary;

    private View rootView;
    private SpinWheel spinWheel;
    private TextView spinWheelCenter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_play, container, false);
        spinWheel = rootView.findViewById(R.id.spin_wheel);
        spinWheel.setDividers(10);
        spinWheelCenter = rootView.findViewById(R.id.spin_wheel_center);

        setUpWheel();
        return rootView;
    }

    @Override
    public int getToolbarColor() {
        return TOOLBAR_COLOR;
    }

    @Override
    public String getToolbarTitle() {
        return TAG_NAME;
    }

    private void setUpWheel(){
        spinWheelCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                int rotate = random.nextInt(360) + 1;
//                Rotates 3 and some number times
                spinWheel.animate().rotation(spinWheel.getRotation() + 360*3 + rotate).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
            }
        });
    }

}
