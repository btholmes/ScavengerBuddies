package ben.holmes.scavenger.buddies.Main;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import ben.holmes.scavenger.buddies.App.Fragments.DrawerFragment;
import ben.holmes.scavenger.buddies.App.Splash.LaunchActivity;
import ben.holmes.scavenger.buddies.App.Tools.Analytics;
import ben.holmes.scavenger.buddies.App.Tools.Tools;
import ben.holmes.scavenger.buddies.Friends.FriendsFragment;
import ben.holmes.scavenger.buddies.Games.Fragments.GameFragment;
import ben.holmes.scavenger.buddies.LeaderBoard.LeaderBoardFragment;
import ben.holmes.scavenger.buddies.Login.LoginHelpers.FacebookLogin;
import ben.holmes.scavenger.buddies.Main.adapter.PageFragmentAdapter;
import ben.holmes.scavenger.buddies.Messages.MessagesFragment;
import ben.holmes.scavenger.buddies.App.ScavengerActivity;

import ben.holmes.scavenger.buddies.R;


public class MainActivity extends ScavengerActivity {

    private View parentView;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ActionBar actionBar;
    private Toolbar toolbar;

    private NavigationView navigationView;

    private PageFragmentAdapter adapter;

    private GameFragment gameFragment;
    private FriendsFragment friendsFragment;
    private MessagesFragment messagesFragment;
    private LeaderBoardFragment leaderBoardFragment;

    private Analytics analytics;

    public int[] tab_icons = {
            R.drawable.tab_feed,
            R.drawable.tab_friend,
            R.drawable.tab_chat,
            R.drawable.ic_form_people
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpAnalytics();

        parentView = findViewById(android.R.id.content);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(Gravity.LEFT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        viewPager = findViewById(R.id.viewpager);
        setUpViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                handleSelectedItem(item);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawer(Gravity.LEFT);
                    }
                },200);

                return true;
            }
        });
//        setNavClickListener(navigationView);

        setupTabIcons();
        setupTabClick();

        getFacebookFriends();

//        Tools.systemBarLolipop(this);
    }

    private void getFacebookFriends(){
        FacebookLogin facebookLogin = new FacebookLogin(this, this);
        facebookLogin.getUserFriends();
    }

    private void handleSelectedItem(MenuItem item){

        if(item.getTitle().equals("Make a Contribution")){
//            GooglePay googlePay = new GooglePay(this);
//            googlePay.connect();
         replaceFragment(new DrawerFragment());

        }else if(item.getTitle().equals("Sign Out")){
            signOut();
        }

    }

    private void replaceFragment(Fragment fragment) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.fade_in, 0);
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    private void setUpAnalytics(){
        analytics = new Analytics(this);
        analytics.logNewGame("Test Challenger", "Test Defender");
    }

    private void setUpViewPager(ViewPager viewPager){

        adapter = new PageFragmentAdapter(getSupportFragmentManager());
        if (gameFragment == null) { gameFragment = new GameFragment(); }
        if (friendsFragment == null) { friendsFragment = new FriendsFragment(); }
        if (messagesFragment == null) { messagesFragment = new MessagesFragment(); }
        if (leaderBoardFragment == null) { leaderBoardFragment = new LeaderBoardFragment(); }
        adapter.addFragment(gameFragment, getString(R.string.tab_game));
        adapter.addFragment(friendsFragment, getString(R.string.tab_friends));
        adapter.addFragment(messagesFragment, getString(R.string.tab_messages));
        adapter.addFragment(leaderBoardFragment, getString(R.string.tab_leaderboard));
        viewPager.setAdapter(adapter);
    }

    private void setTabNotification(int value, int tabIcon){
        FrameLayout tabTwo = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        DisplayMetrics dm = tabTwo.getResources().getDisplayMetrics();


        TextView text = (TextView) tabTwo.findViewById(R.id.text1);
        text.setText("2");

        ImageView icon = (ImageView) tabTwo.findViewById(R.id.icon);
        icon.setBackgroundResource(tabIcon);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tab_icons[0]);

        /**
         * Tab Two
         */
        setTabNotification(2, tab_icons[1]);


//        tabLayout.getTabAt(2).setCustomView(tabThree);
        tabLayout.getTabAt(2).setIcon(tab_icons[2]);

        tabLayout.getTabAt(3).setIcon(tab_icons[3]);

    }

    private void setupTabClick() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
                actionBar.setTitle(adapter.getTitle(position));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void setNavClickListener(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                item.setChecked(true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerLayout.closeDrawer(Gravity.LEFT);
                    }
                },200);

                return true;
            }
        });
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        if(LoginManager.getInstance() != null)
            LoginManager.getInstance().logOut();

        goToLaunch();

    }

    private void goToLaunch(){
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
