package org.firstinspires.ftc.teamcode.robot;

import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.actuators.Actuators;

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

    }
}
