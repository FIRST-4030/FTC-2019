package org.firstinspires.ftc.teamcode.opmodes.teleops;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.TestRobot;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp - Test", group = "Test")
public class TestTeleOp extends OpMode {
    private TestRobot robot = null;

    public void init(){
        robot = new TestRobot(hardwareMap, telemetry);

    }

    public void loop(){
        robot.test.power(0.5);
    }
}
