package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.actuators.Motor;
import org.firstinspires.ftc.teamcode.utils.RobotUtils;

public class Robot extends RobotUtils {
    //tim is a massive dummy -the robot
    public static Robot R = null;

    // All our hardware should have members declared here
    public final Motor m1;
    public final Motor m2;

    public Robot(OpMode opmode) {
        super(opmode);
        R = this;

        // Hardware config
        // If you provide config in the JSON file you can just do this
        // The Motor class reads config parameters out of the JSON by class and device name
        m1 = new Motor("FL");
        // But you can also initialize things directly, if you need to calculate something
        m2 = new Motor("BL", false, true, null);
    }
}
