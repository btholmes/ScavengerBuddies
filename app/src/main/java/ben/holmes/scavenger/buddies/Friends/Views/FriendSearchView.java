package ben.holmes.scavenger.buddies.Friends.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.List;

import ben.holmes.scavenger.buddies.App.Tools.FacebookUtil;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Model.Friend;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.R;
import io.realm.Realm;

public class FriendSearchView extends RelativeLayout {

    private Context ctx;
    private static Context staticContext;
    private View root;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;
    private Database database;
    private DatabaseReference reference;
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

    private void sharedConstructor(Context ctx, @Nullable AttributeSet attrs){
        this.ctx = ctx;
        staticContext = this.ctx;
        root = LayoutInflater.from(ctx).inflate(R.layout.friend_search_view, this);
//        realm = Realm.getDefaultInstance();
        recyclerView = root.findViewById(R.id.recycler_view);

//        if(attrs != null){
//            TypedArray ta = ctx.obtainStyledAttributes()
//        }

    }

    public void populateUserList(Database database, FacebookUtil facebookUtil){
        this.database = database;
        this.facebookUtil = facebookUtil;
        this.reference = FirebaseDatabase.getInstance().getReference();

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
        Query query = reference.child("userList");

        adapter = new FirebaseRecyclerAdapter(User.class, R.layout.item_friend, FriendHolder.class, query) {
            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
                User user = (User)model;
                FriendHolder holder = (FriendHolder)viewHolder;
                holder.setName(user.getDisplayName());
                holder.setSubtitle(user.getEmail());
                holder.setImage(user.getPhotoUrl());
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider));
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
        public TextView subtitle;
        public TextView challengeButton;

        public FriendHolder(View view){
            super(view);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            subtitle = view.findViewById(R.id.subtitle);
            challengeButton = view.findViewById(R.id.challengeButton);
        }


        public void setImage(String url){
            Picasso.with(staticContext).load(url).into(image);
//            image.setImageResource(resource);
        }

        public void setName(String name){
            this.name.setText(name);
        }

        public void setSubtitle(String subtitle){
            this.subtitle.setText(subtitle);
        }

        public void setChallengeListner(View.OnClickListener listener){
            this.challengeButton.setOnClickListener(listener);
        }

    }

}
