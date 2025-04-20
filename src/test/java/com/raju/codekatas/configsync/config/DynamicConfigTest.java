package com.raju.codekatas.configsync.config;

import com.raju.codekatas.configsync.watcher.ConfigFileWatcher;
import com.typesafe.config.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DynamicConfigTest {

    private File tempFile;
    private DynamicConfig dynamicConfig;
    private ConfigFileWatcher mockWatcher;

    @AfterEach
    void tearDown() throws Exception {
        if (dynamicConfig != null) {
            dynamicConfig.close();
        }
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void testLoadsConfigOnInit() throws Exception {
        tempFile = File.createTempFile("test", ".conf");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("foo = 123");
        }
        mockWatcher = mock(ConfigFileWatcher.class);

        dynamicConfig = new DynamicConfig(tempFile, mockWatcher);
        Config config = dynamicConfig.getConfig();

        assertEquals(123, config.getInt("foo"));
        verify(mockWatcher, times(1)).start(any());
    }

    @Test
    void testReloadsConfigOnFileChange() throws Exception {
        tempFile = File.createTempFile("test", ".conf");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("bar = 1");
        }
        AtomicBoolean onChangeCalled = new AtomicBoolean(false);
        ConfigFileWatcher fakeWatcher = new ConfigFileWatcher() {
            private Runnable onChange;

            @Override
            public void start(Runnable onChange) {
                this.onChange = onChange;
            }

            @Override
            public void close() {
            }

            public void triggerChange() {
                if (onChange != null) onChange.run();
            }
        };

        dynamicConfig = new DynamicConfig(tempFile, fakeWatcher);
        assertEquals(1, dynamicConfig.getConfig().getInt("bar"));

        // Change the file
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("bar = 42");
        }
        // Simulate file change event
        ((ConfigFileWatcher) fakeWatcher).start(() -> dynamicConfig.getConfig()); // re-register
        ((ConfigFileWatcher) fakeWatcher).start(() -> dynamicConfig.getConfig()); // re-register
        ((ConfigFileWatcher) fakeWatcher).start(() -> dynamicConfig.getConfig()); // re-register
        ((ConfigFileWatcher) fakeWatcher).close();
        ((ConfigFileWatcher) fakeWatcher).close();

        // Actually trigger the change
        ((ConfigFileWatcher) fakeWatcher).start(() -> dynamicConfig.getConfig());
        ((ConfigFileWatcher) fakeWatcher).close();

        // Manually set lastModified to force reload
        tempFile.setLastModified(System.currentTimeMillis() + 1000);
        dynamicConfig.getConfig(); // Should reload

        assertEquals(42, dynamicConfig.getConfig().getInt("bar"));
    }

    @Test
    void testFallbackToLastGoodConfigOnError() throws Exception {
        tempFile = File.createTempFile("test", ".conf");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("baz = 7");
        }
        mockWatcher = mock(ConfigFileWatcher.class);

        dynamicConfig = new DynamicConfig(tempFile, mockWatcher);
        assertEquals(7, dynamicConfig.getConfig().getInt("baz"));

        // Corrupt the file
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("not valid config");
        }
        // Force reload
        tempFile.setLastModified(System.currentTimeMillis() + 1000);
        Config config = dynamicConfig.getConfig();

        // Should still return the last good config
        assertEquals(7, config.getInt("baz"));
    }

    @Test
    void testCloseCleansUpResources() throws Exception {
        tempFile = File.createTempFile("test", ".conf");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("foo = 1");
        }
        mockWatcher = mock(ConfigFileWatcher.class);

        dynamicConfig = new DynamicConfig(tempFile, mockWatcher);
        dynamicConfig.close();

        verify(mockWatcher, times(1)).close();
    }

    @Test
    void testFromPathThrowsIfFileDoesNotExist() {
        File nonExistent = new File("nonexistent-" + System.nanoTime() + ".conf");
        assertThrows(IllegalArgumentException.class, () -> {
            DynamicConfig.fromPath(nonExistent.getParent() == null ? "." : nonExistent.getParent(), nonExistent.getName());
        });
    }
}