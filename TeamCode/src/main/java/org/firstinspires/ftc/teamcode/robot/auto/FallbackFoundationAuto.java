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

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Grab Foundation (Fallback", group = "Scissor")
public class FallbackFoundationAuto extends OpMode {

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
    private boolean park_by_wall = true;
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

        robot.claw.setPosition(.6f);
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
        telemetry.addData("State", state.prev()); // Prev because it prints the wrong one otherwise
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
                robot.hookRight.max();
                robot.hookLeft.max();
                advance();
                break;

            case DRIVE_TO_FOUNDATION:
                driver.drive = common.drive.distance(InchesToMM(40.0f));
                advance();
                break;

            case INCH:
                driver.drive = common.drive.distance(InchesToMM(1.0f));
                advance();
                break;

            case GRAB:
                robot.hookRight.min();
                robot.hookLeft.min();
                driver.drive = common.drive.sleep(500);
                advance();
                break;

            case MOVE_BACK_TO_TURN:
                driver.drive = common.drive.distance(InchesToMM(-40.0f));
                advance();
                break;

            case TURN_TOWARDS_CORNER:
                if(color==Field.AllianceColor.BLUE){
                    driver.drive = common.drive.heading(270.0f);
                }else{
                    driver.drive = common.drive.heading(90.0f);
                }
                advance();
                break;


            case MOVE_INTO_CORNER:
                robot.hookRight.max();
                robot.hookLeft.max();
                driver.drive = common.drive.distance(InchesToMM(12.0f));
                advance();
                break;

            case CAP_OUT:
                robot.flipper.setPosition(0.85f);
                driver.drive = common.drive.sleep(1000);
                advance();
                break;

            case CAP_RELEASE:
                robot.claw.setPosition(0.35f);
                driver.drive = common.drive.sleep(300);
                advance();
                break;

            case ARM_IN:
                robot.flipper.setPosition(0.0f);
                driver.drive = common.drive.sleep(1000);
                advance();
                break;

            case BACK_UP_AWAY_FROM_CORNER:
                driver.drive = common.drive.distance(InchesToMM(-45.0f));
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

        DRIVE_TO_FOUNDATION, // Drive towards foundation

        INCH,

        GRAB, // Grab foundation

        MOVE_BACK_TO_TURN, // Moves back so that there's room to turn the foundation

        TURN_TOWARDS_CORNER, // Turn 90 degrees towards corner (building site)

        CAP_OUT,

        CAP_RELEASE,

        ARM_IN,


        MOVE_INTO_CORNER, // Push foundation into corner

        BACK_UP_AWAY_FROM_CORNER, // Backs up to previous position

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
}
