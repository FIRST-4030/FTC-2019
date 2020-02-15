package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.actuators.Motor;
import org.firstinspires.ftc.teamcode.robot.Robot;

public class TestRobot extends Robot{
    public static TestRobot robot;

    // Actuators
    public Motor test;

    // Sensors



    public TestRobot(HardwareMap map, Telemetry telemetry){
        super.map = map;
        super.telemetry = telemetry;
        robot = this;

        // Initialize Actuators
        test = new Motor(map, telemetry, "Test", false, true, DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }
}
