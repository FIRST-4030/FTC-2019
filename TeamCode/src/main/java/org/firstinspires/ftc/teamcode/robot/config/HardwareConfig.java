package org.firstinspires.ftc.teamcode.robot.config;

import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.actuators.Actuators;
import org.firstinspires.ftc.teamcode.actuators.ServoNG;
import org.firstinspires.ftc.teamcode.actuators.ServoNG_Params;
import org.firstinspires.ftc.teamcode.defaults.Defaults;

// All robot config goes here
public class HardwareConfig {
    public static void actuators(RobotNG robot, Actuators actuators) {
        Defaults d = robot.defaults;

        // Foundation hooks
        {
            ServoNG_Params p;

            // These are defined in code but the values can be changed in the defaults file on-phone
            d.register("FOUNDATION_R_RANGE", 270);
            d.register("FOUNDATION_R_MIN", 0.2d);
            d.register("FOUNDATION_R_MAX", 0.8d);
            d.register("FOUNDATION_R_CLOSED", 1.0d);
            d.register("FOUNDATION_R_OPEN", 0.0d);

            // They can also be specific to a robot, which will be selected automatically at runtime
            d.register("FOUNDATION_L_RANGE", 180); // All bots
            d.register("FOUNDATION_L_RANGE", 270, BOTS.ARM); // ARM only
            d.register("FOUNDATION_L_MIN", 0.0d);
            d.register("FOUNDATION_L_MAX", 1.0d);
            d.register("FOUNDATION_L_CLOSED", 0.5d);
            d.register("FOUNDATION_L_OPEN", 0.75d);

            // Config and install a servo
            p = new ServoNG_Params(robot, "Foundation_L");
            p.range = d.getD("FOUNDATION_L_RANGE");
            p.setLimits(d.getD("FOUNDATION_L_MIN"),
                    d.getD("FOUNDATION_L_MAX"), ServoNG_Params.UNIT.POSITION);
            p.addPreset("CLOSED", d.getD("FOUNDATION_L_CLOSED"), ServoNG_Params.UNIT.POSITION);
            p.addPreset("OPEN", d.getD("FOUNDATION_L_OPEN"), ServoNG_Params.UNIT.POSITION);
            p.addPreset(ServoNG_Params.INIT_NAME, d.getD("FOUNDATION_L_OPEN"), ServoNG_Params.UNIT.POSITION);
            actuators.add(new ServoNG(p));

            // Config and install a servo
            p = new ServoNG_Params(robot, "Foundation_R");
            p.range = d.getD("FOUNDATION_R_RANGE");
            p.setLimits(d.getD("FOUNDATION_R_MIN"),
                    d.getD("FOUNDATION_R_MAX"), ServoNG_Params.UNIT.POSITION);
            p.addPreset("CLOSED", d.getD("FOUNDATION_R_CLOSED"), ServoNG_Params.UNIT.POSITION);
            p.addPreset("OPEN", d.getD("FOUNDATION_R_OPEN"), ServoNG_Params.UNIT.POSITION);
            p.addPreset(ServoNG_Params.INIT_NAME, d.getD("FOUNDATION_R_OPEN"), ServoNG_Params.UNIT.POSITION);
            actuators.add(new ServoNG(p));
        }
    }
}
