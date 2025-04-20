package com.raju.codekatas.configsync.model;

public class AppConfig {
    private String name;
    private String version;
    private boolean enabled;
    private int maxConnections;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", enabled=" + enabled +
                ", maxConnections=" + maxConnections +
                '}';
    }
}
