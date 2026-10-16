package com.example.diablo_character_editor;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 1. TEN BLOK ŁADUJE BIBLIOTEKĘ C++ (Musi być wewnątrz klasy)
    static {
        System.loadLibrary("native-lib");
    }

    // 2. TA LINIJKA DEKLARUJE FUNKCJĘ Z C++ (Musi być wewnątrz klasy)
    public native String stringFromJNI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Tworzymy prosty widok tekstowy
        TextView tv = new TextView(this);
        
        // Wywołujemy funkcję z C++ i wyświetlamy jej wynik
        tv.setText(stringFromJNI());
        
        // Ustawiamy widok jako główny ekran aplikacji
        setContentView(tv);
    }
}
