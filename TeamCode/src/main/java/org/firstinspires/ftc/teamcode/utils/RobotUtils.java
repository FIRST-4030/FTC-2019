package org.firstinspires.ftc.teamcode.utils;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.actuators.Actuators;
import org.firstinspires.ftc.teamcode.storage.config.Config;
import org.firstinspires.ftc.teamcode.storage.globals.Globals;

import java.util.ArrayList;

public class RobotUtils {
    public static OpMode O = null;
    public final Config C;
    public final Globals G;

    private final ArrayList<Actuators> actuators;

    protected RobotUtils(OpMode opmode) {
        Robot.R = (Robot) this;
        O = opmode;
        C = new Config();
        G = new Globals();
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
     * @param s Log message
     */
    public static void log(String s) {
        Log.i("Robot", s);
        if (O != null) {
            O.telemetry.log().add(s);
        }
    }

    /**
     * Log to the DS and Android log. Saves as "warn" level messages - the blue ones.
     *
     * @param s Log message
     */
    public static void warn(String s) {
        Log.w("Robot", s);
        if (O != null) {
            O.telemetry.log().add(s);
        }
    }

    /**
     * Log to the DS and Android log. Saves as "err" level messages - the red ones.
     *
     * @param s Log message
     */
    public static void err(String s) {
        Log.e("Robot", s);
        if (O != null) {
            O.telemetry.log().add(s);
        }
    }

    /**
     * Log to Android log only. Saves as "verbose" level messages - the grey ones.
     *
     * @param s Log message
     */
    public static void verbose(String s) {
        Log.v("Robot", s);
    }
}
