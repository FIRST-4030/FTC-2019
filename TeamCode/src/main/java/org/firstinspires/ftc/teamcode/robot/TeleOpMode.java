package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.robot.common.Arm;
import org.firstinspires.ftc.teamcode.utils.RateLimit;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp")
public class TeleOpMode extends OpMode {

    // Devices and subsystems
    private Robot robot = null;
    private ButtonHandler buttons;

    private float armX = 5.75f;
    private float armY = 5.5f;
    private float armRotation = 0.5f;
    private final float ARM_MOVEMENT_SCALE = 1.0f / 16;
    private final float ARM_ROTATION_SCALE = 1.0f / 256;
    private final float CLAW_CLOSED = 0.25f;
    private final float CLAW_OPEN = 0.5f;
    private final float NORMAL_SPEED = 0.75f;
    private final float SLOW_MODE = 0.25f;

    // Arm rate limiting
    private RateLimit rateX;
    private RateLimit rateY;
    private static final double MAX_ARM_RATE_X = 1.0d; // In arm-displacement-units (inches) per second
    private static final double MAX_ARM_RATE_Y = 1.0d; // In arm-displacement-units (inches) per second

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

        // Init rate limits for the arm
        rateX = new RateLimit(this, MAX_ARM_RATE_X);
        rateY = new RateLimit(this, MAX_ARM_RATE_Y);

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
        robot.wheels.setSpeedScale((gamepad1.left_bumper) ? SLOW_MODE : NORMAL_SPEED);
        robot.wheels.loop(gamepad1);
    }

    private void auxiliary() {
        // arm movement
        armX -= rateX.update(gamepad2.left_stick_y * ARM_MOVEMENT_SCALE);
        armY -= rateY.update(gamepad2.right_stick_y * ARM_MOVEMENT_SCALE);
        armRotation -= gamepad2.left_stick_x * ARM_ROTATION_SCALE;

        // cap values
        armRotation = Math.min(1.0f, armRotation);
        armRotation = Math.max(0.0f, armRotation);

        telemetry.addData("arm x: ", armX);
        telemetry.addData("arm y: ", armY);
        telemetry.addData("arm rotation: ", armRotation);

        robot.rotation.setPosition(armRotation);
        try {
            robot.common.arm.moveToPos(armX, armY);
        } catch (Arm.OutOfRangeException e) {
            telemetry.addLine("Arm can't move to desired position");
        }

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