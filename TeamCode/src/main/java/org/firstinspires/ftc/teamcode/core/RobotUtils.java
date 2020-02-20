package org.firstinspires.ftc.teamcode.core;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.actuators.Actuators;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.storage.config.Config;
import org.firstinspires.ftc.teamcode.storage.globals.Globals;

import java.util.ArrayList;

public class RobotUtils {
    public static OpMode O = null;
    public final Config C;
    public final Globals G;
    public final ButtonHandler B;

    private final ArrayList<Actuators> actuators;

    protected RobotUtils(OpMode opmode) {
        Robot.R = (Robot) this;
        O = opmode;
        C = new Config();
        G = new Globals();
        B = new ButtonHandler();

        actuators = new ArrayList<>();
    }

    /**
     * Static method to reliably init and return a Robot
     *
     * @param opmode Current opmode (i.e. this)
     * @return The best available Robot, newly built if needed
     */
    public static Robot start(OpMode opmode) {
        if (Robot.R == null) {
            new Robot(opmode);
        }
        return Robot.R;
    }

    /**
     * Stop all registered actuators
     */
    public void stop() {
        for (Actuators a : actuators) {
            a.stop();
        }
    }

    /**
     * Allow actuators to register themselves for Robot-level operations
     *
     * @param s Typically "this" is sufficient
     */
    public void register(Actuators s) {
        actuators.add(s);
    }

    /**
     * Log to the DS and Android log. Saves as "info" level messages - the black ones.
     *
     * @param sender The class sending this message. Typically "this". Can be null.
     * @param msg    Log message
     */
    public static void log(Object sender, String msg) {
        log(sender, msg, Log.INFO);
    }

    /**
     * Log to the DS and Android log. Saves as "warn" level messages - the blue ones.
     *
     * @param sender The class sending this message. Typically "this". Can be null.
     * @param msg    Log message
     */
    public static void warn(Object sender, String msg) {
        log(sender, msg, Log.WARN);
    }

    /**
     * Log to the DS and Android log. Saves as "err" level messages - the red ones.
     *
     * @param sender The class sending this message. Typically "this". Can be null.
     * @param msg    Log message
     */
    public static void err(Object sender, String msg) {
        log(sender, msg, Log.ERROR);
    }

    /**
     * Log to Android log only. Saves as "verbose" level messages - the grey ones.
     *
     * @param sender The class sending this message. Typically "this". Can be null.
     * @param msg    Log message
     */
    public static void verbose(Object sender, String msg) {
        log(sender, msg, Log.VERBOSE);
    }

    /**
     * Internal log handler
     *
     * @param sender   The class sending this message
     * @param msg      Log message
     * @param priority Android log priority
     */
    private static void log(Object sender, String msg, int priority) {
        if (sender != null) {
            msg = sender.getClass().getSimpleName() + ": " + msg;
        }
        Log.println(priority, "Robot", msg);
        if (O != null && (priority != Log.VERBOSE && priority != Log.DEBUG)) {
            O.telemetry.log().add(msg);
        }
    }
}
