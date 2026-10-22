package com.example.d1editor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class CharacterFragment extends Fragment {
    private EditText etName, etLevel, etGold;
    private Button btnSelectFile, btnSaveFile, btnExportFile;
    private Uri selectedFileUri;

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    DataManager.selectedCharacterUri = selectedFileUri;
                    readFile(selectedFileUri);
                }
            }
    );

    private final ActivityResultLauncher<Intent> fileCreateLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        selectedFileUri = uri;
                        DataManager.selectedCharacterUri = uri;
                        saveFileToUri(uri);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_character, container, false);
        etName = view.findViewById(R.id.etCharName);
        etLevel = view.findViewById(R.id.etCharLevel);
        etGold = view.findViewById(R.id.etCharGold);
        btnSelectFile = view.findViewById(R.id.btnSelectFile);
        btnSaveFile = view.findViewById(R.id.btnSaveFile);
        btnExportFile = view.findViewById(R.id.btnExportFile);

        btnSelectFile.setOnClickListener(v -> openCharacterFile());
        btnSaveFile.setOnClickListener(v -> saveFile());
        btnExportFile.setOnClickListener(v -> exportCharacterFile());

        if (DataManager.selectedCharacterUri != null) {
            selectedFileUri = DataManager.selectedCharacterUri;
        }

        if (DataManager.currentJsonString != null && !DataManager.currentJsonString.isEmpty()) {
            try {
                JSONObject json = new JSONObject(DataManager.currentJsonString);
                etName.setText(json.optString("pName", ""));
                etLevel.setText(json.optString("pLevel", ""));
                etGold.setText(json.optString("pGold", ""));
            } catch (Exception ignored) {
            }
        }

        return view;
    }

    private void openCharacterFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        filePickerLauncher.launch(intent);
    }

    private void exportCharacterFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "diablo_character.json");
        fileCreateLauncher.launch(intent);
    }

    private void readFile(Uri uri) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requireContext().getContentResolver().openInputStream(uri)))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) builder.append(line);
            DataManager.currentJsonString = builder.toString();
            JSONObject json = new JSONObject(DataManager.currentJsonString);
            etName.setText(json.optString("pName", ""));
            etLevel.setText(json.optString("pLevel", ""));
            etGold.setText(json.optString("pGold", ""));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Błąd odczytu pliku", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFile() {
        if (selectedFileUri == null && DataManager.selectedCharacterUri == null) {
            exportCharacterFile();
            return;
        }

        Uri uri = selectedFileUri != null ? selectedFileUri : DataManager.selectedCharacterUri;
        if (uri != null) {
            saveFileToUri(uri);
            return;
        }

        Toast.makeText(getContext(), "Wybierz lub utwórz plik postaci", Toast.LENGTH_SHORT).show();
    }

    private void saveFileToUri(Uri uri) {
        try {
            JSONObject json = new JSONObject();
            if (DataManager.currentJsonString != null && !DataManager.currentJsonString.isEmpty()) {
                json = new JSONObject(DataManager.currentJsonString);
            }
            json.put("pName", etName.getText().toString());
            json.put("pLevel", etLevel.getText().toString());
            json.put("pGold", etGold.getText().toString());

            try (OutputStream os = requireContext().getContentResolver().openOutputStream(uri, "wt")) {
                if (os != null) {
                    os.write(json.toString().getBytes());
                }
            }

            DataManager.currentJsonString = json.toString();
            selectedFileUri = uri;
            DataManager.selectedCharacterUri = uri;
            Toast.makeText(getContext(), "Zapisano do postaci JSON", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Błąd zapisu pliku postaci", Toast.LENGTH_SHORT).show();
        }
    }
}
