package org.visualmoder.loader.download;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class Downloader {

    private final JavaPlugin plugin;

    public Downloader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void download(String fileURL, File destination) throws Exception {
        URL url = new URL(fileURL);
        var connection = url.openConnection();
        int fileSize = connection.getContentLength();
        //System.out.println("fileURL x "+fileURL);
        //System.out.println("fliesize  "+fileSize);

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(destination)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalRead = 0;
            long lastLoggedPercent = 0;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;

                if (fileSize > 0) {
                    int percent = (int) (totalRead * 100 / fileSize);
                    if (percent >= lastLoggedPercent + 5) {
                        lastLoggedPercent = percent;
                        plugin.getLogger().info("Download progress: " + percent + "%");
                    }
                } else {
                    plugin.getLogger().info("Downloaded " + (totalRead / 1024) + " KB...");
                }
            }
        }

        plugin.getLogger().info("Download complete.");
    }
}