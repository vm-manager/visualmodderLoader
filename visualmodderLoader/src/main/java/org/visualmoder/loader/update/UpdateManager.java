package org.visualmoder.loader.update;

import org.bukkit.plugin.java.JavaPlugin;
import org.visualmoder.loader.download.Downloader;
import org.visualmoder.loader.security.HashUtil;

import java.io.File;

public class UpdateManager {

    public enum Result {
        INSTALLED,
        UPDATED,
        UP_TO_DATE,
        FAILED
    }

    private final JavaPlugin plugin;
    private final File targetFile;
    private final String url;
    private final String expectedHash;
    private final String version;
    private final boolean autoUpdate;

    public UpdateManager(JavaPlugin plugin, File targetFile, String url, String expectedHash, String version, boolean autoUpdate) {
        this.plugin = plugin;
        this.targetFile = targetFile;
        this.url = url;
        this.expectedHash = expectedHash;
        this.version = version;
        this.autoUpdate = autoUpdate;
    }

    public Result run() {
        try {
            Downloader downloader = new Downloader(plugin);

            // First install
            if (!targetFile.exists()) {
                plugin.getLogger().info("Main plugin not found. Installing...");
                downloader.download(url, targetFile);
                HashUtil.verify(targetFile, expectedHash);
                return Result.INSTALLED;
            }

            // Update check
            if (autoUpdate) {
                String currentHash = HashUtil.sha256(targetFile);

                if (!currentHash.equalsIgnoreCase(expectedHash)) {
                    plugin.getLogger().info("Update detected. Downloading new version...");

                    File tempFile = new File(targetFile.getParent(), targetFile.getName() + ".tmp");

                    downloader.download(url, tempFile);
                    HashUtil.verify(tempFile, expectedHash);

                    if (targetFile.delete() && tempFile.renameTo(targetFile)) {
                        plugin.getLogger().info("Update successful.");
                        return Result.UPDATED;
                    } else {
                        plugin.getLogger().severe("Failed to replace plugin file.");
                        return Result.FAILED;
                    }
                }
            }

            return Result.UP_TO_DATE;

        } catch (Exception e) {
            plugin.getLogger().severe("Update failed: " + e.getMessage());
            e.printStackTrace();
            return Result.FAILED;
        }
    }
}