package dev.sma.uos.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import dev.sma.uos.fragment.FragmentOfflineDoctor;
import dev.sma.uos.fragment.FragmentOnlineDoctor;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    int tabcount;

    public ViewPagerAdapter(FragmentManager supportFragmentManager, int tabCount) {
        super(supportFragmentManager);

        this.tabcount = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                FragmentOnlineDoctor fragmentActive = new FragmentOnlineDoctor();
                return fragmentActive;
            case 1:
                FragmentOfflineDoctor fragmentPast = new FragmentOfflineDoctor();
                return fragmentPast;
            default:
                fragmentActive = new FragmentOnlineDoctor();
                return fragmentActive;
        }
    }

    @Override
    public int getCount() {
        return tabcount;
    }
}
