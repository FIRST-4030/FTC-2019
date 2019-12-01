package org.firstinspires.ftc.teamcode.actuators;

import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.utils.Round;

import java.util.LinkedHashMap;
import java.util.Set;

public class ServoNG_Params implements Actuator_Params {
    public enum UNIT {POSITION, ANGLE, OUTPUT}

    public static final String INIT_NAME = "_INIT";

    RobotNG robot;
    private String name;
    private double min = 0.0d;
    private double max = 1.0d;
    private LinkedHashMap<String, Double> presets;

    public boolean reverse = false;
    public double range = 180.0d;
    public double outputScale = 1.0d;
    public double outputOffset = 0.0d;
    public double rateLimit = Double.MAX_VALUE;

    public ServoNG_Params(String name) {
        this(RobotNG.R, name);
    }

    public ServoNG_Params(RobotNG robot, String name) {
        if (robot == null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + ": Robot not available");
        }
        if (name == null || name.isEmpty()) {
            robot.log("Null/empty name");
            name = "<NULL>";
        }

        this.presets = new LinkedHashMap<>();
        this.robot = robot;
        this.name = name;
    }

    public String name() {
        return name;
    }

    public boolean valid() {
        boolean retval = true;
        if (name == null || name.isEmpty()) {
            robot.log(this, "Null/empty name");
            retval = false;
        }
        if (range <= 0.0d || range > 360.0d) {
            robot.log(this, "Invalid range: " + Round.truncate(range));
            retval = false;
        }
        if (min <= max) {
            robot.log(this, "Invalid min/max: " +
                    Round.truncate(min) + "/" + Round.truncate(max));
            retval = false;
        }
        return retval;
    }

    public void setLimits(double min, double max, UNIT type) {
        if (min < max) {
            robot.log(this, "Invalid min/max: " +
                    Round.truncate(min) + "/" + Round.truncate(max));
            return;
        }
        switch (type) {
            case POSITION:
                this.min = min;
                this.max = max;
                break;
            case ANGLE:
                this.min = angleToPos(min);
                this.max = angleToPos(max);
                break;
            case OUTPUT:
                this.min = outputToPos(min);
                this.max = outputToPos(max);
                break;
        }
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMinMaxRange() {
        return max - min;
    }

    public Set<String> getPresets() {
        return presets.keySet();
    }

    public double getPreset(String name) {
        if (name == null || name.isEmpty()) {
            robot.log(this, "Null/empty present name");
            return 0.0d;
        }
        if (!presets.containsKey(name)) {
            robot.log(this, "Unregistered preset: " + name);
            return 0.0d;
        }
        return presets.get(name);
    }

    public boolean hasPreset(String name) {
        return presets.containsKey(name);
    }

    public void addPreset(String name, double pos, UNIT type) {
        if (name == null || name.isEmpty()) {
            robot.log(this, "Null/empty present name");
            return;
        }
        if (presets.containsKey(name)) {
            robot.log(this, "Preset already exists: " + name);
            return;
        }
        switch (type) {
            case POSITION:
                break;
            case ANGLE:
                pos = angleToPos(pos);
                break;
            case OUTPUT:
                pos = outputToPos(pos);
                break;
        }
        presets.put(name, pos);
    }

    public double posToScale(double pos) {
        return (pos - min) / getMinMaxRange();
    }

    public double scaleToPos(double scale) {
        return (scale * getMinMaxRange()) + min;
    }

    public double posToAngle(double pos) {
        return pos * range;
    }

    public double angleToPos(double angle) {
        return angle / range;
    }

    public double posToOutput(double pos) {
        return (posToAngle(pos) * outputScale) + outputOffset;
    }

    public double outputToPos(double output) {
        return angleToPos(output / outputScale) - outputOffset;
    }
}
