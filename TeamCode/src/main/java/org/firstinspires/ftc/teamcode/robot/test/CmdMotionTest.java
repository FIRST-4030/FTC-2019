package org.firstinspires.ftc.teamcode.robot.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.config.BOT;
import org.firstinspires.ftc.teamcode.driveto.AutoDriver;
import org.firstinspires.ftc.teamcode.robot.Robot;
import org.firstinspires.ftc.teamcode.vuforia.ImageFTC;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Cmd Motion", group = "Test")
public class CmdMotionTest extends OpMode {

    private static final boolean AUTO = false;

    // Devices and subsystems
    private Robot robot = null;
    private ButtonHandler buttons;

    // other consts
    private static final float NORMAL_SPEED = 0.75f;
    private static final float SLOW_MODE = 0.25f;
    // TODO: This should be in a Claw class
    private static final float CLAW_CLOSED = 0.25f;
    private static final float CLAW_OPEN = 0.5f;

    // Dynamic things we need to remember
    private int lastBearing = 0;
    private int lastDistance = 0;
    private String lastImage = "<None>";
    private String lastTarget = "<None>";
    private AutoDriver driver = null;

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
        buttons.register("SLOW_MODE", gamepad1, PAD_BUTTON.left_bumper, BUTTON_TYPE.TOGGLE);
        buttons.register("oh god oh fuck", gamepad1, PAD_BUTTON.back, BUTTON_TYPE.TOGGLE);

        // Disable teleop motion controls if we're in AUTO mode
        robot.wheels.setTeleop(!AUTO);

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

        if (AUTO) {
            // Handle AutoDriver driving
            // This does the actual driving
            driver = robot.common.drive.loop(driver);

            /*
             * Cut the loop short when we are AutoDriver'ing
             */
            if (driver.isRunning(time)) {
                return;
            }

            // TODO: Test and calibrate these
            // Last year's auto:
            // https://github.com/FIRST-4030/FTC-2018/blob/master/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/robot/auto/RuckusAutoTheBetterOne.java
            //robot.common.drive.distance(200);
            //robot.common.drive.degrees(90);
        }

        // This is automatically skipped when setTelop() is false
        robot.wheels.loop(gamepad1);
    }

    private void auxiliary() {
        // claw
        // TODO: This should be in a Claw class and protected there
        if (robot.bot == BOT.PRODUCTION) {
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