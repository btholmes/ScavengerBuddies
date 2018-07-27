package ben.holmes.scavenger.buddies.Database;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ben.holmes.scavenger.buddies.Model.Game;
import ben.holmes.scavenger.buddies.Model.User;

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


    public static void addGameToFirebase(Game gameObj){
        databaseReference.child("userList").child(gameObj.getChallenger()).child("games").child(gameObj.getGameID()).setValue(gameObj);
        databaseReference.child("userList").child(gameObj.getOpponent()).child("games").child(gameObj.getGameID()).setValue(gameObj);
    }




}
