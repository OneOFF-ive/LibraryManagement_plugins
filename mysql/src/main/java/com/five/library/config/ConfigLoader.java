package com.five.library.config;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;

public class ConfigLoader {
    private final File configFile;
    private PluginConfig pluginConfig;

    public ConfigLoader(File configFile) {
        this.configFile = configFile;
    }

    public void loadConfig() throws IOException {
        String content = new String(Files.readAllBytes(configFile.toPath()));
        Gson gson = new Gson();
        var newConfig = gson.fromJson(content, PluginConfig.class);
        newConfig.setObservers(new ArrayList<>());
        if (pluginConfig == null) {
            pluginConfig = newConfig;
        }
        else if (!Objects.equals(newConfig, pluginConfig)) {
            pluginConfig.copy(newConfig);
            pluginConfig.notifyObservers();
        }
    }

    public File getConfigFile() {
        return configFile;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }
}
