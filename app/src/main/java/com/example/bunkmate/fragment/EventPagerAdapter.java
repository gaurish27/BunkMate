package com.example.bunkmate.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class EventPagerAdapter extends FragmentStateAdapter {

    private final String[] days = {"Mon","Tue","Wed","Thu","Fri","Sat"};

    public EventPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return DayPageFragment.newInstance(days[position]);
    }

    @Override
    public int getItemCount() {
        return days.length;
    }

    public String getTitle(int pos) { return days[pos]; }
}
