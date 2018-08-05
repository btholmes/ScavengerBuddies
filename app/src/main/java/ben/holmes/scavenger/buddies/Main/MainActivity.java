package ben.holmes.scavenger.buddies.Main;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import ben.holmes.scavenger.buddies.App.Fragments.DrawerFragment;
import ben.holmes.scavenger.buddies.App.Model.CustomViewPager;
import ben.holmes.scavenger.buddies.App.Splash.LaunchActivity;
import ben.holmes.scavenger.buddies.App.Tools.Analytics;
import ben.holmes.scavenger.buddies.App.Tools.CircleTransform;
import ben.holmes.scavenger.buddies.App.Tools.Tools;
import ben.holmes.scavenger.buddies.App.Views.CustomBottomView;
import ben.holmes.scavenger.buddies.Database.Database;
import ben.holmes.scavenger.buddies.Friends.FriendsFragment;
import ben.holmes.scavenger.buddies.Games.Fragments.GameFragment;
import ben.holmes.scavenger.buddies.LeaderBoard.LeaderBoardFragment;
import ben.holmes.scavenger.buddies.Login.LoginHelpers.FacebookLogin;
import ben.holmes.scavenger.buddies.Main.adapter.PageFragmentAdapter;
import ben.holmes.scavenger.buddies.Messages.MessagesFragment;
import ben.holmes.scavenger.buddies.App.ScavengerActivity;
import ben.holmes.scavenger.buddies.Model.User;
import ben.holmes.scavenger.buddies.Train.dataCollectionActivity;


import ben.holmes.scavenger.buddies.R;


public class MainActivity extends ScavengerActivity {

    public static String OPEN_DRAWER_ON_START = "String, if present in bundle, don't show drawer on start";

    private View parentView;
    private static Context ctx;
    private DrawerLayout drawerLayout;
    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    private ActionBar actionBar;
    private Toolbar toolbar;

    private NavigationView navigationView;

    private PageFragmentAdapter adapter;

    private GameFragment gameFragment;
    private FriendsFragment friendsFragment;
    private MessagesFragment messagesFragment;
    private LeaderBoardFragment leaderBoardFragment;

    private int defaultHeight = -1;
    private Analytics analytics;

    public int[] tab_icons = {
            R.drawable.tab_feed,
            R.drawable.tab_friend,
            R.drawable.tab_chat,
            R.drawable.tab_profile
    };

    private CustomBottomView customBottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpAnalytics();

        this.ctx = this;
        Bundle bundle = getIntent().getExtras();
        boolean showDrawer = bundle.getBoolean(OPEN_DRAWER_ON_START, true);

        customBottomView = findViewById(R.id.custom_bottom_view);
        parentView = findViewById(android.R.id.content);
        drawerLayout = findViewById(R.id.drawer_layout);
        if(showDrawer)
            drawerLayout.openDrawer(Gravity.LEFT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setDefaultHeight();
        setPageListener();
        setUpViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        navigationView = findViewById(R.id.nav_view);
        setUpNavView();
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
                },100);
                return true;
            }
        });
//        setNavClickListener(navigationView);

        setupTabIcons();
        setupTabClick();


//        Tools.systemBarLolipop(this);
    }

    /**
     * Updates app to refelect new user content when FirebaseUser changes
     */
    public void updateApp(){
        setUpNavView();
    }

    private void setUpNavView(){
        navigationView.post(new Runnable() {
            @Override
            public void run() {
                View header = navigationView.getHeaderView(0);
                final ImageView mainImage = header.findViewById(R.id.mainImage);
                final TextView displayName = header.findViewById(R.id.displayName);
                final TextView nameHash = header.findViewById(R.id.nameHash);

                Database database = Database.getInstance();
                database.getUser(new Database.UserCallback() {
                    @Override
                    public void onComplete(User user) {
                        if(user.getDisplayName() == null || user.getDisplayName().length() <= 0){
                            displayName.setVisibility(View.GONE);
                            nameHash.setText(user.getNameHash());
                        }else{
                            displayName.setVisibility(View.VISIBLE);
                            displayName.setText(user.getDisplayName());
                            nameHash.setText(user.getNameHash());
                        }
                        if(user.getPhotoUrl() != null && user.getPhotoUrl().length() > 0)
                            Picasso.with(ctx).load(user.getPhotoUrl()).transform(new CircleTransform()).into(mainImage);
                        else
                            mainImage.setImageResource(R.drawable.ic_generic_account);
                    }
                }, FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });
    }

    public int getDefaultHeight(){
        return defaultHeight;
    }

    private void setDefaultHeight(){
        if(defaultHeight != -1) return;

        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(defaultHeight == -1)
                    defaultHeight = viewPager.getMeasuredHeight();
                viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public CustomViewPager getViewPager() {
        return viewPager;
    }

    public boolean isNormalHeight(){
        CustomViewPager viewPager = getViewPager();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) viewPager.getLayoutParams();
        return params.height == defaultHeight;
    }

    public void setViewPagerHeightNormal(){
        if(defaultHeight == -1 ) return;

        CustomViewPager viewPager = getViewPager();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) viewPager.getLayoutParams();
        params.height = defaultHeight;
        viewPager.setLayoutParams(params);
    }

    public void adjustViewPagerHeight(int amount){
        CustomViewPager viewPager = getViewPager();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) viewPager.getLayoutParams();
        params.height = defaultHeight + amount;
        viewPager.setLayoutParams(params);
    }

    private void setPageListener(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position != 1){
                    if(!isNormalHeight()){
                        setViewPagerHeightNormal();
                    }
                }

                if(isKeyboardVisible()){
                    FriendsFragment friendsFragment = (FriendsFragment) ((PageFragmentAdapter)viewPager.getAdapter()).getItem(1);
                    friendsFragment.hideKeyboard();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private boolean isKeyboardVisible(){
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void handleSelectedItem(MenuItem item){

        if(item.getTitle().equals("Make a Contribution")){
//            GooglePay googlePay = new GooglePay(this);
//            googlePay.connect();
         replaceFragment(new DrawerFragment());

        }else if(item.getTitle().equals("Sign Out")){
            signOut();
        }else if(item.getTitle().equals("Train")){
            Intent intent = new Intent(MainActivity.this, dataCollectionActivity.class);
            startActivity(intent);
        }else if(item.getTitle().equals("Invite Friends")){
            item.setChecked(false);
            customBottomView.show(MainActivity.this);
//            String message = "Text I want to share.";
//            Intent share = new Intent(Intent.ACTION_SEND);
//            share.setType("text/plain");
//            share.putExtra(Intent.EXTRA_TEXT, message);
//
//            startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
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

    public void setViewpagerCanSwipe(boolean canSwipe){
//        if(!canSwipe){
//            viewPager.setCanSwipe(canSwipe);
//        }
    }

    @Override
    public void onBackPressed() {
        if(customBottomView.getVisibility() == View.VISIBLE){
            customBottomView.hide(this);
            navigationView.bringToFront();
            drawerLayout.requestLayout();
            navigationView.getMenu().findItem(R.id.inviteFriends).setChecked(false);
        }else{
            super.onBackPressed();
        }
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
