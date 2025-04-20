package com.raju.codekatas.configsync;

import com.raju.codekatas.configsync.config.DynamicConfig;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class ConfigLoaderExample {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoaderExample.class);


    public static void main(String[] args) throws Exception {
        // Start DynamicConfig
        DynamicConfig dynamicConfig = DynamicConfig.fromPath("src/main/resources", "test.conf");
        logger.info("App is running. Edit test.conf and type 'show' to see updated values.");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            logger.info("Type 'show' to display config, or 'exit' to quit: ");
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                break;
            } else if ("show".equalsIgnoreCase(input)) {
                Config config = dynamicConfig.getConfig();
                String name = config.getString("app.name");
                String version = config.getString("app.version");
                boolean enabled = config.getBoolean("app.enabled");
                int maxConnections = config.getInt("app.maxConnections");
                logger.info("Current Config:");
                logger.info("Name: {}", name);
                logger.info("Version: {}", version);
                logger.info("Enabled: {}", enabled);
                logger.info("Max Connections: {}", maxConnections);
            }
        }
        dynamicConfig.close();

    }
}
