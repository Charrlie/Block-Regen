package com.vanishedd.blockregen.listeners;

import com.vanishedd.blockregen.BlockRegen;
import com.vanishedd.blockregen.file.CustomFile;
import com.vanishedd.blockregen.file.FileManager;
import com.vanishedd.blockregen.util.Lang;
import com.vanishedd.blockregen.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerActivity implements Listener {
    private BlockRegen plugin = BlockRegen.getInstance();
    private FileManager fileManager = plugin.fileManager;
    private CustomFile dataFile = fileManager.getFile("data");

    @EventHandler
    @SuppressWarnings({"unused", "deprecation"})
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        for (String key : plugin.getConfig().getConfigurationSection("Blocks").getKeys(false)) {
            if (!key.equalsIgnoreCase(block.getType().name())) {
                continue;
            }

            int regenTime = plugin.getConfig().getInt("Blocks." + key, 300);
            Location blockLocation = block.getLocation();
            final Material blockType = block.getType();
            final Byte data = block.getData();
            final String blockInfo = blockLocation.getWorld().getName() + ";" + blockLocation.getBlockX() + ";" + blockLocation.getBlockY() + ";" + blockLocation.getBlockZ() + ";" + System.currentTimeMillis() + ";" + blockType.name() + ";" + data;
            List<String> blocks = dataFile.getCustomConfig().getStringList("Regenerating");

            blocks.add(blockInfo);
            dataFile.getCustomConfig().set("Regenerating", blocks);
            dataFile.saveCustomConfig();

            for (ItemStack drop : block.getDrops()) {
                blockLocation.getWorld().dropItemNaturally(blockLocation, drop);
            }
            block.setType(Material.BEDROCK);
            e.setCancelled(true);
            player.sendMessage(Lang.BLOCK_BROKE.toString().replace("%time%", Util.convertMs(regenTime * 1000L)));

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                List<String> regenerating = dataFile.getCustomConfig().getStringList("Regenerating");

                block.setType(blockType);
                block.setData(data);
                regenerating.remove(blockInfo);
                dataFile.getCustomConfig().set("Regenerating", regenerating);
                dataFile.saveCustomConfig();
            }, regenTime * 20L);
        }
    }
}
