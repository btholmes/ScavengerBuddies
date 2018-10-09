package ben.holmes.scavenger.buddies.Friends.Views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import ben.holmes.scavenger.buddies.App.Tools.CircleTransform;
import ben.holmes.scavenger.buddies.App.Tools.FacebookUtil;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Friends.Activities.FriendDetailsActivity;
import ben.holmes.scavenger.buddies.Main.MainActivity;
import ben.holmes.scavenger.buddies.Model.Friend;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.R;
import io.realm.Realm;

public class FriendSearchView extends RelativeLayout {

    private Context ctx;
    private static Activity activity;
    private static Context staticContext;
    private View root;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;
    private FacebookUtil facebookUtil;
    private Realm realm;

    public FriendSearchView(Context ctx){
        super(ctx);
        sharedConstructor(ctx, null);
    }

    public FriendSearchView(Context ctx, AttributeSet attrs){
        super(ctx, attrs);
        sharedConstructor(ctx, attrs);

    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public RelativeLayout getMainContent(){
        return findViewById(R.id.friendSearchView);
    }

    private void sharedConstructor(Context ctx, @Nullable AttributeSet attrs){
        this.ctx = ctx;
        staticContext = this.ctx;
        root = LayoutInflater.from(ctx).inflate(R.layout.friend_search_view, this);
//        realm = Realm.getDefaultInstance();
        recyclerView = root.findViewById(R.id.recycler_view);
    }

    public RecyclerView getRecyclerView() {
         return this.recyclerView;
    }

    public void populateUserList(FacebookUtil facebookUtil){
        this.facebookUtil = facebookUtil;

        setAdapter();
//        database.getGameUserList(new Database.GameUserListCallback() {
//            @Override
//            public void onComplete(List<User> gameUserList) {
//                List copy = gameUserList;
//                setAdapter(gameUserList);
//            }
//        });
    }

    private void setAdapter(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        int limit = 50;
        Query query = reference.child("userList").limitToFirst(limit);

        adapter = new FirebaseRecyclerAdapter(User.class, R.layout.item_friend, FriendHolder.class, query) {
            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
                User user = (User)model;
                FriendHolder holder = (FriendHolder)viewHolder;
                holder.setUserInfo(user);
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return true;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public void updateAdapter(String text){
        if(recyclerView == null) return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        int limit = 50;
        Query query = reference.child("userList").orderByChild("nameHash").startAt(text).endAt(text +"\uf8ff").limitToFirst(limit);

        adapter = new FirebaseRecyclerAdapter(User.class, R.layout.item_friend, FriendHolder.class, query) {
            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
                User user = (User)model;
                FriendHolder holder = (FriendHolder)viewHolder;
                holder.setUserInfo(user);
            }
        };

        recyclerView.setAdapter(adapter);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
            userContent = view.findViewById(R.id.userContent);
            facebookInfo = view.findViewById(R.id.facebookInfo);
            emailInfo = view.findViewById(R.id.emailInfo);
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
            setUserContentListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendDetailsActivity.navigate(activity, user);
                }
            });
            setChallengeListner(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            setMessageListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }


        public void setImage(String url){
            if(url != null && url.length() > 0)
                Picasso.with(staticContext).load(url).transform(new CircleTransform()).into(image);

//            image.setImageResource(resource);
        }

        public void setEmailName(String text){
            this.emailName.setText(text);
        }

        public void showEmailInfo(){
            emailInfo.setVisibility(VISIBLE);
            facebookInfo.setVisibility(GONE);
        }

        public void showFacebookInfo(){
            facebookInfo.setVisibility(VISIBLE);
            emailInfo.setVisibility(GONE);
        }

        public void setName(String name){
            this.name.setText(name);
        }

        public void setSubtitle(String subtitle){
            this.subtitle.setText(subtitle);
        }

        public void setUserContentListener(OnClickListener listener){
            this.userContent.setOnClickListener(listener);
        }

        public void setChallengeListner(View.OnClickListener listener){
            this.challengeButton.setOnClickListener(listener);
        }

        public void setMessageListener(View.OnClickListener listener){
            this.messageButton.setOnClickListener(listener);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if(gestureDetector.onTouchEvent(event)){
//            return true;
//        }
        gestureDetector.onTouchEvent(event);
        if(isSingleTap){
            isSingleTap = false;
        }else
            moveView(event);

        return true;
    }

    public static boolean isSingleTap = false;
    GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //do something
            isSingleTap = true;
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    });

    public void moveView(MotionEvent event) {
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
    }
}
