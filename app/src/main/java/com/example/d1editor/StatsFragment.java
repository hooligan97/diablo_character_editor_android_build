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
import org.json.JSONException;
import org.json.JSONObject;

public class StatsFragment extends Fragment {

    private EditText etStr, etDex, etVit, etEne;
    private EditText etResFire, etResCold, etResLight, etResPoison;
    private Button btnSaveStats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        etStr = view.findViewById(R.id.etStrength);
        etDex = view.findViewById(R.id.etDexterity);
        etVit = view.findViewById(R.id.etVitality);
        etEne = view.findViewById(R.id.etEnergy);
        etResFire = view.findViewById(R.id.etResFire);
        etResCold = view.findViewById(R.id.etResCold);
        etResLight = view.findViewById(R.id.etResLightning);
        etResPoison = view.findViewById(R.id.etResPoison);
        btnSaveStats = view.findViewById(R.id.btnSaveStats);

        btnSaveStats.setOnClickListener(v -> saveStats());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DataManager.currentJsonString != null && !DataManager.currentJsonString.isEmpty()) {
            try {
                JSONObject json = new JSONObject(DataManager.currentJsonString);
                etStr.setText(json.optString("pBaseStr", ""));
                etDex.setText(json.optString("pBaseDex", ""));
                etVit.setText(json.optString("pBaseVit", ""));
                etEne.setText(json.optString("pBaseMag", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveStats() {
        try {
            JSONObject json = new JSONObject();
            if (DataManager.currentJsonString != null && !DataManager.currentJsonString.isEmpty()) {
                json = new JSONObject(DataManager.currentJsonString);
            }
            json.put("pBaseStr", Integer.parseInt(etStr.getText().toString()));
            json.put("pBaseDex", Integer.parseInt(etDex.getText().toString()));
            json.put("pBaseVit", Integer.parseInt(etVit.getText().toString()));
            json.put("pBaseMag", Integer.parseInt(etEne.getText().toString()));

            DataManager.currentJsonString = json.toString();
            Toast.makeText(getContext(), "Statystyki zapisane!", Toast.LENGTH_SHORT).show();
        } catch (JSONException | NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Błąd zapisu statystyk!", Toast.LENGTH_SHORT).show();
        }
    }
}
