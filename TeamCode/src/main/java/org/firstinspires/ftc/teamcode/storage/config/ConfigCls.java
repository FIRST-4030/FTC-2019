package org.firstinspires.ftc.teamcode.storage.config;

import java.util.HashMap;

public class ConfigCls {
    private final Config config;
    private final HashMap<String, HashMap> cls;

    public ConfigCls(Config config, HashMap<String, HashMap> cls) {
        this.config = config;
        this.cls = cls;
    }

    public ConfigDevice device(String device) {
        return new ConfigDevice(config, config.device(cls, device));
    }
}
