package com.vanishedd.blockregen;

import com.vanishedd.blockregen.file.CustomFile;
import com.vanishedd.blockregen.file.FileManager;
import com.vanishedd.blockregen.listeners.PlayerActivity;
import com.vanishedd.blockregen.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class BlockRegen extends JavaPlugin {
    private static BlockRegen instance;
    public FileManager fileManager = new FileManager();

    @Override
    public void onEnable() {
        instance = this;

        registerConfig();

        fixWorldNames();

        registerListeners();
        registerBlocks();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static BlockRegen getInstance() {
        return instance;
    }

    private void registerConfig() {
        CustomFile langFile = new CustomFile("lang");
        CustomFile dataFile = new CustomFile("data");

        saveDefaultConfig();
        langFile.saveDefaultConfig();
        dataFile.saveDefaultConfig();

        Lang.setFile(langFile.getCustomConfig());

        fileManager.registerFile(langFile);
        fileManager.registerFile(dataFile);
    }

    private void registerListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new PlayerActivity(), this);
    }

    private void registerBlocks() {
        for (String blockInfo : fileManager.getFile("data").getCustomConfig().getStringList("Regenerating")) {
            String[] split = blockInfo.split(";");
            Location location = new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
            Long startTime = Long.parseLong(split[4]);
            final Material material = Material.valueOf(split[5]);
            final Byte data = Byte.parseByte(split[6]);
            Block block = location.getBlock();

            Long regenTime = 0L;

            for (String world : getConfig().getConfigurationSection("Blocks").getKeys(false)) {
                if (!world.equalsIgnoreCase(location.getWorld().getName())) {
                    continue;
                }

                for (String blockType : getConfig().getConfigurationSection("Blocks." + world).getKeys(false)) {
                    if (!blockType.equalsIgnoreCase(material.name())) {
                        continue;
                    }

                    regenTime = getConfig().getInt("Blocks." + world + "." + blockType, 30) * 1000L;
                }
            }

            Long endTime = startTime + regenTime;
            Long difference = endTime - System.currentTimeMillis();

            if (difference <= 0) {
                block.setType(material);
                block.setData(data);
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                List<String> regenerating = fileManager.getFile("data").getCustomConfig().getStringList("Regenerating");

                if (getConfig().getBoolean("Random-Regen")) {
                    block.setType(Material.valueOf(getRandomRegenType(location.getWorld().getName().toLowerCase())));
                } else {
                    block.setType(material);
                    block.setData(data);
                }

                regenerating.remove(blockInfo);
                fileManager.getFile("data").getCustomConfig().set("Regenerating", regenerating);
                fileManager.getFile("data").saveCustomConfig();
            }, (difference/1000) * 20L);
        }
    }

    private String getRandomRegenType(String world) {
        String[] randomBlocks = getConfig().getConfigurationSection("Regen-Blocks." + world).getKeys(false).toArray(new String[getConfig().getConfigurationSection("Regen-Blocks." + world).getKeys(false).size()]);
        double totalWeight = 0;

        for (String blockType : randomBlocks) {
            totalWeight += getConfig().getDouble("Regen-Blocks." + world + "." + blockType);
        }

        double random = Math.random() * totalWeight;

        for (String blockType : randomBlocks) {
            random -= getConfig().getDouble("Regen-Blocks." + world + "." + blockType);

            if (random <= 0.0) {
                return blockType;
            }
        }
        return "COBBLESTONE";
    }

    private void fixWorldNames() {
        for (String key : getConfig().getConfigurationSection("Blocks").getKeys(false)) {
            if (key.equals(key.toLowerCase())) {
                continue;
            }

            getConfig().set(key.toLowerCase(), getConfig().getConfigurationSection("Blocks." + key));
        }
        saveConfig();
    }
}
