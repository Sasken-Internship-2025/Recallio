package com.example.saskenproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.saskenproject.OnboardingItem;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout layoutDots;
    private OnboardingAdapter onboardingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        layoutDots = findViewById(R.id.layoutDots);

        setupOnboardingItems();
        viewPager.setAdapter(onboardingAdapter);
        addDots(0);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                addDots(position);
            }
        });

        onboardingAdapter.setOnStartClickListener(() -> {
            // Go to login screen when "Get Started" is pressed
            startActivity(new Intent(OnboardingActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        onboardingItems.add(new OnboardingItem(
                "Welcome to Recallio.",
                "We are built to help keep track of all you want us to. Allow us to help you recall all you need to.",
                "a1.json"));

        onboardingItems.add(new OnboardingItem(
                "Multiple devices? We got you!",
                "We sync data across your devices for a seamless experience so you don't worry about a thing.",
                "a2.json"));

        onboardingItems.add(new OnboardingItem(
                "Let's get you going!",
                " ",
                "a3.json"));

        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }

    private void addDots(int currentPage) {
        layoutDots.removeAllViews();
        TextView[] dots = new TextView[3];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("•");
            dots[i].setTextSize(35);
            dots[i].setTextColor(ContextCompat.getColor(this,
                    i == currentPage ? R.color.gray : R.color.darkgray)); // make sure R.color.brown exists
            layoutDots.addView(dots[i]);
        }
    }
}
