package com.example.d1editor;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Locale;

public class ToolsFragment extends Fragment {

    private EditText etHue;
    private EditText etSaturation;
    private EditText etValue;
    private View colorPreview;
    private TextView tvJsonPreview;
    private Button btnConvert;
    private Button btnCopyCommand;
    private Button btnSaveToJson;
    private Button btnImportJson;
    private Button btnExportJson;
    private Button btnApplyToCharacter;

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        loadJsonFromUri(uri);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> fileCreateLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        exportJsonToUri(uri);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, container, false);

        etHue = view.findViewById(R.id.etHue);
        etSaturation = view.findViewById(R.id.etSaturation);
        etValue = view.findViewById(R.id.etValue);
        colorPreview = view.findViewById(R.id.colorPreview);
        tvJsonPreview = view.findViewById(R.id.tvJsonPreview);
        btnConvert = view.findViewById(R.id.btnConvertHsv);
        btnCopyCommand = view.findViewById(R.id.btnCopyCommand);
        btnSaveToJson = view.findViewById(R.id.btnSaveToJson);
        btnImportJson = view.findViewById(R.id.btnImportJson);
        btnExportJson = view.findViewById(R.id.btnExportJson);
        btnApplyToCharacter = view.findViewById(R.id.btnApplyToCharacter);

        etHue.setText("210");
        etSaturation.setText("75");
        etValue.setText("90");

        btnConvert.setOnClickListener(v -> refreshPreview());
        btnCopyCommand.setOnClickListener(v -> copyShizukuCommand());
        btnSaveToJson.setOnClickListener(v -> saveCurrentJson());
        btnImportJson.setOnClickListener(v -> openJsonFile());
        btnExportJson.setOnClickListener(v -> exportJsonFile());
        btnApplyToCharacter.setOnClickListener(v -> applyToCurrentCharacter());

        refreshPreview();
        return view;
    }

    private void refreshPreview() {
        try {
            float h = parseFloat(etHue, 0f, 360f, 210f);
            float s = parseFloat(etSaturation, 0f, 100f, 75f);
            float v = parseFloat(etValue, 0f, 100f, 90f);

            JSONObject payload = createHsvJson(h, s, v);
            String json = payload.toString(2);
            tvJsonPreview.setText(json);

            String hex = payload.optJSONObject("rgb").optString("hex", "#000000");
            colorPreview.setBackgroundColor(Color.parseColor(hex));
        } catch (Exception e) {
            tvJsonPreview.setText("{\n  \"error\": \"Błąd konwersji HSV\"\n}");
            colorPreview.setBackgroundColor(Color.BLACK);
            Toast.makeText(getContext(), "Błąd konwersji HSV", Toast.LENGTH_SHORT).show();
        }
    }

    private float parseFloat(EditText field, float min, float max, float fallback) {
        String text = field.getText() == null ? "" : field.getText().toString().trim();
        if (text.isEmpty()) {
            field.setText(String.valueOf(fallback));
            return fallback;
        }

        float value = Float.parseFloat(text);
        if (value < min) value = min;
        if (value > max) value = max;
        field.setText(String.valueOf(value));
        return value;
    }

    private JSONObject createHsvJson(float h, float s, float v) {
        float sNorm = clamp01(s / 100f);
        float vNorm = clamp01(v / 100f);
        float c = vNorm * sNorm;
        float x = c * (1f - Math.abs((h / 60f) % 2f - 1f));
        float m = vNorm - c;

        float r = 0f, g = 0f, b = 0f;
        if (h >= 0f && h < 60f) {
            r = c; g = x; b = 0f;
        } else if (h < 120f) {
            r = x; g = c; b = 0f;
        } else if (h < 180f) {
            r = 0f; g = c; b = x;
        } else if (h < 240f) {
            r = 0f; g = x; b = c;
        } else if (h < 300f) {
            r = x; g = 0f; b = c;
        } else {
            r = c; g = 0f; b = x;
        }

        int red = Math.round((r + m) * 255f);
        int green = Math.round((g + m) * 255f);
        int blue = Math.round((b + m) * 255f);

        JSONObject hsv = new JSONObject();
        JSONObject rgb = new JSONObject();
        JSONObject payload = new JSONObject();

        try {
            hsv.put("h", roundFloat(h));
            hsv.put("s", roundFloat(s));
            hsv.put("v", roundFloat(v));

            rgb.put("r", red);
            rgb.put("g", green);
            rgb.put("b", blue);
            rgb.put("hex", String.format(Locale.US, "#%02X%02X%02X", red, green, blue));

            payload.put("source", "hsv_to_json");
            payload.put("hsv", hsv);
            payload.put("rgb", rgb);
            payload.put("notes", "Gotowy do eksportu i zapisania do postaci JSON.");
            payload.put("shizukuCommand", buildShizukuCommand(payload));
            return payload;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildShizukuCommand(JSONObject payload) {
        return "echo '" + payload.toString().replace("'", "\\'") + "' > /sdcard/diablo_hsv.json";
    }

    private void copyShizukuCommand() {
        try {
            String jsonText = tvJsonPreview.getText().toString();
            JSONObject payload = new JSONObject(jsonText);
            String command = payload.optString("shizukuCommand", "echo '{\"source\":\"hsv_to_json\"}' > /sdcard/diablo_hsv.json");
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("diablo_hsv_command", command);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Polecenie Shizuku skopiowane", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Nie można skopiować polecenia", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCurrentJson() {
        try {
            String jsonText = tvJsonPreview.getText().toString();
            JSONObject payload = new JSONObject(jsonText);
            DataManager.currentJsonString = payload.toString();
            Toast.makeText(getContext(), "JSON zapisany do aktywnego kontekstu", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Błąd zapisu JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void openJsonFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        filePickerLauncher.launch(intent);
    }

    private void exportJsonFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "diablo_hsv.json");
        fileCreateLauncher.launch(intent);
    }

    private void applyToCurrentCharacter() {
        try {
            if (DataManager.selectedCharacterUri == null) {
                Toast.makeText(getContext(), "Wybierz najpierw plik postaci z zakładki Character", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject json = new JSONObject(tvJsonPreview.getText().toString());
            DataManager.currentJsonString = json.toString();
            try (OutputStream os = requireContext().getContentResolver().openOutputStream(DataManager.selectedCharacterUri, "wt")) {
                if (os != null) {
                    os.write(json.toString().getBytes());
                }
            }
            Toast.makeText(getContext(), "Zapisano JSON do aktywnej postaci", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Błąd zapisu do postaci", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadJsonFromUri(Uri uri) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requireContext().getContentResolver().openInputStream(uri)))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) builder.append(line);

            JSONObject json = new JSONObject(builder.toString());
            DataManager.currentJsonString = json.toString();

            if (json.has("hsv")) {
                JSONObject hsv = json.getJSONObject("hsv");
                etHue.setText(String.valueOf(hsv.optDouble("h", 210d)));
                etSaturation.setText(String.valueOf(hsv.optDouble("s", 75d)));
                etValue.setText(String.valueOf(hsv.optDouble("v", 90d)));
            }

            refreshPreview();
            Toast.makeText(getContext(), "Wczytano JSON z pliku", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Błąd wczytywania JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void exportJsonToUri(Uri uri) {
        try {
            String content = tvJsonPreview.getText().toString();
            try (OutputStream os = requireContext().getContentResolver().openOutputStream(uri, "wt")) {
                if (os != null) {
                    os.write(content.getBytes());
                }
            }
            Toast.makeText(getContext(), "Eksport JSON wykonany pomyślnie", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Błąd eksportu JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private float clamp01(float value) {
        if (value < 0f) return 0f;
        if (value > 1f) return 1f;
        return value;
    }

    private float roundFloat(float value) {
        return Math.round(value * 10f) / 10f;
    }
}
