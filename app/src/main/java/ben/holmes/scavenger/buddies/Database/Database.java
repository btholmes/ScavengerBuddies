package ben.holmes.scavenger.buddies.Database;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ben.holmes.scavenger.buddies.Model.Game;

public class Database {

    private Context ctx;
    private DatabaseReference databaseReference;

    public Database(Context ctx){
        this.ctx = ctx;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }



    public void addGameToFirebase(Game gameObj){

        databaseReference.child("userList").child(gameObj.getChallenger()).child("games").child(gameObj.getGameID()).setValue(gameObj);
        databaseReference.child("userList").child(gameObj.getOpponent()).child("games").child(gameObj.getGameID()).setValue(gameObj);

    }




}
