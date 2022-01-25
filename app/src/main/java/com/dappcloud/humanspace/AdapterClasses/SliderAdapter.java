package com.dappcloud.humanspace.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.dappcloud.humanspace.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    int images[] = {
            R.drawable.social_space,
            R.drawable.business_space,
            R.drawable.global_search,
    };

    int titles[] = {
            R.string.first_title,
            R.string.second_title,
            R.string.third_title,
    };

    int decs[] = {
            R.string.first_desc,
            R.string.second_desc,
            R.string.third_desc,
    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_onboarding,container,false);

        ImageView sliderImage = view.findViewById(R.id.slider_image);
        TextView sliderTitle = view.findViewById(R.id.slider_title);
        TextView sliderDesc = view.findViewById(R.id.slider_desc);

        sliderImage.setImageResource(images[position]);
        sliderTitle.setText(titles[position]);
        sliderDesc.setText(decs[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
