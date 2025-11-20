package com.example.bunkmate.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bunkmate.R;
import com.example.bunkmate.ui.AddPeriodBottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EventFragment extends Fragment {

    private ViewPager2 vp;
    private EventPagerAdapter adapter;
    private TabLayout tabLayout;

    public EventFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event, container, false);

        tabLayout = v.findViewById(R.id.tabDays);
        vp = v.findViewById(R.id.vpDays);

        // Adapter should be from the same package (fragment)
        adapter = new EventPagerAdapter(requireActivity());
        vp.setAdapter(adapter);

        // Attach TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, vp,
                (tab, position) -> tab.setText(adapter.getTitle(position)))
                .attach();

        // Floating Action Button for adding period
        FloatingActionButton fab = v.findViewById(R.id.fabAddPeriod);
        fab.setOnClickListener(view -> {
            // Open AddPeriod bottom sheet (single argument constructor)
            AddPeriodBottomSheet bottomSheet = new AddPeriodBottomSheet(() -> {
                // Refresh the current fragment page
                int current = vp.getCurrentItem();
                vp.getAdapter().notifyItemChanged(current);
            });
            bottomSheet.show(getParentFragmentManager(), "add_period");
        });

        return v;
    }

}
