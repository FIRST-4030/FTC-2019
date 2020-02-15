package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class Robot {
    //tim is a massive dummy -the robot
    public static Robot R = null;
    public final OpMode opmode;

    public Robot(OpMode opmode) {
        R = this;
        this.opmode = opmode;
        System.out.println('i want this to commit')
    }
}
