package org.firstinspires.ftc.teamcode.robot.auto;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.config.BOT;
import org.firstinspires.ftc.teamcode.driveto.AutoDriver;
import org.firstinspires.ftc.teamcode.field.Field;
import org.firstinspires.ftc.teamcode.robot.Robot;
import org.firstinspires.ftc.teamcode.robot.common.Common;
import org.firstinspires.ftc.teamcode.utils.OrderedEnum;
import org.firstinspires.ftc.teamcode.utils.OrderedEnumHelper;
import org.firstinspires.ftc.teamcode.utils.Round;
import org.firstinspires.ftc.teamcode.vuforia.VuforiaFTC;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Skystone Side", group = "Scissor")
public class SkystoneAuto extends OpMode {

    // Devices and subsystems
    private Robot robot = null;
    private Common common = null;
    private VuforiaFTC vuforia = null;
    private ButtonHandler buttons;
    private AutoDriver driver = new AutoDriver();

    // Runtime vars
    private AUTO_STATE state;
    private boolean gameReady = false;
    private Field.AllianceColor color = Field.AllianceColor.BLUE;
    private boolean stopByWall = true;
    private SkystonePos skyPos = SkystonePos.RIGHT;

    @Override
    public void init() {
        telemetry.addData(">", "Init…");
        telemetry.update();

        // Init the common tasks elements
        robot = new Robot(hardwareMap, telemetry);
        common = robot.common;
        vuforia = robot.vuforia;

        // Check robot
        if (robot.bot != BOT.SCISSOR) {
            telemetry.log().add("Opmode not compatible with bot " + robot.bot);
            requestOpModeStop();
        }

        // Init the camera system
        //vuforia.start();
        //vuforia.enableCapture();

        // TODO: figure out what to do with this
        //initTfod();

        // Register buttons
        buttons = new ButtonHandler(robot);
        buttons.register("SELECT_SIDE", gamepad1, PAD_BUTTON.y, BUTTON_TYPE.TOGGLE);
        buttons.register("AWAY_FROM_WALL", gamepad1, PAD_BUTTON.dpad_up);
        buttons.register("TOWARDS_WALL", gamepad1, PAD_BUTTON.dpad_down);
    }

    @Override
    public void init_loop() {
        // Process driver input
        userSettings();

        // Overall ready status
        gameReady = (robot.gyro.isReady());
        telemetry.addData("\t\t\t", "");
        telemetry.addData(">", gameReady ? "Ready for game start" : "NOT READY");

        // Detailed feedback
        telemetry.addData("\t\t\t", "");
        telemetry.addData("Gyro", robot.gyro.isReady() ? "Ready" : "Calibrating…");

        // Update
        telemetry.update();
    }

    @Override
    public void start() {
        telemetry.clearAll();

        // Log if we didn't exit init as expected
        if (!gameReady) {
            telemetry.log().add("Started before ready");
        }

        // Set initial state
        state = AUTO_STATE.values()[0];

        //robot.vuforia.start();
        //robot.vuforia.enableCapture();

        //tfod.activate();
    }

    @Override
    public void loop() {
        // Handle AutoDriver driving
        driver = common.drive.loop(driver);

        // Debug feedback
        telemetry.addData("State", state);
        telemetry.addData("Running", driver.isRunning(time));
        telemetry.addData("Gyro", Round.truncate(robot.gyro.getHeading()));
        telemetry.addData("Encoder", robot.wheels.getEncoder());

        // Cut the loop short while AutoDriver is driving
        // This prevents the state machine from running before the preceding state is complete
        if (driver.isRunning(time)) return;

        /*
         * Main State Machine
         * enum has descriptions of each state
         */
        switch (state) {

            case INIT:
                driver.done = false;
                robot.hookLeft.min();
                robot.hookRight.min();
                robot.flipper.setPosition(0.0f);
                robot.claw.min();
                advance();
                break;

            case DETECT_SKYSTONE:
                // TODO: actually make this one
                skyPos = SkystonePos.RIGHT;
                advance();
                break;

            case MOVE_TO_STONES:
                driver.drive = common.drive.translate(InchesToMM(36.0f));
                advance();
                break;

            case ADJUST:
                switch(skyPos) {
                    case LEFT:
                        // idk
                        break;

                    case CENTER:
                        driver.drive = common.drive.distance(InchesToMM(6.0f));
                        break;

                    case RIGHT:
                        driver.drive = common.drive.distance(InchesToMM(12.0f));
                        break;
                }
                advance();
                break;

            case PUSH_STONES:
                driver.drive = common.drive.translate(InchesToMM(4.0f));
                advance();
                break;

            case REACH_OUT_HALF:
                robot.flipper.setPosition(0.5f);
                driver.drive = common.drive.sleep(200);
                advance();
                break;

            case OPEN_CLAW:
            case RELEASE_STONE:
                robot.claw.max();
                driver.drive = common.drive.sleep(200);
                advance();
                break;

            case REACH_OUT_MORE:
                robot.flipper.setPosition(1.0f);
                driver.drive = common.drive.sleep(200);
                advance();
                break;

            case GRAB:
                robot.claw.min();
                driver.drive = common.drive.sleep(200);
                advance();
                break;

            case SMALL_LIFT:
                robot.flipper.setPosition(0.01f);
                driver.drive = common.drive.sleep(200);
                advance();
                break;

            case SPIN_ATTACK:
                driver.drive = common.drive.degrees(180);
                advance();
                break;

            case MOVE_TO_WALL:
                if (stopByWall) {
                    driver.drive = common.drive.translate(InchesToMM(36.0f));
                } else {
                    driver.drive = common.drive.translate(InchesToMM(12.0f));
                }
                advance();
                break;

            case DRIVE_UNDER_BRIDGE:
                driver.drive = common.drive.distance(InchesToMM(12.0f));
                advance();
                break;

            case RETRACT_ARM:
                robot.flipper.setPosition(0.0f);
                robot.claw.min();
                driver.drive = common.drive.sleep(300);
                advance();
                break;

            case PARK:
                driver.drive = common.drive.distance(InchesToMM(-3.0f));
                advance();
                break;

            case DONE:
                driver.done = true;
                break;


        }

        // Update telemetry
        telemetry.update();
    }

    /**
     * Defines the order of the auto routine steps
     */
    enum AUTO_STATE implements OrderedEnum {
        INIT, // Initialization

        DETECT_SKYSTONE, // Where's Waldo?

        MOVE_TO_STONES, // Move closer to the stones

        ADJUST, // Line up with the skystone to pick it up

        PUSH_STONES, // Move the normie stones out of the way

        // Steps to grab the arm
        REACH_OUT_HALF, // Reach out the arm
        OPEN_CLAW,
        REACH_OUT_MORE,
        GRAB, //  Grab the skystone
        SMALL_LIFT, // Lift it off of the ground

        SPIN_ATTACK, // Rotate 180

        MOVE_TO_WALL, // Move back towards the wall (user configurable)

        DRIVE_UNDER_BRIDGE, // Go under the bridge

        RELEASE_STONE, // <--
        RETRACT_ARM,

        PARK, // Stop under the bridge

        DONE;

        public AUTO_STATE prev() { return OrderedEnumHelper.prev(this); }
        public AUTO_STATE next() { return OrderedEnumHelper.next(this); }
    }

    /**
     * Sets config booleans according to user input
     */
    private void userSettings(){
        buttons.update();

        if (buttons.get("SELECT_SIDE")) {
            color = Field.AllianceColor.RED;
        } else {
            color = Field.AllianceColor.BLUE;
        }
        telemetry.addData("Team Color", color.toString());

        if (buttons.get("AWAY_FROM_WALL")) stopByWall = false;
        if (buttons.get("TOWARDS_WALL")) stopByWall = true;
        telemetry.addData("Stop by wall?", stopByWall);
    }

    /**
     * Utility function to delegate AutoDriver to an external provider
     * AutoDriver is handed back up to caller when the delegate sets done to true
     *
     * @param autoDriver AutoDriver to be delegated
     * @return AutoDriver once delegate finishes
     */
    private AutoDriver delegateDriver(AutoDriver autoDriver) {
        if (autoDriver.isDone()) {
            autoDriver.done = false;
        }

        return autoDriver;
    }

    /**
     * does what it says on the tin
     *
     * @param inches inches
     * @return those inches but in millimeters
     */
    private int InchesToMM(float inches) {
        return (int) (inches * 25.4);
    }

    /**
     * just does state = state.next()
     * i don't want to keep writing that out
     */
    private void advance() {
        state = state.next();
    }

    private enum SkystonePos {
        LEFT, CENTER, RIGHT
    }
}
