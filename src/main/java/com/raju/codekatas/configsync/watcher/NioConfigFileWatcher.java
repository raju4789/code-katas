package com.raju.codekatas.configsync.watcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class NioConfigFileWatcher implements ConfigFileWatcher {
    private static final Logger logger = LoggerFactory.getLogger(NioConfigFileWatcher.class);

    private final File configFile;
    public Thread watcherThread;
    private volatile boolean running = true;

    public NioConfigFileWatcher(File configFile) {
        this.configFile = configFile;
    }

    @Override
    public void start(Runnable onChange) {
        watcherThread = new Thread(() -> {
            Path path = configFile.toPath().getParent();
            String fileName = configFile.getName();
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                while (running) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changed = (Path) event.context();
                        if (changed.getFileName().toString().equals(fileName)) {
                            logger.info("Detected change in {}", fileName);
                            onChange.run();
                        }
                    }
                    key.reset();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.info("File watcher thread interrupted, shutting down.");
            } catch (IOException e) {
                logger.error("Error watching config file", e);
            }
        }, "ConfigFileWatcher");
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    @Override
    public void close() {
        running = false;
        if (watcherThread != null) {
            watcherThread.interrupt();
        }
        logger.info("ConfigFileWatcher stopped.");
    }
}
