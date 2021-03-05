package edu.fsu.cs.PokeBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

    private void requestCameraPermission() {
        String permission = Manifest.permission.CAMERA;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[2];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestCameraPermission();



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