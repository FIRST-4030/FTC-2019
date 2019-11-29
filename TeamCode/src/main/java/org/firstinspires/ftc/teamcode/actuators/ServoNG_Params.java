package org.firstinspires.ftc.teamcode.actuators;

import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.utils.Round;

public class ServoNG_Params {
    public enum LIMIT_TYPE {POSITION, ANGLE, OUTPUT}

    protected RobotNG robot;

    public final String name;
    public boolean reverse = false;
    public double range = 180.0d;
    public double outputScale = 1.0d;
    public double outputOffset = 0.0d;
    protected double min = 0.0d;
    protected double max = 1.0d;
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

        this.robot = robot;
        this.name = name;
    }

    public boolean validate() {
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

    public void setLimits(double min, double max, LIMIT_TYPE type) {
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

    public double posToScale(double pos) {
        return (pos - min) / (max - min);
    }

    public double scaleToPos(double scale) {
        return (scale * (max - min)) + min;
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
