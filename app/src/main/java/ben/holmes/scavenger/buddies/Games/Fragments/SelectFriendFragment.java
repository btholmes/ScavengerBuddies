package ben.holmes.scavenger.buddies.Games.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

import ben.holmes.scavenger.buddies.App.ScavengerFragment;
import ben.holmes.scavenger.buddies.App.Tools.CircleTransform;
import ben.holmes.scavenger.buddies.Friends.Activities.FriendDetailsActivity;
import ben.holmes.scavenger.buddies.Main.MainActivity;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.R;

public class SelectFriendFragment extends ScavengerFragment implements View.OnTouchListener{

    public static String TAG_NAME = "Select Friend Fragment";

    private static Context ctx;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;
    private View rootView;
    private RelativeLayout mainContent;

    private FrameLayout searchHolder;
    private FrameLayout closeHolder;
    private EditText findUsersText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ctx = getContext();
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
        findUsersText = rootView.findViewById(R.id.findUsersTextView);
        setTextListener();

        setAdapter();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

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
        Query query = reference.child("userList");

        adapter = new FirebaseRecyclerAdapter(User.class, R.layout.item_friend, FriendHolder.class, query) {
            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
                FriendHolder friendHolder = (FriendHolder)viewHolder;
                friendHolder.setUserInfo((User)model);
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setAdapter(adapter);
    }

    private void updateAdapter(String text){
        if(recyclerView == null) return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("userList").orderByChild("nameHash").startAt("@" + text).endAt("@" + text +"\uf8ff");

        adapter = new FirebaseRecyclerAdapter(User.class, R.layout.item_friend, FriendHolder.class, query) {
            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
                FriendHolder friendHolder = (FriendHolder)viewHolder;
                friendHolder.setUserInfo((User)model);
            }
        };

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

    public static class FriendHolder extends RecyclerView.ViewHolder{

        public ImageView image;
        public TextView name;
        public TextView emailName;
        public TextView subtitle;
        public TextView challengeButton;
        public TextView messageButton;
        public LinearLayout userContent;
        public LinearLayout facebookInfo;
        public LinearLayout emailInfo;

        public FriendHolder(View view){
            super(view);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            emailName = view.findViewById(R.id.emailName);
            subtitle = view.findViewById(R.id.subtitle);
            challengeButton = view.findViewById(R.id.challengeButton);
            messageButton = view.findViewById(R.id.messageButton);
            hideButtons();
            userContent = view.findViewById(R.id.userContent);
            facebookInfo = view.findViewById(R.id.facebookInfo);
            emailInfo = view.findViewById(R.id.emailInfo);
        }

        private void hideButtons(){
            challengeButton.setVisibility(View.GONE);
            messageButton.setVisibility(View.GONE);
        }

        public void setUserInfo(User user){
            if(user.getDisplayName() == null || user.getDisplayName().length() <= 0){
                showEmailInfo();
                setEmailName(user.getNameHash());
            }else{
                showFacebookInfo();
                setName(user.getDisplayName());
                setSubtitle(user.getNameHash());
            }
            setImage(user.getPhotoUrl());
            setListeners(user);
        }

        public void setListeners(final User user){
            setUserContentListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    FriendDetailsActivity.navigate(activity, user);
                }
            });
        }


        public void setImage(String url){
            if(url != null && url.length() > 0)
                Picasso.with(ctx).load(url).transform(new CircleTransform()).into(image);

//            image.setImageResource(resource);
        }

        public void setEmailName(String text){
            this.emailName.setText(text);
        }

        public void showEmailInfo(){
            emailInfo.setVisibility(View.VISIBLE);
            facebookInfo.setVisibility(View.GONE);
        }

        public void showFacebookInfo(){
            facebookInfo.setVisibility(View.VISIBLE);
            emailInfo.setVisibility(View.GONE);
        }

        public void setName(String name){
            this.name.setText(name);
        }

        public void setSubtitle(String subtitle){
            this.subtitle.setText(subtitle);
        }

        public void setUserContentListener(View.OnClickListener listener){
            this.userContent.setOnClickListener(listener);
        }

        public void setChallengeListner(View.OnClickListener listener){
            this.challengeButton.setOnClickListener(listener);
        }

        public void setMessageListener(View.OnClickListener listener){
            this.messageButton.setOnClickListener(listener);
        }

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
