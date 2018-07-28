package ben.holmes.scavenger.buddies.Train;

import android.os.AsyncTask;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by akiel on 3/24/2017.
 */

public class TagUpdate extends AsyncTask<Tag, Integer, Long> {

    protected Long doInBackground(final Tag... params) {
        Query ref = FirebaseDatabase.getInstance().getReference().child("TagData").child("TagList").orderByChild("tag").equalTo(params[0].getTag());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tag temp = dataSnapshot.child(params[0].getTag()).getValue(Tag.class);
                if(temp != null){
                    System.out.println(temp.getTag() + "repeat");
                    FirebaseDatabase.getInstance().getReference().child("TagData").child("TagList").child(temp.getTag()).child("consistency").setValue(temp.getConsistency() + 1);
                }else{
                    System.out.println(params[0].getTag() + "new");
                    FirebaseDatabase.getInstance().getReference().child("TagData").child("TagList").child(params[0].getTag()).setValue(params[0]);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        long totalSize = 0;
        return totalSize;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(Long result) {
    }
}
