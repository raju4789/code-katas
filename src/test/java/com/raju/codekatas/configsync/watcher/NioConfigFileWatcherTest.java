package com.raju.codekatas.configsync.watcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class NioConfigFileWatcherTest {

    private NioConfigFileWatcher watcher;
    private File tempFile;
    private Path tempDir;

    @AfterEach
    void tearDown() throws Exception {
        if (watcher != null) {
            watcher.close();
        }
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    void testOnChangeIsCalledWhenFileIsModified() throws Exception {
        tempFile = File.createTempFile("test", ".tmp");
        CountDownLatch latch = new CountDownLatch(1);

        watcher = new NioConfigFileWatcher(tempFile);
        watcher.start(latch::countDown);

        try (FileWriter writer = new FileWriter(tempFile, true)) {
            writer.write("test");
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS), "onChange should be called when file is modified");
    }

    @Test
    void testCloseStopsWatcherThread() throws Exception {
        tempFile = File.createTempFile("test", ".tmp");
        watcher = new NioConfigFileWatcher(tempFile);
        watcher.start(() -> {
        });

        watcher.close();
        Thread.sleep(200);

        assertFalse(watcher.watcherThread.isAlive(), "Watcher thread should be stopped after close()");
    }

    @Test
    void testFileDeletedWhileWatching() throws Exception {
        tempFile = File.createTempFile("test", ".tmp");
        CountDownLatch latch = new CountDownLatch(1);

        watcher = new NioConfigFileWatcher(tempFile);
        watcher.start(latch::countDown);

        assertTrue(tempFile.delete(), "File should be deleted");
        // Touch the directory to trigger the watcher
        File dir = tempFile.getParentFile();
        try (FileWriter writer = new FileWriter(new File(dir, "dummy.txt"))) {
            writer.write("dummy");
        }

        // The watcher should not call onChange for unrelated file
        assertFalse(latch.await(1, TimeUnit.SECONDS), "onChange should not be called for unrelated file");
    }

    @Test
    void testFileRenamedWhileWatching() throws Exception {
        tempFile = File.createTempFile("test", ".tmp");
        File renamedFile = new File(tempFile.getParent(), tempFile.getName() + ".bak");
        CountDownLatch latch = new CountDownLatch(1);

        watcher = new NioConfigFileWatcher(tempFile);
        watcher.start(latch::countDown);

        assertTrue(tempFile.renameTo(renamedFile), "File should be renamed");
        // Touch the directory to trigger the watcher
        File dir = renamedFile.getParentFile();
        try (FileWriter writer = new FileWriter(new File(dir, "dummy2.txt"))) {
            writer.write("dummy2");
        }

        assertFalse(latch.await(1, TimeUnit.SECONDS), "onChange should not be called for renamed file");
        renamedFile.delete();
    }

    @Test
    void testMultipleRapidModifications() throws Exception {
        tempFile = File.createTempFile("test", ".tmp");
        CountDownLatch latch = new CountDownLatch(2);

        watcher = new NioConfigFileWatcher(tempFile);
        watcher.start(latch::countDown);

        for (int i = 0; i < 2; i++) {
            try (FileWriter writer = new FileWriter(tempFile, true)) {
                writer.write("test" + i);
            }
            Thread.sleep(100); // Give the watcher time to pick up the change
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS), "onChange should be called for each modification");
    }

    @Test
    void testCallbackThrowsException() throws Exception {
        tempFile = File.createTempFile("test", ".tmp");
        watcher = new NioConfigFileWatcher(tempFile);

        watcher.start(() -> {
            throw new RuntimeException("Callback error");
        });

        try (FileWriter writer = new FileWriter(tempFile, true)) {
            writer.write("test");
        }

        // No assertion: just ensure no crash, watcher should log error and continue
        Thread.sleep(500);
    }

    @Test
    void testCloseBeforeAnyEvents() throws Exception {
        tempFile = File.createTempFile("test", ".tmp");
        watcher = new NioConfigFileWatcher(tempFile);
        watcher.start(() -> {
        });
        watcher.close();
        // No assertion: just ensure no crash
    }

    @Test
    void testNonExistentFile() {
        File nonExistent = new File("nonexistentfile-" + System.nanoTime() + ".tmp");
        assertFalse(nonExistent.exists());
        assertDoesNotThrow(() -> {
            NioConfigFileWatcher w = new NioConfigFileWatcher(nonExistent);
            w.close();
        });
    }

    @Test
    void testIOExceptionInWatcher() throws Exception {
        // Simulate by passing a file in a directory that cannot be watched (e.g., root dir on Unix)
        File file = new File("/", "forbiddenfile-" + System.nanoTime() + ".tmp");
        NioConfigFileWatcher w = new NioConfigFileWatcher(file);
        w.start(() -> {
        });
        w.close();
        // No assertion: just ensure no crash
    }

    @Test
    void testThreadInterruption() throws Exception {
        tempFile = File.createTempFile("test", ".tmp");
        watcher = new NioConfigFileWatcher(tempFile);
        watcher.start(() -> {
        });
        watcher.watcherThread.interrupt();
        Thread.sleep(200);
        assertFalse(watcher.watcherThread.isAlive(), "Watcher thread should stop on interruption");
    }
}