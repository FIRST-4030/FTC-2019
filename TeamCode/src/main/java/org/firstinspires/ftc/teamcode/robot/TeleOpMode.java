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

    // Consts
    private static final float SLOW_MODE = 0.25f;
    private static final float NORMAL_SPEED = 1.0f;

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

        buttons.register("LIFT_UP", gamepad2, PAD_BUTTON.right_bumper);
        buttons.register("LIFT_DOWN", gamepad2, PAD_BUTTON.left_bumper);
        buttons.register("GRAB", gamepad2, PAD_BUTTON.x, BUTTON_TYPE.TOGGLE);
        buttons.register("CAPSTONE", gamepad2, PAD_BUTTON.y, BUTTON_TYPE.TOGGLE);


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
        //LIFT
        if(buttons.get("LIFT_UP")) {
            robot.lift.setPower(0.5f);
        } else {
            robot.lift.setPower(0.0f);
        }
        if(buttons.get("LIFT_DOWN")) {
            robot.lift.setPower(-0.5f);
        } else {
            robot.lift.setPower(0.0f);
        }

        //Collector
        if(buttons.get("COLLECT")){
            robot.collectorLeft.setPower(0.5f);
            robot.collectorRight.setPower(0.5f);
        } else {
            robot.collectorLeft.setPower(0.0f);
            robot.collectorRight.setPower(0.0f);
        }

        //Confusing trig stuff for swingy arm
        float armX = gamepad2.left_stick_x;
        float armY = -gamepad2.left_stick_y;
        float theta = (float)Math.atan(armY/armX)/360;
        if(armX<0.0f){
            robot.flipper.setPosition(theta + 18.0f/36.0f);
        } else{
            robot.flipper.setPosition(theta);
        }

        //CLAW
        if(buttons.get("GRAB")){
            robot.claw.setPosition(0.0f);
        } else {
            robot.claw.setPosition(0.5f);
        }

        //Capstone thingy
        if(buttons.get("CAPSTONE")){
            robot.capstone.setPosition(0.5f);
        } else {
            robot.capstone.setPosition(0.0f);
        }
    }

    public void stop() {
    }
}