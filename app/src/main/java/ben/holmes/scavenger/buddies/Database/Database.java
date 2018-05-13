package ben.holmes.scavenger.buddies.Database;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {

    private Context ctx;
    private DatabaseReference databaseReference;

    public Database(Context ctx){
        this.ctx = ctx;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }




}
