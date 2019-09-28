package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;


@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp")
public class TeleOpMode extends OpMode {

    private final float SCALE_SLOW = 0.25f;
    private final float SCALE_FULL = 1.0f;

    // Devices and subsystems
    private Robot robot = null;
    private ButtonHandler buttons;

    @Override
    public void init() {

        // Placate drivers
        telemetry.addData(">", "NOT READY");
        telemetry.update();

        // Init the common tasks elements
        robot = new Robot(hardwareMap, telemetry);

        // Register buttons
        buttons = new ButtonHandler(robot);
        buttons.register("SLOW_MODE", gamepad1, PAD_BUTTON.left_bumper, BUTTON_TYPE.TOGGLE);

        // Wait for the game to begin
        telemetry.addData(">", "Ready for game start");
        telemetry.update();

    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        robot.wheels.setTeleop(true);
    }

    @Override
    public void loop() {
        // Update buttons
        buttons.update();

        // Move the robot
        driveBase();
        liftSystem();

        telemetry.update();
    }

    public void driveBase() {
        if (buttons.get("SLOW_MODE")) {
            robot.wheels.setSpeedScale(SCALE_SLOW);
        } else {
            robot.wheels.setSpeedScale(SCALE_FULL);
        }
        robot.wheels.loop(gamepad1);
    }

    public void liftSystem() {

        // oh god it's all gone

    }

    public void stop() {

    }
}