package ben.holmes.scavenger.buddies.Friends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.Friends.Views.FriendSearchView;
import ben.holmes.scavenger.buddies.R;


import ben.holmes.scavenger.buddies.App.ScavengerFragment;
import clarifai2.dto.prediction.Frame;

/**
 * Created by benholmes on 5/7/18.
 */

public class FriendsFragment extends ScavengerFragment{

    public static final String TAG_NAME = "Friends";
    public static final int TOOLBAR_COLOR = R.color.colorPrimary;
    private DatabaseReference reference;
    private FirebaseUser user;
    private View view;
    private TextView friendsText;

    private FrameLayout searchHolder;
    private FrameLayout closeHolder;
    private EditText findUsersTextView;
    private View underLine;

    private FriendSearchView friendSearchView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = ((ScavengerActivity)getActivity()).getDatabaseReference();
        user = ((ScavengerActivity)getActivity()).getFirebaseUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        friendsText = view.findViewById(R.id.friendsText);
        findUsersTextView = view.findViewById(R.id.findUsersTextView);
        closeHolder = view.findViewById(R.id.closeHolder);
        underLine = view.findViewById(R.id.underline);
        friendSearchView = view.findViewById(R.id.friendSearchView);
        setFriendsList();
        init();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }




    private void init(){
        findUsersTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int dp = ((ScavengerActivity)getActivity()).convertDpToPixels(40);
                if(hasFocus){
                    closeHolder.animate().translationXBy(-dp).setDuration(250).setInterpolator(new LinearInterpolator());
                    underLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    friendSearchView.setVisibility(View.VISIBLE);
                }else{
                    closeHolder.animate().translationXBy(dp).setDuration(250).setInterpolator(new LinearInterpolator());
                    underLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
                    hideKeyboard();
                    friendSearchView.setVisibility(View.GONE);
                }
            }
        });

        closeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findUsersTextView.clearFocus();
            }
        });
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface FriendCallback{
        void onComplete(boolean result);
    }

    private void hasFriends(final FriendCallback callback){
        reference.child("userList").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("friends")){
                    callback.onComplete(true);
                }else{
                    callback.onComplete(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setFriendsList(){
        hasFriends(new FriendCallback() {
            @Override
            public void onComplete(boolean hasFriends) {
                if(hasFriends){
                    showFriends();
                }else{
                    friendsText.setText("No friends yet");
                }
            }
        });
    }

    private void showFriends(){

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
