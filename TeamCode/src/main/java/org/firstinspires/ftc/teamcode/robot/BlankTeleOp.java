package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.config.BOT;
import org.firstinspires.ftc.teamcode.utils.RateLimit;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp - Arm", group = "Arm")
public class BlankTeleOp extends OpMode {

    // Devices and subsystems
    private Robot robot = null;
    private ButtonHandler buttons;

    // arm consts
    private static final float ARM_MOVEMENT_SCALE = 1.0f/16;
    private static final float ARM_ROTATION_SCALE = 1.0f/512;
    private static final float WRIST_ROTATION_SCALE = 1.0f/512;
    private static final float ARM_HOME_X = 0.64f;
    private static final float ARM_HOME_Y = 3.81f;
    private static final float ARM_HOME_R = 0.4f;
    private static final float ARM_HOME_W = 0.8f;

    // Arm rate limiting
    private RateLimit rateX;
    private RateLimit rateY;
    private RateLimit rateR;
    private RateLimit rateW;
    private static final double MAX_ARM_RATE_X = 1.5d; // In inches per second
    private static final double MAX_ARM_RATE_Y = 1.5d; // In inches per second
    private static final double MAX_ARM_RATE_R = 0.125d; // In servo position per second
    private static final double MAX_ARM_RATE_W = 0.1d; // In servo position per second

    // other consts
    private static final float NORMAL_SPEED = 0.75f;
    private static final float SLOW_MODE = 0.25f;

    // vars
    private float armRotation = ARM_HOME_R;
    private float wristRotation = ARM_HOME_W;

    @Override
    public void init() {
        // Placate drivers
        telemetry.addData(">", "NOT READY");
        telemetry.update();

        // Init the common tasks elements
        robot = new Robot(hardwareMap, telemetry);
        robot.wheels.setTeleop(true);

        // Check robot
        if (robot.bot != BOT.BLANK) {
            telemetry.log().add("Opmode not compatible with bot " + robot.bot);
            requestOpModeStop();
            return;
        }

        // Register buttons
        buttons = new ButtonHandler(robot);
        buttons.register("SLOW_MODE", gamepad1, PAD_BUTTON.b, BUTTON_TYPE.TOGGLE);



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
        // Update buttons
        buttons.update();

        // Move the robot
        driveBase();
        auxiliary();

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

    }

    public void stop() {
    }
}