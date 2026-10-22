package com.example.d1editor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.Locale;

public class ToolsFragment extends Fragment {

    private EditText etHue;
    private EditText etSaturation;
    private EditText etValue;
    private TextView tvJsonPreview;
    private Button btnConvert;
    private Button btnCopyCommand;
    private Button btnSaveToJson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, container, false);

        etHue = view.findViewById(R.id.etHue);
        etSaturation = view.findViewById(R.id.etSaturation);
        etValue = view.findViewById(R.id.etValue);
        tvJsonPreview = view.findViewById(R.id.tvJsonPreview);
        btnConvert = view.findViewById(R.id.btnConvertHsv);
        btnCopyCommand = view.findViewById(R.id.btnCopyCommand);
        btnSaveToJson = view.findViewById(R.id.btnSaveToJson);

        etHue.setText("210");
        etSaturation.setText("75");
        etValue.setText("90");

        btnConvert.setOnClickListener(v -> convertToJson());
        btnCopyCommand.setOnClickListener(v -> copyShizukuCommand());
        btnSaveToJson.setOnClickListener(v -> saveCurrentJson());

        convertToJson();
        return view;
    }

    private void convertToJson() {
        try {
            float h = parseFloat(etHue, 0f, 360f, 210f);
            float s = parseFloat(etSaturation, 0f, 100f, 75f);
            float v = parseFloat(etValue, 0f, 100f, 90f);

            JSONObject root = createHsvJson(h, s, v);
            String json = root.toString(2);
            tvJsonPreview.setText(json);
        } catch (Exception e) {
            tvJsonPreview.setText("{\n  \"error\": \"Błąd konwersji HSV\"\n}");
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
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        field.setText(String.valueOf(value));
        return value;
    }

    private JSONObject createHsvJson(float h, float s, float v) {
        float sNorm = clamp01(s / 100f);
        float vNorm = clamp01(v / 100f);
        float c = vNorm * sNorm;
        float x = c * (1f - Math.abs((h / 60f) % 2f - 1f));
        float m = vNorm - c;

        float r = 0f;
        float g = 0f;
        float b = 0f;

        if (h >= 0 && h < 60f) {
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
            payload.put("notes", "Bez root, bez Shizuku, gotowy do zapisu do pliku JSON.");
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
            Toast.makeText(getContext(), "JSON zapisany do aktywnego kontekstu aplikacji", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Błąd zapisu JSON", Toast.LENGTH_SHORT).show();
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
