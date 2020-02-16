package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.actuators.Motor;
import org.firstinspires.ftc.teamcode.utils.RobotUtils;

public class Robot extends RobotUtils {
    //tim is a massive dummy -the robot
    public static Robot R = null;

    // All our hardware should have members declared here
    public final Motor m;

    public Robot(OpMode opmode) {
        super(opmode);
        R = this;

        // Hardware config
        m = new Motor("FL", false, true, null);
    }
}
