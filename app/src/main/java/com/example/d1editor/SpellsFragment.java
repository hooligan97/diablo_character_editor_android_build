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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpellsFragment extends Fragment {

    private EditText etFireball, etTeleport, etChainLightning, etFrozenOrb;
    private Button btnSaveSpells;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spells, container, false);

        etFireball = view.findViewById(R.id.etSpellFireball);
        etTeleport = view.findViewById(R.id.etSpellTeleport);
        etChainLightning = view.findViewById(R.id.etSpellChainLightning);
        etFrozenOrb = view.findViewById(R.id.etSpellFrozenOrb);
        btnSaveSpells = view.findViewById(R.id.btnSaveSpells);

        btnSaveSpells.setOnClickListener(v -> saveSpells());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DataManager.currentJsonString != null && !DataManager.currentJsonString.isEmpty()) {
            try {
                JSONObject json = new JSONObject(DataManager.currentJsonString);
                JSONArray splLvl = json.optJSONArray("pSplLvl");
                if (splLvl != null) {
                    if (splLvl.length() > 0) etFireball.setText(String.valueOf(splLvl.optInt(0)));
                    if (splLvl.length() > 1) etTeleport.setText(String.valueOf(splLvl.optInt(1)));
                    if (splLvl.length() > 2) etChainLightning.setText(String.valueOf(splLvl.optInt(2)));
                    if (splLvl.length() > 3) etFrozenOrb.setText(String.valueOf(splLvl.optInt(3)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSpells() {
        try {
            JSONObject json = new JSONObject();
            if (DataManager.currentJsonString != null && !DataManager.currentJsonString.isEmpty()) {
                json = new JSONObject(DataManager.currentJsonString);
            }
            JSONArray splLvl = json.optJSONArray("pSplLvl");
            if (splLvl == null) {
                splLvl = new JSONArray();
                for (int i = 0; i < 4; i++) splLvl.put(0);
                json.put("pSplLvl", splLvl);
            }
            
            splLvl.put(0, Integer.parseInt(etFireball.getText().toString()));
            splLvl.put(1, Integer.parseInt(etTeleport.getText().toString()));
            splLvl.put(2, Integer.parseInt(etChainLightning.getText().toString()));
            splLvl.put(3, Integer.parseInt(etFrozenOrb.getText().toString()));

            DataManager.currentJsonString = json.toString();
            Toast.makeText(getContext(), "Poziomy czarów zapisane!", Toast.LENGTH_SHORT).show();
        } catch (JSONException | NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Błąd zapisu poziomów czarów!", Toast.LENGTH_SHORT).show();
        }
    }
}
