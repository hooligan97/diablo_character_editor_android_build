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

    private EditText[] etSpells = new EditText[10];
    private Button btnSaveSpells;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spells, container, false);

        for (int i = 0; i < 10; i++) {
            int resId = getResources().getIdentifier("etSpell" + i, "id", getContext().getPackageName());
            etSpells[i] = view.findViewById(resId);
        }
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
                    for (int i = 0; i < 10; i++) {
                        if (i < splLvl.length()) {
                            etSpells[i].setText(String.valueOf(splLvl.optInt(i, 0)));
                        }
                    }
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
            if (splLvl == null) splLvl = new JSONArray();

            for (int i = 0; i < 10; i++) {
                String text = etSpells[i].getText().toString();
                int level = text.isEmpty() ? 0 : Integer.parseInt(text);
                
                // Ensure array has enough elements
                while (splLvl.length() <= i) {
                    splLvl.put(0);
                }
                splLvl.put(i, level);
            }
            json.put("pSplLvl", splLvl);

            DataManager.currentJsonString = json.toString();
            Toast.makeText(getContext(), "Czary zapisane!", Toast.LENGTH_SHORT).show();
        } catch (JSONException | NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Błąd zapisu czarów!", Toast.LENGTH_SHORT).show();
        }
    }
}
