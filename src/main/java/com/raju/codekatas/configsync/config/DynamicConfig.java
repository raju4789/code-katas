package com.raju.codekatas.configsync.config;

import com.raju.codekatas.configsync.watcher.ConfigFileWatcher;
import com.raju.codekatas.configsync.watcher.NioConfigFileWatcher;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class DynamicConfig implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(DynamicConfig.class);

    private final File configFile;
    private final ConfigFileWatcher fileWatcher;
    private final AtomicReference<Config> cachedConfig = new AtomicReference<>();
    private volatile long lastModified;

    public DynamicConfig(File configFile, ConfigFileWatcher fileWatcher) {
        this.configFile = Objects.requireNonNull(configFile, "Config file must not be null");
        this.fileWatcher = Objects.requireNonNull(fileWatcher, "FileWatcher must not be null");
        this.cachedConfig.set(loadConfigOrDefault());
        this.lastModified = configFile.lastModified();
        this.fileWatcher.start(this::onFileChanged);
    }

    // Factory method for convenience
    public static DynamicConfig fromPath(String dir, String fileName) {
        File configFile = new File(dir, fileName);
        if (!configFile.exists()) {
            throw new IllegalArgumentException("Config file does not exist: " + configFile.getAbsolutePath());
        }
        return new DynamicConfig(configFile, new NioConfigFileWatcher(configFile));
    }

    public Config getConfig() {
        long currentLastModified = configFile.lastModified();
        if (currentLastModified > lastModified) {
            logger.info("Config file changed, reloading...");
            reloadConfig();
        }
        return cachedConfig.get();
    }

    private void reloadConfig() {
        Config newConfig = loadConfigOrDefault();
        cachedConfig.set(newConfig);
        lastModified = configFile.lastModified();
    }

    private Config loadConfigOrDefault() {
        try {
            Config config = ConfigFactory.parseFile(configFile).resolve();
            logger.info("Loaded config from {}", configFile.getAbsolutePath());
            return config;
        } catch (Exception e) {
            logger.error("Failed to load config, using last known good config", e);
            Config fallback = cachedConfig.get();
            return fallback != null ? fallback : ConfigFactory.empty();
        }
    }

    private void onFileChanged() {
        lastModified = 0; // Force reload on next access
    }

    @Override
    public void close() throws IOException {
        fileWatcher.close();
        logger.info("DynamicConfig closed.");
    }
}
