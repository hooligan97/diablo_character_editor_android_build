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
import org.json.JSONObject;

public class EquipmentFragment extends Fragment {

    private EditText etGold, etHelmet, etArmor, etWeapon, etShield;
    private Button btnSaveEquipment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipment, container, false);
        etGold = view.findViewById(R.id.etGold);
        etHelmet = view.findViewById(R.id.etHelmet);
        etArmor = view.findViewById(R.id.etArmor);
        etWeapon = view.findViewById(R.id.etWeapon);
        etShield = view.findViewById(R.id.etShield);
        btnSaveEquipment = view.findViewById(R.id.btnSaveEquipment);
        
        btnSaveEquipment.setOnClickListener(v -> saveData());
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        try {
            JSONObject json = new JSONObject(DataManager.currentJsonString);
            etGold.setText(String.valueOf(json.optInt("pGold")));

            JSONArray invBody = json.optJSONArray("InvBody");
            if (invBody != null && invBody.length() >= 4) {
                etHelmet.setText(ItemDatabase.getItemName(invBody.getJSONObject(0).optInt("idx")));
                etArmor.setText(ItemDatabase.getItemName(invBody.getJSONObject(1).optInt("idx")));
                etWeapon.setText(ItemDatabase.getItemName(invBody.getJSONObject(2).optInt("idx")));
                etShield.setText(ItemDatabase.getItemName(invBody.getJSONObject(3).optInt("idx")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try {
            JSONObject json = new JSONObject(DataManager.currentJsonString);
            json.put("pGold", Integer.parseInt(etGold.getText().toString()));

            // Equipment fields are now read-only, so we only save gold.
            // If they need to be editable later, we'd need to map names back to IDs.

            DataManager.currentJsonString = json.toString();
            Toast.makeText(getContext(), "Zapisano złoto", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Błąd zapisu", Toast.LENGTH_SHORT).show();
        }
    }
}
