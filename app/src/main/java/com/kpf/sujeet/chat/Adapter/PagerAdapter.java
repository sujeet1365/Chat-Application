package com.kpf.sujeet.chat.Adapter;

import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kpf.sujeet.chat.Fragments.ChatListFragment;
import com.kpf.sujeet.chat.Fragments.ContactListFragment;

/**
 * Created by SUJEET on 1/7/2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    String tabtitles[] = new String[]{"Chat","Contacts"};
    public PagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }
    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ChatListFragment();
            case 1:
                return new ContactListFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
