package com.five.library.config;

import com.five.logger.Logger;

import java.io.File;
import java.io.IOException;

public class FileListener extends Thread {
    private final File file;
    private final ConfigLoader configLoader;
    private volatile boolean isRunning = true;
    public FileListener(File file, ConfigLoader configLoader) {
        this.file = file;
        this.configLoader = configLoader;
    }

    @Override
    public void run() {
        Logger.info("[Library Mysql Supply] start listener");
        long lastModified = file.lastModified();
        while (isRunning) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long newModified = file.lastModified();
            if (newModified != lastModified) {
                lastModified = newModified;
                try {
                    Logger.info("[Library Mysql Supply] config has changed");
                    configLoader.loadConfig();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void close() {
        isRunning = false;
        Logger.info("[Library Mysql Supply] close listener");
    }
}
