package com.dappcloud.humanspace.AdapterClasses;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dappcloud.humanspace.User.Infrastructure.Fragments.ChatRequestFragment;
import com.dappcloud.humanspace.User.Infrastructure.Fragments.ChatsFragment;
import com.dappcloud.humanspace.User.Infrastructure.Fragments.OnlineUsersFragment;
import com.dappcloud.humanspace.User.Infrastructure.Fragments.UsersFragment;

public class ChatModulesAdapter extends FragmentStateAdapter {
    public ChatModulesAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {

            case 1:
                return new ChatRequestFragment();
            case 2:
                return new OnlineUsersFragment();
            case 3:
                return new UsersFragment();
        }

        return new ChatsFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
