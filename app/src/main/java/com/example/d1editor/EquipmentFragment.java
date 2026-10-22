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
            if (invBody != null) {
                // Diablo 1 InvBody mapping:
                // Index 0: Helmet, 1: Armor, 2: Weapon, 3: Shield
                if (invBody.length() > 0) etHelmet.setText(ItemDatabase.getItemName(invBody.getJSONObject(0).optInt("idx")));
                if (invBody.length() > 1) etArmor.setText(ItemDatabase.getItemName(invBody.getJSONObject(1).optInt("idx")));
                if (invBody.length() > 2) etWeapon.setText(ItemDatabase.getItemName(invBody.getJSONObject(2).optInt("idx")));
                if (invBody.length() > 3) etShield.setText(ItemDatabase.getItemName(invBody.getJSONObject(3).optInt("idx")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try {
            JSONObject json = new JSONObject(DataManager.currentJsonString);
            json.put("pGold", Integer.parseInt(etGold.getText().toString()));

            JSONArray invBody = json.optJSONArray("InvBody");
            if (invBody == null) invBody = new JSONArray();

            // Ensure we have at least 4 items
            while (invBody.length() < 4) {
                invBody.put(new JSONObject().put("idx", 65535));
            }

            // Update items if they exist and are valid (based on text input)
            invBody.getJSONObject(0).put("idx", ItemDatabase.getItemId(etHelmet.getText().toString()));
            invBody.getJSONObject(1).put("idx", ItemDatabase.getItemId(etArmor.getText().toString()));
            invBody.getJSONObject(2).put("idx", ItemDatabase.getItemId(etWeapon.getText().toString()));
            invBody.getJSONObject(3).put("idx", ItemDatabase.getItemId(etShield.getText().toString()));

            json.put("InvBody", invBody);
            
            DataManager.currentJsonString = json.toString();
            Toast.makeText(getContext(), "Zapisano wyposażenie i złoto", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Błąd zapisu", Toast.LENGTH_SHORT).show();
        }
    }
}
