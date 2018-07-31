package ben.holmes.scavenger.buddies.Messages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.R;


import ben.holmes.scavenger.buddies.App.ScavengerFragment;

/**
 * Created by benholmes on 5/7/18.
 */

public class MessagesFragment extends ScavengerFragment {

//    private DatabaseReference reference;
//    private FirebaseUser user;

    public static final String TAG_NAME = "Messages";
    public static final int TOOLBAR_COLOR = R.color.colorPrimary;
    private View view;
    private TextView messagesText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        reference = ((ScavengerActivity)getActivity()).getDatabaseReference();
//        user = ((ScavengerActivity)getActivity()).getFirebaseUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_messages, container, false);
        messagesText = view.findViewById(R.id.messagesText);
        setMessagesList();
        return view;
    }

    public interface MessagesCallback{
        void onComplete(boolean hasMessages);
    }

    private void hasMessages(final MessagesCallback callback){
        DatabaseReference reference = ((ScavengerActivity)getActivity()).getDatabaseReference();
        FirebaseUser user = ((ScavengerActivity)getActivity()).getFirebaseUser();

        reference.child("userList").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("messages")){
                    callback.onComplete(true);
                }else{
                    callback.onComplete(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setMessagesList(){
        hasMessages(new MessagesCallback() {
            @Override
            public void onComplete(boolean hasMessages) {
                if(hasMessages){
                    showMessages();
                }else{
                    messagesText.setText("No messages yet");
                }
            }
        });
    }

    private void showMessages(){

    }

    @Override
    public int getToolbarColor() {
        return TOOLBAR_COLOR;
    }

    @Override
    public String getToolbarTitle() {
        return TAG_NAME;
    }
}
