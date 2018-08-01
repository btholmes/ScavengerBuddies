package ben.holmes.scavenger.buddies.Friends.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ben.holmes.scavenger.buddies.Model.ShadowButton;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.R;

public class FriendDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_OBJCT = "User object";

    private User user;

    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView mainImage;

    private TextView name;
    private TextView email;

    private ShadowButton messageButton;
    private ShadowButton challengeButton;

    private ActionBar actionBar;


    // give preparation animation activity transition
    public static void navigate(Activity activity, User obj) {
        Intent intent = new Intent(activity, FriendDetailsActivity.class);
        intent.putExtra(EXTRA_OBJCT, obj);
        ActivityCompat.startActivity(activity, intent, null);
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        mainImage = findViewById(R.id.mainImage);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);

        messageButton = findViewById(R.id.messageButton);
        challengeButton = findViewById(R.id.challengeButton);

        user = (User) getIntent().getSerializableExtra(EXTRA_OBJCT);
        init();

        collapsingToolbar.setTitle(user.getNameHash());
        if (user.getPhotoUrl() != null && user.getPhotoUrl().length() > 0)
            Picasso.with(this).load(user.getPhotoUrl()).into(mainImage);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    public void init(){
        if(user.getDisplayName() == null || user.getDisplayName().length() <= 0){
            name.setVisibility(View.GONE);
            email.setText(user.getNameHash());
        }else{
            name.setVisibility(View.VISIBLE);
            name.setText(user.getDisplayName());
            email.setText(user.getNameHash());
        }

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_friend_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }else if(item.getItemId() == R.id.chat){

            return true;
        }else if(item.getItemId() == R.id.challenge){

            return true;
        }else if(item.getItemId() == R.id.block){

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
