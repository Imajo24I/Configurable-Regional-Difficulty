package net.configurable_regional_difficulty.majo24.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.configurable_regional_difficulty.majo24.ConfigurableRegionalDifficulty;
import net.configurable_regional_difficulty.majo24.config.selection.Selection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConfigManager {
    private final Config config;
    public final Path configPath;

    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .setPrettyPrinting()
                .registerTypeAdapter(Selection.class, new CustomSelectionSerializer())
                .create();


    public ConfigManager(Config config, Path path) {
        this.config = config;
        this.configPath = path;
    }

    public static Config getConfigFromFile(Path path) {
        if (!Files.exists(path)) {
            Config newConfig = new Config();
            try {
                ConfigurableRegionalDifficulty.LOGGER.info("Creating config file");
                Files.createFile(path);
                String jsonConfig = GSON.toJson(newConfig);
                Files.writeString(path, jsonConfig);
            } catch (IOException e) {
                ConfigurableRegionalDifficulty.LOGGER.error("Could not create config file", e);
            }
            return newConfig;
        } else {
            String jsonConfig;
            try {
                ConfigurableRegionalDifficulty.LOGGER.info("Reading config file");
                jsonConfig = new String(Files.readAllBytes(path));
                return GSON.fromJson(jsonConfig, Config.class);
            } catch (IOException e) {
                ConfigurableRegionalDifficulty.LOGGER.error("Could not read config file", e);
                return new Config();
            }
        }
    }

    public void saveConfig() {
        ConfigurableRegionalDifficulty.LOGGER.info("Saving config file");
        String jsonConfig = GSON.toJson(config);
        try {
            Files.writeString(configPath, jsonConfig);
        } catch (IOException e) {
            ConfigurableRegionalDifficulty.LOGGER.error("Could not save config file", e);
        }
    }

    public List<Selection> getSelectionList() {
        return config.getSelections();
    }

    public void addSelection(Selection selection) {
        config.addSelection(selection);
    }

    /**
    @return True if removal was successful
     */
    public boolean removeSelection(Selection selection) {
        System.out.println(selection.getSelection());

        boolean successfullyRemoved = config.removeSelection(selection);
        this.saveConfig();
        return successfullyRemoved;
    }
}
