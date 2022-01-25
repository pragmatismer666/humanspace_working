package com.dappcloud.humanspace.User.Infrastructure.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dappcloud.humanspace.AdapterClasses.SliderAdapter;
import com.dappcloud.humanspace.R;
import com.dappcloud.humanspace.User.SigninSignup.UsersStartUpScreen;

public class OnBoardingActivity extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    Button getStarted;
    Animation animation;
    int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        getStarted = findViewById(R.id.get_started_btn);

        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
    }

    public void skip(View view) {
        startActivity(new Intent(getApplicationContext(), UsersStartUpScreen.class));
    }

    public void start(View view){
        startActivity(new Intent(getApplicationContext(), UsersStartUpScreen.class)); //UserDashboardActivity
    }

    public void next(View view){
        viewPager.setCurrentItem(currentPosition + 1);
    }

    private void addDots(int position){

        dots = new TextView[4];
        dotsLayout.removeAllViews();

        for (int i=0; i<dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.red));
        }

    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPosition = position;

            if (position == 0) {
                animation = AnimationUtils.loadAnimation(OnBoardingActivity.this,R.anim.bottom_anim);
                getStarted.setAnimation(animation);
                getStarted.setVisibility(View.VISIBLE);
            } else if (position == 1) {
                animation = AnimationUtils.loadAnimation(OnBoardingActivity.this,R.anim.bottom_anim);
                getStarted.setAnimation(animation);
                getStarted.setVisibility(View.VISIBLE);
            } else if (position == 2) {
                animation = AnimationUtils.loadAnimation(OnBoardingActivity.this,R.anim.bottom_anim);
                getStarted.setAnimation(animation);
                getStarted.setVisibility(View.VISIBLE);
            } else {
                animation = AnimationUtils.loadAnimation(OnBoardingActivity.this,R.anim.bottom_anim);
                getStarted.setAnimation(animation);
                getStarted.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}

