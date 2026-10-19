package com.example.d1editor;

// Test automatycznego pushu z Termuxa.

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // Ustawiamy adapter dla ViewPager2
        viewPager.setAdapter(new MyPagerAdapter(this));

        // Łączymy TabLayout z ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Stats");
                    break;
                case 1:
                    tab.setText("Spells");
                    break;
                case 2:
                    tab.setText("Equipment");
                    break;
                case 3:
                    tab.setText("Character");
                    break;
            }
        }).attach();
    }

    // Adapter, który zarządza fragmentami dla każdej zakładki
    private static class MyPagerAdapter extends FragmentStateAdapter {
        public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new StatsFragment();
                case 1:
                    return new SpellsFragment();
                case 2:
                    return new EquipmentFragment();
                case 3:
                    return new CharacterFragment();
                default:
                    return new StatsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4; // Liczba zakładek
        }
    }
}
