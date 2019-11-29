package org.firstinspires.ftc.teamcode.robot;

import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.actuators.Actuators;
import org.firstinspires.ftc.teamcode.actuators.ServoNG;
import org.firstinspires.ftc.teamcode.actuators.ServoNG_Params;

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
        // Foundation hooks
        {
            ServoNG_Params p;

            p = new ServoNG_Params(robot, "Foundation_L");
            p.range = 270;
            p.addPreset("CLOSED", 0.0d, ServoNG_Params.UNIT_TYPE.POSITION);
            p.addPreset("OPEN", 1.0d, ServoNG_Params.UNIT_TYPE.POSITION);
            p.addPreset(ServoNG_Params.INIT_NAME, 0.0d, ServoNG_Params.UNIT_TYPE.POSITION);
            actuators.add(new ServoNG(p));

            p = new ServoNG_Params(robot, "Foundation_R");
            p.range = 270;
            p.addPreset("CLOSED", 1.0d, ServoNG_Params.UNIT_TYPE.POSITION);
            p.addPreset("OPEN", 0.0d, ServoNG_Params.UNIT_TYPE.POSITION);
            p.addPreset(ServoNG_Params.INIT_NAME, 1.0d, ServoNG_Params.UNIT_TYPE.POSITION);
            actuators.add(new ServoNG(p));
        }
    }
}
