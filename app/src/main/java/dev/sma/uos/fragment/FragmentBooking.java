package dev.sma.uos.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import dev.sma.uos.R;
import dev.sma.uos.adapter.ViewPagerAdapter;


public class FragmentBooking extends Fragment implements View.OnClickListener {

    TabLayout tabLayout;
    ViewPager viewpager;

    // define adapter for viewpager
    ViewPagerAdapter viewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking, container, false);


        tabLayout = view.findViewById(R.id.tabLayout);
        viewpager = view.findViewById(R.id.viewpager);

        tabLayout.addTab(tabLayout.newTab().setText("Online"));
        tabLayout.addTab(tabLayout.newTab().setText("Offline"));

        viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewpager.setAdapter(viewPagerAdapter);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        return view;
    }

    @Override
    public void onClick(View view) {

    }
}