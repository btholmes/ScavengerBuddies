package ben.holmes.scavenger.buddies.Games.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import ben.holmes.scavenger.buddies.App.PopUp.ScavengerDialog;
import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.App.Tools.CircleTransform;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Games.Activities.NewGameActivity;
import ben.holmes.scavenger.buddies.Main.MainActivity;
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
    private RelativeLayout yourTurnRecyclerHolder;
    private RelativeLayout theirTurnRecyclerHolder;
    private RecyclerView yourTurnRecyclerView;
    private RecyclerView theirTurnRecyclerView;
    private CardView yourTurnCard;
    private CardView theirTurnCard;
    private static Context ctx;
    private FirebaseRecyclerAdapter theirTurnAdapter;
    private FirebaseRecyclerAdapter yourTurnAdapter;
//    private DatabaseReference reference;
//    private FirebaseUser user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.ctx = getContext();
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
        yourTurnRecyclerHolder = view.findViewById(R.id.your_turn_recycler_holder);
        theirTurnRecyclerHolder = view.findViewById(R.id.their_turn_recycler_holder);
        yourTurnRecyclerView = view.findViewById(R.id.your_turn_recycler_view);
        theirTurnRecyclerView = view.findViewById(R.id.their_turn_recycler_view);
        yourTurnCard = view.findViewById(R.id.your_turn_card);
        theirTurnCard = view.findViewById(R.id.their_turn_card);
        yourTurnCard.setVisibility(View.GONE);
        theirTurnCard.setVisibility(View.GONE);
        setUpGameButton();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setMyTurnAdapter();
        setTheirTurnAdapter();
    }

    public interface BasicCallback{
        void onComplete(boolean result);
    }

    private boolean hasTheirTurnGames(final BasicCallback callback){
        boolean result = false;
        if(!isAdded()) return  false;

        DatabaseReference reference = ((ScavengerActivity)getActivity()).getDatabaseReference();
        FirebaseUser user = ((ScavengerActivity)getActivity()).getFirebaseUser();

        Query query = reference.child("userList")
                .child(user.getUid())
                .child("games")
                .orderByChild("yourTurn")
                .equalTo(false);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Game>> ta = new GenericTypeIndicator<HashMap<String, Game>>(){};
                HashMap<String, Game> map = dataSnapshot.getValue(ta);
                if(map != null){
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


    private boolean hasMyTurnGames(final BasicCallback callback){
        boolean result = false;
        if(!isAdded()) return  false;

        DatabaseReference reference = ((ScavengerActivity)getActivity()).getDatabaseReference();
        FirebaseUser user = ((ScavengerActivity)getActivity()).getFirebaseUser();

        Query query = reference.child("userList")
                .child(user.getUid())
                .child("games")
                .orderByChild("yourTurn")
                .equalTo(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Game>> ta = new GenericTypeIndicator<HashMap<String, Game>>(){};
                HashMap<String, Game> map = dataSnapshot.getValue(ta);
                if(map != null){
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

    /**
     * Firebase doesn't have a notEqualTo query option, but it can detect the absence of
     * a field using null.
     */
    private void setTheirTurnAdapter(){
        if(!isAdded()) return;

        DatabaseReference reference = ((ScavengerActivity)getActivity()).getDatabaseReference();
        FirebaseUser user = ((ScavengerActivity)getActivity()).getFirebaseUser();
        if(user == null || reference == null) return;

        Query query = reference.child("userList").child(user.getUid())
                .child("games")
                .orderByChild("yourTurn")
                .equalTo(false);

        theirTurnAdapter = new FirebaseRecyclerAdapter(Game.class, R.layout.item_game, GameHolder.class, query) {
            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
                Game game = (Game)model;
                viewHolder = (GameHolder)viewHolder;
                ((GameHolder) viewHolder).setGame(getContext(), game, false);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                checkForGamesInTheirTurn();
            }
        };

        theirTurnRecyclerView.setHasFixedSize(true);
        theirTurnRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider));
        theirTurnRecyclerView.addItemDecoration(decoration);
        theirTurnRecyclerView.setAdapter(theirTurnAdapter);
        theirTurnAdapter.startListening();
    }

    private void setMyTurnAdapter(){
        if(!isAdded()) return;

        DatabaseReference reference = ((ScavengerActivity)getActivity()).getDatabaseReference();
        FirebaseUser user = ((ScavengerActivity)getActivity()).getFirebaseUser();
        if(user == null || reference == null) return;

        Query query = reference.child("userList").child(user.getUid())
                .child("games")
                .orderByChild("yourTurn")
                .equalTo(true);

        yourTurnAdapter = new FirebaseRecyclerAdapter(Game.class, R.layout.item_game, GameHolder.class, query) {
            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
                Game game = (Game)model;
                viewHolder = (GameHolder)viewHolder;
                ((GameHolder) viewHolder).setGame(getContext(), game, true);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                checkForGamesInMyTurn();
            }
        };

        yourTurnRecyclerView.setHasFixedSize(true);
        yourTurnRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider));
        yourTurnRecyclerView.addItemDecoration(decoration);
        yourTurnRecyclerView.setAdapter(yourTurnAdapter);
        yourTurnAdapter.startListening();
    }

    private void checkForGamesInMyTurn(){
        hasMyTurnGames(new BasicCallback() {
            @Override
            public void onComplete(boolean result) {
                if(result){
                    yourTurnCard.setVisibility(View.VISIBLE);
                    adjustMyTurnSize();
                }else{
                    yourTurnCard.setVisibility(View.GONE);
                }
            }
        });
    }


    private void checkForGamesInTheirTurn(){
        hasTheirTurnGames(new BasicCallback() {
            @Override
            public void onComplete(boolean result) {
                if(result){
                    theirTurnCard.setVisibility(View.VISIBLE);
                    adjustTheirTurnSize();
                }else{
                    theirTurnCard.setVisibility(View.GONE);
                }
            }
        });
    }

    private void adjustTheirTurnSize(){
        if(!isAdded()) return;

        if(theirTurnCard.getVisibility() == View.GONE) return;
        if(theirTurnAdapter == null) return;

        int totalHeight = 0;
        if(isAdded()){
            int dp = ((ScavengerActivity) getActivity()).convertDpToPixels(90);
            totalHeight += theirTurnAdapter.getItemCount() * dp;
        }

        if(totalHeight > 0 && isAdded()){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) theirTurnRecyclerHolder.getLayoutParams();
            int height = 0;
            if(isAdded())
                height = ((ScavengerActivity)getActivity()).convertDpToPixels(2);
            params.height = totalHeight + (theirTurnRecyclerView.getItemDecorationCount() * height );
            theirTurnRecyclerHolder.setLayoutParams(params);
        }
    }

    /**
     * Hard programs the height of item_game into this
     */
    private void adjustMyTurnSize(){
        if(!isAdded()) return;

        if(yourTurnCard.getVisibility() == View.GONE) return;
        if(yourTurnAdapter == null) return;

        int totalHeight = 0;
        if(isAdded()){
            int dp = ((ScavengerActivity) getActivity()).convertDpToPixels(90);
            totalHeight += yourTurnAdapter.getItemCount() * dp;
        }

        if(totalHeight > 0 && isAdded()){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) yourTurnRecyclerHolder.getLayoutParams();
            int height = ((ScavengerActivity)getActivity()).convertDpToPixels(2);
            params.height = totalHeight + (yourTurnRecyclerView.getItemDecorationCount() * height );
            yourTurnRecyclerHolder.setLayoutParams(params);
        }
    }


    private void setUpGameButton(){
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameButton.quickClick(new ShadowButton.QuickClick() {
                    @Override
                    public void onSuccess() {
                        Database.getInstance().getGameList(new Database.GameCallback() {
                            @Override
                            public void onComplete(List<Game> games) {
                                if(games.size() < 25){
                                    goToNewGameActivity();
                                }else{
                                    showTooManyGamesDialog();
                                }
                            }
                        });
                    }
                });
            }
        });
    }


    private void showTooManyGamesDialog(){
        final ScavengerDialog dialog = new ScavengerDialog(getContext());
        dialog.hideHeader();
        dialog.setBannerText("Small Problem");
        dialog.setMessageText("You are only allowed to have 25 games at a time. In order to add more, either finish " +
                "an existing game, or simply delete some.");
        dialog.showSingleOkButton();
        dialog.setSingleOkButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void goToNewGameActivity(){
        if(!isAdded()) return;
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

        public void setGame(Context ctx, Game game, boolean myTurn){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user.getUid().equals(game.getChallenger())){
                setName(game.getOpponentDisplayName());
                String photoUrl = game.getOpponentPhotoUrl();
                if(photoUrl == null || photoUrl.length() <= 0){
                    setImage(R.drawable.ic_generic_account);
                }else{
                    setImage(ctx, photoUrl);
                }
                String timeLeft = game.getTimeLeft(user.getUid());
                if(timeLeft == null || timeLeft.length() <= 0)
                    setStatus("Waiting on opponent...");
                else
                    setStatus(game.getTimeLeft(user.getUid()));
                setDate(game.getCreationDate());
            }else{
                setName(game.getChallengerDisplayName());
                String photoUrl = game.getChallengerPhotoUrl();
                if(photoUrl == null || photoUrl.length() <= 0){
                    setImage(R.drawable.ic_generic_account);
                }else{
                    setImage(ctx, photoUrl);
                }
                String timeLeft = game.getTimeLeft(user.getUid());
                if(timeLeft == null || timeLeft.length() <= 0)
                    setStatus("Waiting on opponent...");
                else
                    setStatus(game.getTimeLeft(user.getUid()));
                setDate(game.getCreationDate());
            }

            if(myTurn)
                setClickListener(ctx, game);
        }

        private void setClickListener(final Context ctx, final Game game){
            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity activity = (MainActivity)ctx;
                    Intent intent = new Intent(activity, NewGameActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(PlayFragment.GAME_KEY, game);
                    ctx.startActivity(intent);
                    ((MainActivity) ctx).finish();
                }
            });
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

        public void setImage(Context ctx, String url){
            Picasso.with(ctx).load(url).transform(new CircleTransform()).into(image);
        }

        public void setGamePicture(int resource){
            gamePicture.setBackgroundResource(resource);
        }

        public void setOnClick(View.OnClickListener listener){
            content.setOnClickListener(listener);
        }
    }


}
