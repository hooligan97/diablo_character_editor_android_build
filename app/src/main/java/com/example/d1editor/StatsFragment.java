package com.example.d1editor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StatsFragment extends Fragment {

    private EditText etStr, etDex, etVit, etEne;
    private EditText etResFire, etResCold, etResLight, etResPoison;
    private Button btnSaveStats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        // Inicjalizacja pól
        etStr = view.findViewById(R.id.etStrength);
        etDex = view.findViewById(R.id.etDexterity);
        etVit = view.findViewById(R.id.etVitality);
        etEne = view.findViewById(R.id.etEnergy);
        etResFire = view.findViewById(R.id.etResFire);
        etResCold = view.findViewById(R.id.etResCold);
        etResLight = view.findViewById(R.id.etResLightning);
        etResPoison = view.findViewById(R.id.etResPoison);
        btnSaveStats = view.findViewById(R.id.btnSaveStats); // Dodamy ten przycisk w XML

        // Obsługa przycisku zapisu
        btnSaveStats.setOnClickListener(v -> {
            // Tutaj dodamy logikę zapisu do SharedPreferences lub pliku
            Toast.makeText(getContext(), "Statystyki zapisane!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
