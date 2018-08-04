package ben.holmes.scavenger.buddies.Games.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.App.ScavengerFragment;
import ben.holmes.scavenger.buddies.App.Tools.CircleTransform;
import ben.holmes.scavenger.buddies.Friends.Adapters.CustomFirebaseAdapter;
import ben.holmes.scavenger.buddies.Games.Activities.NewGameActivity;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.R;

public class SelectFriendFragment extends ScavengerFragment implements View.OnTouchListener{

    public static String TAG_NAME = "Select Friend Fragment";
    /**
     * This fragment may be called from either the newGameFragment or the Messages Fragment
     * determine which to handle correct onClickListener in the Custom Firebase adapter.
     */
    public static String CALLING_FRAGMENT = "The fragment which spawned me";

    private int callingFragment;
    public boolean friendKey;
    public boolean fiveWordKey;

    private static Context ctx;
    private RecyclerView recyclerView;
    private CustomFirebaseAdapter adapter;
    private View rootView;
    private RelativeLayout mainContent;

    private FrameLayout searchHolder;
    private FrameLayout closeHolder;
    private View underLine;
    private EditText findUsersText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ctx = getContext();
        Bundle bundle = getArguments();
        if(bundle != null){
            callingFragment = bundle.getInt(CALLING_FRAGMENT);
            if(callingFragment == NewGameFragment.class.hashCode()){
                friendKey = bundle.getBoolean(FRIEND_KEY);
                fiveWordKey = bundle.getBoolean(FIVE_WORD_KEY);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_select_friend, container, false);
        mainContent = rootView.findViewById(R.id.main_content);
        setDefaultHeight();
        recyclerView  = rootView.findViewById(R.id.recycler_view);
        recyclerView.setOnTouchListener(this);
        searchHolder = rootView.findViewById(R.id.searchHolder);
        setSearchHolderInitialY();
        closeHolder = rootView.findViewById(R.id.closeHolder);
        setCloseHolderInitialPosition();
        underLine = rootView.findViewById(R.id.underline);

        findUsersText = rootView.findViewById(R.id.findUsersTextView);
        setTextListener();

        init();
        setAdapter();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void init(){
        findUsersText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    animateHasFocus();
                }
            }
        });

        closeHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRemoveFocus();
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
            findUsersText.getText().clear();
            findUsersText.clearFocus();
        }
    }


    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
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


    private void setTextListener(){
        findUsersText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateAdapter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setAdapter(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        int limit = 50;

        Query query = reference.child("userList").limitToFirst(limit);

        adapter = new CustomFirebaseAdapter(User.class, R.layout.item_friend, CustomFirebaseAdapter.FriendHolder.class, query);
        adapter.setContext(getContext());

        adapter.setOnItemClickListener(new CustomFirebaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, User obj, int position) {
                if(callingFragment == NewGameFragment.class.hashCode())
                    ((NewGameActivity)getActivity()).goToGame((User) obj, fiveWordKey);
            }
        });


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setAdapter(adapter);
    }

    private void updateAdapter(String text){
        if(recyclerView == null) return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("userList").orderByChild("nameHash").startAt("@" + text).endAt("@" + text +"\uf8ff");

        adapter = new CustomFirebaseAdapter(User.class, R.layout.item_friend, CustomFirebaseAdapter.FriendHolder.class, query);
        adapter.setContext(getContext());

        adapter.setOnItemClickListener(new CustomFirebaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, User obj, int position) {
                if(callingFragment == NewGameFragment.class.hashCode())
                    ((NewGameActivity)getActivity()).goToGame((User) obj, fiveWordKey);
            }
        });

        recyclerView.setAdapter(adapter);
    }


    @Override
    public String getToolbarTitle() {
        return "Select Friend";
    }

    @Override
    public int getToolbarColor() {
        return R.color.colorPrimary;
    }





    private void setSearchHolderInitialY(){
        if(startingPosition == -1){
            searchHolder.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(startingPosition == -1){
                        startingPosition = searchHolder.getY();
                        hiddenPosition = startingPosition - searchHolder.getMeasuredHeight();
                        searchHolderHeight = searchHolder.getMeasuredHeight();
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(startingPosition == -1) return false;

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            y1 = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_MOVE){
            y2 = event.getY();

            float difference = y2-y1;

            if(difference < 0){
//                upward swipe
                adjustPageHeight();

                if(rootView.getY() + difference >= hiddenPosition){
                    float newY = rootView.getY() + difference;
                    rootView.setY(newY);
                }else{
                    if(rootView.getY() != hiddenPosition){
                        rootView.setY(hiddenPosition);
                    }
                }
            }else{
//                Downward swipe
                if(rootView.getY() + difference <= startingPosition){
                    float newY = rootView.getY() + difference;
                    rootView.setY(newY);
                }else{
                    if(rootView.getY() != startingPosition){
                        rootView.setY(startingPosition);
                        setHeightNormal();
                    }
                }
            }
        }
        return false;
    }

    private void setDefaultHeight(){
        if(defaultHeight != -1) return;
        mainContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(defaultHeight == -1)
                    defaultHeight = mainContent.getMeasuredHeight();

                mainContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private float defaultHeight = -1;

    private void adjustPageHeight(){
        if(searchHolderHeight == -1) return;

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mainContent.getLayoutParams();
        params.height = (int)defaultHeight + (int)searchHolderHeight;
        mainContent.setLayoutParams(params);
    }

    private void setHeightNormal(){
        if(defaultHeight == -1) return;

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mainContent.getLayoutParams();
        params.height = (int)defaultHeight;
        mainContent.setLayoutParams(params);
    }

}
