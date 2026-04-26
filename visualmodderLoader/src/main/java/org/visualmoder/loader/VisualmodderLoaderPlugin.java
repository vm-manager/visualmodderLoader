package org.visualmoder.loader;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.visualmoder.loader.update.UpdateManager;

import java.io.File;

public class VisualmodderLoaderPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        File pluginsFolder = getDataFolder().getParentFile();
        File targetFile = new File(pluginsFolder, "Visualmodder.jar");

        String url = "https://visualmodder.org/?sdm_process_download=1&download_id=1950";
        // String url = "https://visualmodder.org/downloads/visualmodder-1.21-20260324_1153.jar";
        // Calculate with bash command: sha256sum /c/minecraft/spigot_1_21/plugins/visualmodder-1.21-20260324_1153.jar
        String sha256 = "de86e81ca46f28b58dc564bd08c2913bd6e1c8ed2dca22e487ab88b8741ca096";
        String version = "1.0.0-";
        boolean autoUpdate = true;

        UpdateManager updater = new UpdateManager(this, targetFile, url, sha256, version, autoUpdate);

        try {
            UpdateManager.Result result = updater.run();

            switch (result) {
                case INSTALLED:
                case UPDATED:
                    getLogger().info("Restarting server to load plugin... (Restart manually if needed)");
                    Bukkit.getScheduler().runTaskLater(this, Bukkit::shutdown, 40L);
                    break;

                case UP_TO_DATE:
                    getLogger().info("Visualmodder is up to date.");
                    break;

                case FAILED:
                    getLogger().severe("Update process failed. See errors above.");
                    break;
            }

        } catch (Exception e) {
            getLogger().severe("Fatal error in loader: " + e.getMessage());
            e.printStackTrace();
        }
    }
}