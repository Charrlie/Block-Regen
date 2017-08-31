package com.vanishedd.blockregen.util;

import org.bukkit.ChatColor;

import java.util.concurrent.TimeUnit;

public class Util {

    public static String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String convertMs(Long ms) {
        int days = (int) TimeUnit.MILLISECONDS.toDays(ms);
        int hours = (int) (TimeUnit.MILLISECONDS.toHours(ms) % TimeUnit.DAYS.toHours(1));
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(ms) % TimeUnit.HOURS.toMinutes(1));
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(ms) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder sb = new StringBuilder();

        if (days > 1) {
            sb.append(Integer.toString(days)).append(" Days, ");
        } else if (days == 1) {
            sb.append(Integer.toString(days)).append(" Day, ");
        }

        if (hours > 1) {
            sb.append(Integer.toString(hours)).append(" Hours, ");
        } else if (hours == 1) {
            sb.append(Integer.toString(hours)).append(" Hour, ");
        }

        if (minutes > 1) {
            sb.append(Integer.toString(minutes)).append(" Minutes, ");
        } else if (minutes == 1) {
            sb.append(Integer.toString(minutes)).append(" Minute, ");
        }

        if (seconds > 1) {
            sb.append(Integer.toString(seconds)).append(" Seconds");
        } else if (seconds == 1) {
            sb.append(Integer.toString(seconds)).append(" Second");
        }

        String convertedTime = sb.toString();
        convertedTime = convertedTime.trim();

        if (convertedTime.endsWith(",")) {
            convertedTime = convertedTime.substring(0, convertedTime.length() - 1);
        }

        return convertedTime;
    }

    public static Long convertTime(String time) {
        Long length = (long) 0;

        for (String timeSection : time.split(", ")) {
            int amount = Integer.parseInt(timeSection.split(" ")[0]);
            String value = timeSection.split(" ")[1];

            if (value.contains("Day")) {
                length = length + (amount * 86400000);
            }

            if (value.contains("Hour")) {
                length = length + (amount * 3600000);
            }

            if (value.contains("Minute")) {
                length = length + (amount * 60000);
            }

            if (value.contains("Second")) {
                length = length + (amount * 1000);
            }
        }

        return length;
    }
}
