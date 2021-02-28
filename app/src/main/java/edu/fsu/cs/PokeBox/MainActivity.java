package edu.fsu.cs.PokeBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    // list of icons used for page navigation bar
    private final int[] icons = {
            R.drawable.camera_tab_icon,
            R.drawable.cards_tab_icon,
            R.drawable.watchlist_tab_icon,
            R.drawable.settings_tab_icon
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.navigation_tabs);

        // setup view pager
        viewPager.setAdapter(new ViewPagerFragmentAdaptor(this));

        // attaching tab mediator
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setIcon(icons[position])).attach();
    }

    // Fragment Adaptor class for View Pager
    private class ViewPagerFragmentAdaptor extends FragmentStateAdapter {

        public ViewPagerFragmentAdaptor(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new ScannerFragment();
                case 1:
                    return new CardViewerFragment();
                case 2:
                    return new WatchlistFragment();
                case 3:
                    return new SettingsFragment();
            }
            return new CardViewerFragment();
        }

        @Override
        public int getItemCount() {
            return icons.length;
        }
    }
}