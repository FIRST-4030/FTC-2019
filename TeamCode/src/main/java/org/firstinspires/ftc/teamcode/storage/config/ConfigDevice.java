package org.firstinspires.ftc.teamcode.storage.config;

import org.firstinspires.ftc.teamcode.storage.anytype.AnyType;

import java.util.HashMap;

public class ConfigDevice {
    private final Config config;
    private final HashMap<String, AnyType> device;

    public ConfigDevice(Config config, HashMap<String, AnyType> device) {
        this.config = config;
        this.device = device;
    }

    public boolean exists(String item) {
        return config.exists(device, item);
    }

    public AnyType any(String item) {
        return config.item(device, item);
    }

    public boolean b(String item) {
        return config.item(device, item).b;
    }

    public int i(String item) {
        return config.item(device, item).i;
    }

    public double d(String item) {
        return config.item(device, item).d;
    }

    public String s(String item) {
        return config.item(device, item).s;
    }

    public boolean bOptional(String item, boolean fallback) {
        if (exists(item)) {
            fallback = b(item);
        }
        return fallback;
    }

    public int iOptional(String item, int fallback) {
        if (exists(item)) {
            fallback = i(item);
        }
        return fallback;
    }

    public double dOptional(String item, double fallback) {
        if (exists(item)) {
            fallback = d(item);
        }
        return fallback;
    }

    public String sOptional(String item, String fallback) {
        if (exists(item)) {
            fallback = s(item);
        }
        return fallback;
    }
}
