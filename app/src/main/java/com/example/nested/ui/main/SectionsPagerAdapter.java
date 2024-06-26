package com.example.nested.ui.main;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.nested.ChatFragment;
import com.example.nested.ProfileFragment;
import com.example.nested.R;
import com.example.nested.SwipeFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[] {R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;
    ProfileFragment profileFragment;
    SwipeFragment swipeFragment;
    ChatFragment chatFragment = new ChatFragment();

    public SectionsPagerAdapter(Context context, FragmentManager fm, String uid) {
        super(fm);
        mContext = context;
        profileFragment = ProfileFragment.newInstance(uid);
        swipeFragment = SwipeFragment.newInstance(uid);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = swipeFragment;
                break;
            case 1:
                fragment = chatFragment;
                break;
            case 2:
                fragment = profileFragment;
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 3;
    }
}