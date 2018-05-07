package com.example.benholmes.scavengerbuddies.Main;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.benholmes.scavengerbuddies.App.ScavengerActivity;
import com.example.benholmes.scavengerbuddies.R;

public class MainActivity extends ScavengerActivity {

    private ViewPager viewPager;
    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentView = findViewById(android.R.id.content);
        viewPager = findViewById(R.id.viewpager);

    }
}
