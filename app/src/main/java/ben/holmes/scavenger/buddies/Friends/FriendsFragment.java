package ben.holmes.scavenger.buddies.Friends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ben.holmes.scavenger.buddies.R;


import ben.holmes.scavenger.buddies.App.ScavengerFragment;

/**
 * Created by benholmes on 5/7/18.
 */

public class FriendsFragment extends ScavengerFragment{

    public static final String TAG_NAME = "Friends";
    public static final int TOOLBAR_COLOR = R.color.colorPrimary;

    private View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public int getToolbarColor() {
        return TOOLBAR_COLOR;
    }

    @Override
    public String getToolbarTitle() {
        return TAG_NAME;
    }
}
