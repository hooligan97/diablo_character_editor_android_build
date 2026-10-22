package com.example.d1editor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class ItemDatabase {
    private static final Map<Integer, String> ITEMS = new LinkedHashMap<>();
    private static final Map<String, Integer> ITEM_LOOKUP = new HashMap<>();

    static {
        register(0, "Brak", "Empty", "None");
        register(1, "Hełm", "Helmet");
        register(2, "Tarcza", "Shield");
        register(3, "Topór", "Axe");
        register(4, "Łuk", "Bow");
        register(5, "Kostur", "Staff");
        register(6, "Pierścień", "Ring");
        register(7, "Amulet", "Amulet");
        register(8, "Mikstura", "Potion");
        register(9, "Eliksir", "Elixir");
        register(10, "Miecz", "Sword");
        register(11, "Miecz długi", "Long sword");
        register(12, "Miecz krótki", "Short sword");
        register(13, "Dagger", "Sztylet", "Dagger");
        register(14, "Zbroja skórzana", "Leather armor");
        register(15, "Zbroja", "Armor");
        register(16, "Bardzo silna zbroja", "Strong armor");
        register(17, "Płaszcz", "Cloak");
        register(18, "Tarcza z drewna", "Wooden shield");
        register(19, "Płonąca pochodnia", "Torch");
        register(20, "Mikstura zdrowia", "Healing potion");
        register(21, "Mikstura many", "Mana potion");
        register(22, "Mikstura lecznicza", "Healing potion");
        register(23, "Skrytka", "Chest");
        register(24, "Zwoj teleportacji", "Town portal scroll");
        register(25, "Zwój identyfikacji", "Identify scroll");
        register(26, "Płonący czar", "Fire spell");
        register(27, "Lodowy czar", "Cold spell");
        register(28, "Błyskawiczny czar", "Lightning spell");
        register(29, "Runa", "Rune");
        register(30, "Klucz", "Key");
        register(31, "Moneta", "Coin");
        register(32, "Miecz niebiański", "Holy sword");
        register(33, "Przepis na miksturę", "Potion recipe");
        register(34, "Skrzynia", "Crate");
        register(35, "Zbroja żelazna", "Iron armor");
        register(36, "Hełm żelazny", "Iron helm");
        register(37, "Żelazna tarcza", "Iron shield");
        register(38, "Łuk elfi", "Elven bow");
        register(39, "Kostur mądrości", "Wizard staff");
        register(40, "Bicz", "Whip");
        register(41, "Sierp", "Scythe");
        register(42, "Kostur ognia", "Flame staff");
        register(43, "Tylko puste", "Empty slot");
        register(44, "Łódka", "Boat");
        register(45, "Lampa", "Lamp");
        register(46, "Księga", "Book");
        register(47, "Puste", "Empty");
        register(48, "Skarb", "Treasure");
        register(49, "Kufel", "Cup");
        register(50, "Mapa", "Map");
        register(68, "Hełm");
        register(101, "Zwój town portal");
        register(103, "Zwój identyfikacji");
        register(152, "Miecz krótki");
        register(2170, "Zbroja skórzana");
        register(65535, "Puste", "Empty");
    }

    private static void register(int id, String... names) {
        if (names == null || names.length == 0) {
            return;
        }

        String displayName = names[0];
        ITEMS.put(id, displayName);

        for (String name : names) {
            if (name == null || name.trim().isEmpty()) {
                continue;
            }
            ITEM_LOOKUP.put(normalize(name), id);
        }
    }

    public static String getItemName(int idx) {
        return ITEMS.getOrDefault(idx, "Nieznany przedmiot (" + idx + ")");
    }

    public static int getItemId(String name) {
        if (name == null) {
            return 65535;
        }

        String normalized = normalize(name);
        if (normalized.isEmpty()) {
            return 65535;
        }

        Integer id = ITEM_LOOKUP.get(normalized);
        if (id != null) {
            return id;
        }

        for (Map.Entry<Integer, String> entry : ITEMS.entrySet()) {
            if (normalize(entry.getValue()).equals(normalized)) {
                return entry.getKey();
            }
        }

        return 65535;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        String result = value.toLowerCase(Locale.US);
        result = result.replace('ą', 'a')
                .replace('ć', 'c')
                .replace('ę', 'e')
                .replace('ł', 'l')
                .replace('ń', 'n')
                .replace('ó', 'o')
                .replace('ś', 's')
                .replace('ż', 'z')
                .replace('ź', 'z');

        return result.trim();
    }
}
