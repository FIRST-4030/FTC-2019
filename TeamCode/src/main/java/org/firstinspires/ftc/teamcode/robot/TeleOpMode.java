package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.config.BOT;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp - Prod", group = "Scissor")
public class TeleOpMode extends OpMode {

    // Devices and subsystems
    private Robot robot = null;
    private ButtonHandler buttons;
    private int loops = 0;
    private int lastLoops = 0;
    private int lastCountTime = 0;

    // Changies
    private float armPos = ARM_HOME;

    // Consts
    private static final float SLOW_MODE = 0.7f;
    private static final float NORMAL_SPEED = 1.0f;

    private static final float CLAW_CLOSED = 0.6f;
    private static final float SMALL_OPEN = 0.35f;
    private static final float BIG_OPEN = 0.0f;
    private static final float BIG_MIN_POS = 0.6f;

    private static final float CAP_UP = 0.0f;
    private static final float CAP_DOWN = 0.75f;

    private static final float ARM_SPEED = 0.02f;
    private static final float ARM_HOME = 0.1f;
    private static final float ARM_OUT = 0.65f;

    private static final float COLLECT_SPEED = 0.9f;



    @Override
    public void init() {
        // Placate drivers
        telemetry.addData(">", "NOT READY");
        telemetry.update();

        // Init the common tasks elements
        robot = new Robot(hardwareMap, telemetry);
        robot.wheels.setTeleop(true);

        // Check robot
        if (robot.bot != BOT.SCISSOR) {
            telemetry.log().add("Opmode not compatible with bot " + robot.bot);
            requestOpModeStop();
        }

        // Register buttons
        //game pad one controls movement
        buttons = new ButtonHandler(robot);
        buttons.register("COLLECT", gamepad1, PAD_BUTTON.a, BUTTON_TYPE.TOGGLE);
        buttons.register("FOUNDATION_HOOK", gamepad1, PAD_BUTTON.y, BUTTON_TYPE.TOGGLE);
        buttons.register("CAPSTONE1", gamepad1, PAD_BUTTON.x);
        buttons.register("CAPSTONE3", gamepad1, PAD_BUTTON.left_bumper);

        //game pad two controls the arm, aka everything else
        buttons.register("ARM_RESET", gamepad2, PAD_BUTTON.b, BUTTON_TYPE.SINGLE_PRESS);
        buttons.register("ARM_OUT", gamepad2, PAD_BUTTON.a, BUTTON_TYPE.SINGLE_PRESS);
        buttons.register("ARM_TO_1", gamepad2, PAD_BUTTON.right_bumper);
        buttons.register("ARM_TO_0", gamepad2, PAD_BUTTON.left_bumper);
        buttons.register("GRAB", gamepad2, PAD_BUTTON.x, BUTTON_TYPE.TOGGLE);
        buttons.register("CAPSTONE2", gamepad2, PAD_BUTTON.y);
        buttons.getListener("ARM_TO_0").setLongHeldTimeout(0);
        buttons.getListener("ARM_TO_1").setLongHeldTimeout(0);
        buttons.getListener("ARM_TO_0").setAutokeyTimeout(0);
        buttons.getListener("ARM_TO_1").setAutokeyTimeout(0);


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

        // Loops per second
        if (time >= lastCountTime + 1) {
            lastCountTime = (int) time;
            lastLoops = loops;
            loops = 0;
        }
        telemetry.addData("Loop Frequency", lastLoops);

        telemetry.update();
    }

    private void driveBase() {
        robot.wheels.loop(gamepad1);
    }

    //moves everything

    private void auxiliary() {
        // LIFT
        robot.lift.setPower(gamepad2.right_trigger - gamepad2.left_trigger);


        // Stone Collector
        if (buttons.get("COLLECT")) {
            robot.collectorLeft.setPower(COLLECT_SPEED);
            robot.collectorRight.setPower(COLLECT_SPEED);
        } else {
            robot.collectorLeft.setPower(0.0f);
            robot.collectorRight.setPower(0.0f);
        }

        // Swingy arm
        if (buttons.autokey("ARM_TO_0")) {
            armPos -= ARM_SPEED;
        }
        //Automatically spams the Arm buttons at regular intervals.
        if (buttons.autokey("ARM_TO_1")) {
            armPos += ARM_SPEED;
        }
        //Homes arm inside the robot
        if (buttons.get("ARM_RESET")) {
            armPos = ARM_HOME;
        }
        //Quickly moves arm into a decent position for collecting
        if (buttons.get("ARM_OUT")) {
            armPos = ARM_OUT;
        }

        // Arm limits
        if (armPos > 1.0f) armPos = 1.0f;
        if (armPos < 0.0f) armPos = 0.0f;
        robot.flipper.setPosition(armPos);
        telemetry.addData("Arm Pos", robot.flipper.getPosition());

        // CLAW
        if (buttons.get("GRAB")) {
            //Ensures the arm doesn't open wide enough to get stuck in the robot
            if (robot.flipper.getPosition() > BIG_MIN_POS) {
                robot.claw.setPosition(BIG_OPEN);
            } else {
                robot.claw.setPosition(SMALL_OPEN);
            }
        } else {
            robot.claw.setPosition(CLAW_CLOSED);
        }

        // Capstone thingy
        if (buttons.held("CAPSTONE1") && buttons.held("CAPSTONE2") && buttons.held("CAPSTONE3")) {
            robot.capstone.setPosition(CAP_DOWN);
        } else {
            robot.capstone.setPosition(CAP_UP);
        }

        // Foundation hooks + Slowmode
        if (buttons.get("FOUNDATION_HOOK")) {
            robot.hookLeft.min();
            robot.hookRight.min();

            robot.wheels.setSpeedScale(SLOW_MODE);
            telemetry.addLine("slow mode");
        } else {
            robot.hookLeft.max();
            robot.hookRight.max();

            robot.wheels.setSpeedScale(NORMAL_SPEED);
            telemetry.addLine("normal mode");
        }
    }

    public void stop() {
    }
}