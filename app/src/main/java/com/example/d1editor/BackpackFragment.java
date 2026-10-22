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

public class BackpackFragment extends Fragment {

    private EditText[] etItems = new EditText[10];
    private Button btnSaveBackpack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backpack, container, false);

        for (int i = 0; i < 10; i++) {
            int id = getResources().getIdentifier("etItem" + i, "id", getContext().getPackageName());
            etItems[i] = view.findViewById(id);
        }
        btnSaveBackpack = view.findViewById(R.id.btnSaveBackpack);
        btnSaveBackpack.setOnClickListener(v -> saveBackpack());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DataManager.currentJsonString != null && !DataManager.currentJsonString.isEmpty()) {
            try {
                JSONObject json = new JSONObject(DataManager.currentJsonString);
                JSONArray invList = json.optJSONArray("InvList");
                if (invList != null) {
                    for (int i = 0; i < 10 && i < invList.length(); i++) {
                        JSONObject item = invList.getJSONObject(i);
                        int idx = item.optInt("idx", -1);
                        etItems[i].setText(ItemDatabase.getItemName(idx));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveBackpack() {
        try {
            JSONObject json = new JSONObject(DataManager.currentJsonString);
            JSONArray invList = json.optJSONArray("InvList");
            if (invList == null) invList = new JSONArray();

            for (int i = 0; i < 10; i++) {
                String itemName = etItems[i].getText().toString();
                int idx = ItemDatabase.getItemId(itemName);
                
                JSONObject item = (i < invList.length()) ? invList.getJSONObject(i) : new JSONObject();
                item.put("idx", idx);
                
                if (i >= invList.length()) {
                    invList.put(item);
                }
            }
            json.put("InvList", invList);
            DataManager.currentJsonString = json.toString();
            Toast.makeText(getContext(), "Plecak zapisany!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Błąd zapisu plecaka!", Toast.LENGTH_SHORT).show();
        }
    }
}
