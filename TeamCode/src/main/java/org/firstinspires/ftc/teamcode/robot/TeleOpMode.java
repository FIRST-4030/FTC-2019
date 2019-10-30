package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.utils.RateLimit;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp")
public class TeleOpMode extends OpMode {

    // Devices and subsystems
    private Robot robot = null;
    private ButtonHandler buttons;
    private int loops = 0;
    private int lastLoops = 0;
    private int lastCountTime = 0;

    // Consts
    private static final float SLOW_MODE = 0.25f;
    private static final float NORMAL_SPEED = 1.0f;

    @Override
    public void init() {
        // Placate drivers
        telemetry.addData(">", "NOT READY");
        telemetry.update();

        // Init the common tasks elements
        robot = new Robot(hardwareMap, telemetry);
        robot.wheels.setTeleop(true);

        // Register buttons
        buttons = new ButtonHandler(robot);

        // Wait for the game to begin
        telemetry.addData(">", "Ready for game start");
        telemetry.update();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
        // update loop counter
        loops++;

        // Update buttons
        buttons.update();

        // Move the robot
        driveBase();
        auxiliary();

        // hurts
        if (time >= lastCountTime + 1) {
            lastCountTime = (int) time;
            lastLoops = loops;
            loops = 0;
        }
        telemetry.addData("Loop Frequency", lastLoops);

        telemetry.update();
    }

    private void driveBase() {
        if (buttons.get("SLOW_MODE")) {
            robot.wheels.setSpeedScale(SLOW_MODE);
            telemetry.addLine("slow mode");
        } else {
            robot.wheels.setSpeedScale(NORMAL_SPEED);
            telemetry.addLine("normal mode");
        }

        robot.wheels.loop(gamepad1);
    }

    private void auxiliary() {
        // nothing yet ;)
    }

    public void stop() {
    }
}