package ben.holmes.scavenger.buddies.Database;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ben.holmes.scavenger.buddies.Games.Fragments.GameFragment;
import ben.holmes.scavenger.buddies.Model.Friend;
import ben.holmes.scavenger.buddies.Model.Game;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.Train.Tag;

/**
 * Singleton class, functionality for database
 */
public class Database {

    private DatabaseReference databaseReference;
    public static Database database;

    public static Database getInstance(){
        return  database == null ? new Database() : database;
    }

    private Database(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }


    public void addUser(final User user){
        Query query = databaseReference.child("userList").orderByChild("uid").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    databaseReference.child("userList").child(user.getUid()).setValue(user);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateUserName(String firstName, String lastName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("userList").child(user.getUid()).child("firstName").setValue(firstName);
        databaseReference.child("userList").child(user.getUid()).child("lastName").setValue(lastName);
        databaseReference.child("userList").child(user.getUid()).child("displayName").setValue(firstName + " " + lastName);
    }

    public void updateUserPhotoUrl(String url){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("userList").child(user.getUid()).child("photoUrl").setValue(url);
    }


    /**
     * Method is called from NewGameFragment after words are retrieved from firebase. Save this gameObj in
     * both the challener (current user) and opponent's firebase trees.
     *
     * @param gameObj
     */
    public void addGameToFirebase(Game gameObj){
        databaseReference.child("userList").child(gameObj.getChallenger()).child("games").child(gameObj.getGameID()).setValue(gameObj);
        gameObj.setYourTurn(false);
        databaseReference.child("userList").child(gameObj.getOpponent()).child("games").child(gameObj.getGameID()).setValue(gameObj);

        /**
         * Set gameObj back to true because this instance of the gameObj gets passed from NewGameFragment to PlayFragment, and currentUser's
         * turn should not be updated until one of two conditions are met.
         *
         * 1.) Current user's turn expires, so opponent is notified of their turn.
         * 2.) Current user's matches the correct word, so either they win the game, or the opponent again is notified of their turn
         */
        gameObj.setYourTurn(true);
    }

    /**
     * Method is called from PlayFragment OnSaveInstanceState, just saves the game in Realm in case of an orientation change. The gameObj
     * only needs to be stored in the currentUser's tree at this point. The next time the opponent's tree will need to be updated is given
     * in 2 conditions.
     *
     * 1.) Current user's turn expires, so opponent is notified of their turn.
     * 2.) Current user's matches the correct word, so either they win the game, or the opponent again is notified of their turn
     *
     * @param gameObj
     */
    public void updateGame(Game gameObj){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("userList").child(user.getUid()).child("games").child(gameObj.getGameID()).setValue(gameObj);
    }

    public void storeWord(String word, Game gameObj){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("userList").child(user.getUid()).child("games").child(gameObj.getGameID()).child("currentWord").setValue(word);
    }



    /**
     * Method is called from Camera2Activity if the current user has matched the word
     * they were searching for.
     *
     * 3 Things must be done
     *
     * 1. ) Current Users's turn ends
     * 2. ) Current User marks this word off their word list
     * 3. ) if this was the game winning word, update PlayFragment()
     *
     *
     * @param gameObj
     * @return true if this user just won the game
     */
    public boolean updateCurrentUserFoundWord(Game gameObj){
        boolean result = false;
        boolean challenger = true;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentWord = gameObj.getCurrentWord();

        gameObj.setCurrentWord(null);
        gameObj.setYourTurn(false);

        if(user.getUid().equals(gameObj.getChallenger())){
            gameObj.challengerWordFound(currentWord);
            if(gameObj.getChallengerWordsLeft().size() == 0){
                /**
                 * User has just won the game
                 */
                result = true;
            }
        }
        else{
            challenger = false;
            gameObj.opponentWordFound(currentWord);
            if(gameObj.getChallengerWordsLeft().size() == 0){
                /**
                 * User has just won the game
                 */
                result = true;
            }
        }

        databaseReference.child("userList").child(user.getUid()).child("games").child(gameObj.getGameID()).setValue(gameObj);
        /**
         * Now save the updated game obj to the opponents GameList also, opponents yourTurn boolean is just
         * the opposite of whatever the current user's is
         */
        gameObj.setYourTurn(true);
        if(challenger)
            databaseReference.child("userList").child(gameObj.getOpponent()).child("games").child(gameObj.getGameID()).setValue(gameObj);

        return result;
    }


    public interface GameUserListCallback{
        void onComplete(List<User> gameUserList);
    }

    public void getGameUserList(final GameUserListCallback callback){
        final Query query = databaseReference.child("userList");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                query.removeEventListener(this);

                GenericTypeIndicator<HashMap<String, User>> ta = new GenericTypeIndicator<HashMap<String, User>>(){};
                HashMap<String, User> map = dataSnapshot.getValue(ta);
                List<User> list = new ArrayList<>(map.values());
                callback.onComplete(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface UserCallback{
        void onComplete(User user);
    }

    public void getUser(final UserCallback callback, String uid){
        final Query query = databaseReference.child("userList").child(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                query.removeEventListener(this);

                User user = dataSnapshot.getValue(User.class);
                callback.onComplete(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface GameCallback{
        void onComplete(List<Game> games);
    }

    public void getGameList(final GameCallback callback){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final Query query = databaseReference.child("userList").child(user.getUid()).child("games");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Game>> ta = new GenericTypeIndicator<HashMap<String, Game>>(){};
                HashMap<String, Game> map = dataSnapshot.getValue(ta);
                if(map == null || map.size() == 0){
                    ArrayList<Game> list = new ArrayList<>();
                    callback.onComplete(list);
                    query.removeEventListener(this);
                    return;
                }

                List<Game> list = new ArrayList<>(map.values());
                callback.onComplete(list);
                query.removeEventListener(this);
                return;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface TagCallback{
        void onComplete(List<String> list);
    }

    public void getTags(final TagCallback callback, final int count){
        ArrayList<String> result = new ArrayList<>();
        final Query query = databaseReference.child("TagData").child("TagList");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                query.removeEventListener(this);

                GenericTypeIndicator<HashMap<String, Tag>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Tag>>(){};
                HashMap<String, Tag> map = dataSnapshot.getValue(genericTypeIndicator);
                List<String> list = new ArrayList<>(map.keySet());
                List<String> result = filterTags(list, count);
                callback.onComplete(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isValidWord(String word){
        return !word.equalsIgnoreCase("abstract") &&
                !word.equalsIgnoreCase("vertical") &&
                !word.equalsIgnoreCase("blur") &&
                !word.equalsIgnoreCase("horizontal") &&
                !word.equalsIgnoreCase("internet");
    }

    private List<String> filterTags(List<String> list, int count){
        List<String> result = new ArrayList<>();
        boolean[] values = new boolean[list.size()];
        Random rand = new Random();
        int found = 0;
        while(found < count){
            int value = rand.nextInt(list.size());
            if(values[value] == false && isValidWord(list.get(value))){
                result.add(list.get(value));
                values[value] = true;
                found++;
            }
        }
        return result;
    }


    public void getRandomFriend(String myHash, final UserCallback callback){
        Random r = new Random();
        char myFirstLetter = myHash.charAt(0);
        char c = myFirstLetter;
        while(c == myFirstLetter){
            c = (char)(r.nextInt(26) + 'a');
        }
        String item = Character.toString(c);
        final Query query = databaseReference.child("userList").startAt(item).endAt(item + "\uf8ff").limitToFirst(100);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                query.removeEventListener(this);

                GenericTypeIndicator<HashMap<String, User>> ta = new GenericTypeIndicator<HashMap<String, User>>(){};
                HashMap<String, User> map = dataSnapshot.getValue(ta);
                ArrayList<User> list = new ArrayList<>(map.values());
                Random rand = new Random();
                User friend = null;
                if(list.size() == 1) {
                    callback.onComplete(null);

                    return;
                }

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                while(friend == null){
                    friend = list.get(rand.nextInt(list.size()));
                    if(friend.getUid().equals(currentUser.getUid()))
                        friend = null;
                }
                callback.onComplete(friend);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public interface NameHashCallback{
        void onComplete(String hash);
    }

    /**
     *
     * TODO make this faster, and fix the error for random friends
     *
     * Function for creating a unique hash to ensure no two people have the same.
     * @param email
     * @param callback
     */
    public void createNameHash(final String email, final NameHashCallback callback){
        final Query query = databaseReference.child("nameHashes");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                query.removeEventListener(this);
                String emailHash = email.substring(0, email.indexOf("@"));

                while(dataSnapshot.hasChild(emailHash)){
                    Random rand = new Random();
                    emailHash = emailHash + rand.nextInt(10);
                }
                databaseReference.child("nameHashes").child(emailHash).setValue(emailHash);
                callback.onComplete(emailHash);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
