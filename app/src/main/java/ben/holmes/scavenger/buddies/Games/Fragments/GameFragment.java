package ben.holmes.scavenger.buddies.Games.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.Games.Activities.NewGameActivity;
import ben.holmes.scavenger.buddies.Model.Game;
import ben.holmes.scavenger.buddies.Model.GameButton;
import ben.holmes.scavenger.buddies.Model.ShadowButton;
import ben.holmes.scavenger.buddies.R;


import ben.holmes.scavenger.buddies.App.ScavengerFragment;

/**
 * Created by benholmes on 5/7/18.
 */

public class GameFragment extends ScavengerFragment{

    public static final String TAG_NAME = "Games";
    public static final int TOOLBAR_COLOR = R.color.colorPrimary;

    private View view;
    private ShadowButton gameButton;
    private RelativeLayout recyclerHolder;
    private RecyclerView recyclerView;
    private CardView cardView;
    private static Context ctx;
    private FirebaseRecyclerAdapter adapter;
    private DatabaseReference reference;
    private FirebaseUser user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ctx = getContext();
        reference = ((ScavengerActivity)getActivity()).getDatabaseReference();
        user = ((ScavengerActivity)getActivity()).getFirebaseUser();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game, container, false);
        gameButton = view.findViewById(R.id.game_button);
        recyclerHolder = view.findViewById(R.id.recycler_holder);
        recyclerView = view.findViewById(R.id.recycler_view);
        cardView = view.findViewById(R.id.card_view);
        cardView.setVisibility(View.GONE);
        setUpGameButton();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAdapter();
    }

    public interface BasicCallback{
        void onComplete(boolean result);
    }

    private boolean hasGames(final BasicCallback callback){
        boolean result = false;
        reference.child("userList").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("games")){
                    callback.onComplete(true);
                }else{
                    callback.onComplete(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return result;
    }

    private void setAdapter(){
        Query query = reference.child("userList").child(user.getUid()).child("games");
        adapter = new FirebaseRecyclerAdapter(Game.class, R.layout.item_game, GameHolder.class, query) {
            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
                Game game = (Game)model;
                viewHolder = (GameHolder)viewHolder;
                ((GameHolder) viewHolder).setName("Is it working");

                ((GameHolder) viewHolder).setOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ctx, "Whaddup", Toast.LENGTH_LONG);
                    }
                });
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                checkForGames();
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

    private void checkForGames(){
        hasGames(new BasicCallback() {
            @Override
            public void onComplete(boolean result) {
                if(result){
                    cardView.setVisibility(View.VISIBLE);
                    adjustSize();
                }else{
                    cardView.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Hard programs the height of item_game into this
     */
    private void adjustSize(){
        if(cardView.getVisibility() == View.GONE) return;
        if(adapter == null) return;

        int totalHeight = 0;
        if(isAdded()){
            int dp = ((ScavengerActivity)getActivity()).convertDpToPixels(90);
            totalHeight += adapter.getItemCount() * dp;
        }

        if(totalHeight > 0 && isAdded()){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recyclerHolder.getLayoutParams();
            int height = ((ScavengerActivity)getActivity()).convertDpToPixels(2);
            params.height = totalHeight + (recyclerView.getItemDecorationCount() * height );
            recyclerHolder.setLayoutParams(params);
        }
    }


    private void setUpGameButton(){
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameButton.quickClick(new ShadowButton.QuickClick() {
                    @Override
                    public void onSuccess() {
                        goToNewGameActivity();
                    }
                });
            }
        });
    }


    private void goToNewGameActivity(){
        Intent intent = new Intent(getActivity(), NewGameActivity.class);
        startActivity(intent);
    }

    @Override
    public String getToolbarTitle() {
        return TAG_NAME;
    }

    @Override
    public int getToolbarColor() {
        return TOOLBAR_COLOR;
    }


    public static class GameHolder extends RecyclerView.ViewHolder{

        public RelativeLayout content;
        public ImageView image;
        public ImageView gamePicture;
        public TextView name;
        public TextView status;
        public TextView date;
        public TextView score;

        public GameHolder(View view){
            super(view);
            content = view.findViewById(R.id.content);
            image = view.findViewById(R.id.image);
            gamePicture = view.findViewById(R.id.game_picture);
            name = view.findViewById(R.id.name);
            status = view.findViewById(R.id.status);
            date = view.findViewById(R.id.date);
            score = view.findViewById(R.id.score);
        }

        public void setName(String name){
            this.name.setText(name);
        }

        public void setStatus(String status){
            this.status.setText(status);
        }

        public void setDate(String date){
            this.date.setText(date);
        }

        public void setScore(String score){
            this.score.setText(score);
        }

        public void setImage(int resource){
            image.setBackgroundResource(resource);
        }

        public void setGamePicture(int resource){
            gamePicture.setBackgroundResource(resource);
        }

        public void setOnClick(View.OnClickListener listener){
            content.setOnClickListener(listener);
        }
    }


}
