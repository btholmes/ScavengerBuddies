package ben.holmes.scavenger.buddies.Notifications.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import ben.holmes.scavenger.buddies.App.Tools.CircleTransform;
import ben.holmes.scavenger.buddies.App.Tools.Prefs;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Login.LoginActivity;
import ben.holmes.scavenger.buddies.Login.LoginHelpers.LoginUtil;
import ben.holmes.scavenger.buddies.Model.ShadowButton;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.R;

public class NotificationActivity extends AppCompatActivity {

//    private View rootView;
    private Toolbar toolbar;
    private Context ctx;
    private ImageView image;
    private TextView nameHash;
    private TextView email;

    private RecyclerView recyclerView;
    private ShadowButton signOutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ctx = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        image = findViewById(R.id.image);
        nameHash = findViewById(R.id.nameHash);
        email = findViewById(R.id.email);

        recyclerView = findViewById(R.id.recycler_view);
        signOutButton = findViewById(R.id.btn_sign_out);
    }

//    @Override
//    public View onCreateView(String name, Context context, AttributeSet attrs) {
//        rootView = LayoutInflater.from(context).inflate(R.layout.activity_notification,  null);
//        toolbar = rootView.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        image = rootView.findViewById(R.id.image);
//        nameHash = rootView.findViewById(R.id.nameHash);
//        email = rootView.findViewById(R.id.email);
//
//        recyclerView = findViewById(R.id.recycler_view);
//        signOutButton = findViewById(R.id.btn_sign_out);
//        return rootView;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUp();
        setRecyclerView();
        setSignOut();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setUp(){
        Database.getInstance().getUser(new Database.UserCallback() {
            @Override
            public void onComplete(User user) {
                if(user.getPhotoUrl() != null && user.getPhotoUrl().length() > 0){
                    Picasso.with(ctx).load(user.getPhotoUrl()).transform(new CircleTransform()).into(image);
                }
                email.setText(user.getEmail());
                nameHash.setText(user.getNameHash());
            }
        }, FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private void setSignOut(){
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutButton.quickClick(new ShadowButton.QuickClick() {
                    @Override
                    public void onSuccess() {
                        LoginUtil.logOut();
                        Intent intent = new Intent(NotificationActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    private void setRecyclerView(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter(this));
    }



    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements CompoundButton.OnCheckedChangeListener{

        final private String[] preferences = new String[]{"Push Notifications", "Email Notifications", "Chat"};
        Prefs prefs;

        public Adapter(Context ctx){
            prefs = new Prefs(ctx);
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView text;
            SwitchCompat switchCompat;
            public ViewHolder(View v){
                super(v);
                text = v.findViewById(R.id.text);
                switchCompat = v.findViewById(R.id.pref_switch);
            }
        }

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preference, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
            holder.text.setText(preferences[position]);
            holder.switchCompat.setTag(preferences[position]);
            boolean startState = true;
            switch (preferences[position]){
                case "Push Notifications":
                    startState = prefs.getPushNotificationsEnabled();
                    break;
                case "Email Notifications":
                    startState = prefs.getEmailNotificationsEnabled();
                    break;
                case "Chat":
                    startState = prefs.getChatEnabled();
                    break;
            }
            holder.switchCompat.setChecked(startState);
            holder.switchCompat.setOnCheckedChangeListener(this);
        }


        @Override
        public int getItemCount() {
            return preferences.length;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getTag().toString()){
                case "Push Notifications":
                    prefs.setPushNotificationsEnabled(isChecked);
                    break;
                case "Email Notifications":
                    prefs.setEmailNotificationsEnabled(isChecked);
                    break;
                case "Chat":
                    prefs.setChatEnabled(isChecked);
                    break;
            }
        }
    }



}
