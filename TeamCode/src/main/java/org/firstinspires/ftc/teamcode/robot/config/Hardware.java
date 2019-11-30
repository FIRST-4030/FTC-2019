package org.firstinspires.ftc.teamcode.robot.config;

import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.actuators.Actuators;
import org.firstinspires.ftc.teamcode.actuators.ServoNG;
import org.firstinspires.ftc.teamcode.actuators.ServoNG_Params;
import org.firstinspires.ftc.teamcode.defaults.Defaults;

public class Hardware {
    private final RobotNG robot;
    private final Actuators actuators;

    public Hardware(RobotNG robot) {
        if (robot == null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + ": Robot not available");
        }
        this.robot = robot;

        actuators = new Actuators(robot);

        config();
    }

    // All robot config goes here
    private void config() {
        Defaults d = robot.defaults;

        // Foundation hooks
        {
            ServoNG_Params p;

            // These become editable in the defaults file
            d.register("FOUNDATION_L_RANGE", 180);
            d.register("FOUNDATION_L_CLOSED", 0.0d);
            d.register("FOUNDATION_L_OPEN", 1.0d);
            d.register("FOUNDATION_L_RANGE", 270);
            d.register("FOUNDATION_R_CLOSED", 1.0d);
            d.register("FOUNDATION_R_OPEN", 0.0d);

            // Build and install a servo config
            p = new ServoNG_Params(robot, "Foundation_L");
            p.range = d.getD("FOUNDATION_L_RANGE");
            p.addPreset("CLOSED", d.getD("FOUNDATION_L_CLOSED"), ServoNG_Params.UNIT_TYPE.POSITION);
            p.addPreset("OPEN", d.getD("FOUNDATION_L_OPEN"), ServoNG_Params.UNIT_TYPE.POSITION);
            p.addPreset(ServoNG_Params.INIT_NAME, d.getD("FOUNDATION_L_OPEN"), ServoNG_Params.UNIT_TYPE.POSITION);
            actuators.add(new ServoNG(p));

            p = new ServoNG_Params(robot, "Foundation_R");
            p.range = d.getD("FOUNDATION_R_RANGE");
            p.addPreset("CLOSED", d.getD("FOUNDATION_R_CLOSED"), ServoNG_Params.UNIT_TYPE.POSITION);
            p.addPreset("OPEN", d.getD("FOUNDATION_R_OPEN"), ServoNG_Params.UNIT_TYPE.POSITION);
            p.addPreset(ServoNG_Params.INIT_NAME, d.getD("FOUNDATION_R_OPEN"), ServoNG_Params.UNIT_TYPE.POSITION);
            actuators.add(new ServoNG(p));
        }
    }
}
