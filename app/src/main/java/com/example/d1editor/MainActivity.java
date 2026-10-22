package com.example.d1editor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainActivity extends androidx.appcompat.app.AppCompatActivity {

    private androidx.viewpager2.widget.ViewPager2 viewPager;
    private com.google.android.material.tabs.TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager.setAdapter(new MyPagerAdapter(this));

        new com.google.android.material.tabs.TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
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
                case 4:
                    tab.setText("Backpack");
                    break;
                case 5:
                    tab.setText("Tools");
                    break;
                default:
                    tab.setText("Stats");
                    break;
            }
        }).attach();
    }

    private static class MyPagerAdapter extends FragmentStateAdapter {
        public MyPagerAdapter(@NonNull androidx.fragment.app.FragmentActivity fragmentActivity) {
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
                case 4:
                    return new BackpackFragment();
                case 5:
                    return new ToolsFragment();
                default:
                    return new StatsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 6;
        }
    }
}
