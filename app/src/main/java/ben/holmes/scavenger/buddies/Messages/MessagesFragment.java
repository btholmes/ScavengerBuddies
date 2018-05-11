package ben.holmes.scavenger.buddies.Messages;

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

public class MessagesFragment extends ScavengerFragment {

    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_messages, container, false);

        return view;
    }

}