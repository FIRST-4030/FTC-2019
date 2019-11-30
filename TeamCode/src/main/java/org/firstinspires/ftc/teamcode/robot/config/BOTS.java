package org.firstinspires.ftc.teamcode.robot.config;

public enum BOTS {
    PROD("Serial1"), ARM("Serial2"), NONE("");

    private String serial;

    public String serial() {
        return serial;
    }

    private BOTS(String serial) {
        this.serial = serial;
    }
}
