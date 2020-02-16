package org.firstinspires.ftc.teamcode.utils;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.actuators.Actuators;
import org.firstinspires.ftc.teamcode.storage.config.Config;

import java.util.ArrayList;

public class RobotUtils {
    private final ArrayList<Actuators> actuators;
    public final OpMode opmode;
    public final Config config;

    protected RobotUtils(OpMode opmode) {
        Robot.R = (Robot) this;
        this.opmode = opmode;
        actuators = new ArrayList<>();
        config = new Config();
    }

    /**
     * Static method to kickstart this operation
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
     * Allow actuators to register themselves for global operations
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
    public void log(String s) {
        Log.i("Robot", s);
        opmode.telemetry.log().add(s);
    }

    /**
     * Log to the DS and Android log. Saves as "warn" level messages - the blue ones.
     *
     * @param s Log message
     */
    public void warn(String s) {
        Log.w("Robot", s);
        opmode.telemetry.log().add(s);
    }

    /**
     * Log to the DS and Android log. Saves as "err" level messages - the red ones.
     *
     * @param s Log message
     */
    public void err(String s) {
        Log.e("Robot", s);
        opmode.telemetry.log().add(s);
    }
}
