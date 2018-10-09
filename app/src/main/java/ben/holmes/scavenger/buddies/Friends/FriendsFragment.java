package ben.holmes.scavenger.buddies.Friends;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ben.holmes.scavenger.buddies.App.Model.CustomViewPager;
import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.App.Tools.FacebookUtil;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Friends.Views.FriendSearchView;
import ben.holmes.scavenger.buddies.Login.LoginActivity;
import ben.holmes.scavenger.buddies.Login.LoginHelpers.FacebookLogin;
import ben.holmes.scavenger.buddies.Login.LoginHelpers.LoginUtil;
import ben.holmes.scavenger.buddies.Main.MainActivity;
import ben.holmes.scavenger.buddies.Main.adapter.PageFragmentAdapter;
import ben.holmes.scavenger.buddies.Model.ShadowButton;
import ben.holmes.scavenger.buddies.R;


import ben.holmes.scavenger.buddies.App.ScavengerFragment;
import clarifai2.dto.prediction.Frame;

/**
 * Created by benholmes on 5/7/18.
 */

public class FriendsFragment extends ScavengerFragment
//        implements RecyclerView.OnItemTouchListener, View.OnTouchListener
{

    public static final String TAG_NAME = "Friends";
    public static final int TOOLBAR_COLOR = R.color.colorPrimary;
//    private DatabaseReference reference;
//    private FirebaseUser user;
    private View view;
    private TextView friendsText;

//    private HideSearchHelper hideSearchHelper;

    private RelativeLayout mainContent;
    private FrameLayout searchHolder;
    private FrameLayout closeHolder;
    private EditText findUsersTextView;
    private View underLine;

    private FriendSearchView friendSearchView;

    private FrameLayout facebookButtonHolder;
    private LoginButton facebookButton;
    private ShadowButton facebookButtonImposter;

    private FacebookLogin facebookLogin;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);
        mainContent = view.findViewById(R.id.main_content);
        friendsText = view.findViewById(R.id.friendsText);
        searchHolder = view.findViewById(R.id.searchHolder);
//        hideSearchHelper = new HideSearchHelper(getContext(), searchHolder);
//        hideSearchHelper.setSearchHolderInitialY();
        setSearchHolderInitialY();
        closeHolder = view.findViewById(R.id.closeHolder);
        setCloseHolderInitialPosition();
        findUsersTextView = view.findViewById(R.id.findUsersTextView);
        setTextListener();
        underLine = view.findViewById(R.id.underline);
        friendSearchView = view.findViewById(R.id.friendSearchView);
        friendSearchView.setActivity(getActivity());
//        customGesture = new CustomGesture(getContext(), gestureListener);
//        friendSearchView.getRecyclerView().addOnItemTouchListener(this);
//        friendSearchView.getRecyclerView().
        facebookButtonHolder = view.findViewById(R.id.facebookButtonHolder);
        facebookButton = view.findViewById(R.id.facebookButton);
        facebookButtonImposter = view.findViewById(R.id.facebookButtonImposter);
        setFriendsList();
        init();

        if(!facebookConnected()){
            setUpFacebookConnect();
        }else{
            facebookButtonHolder.setVisibility(View.GONE);
            showFriendPage();
        }

//        facebookConnected(new FacebookConnectedCallback() {
//            @Override
//            public void onComplete(boolean isConnected) {
//                if(!isConnected)
//                    setUpFacebookConnect();
//                else{
//                    facebookButtonHolder.setVisibility(View.GONE);
//                    showUserList();
//                }
//            }
//        });

        return view;
    }

    private void showFriendPage(){

    }

    //HIde facebook button, show users
    private void showUserList(){
        Database database = ((ScavengerActivity)getActivity()).getDatabase();
        FacebookUtil facebookUtil = ((ScavengerActivity)getActivity()).getFacebookUtil();
        friendSearchView.setVisibility(View.VISIBLE);
        friendSearchView.populateUserList(facebookUtil);
    }

    public interface FacebookConnectedCallback{
        void onComplete(boolean isConnected);
    }
    private boolean facebookConnected(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            LoginUtil.logOut();
            ((ScavengerActivity)getActivity()).goToLoginActivity();
            return false;
        }

        return ((ScavengerActivity)getActivity()).getPrefs().getFacebookConnected(user.getUid());
//        FacebookUtil facebookUtil = ((ScavengerActivity)getActivity()).getFacebookUtil();
//        facebookUtil.getUserFriends(new FacebookUtil.FacebookFriendsCallback() {
//            @Override
//            public void onComplete(GraphResponse response) {
//                if(response != null)
//                    callback.onComplete(true);
//                else
//                    callback.onComplete(false);
//            }
//        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void init(){
        findUsersTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                CustomViewPager viewPager = ((MainActivity)getActivity()).getViewPager();
                if(viewPager.getCurrentItem() != 1) return;
                if(hasFocus){
                    animateHasFocus();
                }else{
                    animateRemoveFocus();
                }
            }
        });

        closeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setViewPagerHeightNormal();
                mainContent.setY(startingPosition);
                ((MainActivity)getActivity()).setViewPagerHeightNormal();
                animateRemoveFocus();
            }
        });
    }

    float closeHolderHiddenPosition = -1;
    float closeHolderVisiblePosition = -1;
    private void setCloseHolderInitialPosition(){
        closeHolder.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(closeHolderHiddenPosition == -1){
                    closeHolderHiddenPosition = closeHolder.getX();
                    int dp = ((ScavengerActivity)getActivity()).convertDpToPixels(40);
                    closeHolderVisiblePosition = closeHolderHiddenPosition - dp;
                    closeHolder.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    private void animateHasFocus(){
        float closeHolderCurrentPosition = closeHolder.getX();
        if(closeHolderCurrentPosition == closeHolderHiddenPosition){
            closeHolder.animate().x(closeHolderVisiblePosition).setDuration(250).setInterpolator(new LinearInterpolator());

            underLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)underLine.getLayoutParams();
            params.height = ((ScavengerActivity)getActivity()).convertDpToPixels(3);
            underLine.setLayoutParams(params);


            RelativeLayout.LayoutParams friendParams = (RelativeLayout.LayoutParams) friendSearchView.getMainContent().getLayoutParams();
            friendParams.height = ((MainActivity)getActivity()).getDefaultHeight();
            friendSearchView.getMainContent().setLayoutParams(friendParams);
            friendSearchView.setVisibility(View.VISIBLE);

            showUserList();
        }
    }

    private void animateClose(){
        if(mainContent.getY() != startingPosition){
            mainContent.animate().y(startingPosition).setDuration(250).setInterpolator(new LinearInterpolator());
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeHolder.animate().x(closeHolderHiddenPosition).setDuration(250).setInterpolator(new LinearInterpolator());
                }
            }, 250);
        }else{
            closeHolder.animate().x(closeHolderHiddenPosition).setDuration(250).setInterpolator(new LinearInterpolator());
        }
    }

    private void animateRemoveFocus(){
//        int dp = ((ScavengerActivity)getActivity()).convertDpToPixels(40);
        float closeHolderCurrentPosition = closeHolder.getX();
        if(closeHolderCurrentPosition == closeHolderVisiblePosition){
            animateClose();

            underLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)underLine.getLayoutParams();
            params.height = ((ScavengerActivity)getActivity()).convertDpToPixels(1);
            underLine.setLayoutParams(params);
            hideKeyboard();
            findUsersTextView.setText("");
            friendSearchView.setVisibility(View.GONE);
            findUsersTextView.clearFocus();
        }
    }


    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface FriendCallback{
        void onComplete(boolean result);
    }

    private void hasFriends(final FriendCallback callback){
        FirebaseUser user = ((ScavengerActivity)getActivity()).getFirebaseUser();
        DatabaseReference reference = ((ScavengerActivity)getActivity()).getDatabaseReference();
        if(user == null || reference == null) return;

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

    public void updatePage(){
        if(facebookConnected())
            facebookButtonHolder.setVisibility(View.GONE);
    }

    private void setUpFacebookConnect(){
        facebookLogin = new FacebookLogin(getContext(), getActivity());
        ((ScavengerActivity)getActivity()).setFacebookLogin(facebookLogin);
        facebookLogin.initalizeLoginButton(facebookButton);
        facebookButtonImposter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookButtonImposter.quickClick(new ShadowButton.QuickClick() {
                    @Override
                    public void onSuccess() {
                        facebookButton.performClick();
                    }
                });
            }
        });
    }


    @Override
    public int getToolbarColor() {
        return TOOLBAR_COLOR;
    }

    @Override
    public String getToolbarTitle() {
        return TAG_NAME;
    }


    private void setTextListener(){
        findUsersTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                friendSearchView.updateAdapter("@" + text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void setSearchHolderInitialY(){
        if(startingPosition == -1){
            searchHolder.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(startingPosition == -1){
                        startingPosition = searchHolder.getY();
                        hiddenPosition = startingPosition - searchHolder.getMeasuredHeight();
                        searchHolderHeight = searchHolder.getMeasuredHeight();
                        RelativeLayout friendView = friendSearchView.getMainContent();
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)friendView.getLayoutParams();
                        params.height += searchHolderHeight;
                        friendView.setLayoutParams(params);
                        searchHolder.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    /**
     * On Touch variables
     */
    float y1 = 0;
    float y2 = 0;
    float startingPosition = -1;
    float hiddenPosition = -1;
    float searchHolderHeight = -1;
    boolean movement = false;
    float movementThreshold = 10f;

//    @Override
//    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
////        sendEvent(e);
//        // true if single tap, so don't consume tap, and send to recycler view
////        if(isSingleTap){
////            return false;
////        }
////        else{
////            //consume the tap
////            onTouchEvent(rv, e);
////            return true;
////        }
//        return false;
//    }
//    @Override
//    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent e) {
//        sendEvent(e);
//        // true if single tap, so don't consume tap, and send to recycler view
//        if(isSingleTap){
//            return false;
//        }
//        else{
//            //consume the tap
//            onTouchEvent(null, e);
//            return true;
//        }
//    }
//
//    private boolean sendEvent(MotionEvent e){
//        isSingleTap = false;
//        return gestureDetector.onTouchEvent(e);
//    }


//    @Override
//    public void onTouchEvent(@Nullable RecyclerView rv, MotionEvent event) {
//        if(startingPosition == -1) return;
//
//        if(event.getAction() == MotionEvent.ACTION_DOWN){
//            y1 = event.getY();
//        }
//        else if(event.getAction() == MotionEvent.ACTION_MOVE){
//            movement = true;
//            y2 = event.getY();
//
//
//            float difference = y2-y1;
////            if(Math.abs(difference) <= movementThreshold){
////                movement = false;
////            }
//
//            if(difference <= 0){
////                upward swipe
//                ((MainActivity)getActivity()).adjustViewPagerHeight((int)searchHolderHeight);
//
//                if(mainContent.getY() + difference >= hiddenPosition){
//                    float newY = mainContent.getY() + difference;
//                    mainContent.setY(newY);
//                }else{
//                    if(mainContent.getY() != hiddenPosition){
//                        mainContent.setY(hiddenPosition);
//                    }
//                }
//            }else{
////                Downward swipe
//                if(mainContent.getY() + difference <= startingPosition){
//                    float newY = mainContent.getY() + difference;
//                    mainContent.setY(newY);
//                }else{
//                    if(mainContent.getY() != startingPosition){
//                        mainContent.setY(startingPosition);
//                        ((MainActivity)getActivity()).setViewPagerHeightNormal();
//                    }
//                }
//            }
//        }
//        else if(event.getAction() == MotionEvent.ACTION_UP){
////            if(movement){
////                movement = false;
////                return true;
////            }else{
////                return false;
////            }
//        }
//    }

//    public static boolean isSingleTap = false;
//    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent e) {
//            //do something
//            isSingleTap = true;
//            return false;
//        }
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return true;
//        }
//    });



}
