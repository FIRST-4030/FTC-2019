package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class Robot {
    //tim is a massive dummy -the robot
    public static Robot R = null;
    public final OpMode opmode;

    //Shared


    public Robot(OpMode opmode) {
        R = this;
        this.opmode = opmode;
    }
}
