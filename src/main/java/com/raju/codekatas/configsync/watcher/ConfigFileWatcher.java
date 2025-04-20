package com.raju.codekatas.configsync.watcher;

import java.io.Closeable;

public interface ConfigFileWatcher extends Closeable {
    void start(Runnable onChange);
}
