package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.robot.common.Arm;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "TeleOp")
public class TeleOpMode extends OpMode {

    // Devices and subsystems
    private Robot robot = null;
    private ButtonHandler buttons;

    // I don't wanna type out robot.common.arm
    private Arm arm;

    private float armX = 6.75f;
    private float armY = 5.5f;
    private final float ARM_ADJUSTMENT = 0.25f;

    @Override
    public void init() {
        // Placate drivers
        telemetry.addData(">", "NOT READY");
        telemetry.update();

        // Init the common tasks elements
        robot = new Robot(hardwareMap, telemetry);
        arm = robot.common.arm;

        // Register buttons
        buttons = new ButtonHandler(robot);
        buttons.register("ARMY_INC", gamepad1, PAD_BUTTON.dpad_up, BUTTON_TYPE.SINGLE_PRESS);
        buttons.register("ARMY_DEC", gamepad1, PAD_BUTTON.dpad_down, BUTTON_TYPE.SINGLE_PRESS);
        buttons.register("ARMX_INC", gamepad1, PAD_BUTTON.dpad_right, BUTTON_TYPE.SINGLE_PRESS);
        buttons.register("ARMX_DEC", gamepad1, PAD_BUTTON.dpad_left, BUTTON_TYPE.SINGLE_PRESS);


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
        // wheels are for the weak
    }

    private void auxiliary() {
        if (buttons.get("ARMY_INC")) armY += ARM_ADJUSTMENT;
        if (buttons.get("ARMY_DEC")) armY -= ARM_ADJUSTMENT;
        if (buttons.get("ARMX_INC")) armX += ARM_ADJUSTMENT;
        if (buttons.get("ARMX_DEC")) armX -= ARM_ADJUSTMENT;

        telemetry.addData("arm x: ", armX);
        telemetry.addData("arm y: ", armY);

        try {
            arm.moveToPos(armX, armY);
        } catch (Arm.OutOfRangeException e) {
            telemetry.addLine("Arm can't move to desired position");
        }
    }

    public void stop() {
    }

}