package org.firstinspires.ftc.teamcode;

import java.util.HashMap;
import java.util.Map;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Robot {
    //tim is a massive dummy -the robot
    public static Robot R = null;
    public final OpMode opmode;
    public final HardwareMap map;
    public final Telemetry telemetry;

    //Scissor Lift Robot
    Map scissor = new HashMap();


    public Robot(OpMode opmode, HardwareMap map, Telemetry telemetry) {
        R = this;
        this.opmode = opmode;
        this.map = map;
        this.telemetry = telemetry;
    }

    public enum BOT {
        TEST
    }
}
