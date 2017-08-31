package com.vanishedd.blockregen.util;

import com.vanishedd.blockregen.BlockRegen;
import org.bukkit.configuration.file.FileConfiguration;

public enum Lang {
    BLOCK_BROKE("block-broke", "This block will regenerate in &e%time%&7!"),

    PREFIX("prefix", "&6&lBlock Regen &8&l\u00BB &7");

    private final String path;
    private final String def;
    private static FileConfiguration LANG;

    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }

    public static void setFile(FileConfiguration fileConfiguration) {
        LANG = fileConfiguration;
    }

    @Override
    public String toString() {
        BlockRegen plugin = BlockRegen.getInstance();

        StringBuilder stringBuilder = new StringBuilder();

        if (plugin.getConfig().getBoolean("Use-Prefix")) {
            stringBuilder.append(Lang.LANG.getString("prefix", PREFIX.def));
        }

        stringBuilder.append(LANG.getString(path, def));

        return Util.colorize(stringBuilder.toString());
    }

    public String toString(boolean addPrefix) {
        BlockRegen plugin = BlockRegen.getInstance();

        StringBuilder stringBuilder = new StringBuilder();

        if (addPrefix && plugin.getConfig().getBoolean("Use-Prefix")) {
            stringBuilder.append(Lang.LANG.getString("prefix", PREFIX.def));
        }

        stringBuilder.append(LANG.getString(path, def));

        return Util.colorize(stringBuilder.toString());
    }
}