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
    private RateLimit armRate;

    // Changies
    private float armPos = ARM_HOME;

    // Consts
    private static final float SLOW_MODE = 0.25f;
    private static final float NORMAL_SPEED = 1.0f;

    private static final float CLAW_CLOSED = 0.5f;
    private static final float CLAW_OPEN = 0.75f;

    private static final float CAP_UP = 0.0f;
    private static final float CAP_DOWN = 0.75f;

    private static final float SWING_OFFSET = 0.05f;
    private static final float ARM_SPEED = 0.005f;
    private static final float ARM_HOME = 0.1f;
    private static final double ARM_MAX_SPEED = 0.25;

    private static final float COLLECT_SPEED = 0.8f;


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
        buttons.register("SLOW_MODE", gamepad1, PAD_BUTTON.b, BUTTON_TYPE.TOGGLE);
        buttons.register("COLLECT", gamepad1, PAD_BUTTON.a, BUTTON_TYPE.TOGGLE);
        buttons.register("FOUNDATION_HOOK", gamepad1, PAD_BUTTON.y, BUTTON_TYPE.TOGGLE);

        buttons.register("ARM_RESET", gamepad2, PAD_BUTTON.b, BUTTON_TYPE.SINGLE_PRESS);
        buttons.register("LIFT_UP", gamepad2, PAD_BUTTON.right_bumper);
        buttons.register("LIFT_DOWN", gamepad2, PAD_BUTTON.left_bumper);
        buttons.register("GRAB", gamepad2, PAD_BUTTON.x, BUTTON_TYPE.TOGGLE);
        buttons.register("CAPSTONE", gamepad2, PAD_BUTTON.y, BUTTON_TYPE.TOGGLE);

        // Speed limiting
        armRate = new RateLimit(this, ARM_MAX_SPEED);


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
        // LIFT
        robot.lift.setPower(gamepad2.right_trigger - gamepad2.left_trigger);

        // Collector
        if (buttons.get("COLLECT")) {
            robot.collectorLeft.setPower(COLLECT_SPEED);
            robot.collectorRight.setPower(COLLECT_SPEED);
        } else {
            robot.collectorLeft.setPower(0.0f);
            robot.collectorRight.setPower(0.0f);
        }

        // Flipper
        float dy = (float) armRate.update(ARM_SPEED * gamepad2.left_stick_x);
        armPos += dy;
        if (buttons.get("ARM_RESET")) {
            armPos = ARM_HOME;
        }
        robot.flipper.setPosition(armPos);
        telemetry.addData("Arm Pos", robot.flipper.getPosition());

        // CLAW
        if (buttons.get("GRAB")) {
            robot.claw.setPosition(CLAW_OPEN);
        } else {
            robot.claw.setPosition(CLAW_CLOSED);
        }

        // Capstone thingy
        if (buttons.get("CAPSTONE")) {
            robot.capstone.setPosition(CAP_DOWN);
        } else {
            robot.capstone.setPosition(CAP_UP);
        }

        // Foundation hooks
        if (buttons.get("FOUNDATION_HOOK")) {
            robot.hookLeft.min();
            robot.hookRight.min();
        } else {
            robot.hookLeft.max();
            robot.hookRight.max();
        }
    }

    public void stop() {
    }
}