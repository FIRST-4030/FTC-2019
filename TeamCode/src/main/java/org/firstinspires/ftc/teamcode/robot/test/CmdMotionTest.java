package org.firstinspires.ftc.teamcode.robot.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.config.BOT;
import org.firstinspires.ftc.teamcode.robot.Robot;
import org.firstinspires.ftc.teamcode.utils.RateLimit;
import org.firstinspires.ftc.teamcode.vuforia.ImageFTC;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Cmd Motion", group = "Test")
public class CmdMotionTest extends OpMode {

    // Devices and subsystems
    private Robot robot = null;
    private ButtonHandler buttons;

    // arm consts
    private static final float ARM_MOVEMENT_SCALE = 1.0f / 16;
    private static final float ARM_ROTATION_SCALE = 1.0f / 512;
    private static final float CLAW_CLOSED = 0.25f;
    private static final float CLAW_OPEN = 0.5f;
    private static final float ARM_HOME_X = 5.0f;
    private static final float ARM_HOME_Y = 5.0f;

    // Arm rate limiting
    // TODO: Move this into the Arm class -- we want config at runtime but all this can be hidden
    private RateLimit rateX;
    private RateLimit rateY;
    private RateLimit rateR;
    private static final double MAX_ARM_RATE_X = 1.0d; // In inches per second
    private static final double MAX_ARM_RATE_Y = 1.0d; // In inches per second
    private static final double MAX_ARM_RATE_R = 0.25d; // In servo position per second

    // other consts
    private static final float NORMAL_SPEED = 0.75f;
    private static final float SLOW_MODE = 0.25f;

    // variables
    private float armRotation = 0.5f;

    // Dynamic things we need to remember
    private int lastBearing = 0;
    private int lastDistance = 0;
    private String lastImage = "<None>";
    private String lastTarget = "<None>";


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
        buttons.register("CAPTURE", gamepad1, PAD_BUTTON.a);
        buttons.register("CLAW", gamepad2, PAD_BUTTON.left_bumper, BUTTON_TYPE.TOGGLE);
        buttons.register("HOME_ARM", gamepad2, PAD_BUTTON.y, BUTTON_TYPE.SINGLE_PRESS);
        buttons.register("SLOW_MODE", gamepad1, PAD_BUTTON.left_bumper, BUTTON_TYPE.TOGGLE);
        buttons.register("oh god oh fuck", gamepad1, PAD_BUTTON.back, BUTTON_TYPE.TOGGLE);

        // Init rate limits for the arm
        rateX = new RateLimit(this, MAX_ARM_RATE_X);
        rateY = new RateLimit(this, MAX_ARM_RATE_Y);
        rateR = new RateLimit(this, MAX_ARM_RATE_R);

        // Move arm to home
        robot.common.arm.setPosition(ARM_HOME_X, ARM_HOME_Y);
        // TODO: This should be in the arm class and protected there
        if (robot.bot == BOT.ARM) {
            robot.rotation.setPosition(0.5f);
        }

        // Wait for the game to begin
        telemetry.addData(">", "Ready for game start");
        telemetry.update();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        telemetry.clearAll();

        // Start Vuforia tracking and enable capture
        robot.vuforia.start();
        robot.vuforia.enableCapture();
    }

    @Override
    public void loop() {
        // Update buttons, location, and target info
        buttons.update();
        robot.vuforia.track();

        // Capture
        if (buttons.get("CAPTURE")) {
            robot.vuforia.capture();
        }
        if (robot.vuforia.getImage() != null) {
            ImageFTC image = robot.vuforia.getImage();
            lastImage = "(" + image.getWidth() + "," + image.getHeight() + ") " + image.getTimestamp();
            String filename = "vuforia-" + image.getTimestamp() + ".png";
            if (!image.savePNG(filename)) {
                telemetry.log().add(this.getClass().getSimpleName() + ": Unable to save file: " + filename);
            }
            robot.vuforia.clearImage();
        }

        // Collect data about the first visible target
        String target = null;
        int bearing = 0;
        int distance = 0;
        if (!robot.vuforia.isStale()) {
            for (String t : robot.vuforia.getVisible().keySet()) {
                if (t == null || t.isEmpty()) {
                    continue;
                }
                if (robot.vuforia.getVisible(t)) {
                    target = t;
                    int index = robot.vuforia.getTargetIndex(target);
                    bearing = robot.vuforia.bearing(index);
                    distance = robot.vuforia.distance(index);
                    break;
                }
            }
            lastTarget = target;
            lastBearing = bearing;
            lastDistance = distance;
        }

        // Move the robot
        driveBase();
        auxiliary();

        robot.vuforia.display(telemetry);
        telemetry.addData("Image", lastImage);
        telemetry.addData("Target (" + lastTarget + ")", lastDistance + "mm @ " + lastBearing + "°");
        telemetry.update();
    }

    private void driveBase() {
        if (buttons.get("oh god oh fuck")) {
            robot.wheels.setSpeedScale((float) Math.sin(time * 10));
            telemetry.addLine("uh oh");
        } else if (buttons.get("SLOW_MODE")) {
            robot.wheels.setSpeedScale(SLOW_MODE);
            telemetry.addLine("slow mode");
        } else {
            robot.wheels.setSpeedScale(NORMAL_SPEED);
            telemetry.addLine("normal mode");
        }

        robot.wheels.loop(gamepad1);
    }

    private void auxiliary() {
        // arm movement
        float dx = (float) rateX.update(-gamepad2.left_stick_y * ARM_MOVEMENT_SCALE);
        float dy = (float) rateY.update(-gamepad2.right_stick_y * ARM_MOVEMENT_SCALE);
        armRotation -= (float) rateR.update(Math.pow(gamepad2.left_stick_x, 2) * ARM_ROTATION_SCALE);

        // cap values
        armRotation = Math.min(1.0f, armRotation);
        armRotation = Math.max(0.0f, armRotation);

        // TODO: This should be in the arm class and protected there
        if (robot.bot == BOT.ARM) {
            robot.rotation.setPosition(armRotation);
        }
        robot.common.arm.setPositionDelta(dx, dy);

        if (buttons.get("HOME_ARM")) {
            robot.common.arm.setPosition(ARM_HOME_X, ARM_HOME_Y);
        }

        telemetry.addData("arm x", robot.common.arm.getArmX());
        telemetry.addData("arm y", robot.common.arm.getArmY());
        telemetry.addData("arm rotation", armRotation);

        // claw
        // TODO: This should be in the Claw class and protected there
        if (robot.bot == BOT.ARM) {
            if (buttons.get("CLAW")) {
                robot.claw.setPosition(CLAW_CLOSED);
            } else {
                robot.claw.setPosition(CLAW_OPEN);
            }
        }
    }

    public void stop() {
        robot.vuforia.stop();
    }
}