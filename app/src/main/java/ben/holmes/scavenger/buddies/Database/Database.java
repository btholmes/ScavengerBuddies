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

import ben.holmes.scavenger.buddies.Model.Friend;
import ben.holmes.scavenger.buddies.Model.Game;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.Train.Tag;

/**
 * Singleton class, functionality for database
 */
public class Database {

    public static Context ctx;
    public static DatabaseReference databaseReference;
    public static Database database;

    public static Database getInstance(Context ctx){
        if(database == null)
            return new Database(ctx);

        return database;
    }

    private Database(Context ctx){
        this.ctx = ctx;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static void addUser(User user){
        databaseReference.child("userList").child(user.getUid()).setValue(user);
    }

    public static void updateUserName(String firstName, String lastName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("userList").child(user.getUid()).child("firstName").setValue(firstName);
        databaseReference.child("userList").child(user.getUid()).child("lastName").setValue(lastName);
        databaseReference.child("userList").child(user.getUid()).child("displayName").setValue(firstName + " " + lastName);
    }

    public static void updateUserPhotoUrl(String url){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("userList").child(user.getUid()).child("photoUrl").setValue(url);
    }


    public static void addGameToFirebase(Game gameObj){
        databaseReference.child("userList").child(gameObj.getChallenger()).child("games").child(gameObj.getGameID()).setValue(gameObj);
        databaseReference.child("userList").child(gameObj.getOpponent()).child("games").child(gameObj.getGameID()).setValue(gameObj);
    }


    public interface GameUserListCallback{
        void onComplete(List<User> gameUserList);
    }

    public void getGameUserList(final GameUserListCallback callback){
        databaseReference.child("userList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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

    public void getUser(final UserCallback callback){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child("userList").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.onComplete(user);
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
        databaseReference.child("TagData").child("TagList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

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

    private List<String> filterTags(List<String> list, int count){
        List<String> result = new ArrayList<>();
        boolean[] values = new boolean[list.size()];
        Random rand = new Random();
        int found = 0;
        while(found < count){
            int value = rand.nextInt(list.size());
            if(values[value] == false){
                result.add(list.get(value));
                values[value] = true;
                found++;
            }
        }
        return result;
    }


    public void getRandomFriend(final UserCallback callback){
        final Query query = databaseReference.child("userList");
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


}
