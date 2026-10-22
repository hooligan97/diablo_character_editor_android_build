package com.example.d1editor;

import java.util.HashMap;
import java.util.Map;

public class ItemDatabase {
    private static final Map<Integer, String> items = new HashMap<>();

    static {
        items.put(68, "Miecz");
        items.put(2170, "Zbroja Skórzana");
        items.put(1, "Hełm");
        items.put(2, "Tarcza");
        items.put(3, "Topór");
        items.put(4, "Łuk");
        items.put(5, "Kostur");
        items.put(6, "Pierścień");
        items.put(7, "Amulet");
        items.put(8, "Mikstura");
    }

    public static String getItemName(int idx) {
        return items.getOrDefault(idx, "Nieznany przedmiot (" + idx + ")");
    }
}
