package com.example.keyboardoverlay;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonStorage {
    private static final Path cfg = Paths.get(System.getenv("APPDATA"), "KeyRemap", "settings.json");
    public static Settings load() {
        try {
            if (Files.exists(cfg)) {
                ObjectMapper om = new ObjectMapper();
                return om.readValue(cfg.toFile(), Settings.class);
            }
        } catch (Exception e) { e.printStackTrace(); }
        Settings s = new Settings();
        save(s);
        return s;
    }
    public static void save(Settings s) {
        try {
            if (!Files.exists(cfg.getParent())) Files.createDirectories(cfg.getParent());
            ObjectMapper om = new ObjectMapper();
            om.writerWithDefaultPrettyPrinter().writeValue(cfg.toFile(), s);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
