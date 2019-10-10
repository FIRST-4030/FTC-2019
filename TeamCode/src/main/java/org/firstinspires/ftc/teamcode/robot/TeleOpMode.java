package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.utils.RateLimit;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp")
public class TeleOpMode extends OpMode {

    // Devices and subsystems
    private Robot robot = null;
    private ButtonHandler buttons;

    // arm consts
    private static final float ARM_MOVEMENT_SCALE = 1.0f/16;
    private static final float ARM_ROTATION_SCALE = 1.0f/256;
    private static final float CLAW_CLOSED = 0.25f;
    private static final float CLAW_OPEN = 0.5f;
    private static final float ARM_HOME_X = 2.0f;
    private static final float ARM_HOME_Y = 2.0f;

    // other consts
    private static final float NORMAL_SPEED = 0.75f;
    private static final float SLOW_MODE = 0.25f;

    // variables
    private int lastCountTime = 0;
    private int loops = 0;
    private int lastLoops = 0;
    private float armRotation = 0.5f;

    // Arm rate limiting
    private RateLimit rateX;
    private RateLimit rateY;
    private RateLimit rateR;
    private static final double MAX_ARM_RATE_X = 1.0d; // In arm-displacement-units (inches) per second
    private static final double MAX_ARM_RATE_Y = 1.0d; // In arm-displacement-units (inches) per second
    private static final double MAX_ARM_RATE_R = 0.25d; // In servo position per second

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
        buttons.register("CLAW", gamepad2, PAD_BUTTON.left_bumper, BUTTON_TYPE.TOGGLE);
        buttons.register("HOME_ARM", gamepad2, PAD_BUTTON.b, BUTTON_TYPE.SINGLE_PRESS);
        buttons.register("SLOW_MODE", gamepad1, PAD_BUTTON.left_bumper, BUTTON_TYPE.TOGGLE);
        buttons.register("oh god oh fuck", gamepad1, PAD_BUTTON.start, BUTTON_TYPE.TOGGLE);

        // Init rate limits for the arm
        rateX = new RateLimit(this, MAX_ARM_RATE_X);
        rateY = new RateLimit(this, MAX_ARM_RATE_Y);
        rateR = new RateLimit(this, MAX_ARM_RATE_R);

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
        if (buttons.get("oh god oh fuck")) {
            robot.wheels.setSpeedScale((float) Math.sin(time / 100));
        } else if (buttons.get("SLOW_MODE")) {
            robot.wheels.setSpeedScale(SLOW_MODE);
        } else {
            robot.wheels.setSpeedScale(NORMAL_SPEED);
        }

        robot.wheels.loop(gamepad1);
    }

    private void auxiliary() {
        // arm movement
        float dx = (float) rateX.update(-gamepad2.left_stick_y * ARM_MOVEMENT_SCALE);
        float dy = (float) rateY.update(-gamepad2.right_stick_y * ARM_MOVEMENT_SCALE);
        armRotation += (float) rateR.update(gamepad2.left_stick_x * ARM_ROTATION_SCALE);

        // cap values
        armRotation = Math.min(1.0f, armRotation);
        armRotation = Math.max(0.0f, armRotation);

        robot.rotation.setPosition(armRotation);
        robot.common.arm.setPositionDelta(dx, dy);

        if (buttons.get("HOME_ARM")) {
            robot.common.arm.setPosition(ARM_HOME_X, ARM_HOME_Y);
        }

        telemetry.addData("arm x", robot.common.arm.getArmX());
        telemetry.addData("arm y", robot.common.arm.getArmY());
        telemetry.addData("arm rotation", armRotation);

        // claw
        if (buttons.get("CLAW")) {
            robot.claw.setPosition(CLAW_CLOSED);
        } else {
            robot.claw.setPosition(CLAW_OPEN);
        }
    }

    public void stop() {
    }
}