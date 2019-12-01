package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.defaults.Defaults;
import org.firstinspires.ftc.teamcode.robot.config.Hardware;
import org.firstinspires.ftc.teamcode.robot.config.BOTS;

public class RobotNG {
    // Static self reference
    public static RobotNG R = null;

    // Global public bits
    public final BOTS bot;
    public final ButtonHandler buttons;
    public final OpMode opMode;
    public final Hardware hardware;
    public final Defaults defaults;

    public RobotNG(OpMode opMode) {
        this.opMode = opMode;
        this.defaults = new Defaults(this);
        this.buttons = new ButtonHandler(this);

        // TODO: Bot detection based on RevHub or name
        bot = BOTS.NONE;

        // Config lives in Hardware
        this.hardware = new Hardware(this);

        // Provide a static reference once we're up and running
        R = this;
    }

    // Logging shortcut
    public void log(String msg) {
        opMode.telemetry.log().add(msg);
    }

    // Logging shortcut
    public void log(Object obj, String msg) {
        log(obj.getClass().getSimpleName() + ": " + msg);
    }
}
