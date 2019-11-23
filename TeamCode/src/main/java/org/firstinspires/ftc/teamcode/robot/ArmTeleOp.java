package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.ViewerParameters;

import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.Button;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.config.BOT;
import org.firstinspires.ftc.teamcode.utils.RateLimit;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp - Arm", group = "Arm")
public class ArmTeleOp extends OpMode {

    // Devices and subsystems
    private Robot robot = null;
    private ButtonHandler buttons;
    private int loops = 0;
    private int lastLoops = 0;
    private int lastCountTime = 0;

    // arm consts
    private static final float ARM_MOVEMENT_SCALE = 1.0f/16;
    private static final float ARM_ROTATION_SCALE = 1.0f/512;
    private static final float CLAW_CLOSED = 0.0f;
    private static final float CLAW_OPEN = 1.0f;
    private static final float ARM_HOME_X = 1.8f;
    private static final float ARM_HOME_Y = 5.9f;

    // Arm rate limiting
    private RateLimit rateX;
    private RateLimit rateY;
    private RateLimit rateR;
    private RateLimit rateW;
    private static final double MAX_ARM_RATE_X = 1.0d; // In inches per second
    private static final double MAX_ARM_RATE_Y = 1.0d; // In inches per second
    private static final double MAX_ARM_RATE_R = 0.25d; // In servo position per second
    private static final double MAX_ARM_RATE_W = 0.25d; // In servo position per second

    // other consts
    private static final float NORMAL_SPEED = 0.75f;
    private static final float SLOW_MODE = 0.25f;

    // vars
    private float wristRotation = 0.5f;
    private float armRotation = 0.5f;

    @Override
    public void init() {
        // Placate drivers
        telemetry.addData(">", "NOT READY");
        telemetry.update();

        // Init the common tasks elements
        robot = new Robot(hardwareMap, telemetry);
        robot.wheels.setTeleop(true);

        // Check robot
        if (robot.bot != BOT.ARM) {
            telemetry.log().add("Opmode not compatible with bot " + robot.bot);
            requestOpModeStop();
        }

        // Register buttons
        buttons = new ButtonHandler(robot);
        buttons.register("CLAW", gamepad2, PAD_BUTTON.x, BUTTON_TYPE.TOGGLE);
        buttons.register("HOME_ARM", gamepad2, PAD_BUTTON.y, BUTTON_TYPE.SINGLE_PRESS);
        buttons.register("SLOW_MODE", gamepad1, PAD_BUTTON.left_bumper, BUTTON_TYPE.TOGGLE);

        // Init rate limits for the arm
        rateX = new RateLimit(this, MAX_ARM_RATE_X);
        rateY = new RateLimit(this, MAX_ARM_RATE_Y);
        rateR = new RateLimit(this, MAX_ARM_RATE_R);
        rateW = new RateLimit(this, MAX_ARM_RATE_W);

        // Move arm to home
        robot.common.arm.setPosition(ARM_HOME_X, ARM_HOME_Y);
        robot.rotation.setPosition(0.5f);
        robot.swivel.setPosition(wristRotation);

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
        } else {
            robot.wheels.setSpeedScale(NORMAL_SPEED);
        }
        robot.wheels.loop(gamepad1);
    }

    private void auxiliary() {
        // arm movement
        float dx = (float) rateX.update(-gamepad2.left_stick_y * ARM_MOVEMENT_SCALE);
        float dy = (float) rateY.update(-gamepad2.right_stick_y * ARM_MOVEMENT_SCALE);
        armRotation -= (float) rateR.update(Math.pow(gamepad2.left_stick_x, 3) * ARM_ROTATION_SCALE);
        wristRotation -= (float) rateW.update(Math.pow(gamepad2.right_stick_x, 3) * ARM_ROTATION_SCALE);

        // cap values
        armRotation = Math.min(1.0f, armRotation);
        armRotation = Math.max(0.0f, armRotation);
        wristRotation = Math.min(1.0f, wristRotation);
        wristRotation = Math.max(0.0f, wristRotation);


        robot.rotation.setPosition(armRotation);
        robot.swivel.setPosition(wristRotation);
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