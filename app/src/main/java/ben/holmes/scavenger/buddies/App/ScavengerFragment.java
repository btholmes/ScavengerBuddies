package ben.holmes.scavenger.buddies.App;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import ben.holmes.scavenger.buddies.R;

/**
 * Created by benholmes on 5/7/18.
 */

public abstract class ScavengerFragment extends Fragment {

    public static String FRIEND_KEY = "friend is checked?";
    public static String FIVE_WORD_KEY = "five words is checked";

    private Toolbar toolbar;
    private ActionBar actionBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        setToolbar();
    }

    private void setToolbar(){
        actionBar = ((ScavengerActivity)getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(getToolbarTitle());
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(getToolbarColor())));
        }

    }

    public void displayHomeBackButton(){
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

    }

    public void setDarkTheme(){
        android.support.v7.widget.Toolbar toolbar = ((ScavengerActivity)getActivity()).getToolbar();
        toolbar.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Dark_ActionBar);
    }

    public void setLightTheme(){
        android.support.v7.widget.Toolbar toolbar = ((ScavengerActivity)getActivity()).getToolbar();
        toolbar.getContext().setTheme(R.style.ThemeOverlay_AppCompat_Light);
    }


    public abstract String getToolbarTitle();

    public abstract int getToolbarColor();




}
